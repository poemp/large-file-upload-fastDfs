package org.poem.common.uploader;

import org.apache.commons.io.IOUtils;
import org.poem.common.controller.LargeFileUploaderController;
import org.poem.common.helper.LargeFileUploaderHelper;
import org.poem.entity.LargeFileUploadResult;
import org.poem.entity.upload.FileUploadConfiguration;
import org.poem.utils.StorageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * 上查的对象
 */
public class FileUploader implements Callable<Void> {

    private static final Logger logger = LoggerFactory.getLogger(FileUploader.class);

    private String clientId;
    /**
     * 上传对象的信息
     */
    private FileUploadConfiguration fileUploadConfiguration;

    /**
     * 执行器
     */
    private ExecutorService executorService;

    /**
     * 文件基本信息
     */
    private LargeFileUploadResult largeFileUploadResult;

    /**
     * 完成监控
     */
    private WriteChunkCompletionListener writeChunkCompletionListener;

    /**
     * 更新数据
     */
    private LargeFileUploaderHelper largeFileUploaderHelper;

    /**
     * constructor
     *
     * @param fileUploadConfiguration
     * @param executorService
     * @param largeFileUploadResult
     * @param writeChunkCompletionListener
     */
    public FileUploader(String clientId,FileUploadConfiguration fileUploadConfiguration, ExecutorService executorService,
                        LargeFileUploadResult largeFileUploadResult,
                        WriteChunkCompletionListener writeChunkCompletionListener,
                        LargeFileUploaderHelper largeFileUploaderHelper) {
        this.clientId = clientId;
        this.fileUploadConfiguration = fileUploadConfiguration;
        this.executorService = executorService;
        this.largeFileUploadResult = largeFileUploadResult;
        this.writeChunkCompletionListener = writeChunkCompletionListener;
        this.largeFileUploaderHelper = largeFileUploaderHelper;
    }

    /**
     * 执行
     *
     * @return
     * @throws Exception
     */
    @Override
    public Void call() throws Exception {
        logger.info(this.clientId + "   \t\tbegin Upload");
        this.writeChunkCompletionListener.start();
        byte[] bytes = new byte[LargeFileUploaderController.default_size];
        InputStream inputStream = this.fileUploadConfiguration.getInputStream();
        try {
            int length = inputStream.read(bytes);
            if (length != -1){
                largeFileUploadResult = StorageUtils.uploadLargeFile(bytes, largeFileUploadResult, this.fileUploadConfiguration);
                executorService.submit(this);
            }else{
                this.writeChunkCompletionListener.success();
            }
            logger.info("上传完成.");
            largeFileUploaderHelper.updateEntity(clientId,fileUploadConfiguration.getFileId().toString(),largeFileUploadResult);
        } catch (Exception e) {
            e.printStackTrace();
            this.writeChunkCompletionListener.error(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return null;
    }

    public FileUploadConfiguration getFileUploadConfiguration() {
        return fileUploadConfiguration;
    }

    public void setFileUploadConfiguration(FileUploadConfiguration fileUploadConfiguration) {
        this.fileUploadConfiguration = fileUploadConfiguration;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public LargeFileUploadResult getLargeFileUploadResult() {
        return largeFileUploadResult;
    }

    public void setLargeFileUploadResult(LargeFileUploadResult largeFileUploadResult) {
        this.largeFileUploadResult = largeFileUploadResult;
    }

    public WriteChunkCompletionListener getWriteChunkCompletionListener() {
        return writeChunkCompletionListener;
    }

    public void setWriteChunkCompletionListener(WriteChunkCompletionListener writeChunkCompletionListener) {
        this.writeChunkCompletionListener = writeChunkCompletionListener;
    }
}
