package org.poem.utils;

import org.apache.commons.io.FilenameUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.poem.entity.LargeFileUploadChunkResult;
import org.poem.entity.LargeFileUploadResult;
import org.poem.entity.upload.FileUploadConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * fastDfs uploader config
 */
public class StorageUtils {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageUtils.class);

    /**
     * single thread to config fastdfs config
     */
    private static boolean isInit = false;

    /**
     * defaultSize to spilt
     */
    private final static long defaultSize = 10 * 1024 * 1024;


    /**
     * init
     *
     * @throws Exception
     */
    private static void init() throws Exception {
        String classPath = URLUtil.getClassPath(StorageUtils.class);
        int index = classPath.indexOf("file:");
        classPath = classPath.substring(0, index == -1 ? classPath.length() : index);
        String configFilePath = classPath.lastIndexOf(File.separator) != (classPath.length() - 1) ? classPath + File.separator + "client.conf" : classPath + "client.conf";
        ClientGlobal.init(configFilePath);
    }

    private synchronized static TrackerClient getTrackerClient() throws Exception {
        if (!isInit) {
            init();
            isInit = true;
        }
        TrackerClient trackerClient = new TrackerClient();
        return trackerClient;
    }


    /**
     * uplod file
     *
     * @param buffer
     * @param largeFileUploadResult
     * @param process
     */
    public static LargeFileUploadResult uploadLargeFile(byte[] buffer, LargeFileUploadResult largeFileUploadResult, FileUploadConfiguration process) throws Exception {
        ConcurrentHashMap<String, LargeFileUploadChunkResult> largeFileUploadChunkResultMap;
        long fileLength = 0L;
        //第一次上传
        if (null == largeFileUploadResult.getCrcedBytes() || largeFileUploadResult.getCrcedBytes().get() == 0) {
            //初始化一个文件
            if (largeFileUploadResult.getOriginalFileSizeInBytes() >= defaultSize) {
                largeFileUploadResult = StorageUtils.intUploadLargeFileToFastDfs(largeFileUploadResult.getOriginalFileName(), largeFileUploadResult, "group1", process, buffer, process.getPartNumber());
            } else {
                largeFileUploadResult = StorageUtils.intFile(largeFileUploadResult.getOriginalFileName(), largeFileUploadResult, "group1", process, buffer);
                largeFileUploadResult.setCrcedBytes(new AtomicLong(buffer.length));
            }
        } else {
            if (largeFileUploadResult.getOriginalFileSize() == null) {
                largeFileUploadResult.setOriginalFileSize(new AtomicLong(0L));
            }
            fileLength = StorageUtils.modifyFastDFs(largeFileUploadResult.getGroupName(), largeFileUploadResult.getUploadFileId(), process, buffer, process.getPartNumber());
            largeFileUploadResult.setCrcedBytes(new AtomicLong(fileLength + largeFileUploadResult.getCrcedBytes().get()));
        }
        largeFileUploadResult.setOriginalFileSize(new AtomicLong(fileLength));
        largeFileUploadResult.setCrcedBytes(new AtomicLong(fileLength + largeFileUploadResult.getCrcedBytes().get()));
        largeFileUploadChunkResultMap = largeFileUploadResult.getLargeFileUploadChunkResultMap();
        if (largeFileUploadChunkResultMap == null) {
            largeFileUploadChunkResultMap = new ConcurrentHashMap<>();
        }
        int length = (int) (process.getFileEnd() - process.getFileOffset());
        largeFileUploadChunkResultMap.put(process.getCrc() + "@" + process.getPartNumber(), new LargeFileUploadChunkResult(process.getFileOffset(), process.getFileEnd(), process.getPartNumber(), process.getCrc(), length, true));
        largeFileUploadResult.setLargeFileUploadChunkResultMap(largeFileUploadChunkResultMap);
        largeFileUploadResult.setCrcedBytes(new AtomicLong(fileLength + largeFileUploadResult.getCrcedBytes().get()));
        return largeFileUploadResult;
    }

    /**
     * 初始化一个文件
     *
     * @param fileName  文件名
     * @param largeFileUploadResult 文件资源信息
     * @param group     文件分组
     * @param process   文件上传信息
     * @param buffer    文件流
     * @return
     * @throws Exception
     */
    private static synchronized LargeFileUploadResult intUploadLargeFileToFastDfs(String fileName, LargeFileUploadResult largeFileUploadResult, String group, FileUploadConfiguration process, byte[] buffer, int part) throws Exception {
        //同步锁，锁住，不能加载static方法上，是锁定所有的至
        if (null == largeFileUploadResult.getCrcedBytes() || largeFileUploadResult.getCrcedBytes().get() == 0) {
            largeFileUploadResult = StorageUtils.intFile(fileName, largeFileUploadResult, group, process, buffer);
            largeFileUploadResult.setCrcedBytes(new AtomicLong(buffer.length));
        } else {
            long length = modifyFastDFs(group, largeFileUploadResult.getUploadFileId(), process, buffer, part);
            largeFileUploadResult.setOriginalFileSize(new AtomicLong(length));
            largeFileUploadResult.setCrcedBytes(new AtomicLong(length + largeFileUploadResult.getCrcedBytes().get()));
        }
        return largeFileUploadResult;
    }

    /**
     * 初始化一个文件
     *
     * @param fileName  文件名
     * @param fileState 文件资源信息
     * @param group     文件分组
     * @param process   文件上传信息
     * @param buffer    文件流
     * @return
     * @throws Exception
     */
    private static LargeFileUploadResult intFile(String fileName, LargeFileUploadResult fileState, String group, FileUploadConfiguration process, byte[] buffer) throws Exception {
        org.poem.client.StorageClient storageClient;
        TrackerServer trackerServer = null;
        String[] results = null;
        FileInputStream fis = null;
        long length;
        TrackerClient trackerClient;
        try {
            //建立连接
            trackerClient = getTrackerClient();
            trackerServer = trackerClient.getConnection();
            storageClient = new org.poem.client.StorageClient(trackerServer, null);
            long originalFileSize = fileState.getOriginalFileSizeInBytes();
            NameValuePair[] vars = new NameValuePair[]{new NameValuePair("fileName", fileName), new NameValuePair("fileSize", String.valueOf(originalFileSize))};
            int number = (int) (originalFileSize / defaultSize), leftLength;
            number = originalFileSize % defaultSize == 0 ? number : number + 1;
            byte[] bytes;
            if (originalFileSize > defaultSize) {
                /**
                 * 如果文件块大，则实现分块上传，需要准备一个空的文件
                 */
                for (int i = 0; i < number; i++) {
                    if (originalFileSize - (i) * defaultSize < defaultSize) {
                        leftLength = (int) (originalFileSize - (i) * defaultSize);
                        leftLength = leftLength < 0 ? (int) originalFileSize : leftLength;
                        bytes = new byte[leftLength];
                        if (i == 0) {
                            results = storageClient.upload_appender_file(group, bytes, 0, leftLength, FilenameUtils.getExtension(fileName), vars);
                        } else {
                        /*采用追加的方式*/
                            storageClient.append_file(results[0], results[1], bytes, 0, leftLength);
                        }
                    } else {
                        bytes = new byte[(int) defaultSize];
                        leftLength = (int) defaultSize;
                        if (i == 0) {
                            results = storageClient.upload_appender_file(group, bytes, 0, leftLength, FilenameUtils.getExtension(fileName), vars);
                        } else {
                            /*采用追加的方式*/
                            storageClient.append_file(results[0], results[1], bytes, 0, leftLength);
                        }
                    }
                }
                fileState.setStatus(new AtomicBoolean(false));
                fileState.setUploadFileId( results[1]);
                fileState.setGroupName(results[0]);
                if (ArrayUtils.isEmpty(results)) {
                    LOGGER.warn("upload_file: " + fileName + ",result is null, haven't been upload to FastFS, please check");
                }
                length = modifyFastDFs(group, results[1], process, buffer, process.getPartNumber());
                fileState.setOriginalFileSize(new AtomicLong(length));
            } else {
                /**
                 * 如果文件比默认的文件要小，则直接上传，就不走创建空文件这一步，减少流量，减少操作
                 * 减少CPU的占用
                 */
                results = storageClient.upload_file(group, buffer, FilenameUtils.getExtension(fileName), vars);
                fileState.setStatus(new AtomicBoolean(false));
                fileState.setUploadFileId( results[1]);
                fileState.setGroupName(results[0]);
                length = buffer.length;
                fileState.setOriginalFileSize(new AtomicLong(length));
            }
            fileState.getLargeFileUploadChunkResultMap().put(process.getCrc() + "@" + process.getPartNumber(),
                    new LargeFileUploadChunkResult(process.getFileOffset(), process.getFileEnd(), process.getPartNumber(), process.getCrc(), (int) length, true));
            LOGGER.info(String.format("%s uploadFileResult : %40s  %30s upload result is  %10s[%50s]", Thread.currentThread().getName(), process.getFileId(), fileName, fileState.getStatus(), fileState.getUploadFileId()));
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(e);
        } catch (MyException e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (null != trackerServer) {
                    trackerServer.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                throw new Exception(e);
            }
        }
        return fileState;
    }

    /**
     * FastDFs 断点上传
     *
     * @param group
     * @param fileId
     * @return
     */
    private static long modifyFastDFs(String group, String fileId, FileUploadConfiguration configuration, byte[] bytes, int part) throws Exception {
        long appendLength = configuration.getFileOffset();
        int length = (int) (configuration.getFileEnd() - configuration.getFileOffset());
        appendLength += StorageUtils.appendFileToFastDfs(group, fileId, bytes, configuration.getFileOffset(), length, part);
        return appendLength;
    }

    /**
     * FastDFs 断点上传
     *
     * @param group        文件group
     * @param fileId       文件id
     * @param fileBuff     文件字节
     * @param fileOffset   上传文件 字节 开始位置
     * @param bufferLength 上传文件 字节 长度
     * @return
     */
    private static long appendFileToFastDfs(String group, String fileId, byte[] fileBuff, long fileOffset, int bufferLength, int part) throws Exception {
        StorageClient storageClient;
        TrackerServer trackerServer = null;
        TrackerClient trackerClient;
        try {
            //建立连接
            trackerClient = getTrackerClient();
            trackerServer = trackerClient.getConnection();
            storageClient = new StorageClient(trackerServer, null);
            return storageClient.modify_file(group, fileId, fileOffset, fileBuff, 0, bufferLength);
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(e);
        } catch (MyException e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new Exception(e);
        } finally {
            try {
                if (null != trackerServer) {
                    trackerServer.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                throw new Exception(e);
            }
            return 0L;
        }
    }
}
