package uploderTest.uploadTest;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.poem.entity.InitializationConfiguration;
import org.poem.entity.LargeFileUploadChunkResult;
import org.poem.entity.LargeFileUploadResult;
import org.poem.entity.upload.PrepareUploadJson;
import org.poem.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uploderTest.entity.ConfigEntity;
import uploderTest.entity.FileEntity;
import uploderTest.entity.Uploader;
import uploderTest.utils.SplitFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;


/**
 *
 */
public class FileUploaderTest {


    private static final Logger logger = LoggerFactory.getLogger(FileUploaderTest.class);

    public static final String CHARSET_NAME = "UTF-8";

    public static final String url = "http://10.6.22.32:8082";

    /**
     * 超时时间 5 分钟
     */
    public static final long timeout = TimeUnit.MINUTES.toMillis(5);

    private static final String clinetId;

    private static String filePath = "C:\\Users\\yineng\\Desktop\\R0011368.docx";

    private static ExecutorService executorService;

    static {
        clinetId = UUID.randomUUID().toString();
        executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * @param file
     * @return
     * @throws Exception
     */
    private static String doChecksum(File file) throws Exception {
        CRC32 crc32 = new CRC32();
        // MessageDigest.get
        FileInputStream fileInputStream = null;
        String code;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[102400];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                crc32.update(buffer, 0, length);
            }
            code = crc32.getValue() + "";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return code;
    }


    /**
     * 条件查询
     *
     * @return
     */
    public static Map<String, String> requestMap() {
        Map<String, String> requestMaps = Maps.newHashMap();
        requestMaps.put("clientId", clinetId);
        return requestMaps;
    }

    /**
     * 获取文件的配置
     *
     * @param cookieStore
     * @return
     */
    public static InitializationConfiguration getConfig(CookieStore cookieStore) {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        Gson gson = new Gson();
        HttpPost request = new HttpPost(url + "/fs/largeUploader/getConfig");
        List<BasicNameValuePair> nameValuePairList = new ArrayList<>();
        Map<String, String> prepareUploadReustMap = Maps.newHashMap();
        prepareUploadReustMap.putAll(requestMap());
        for (String key : prepareUploadReustMap.keySet()) {
            nameValuePairList.add(new BasicNameValuePair(key, prepareUploadReustMap.get(key)));
        }
        try {
            UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(nameValuePairList, CHARSET_NAME);
            request.setEntity(paramsEntity);
            client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String resultString = "";
            if (entity != null) {
                resultString = EntityUtils.toString(entity, CHARSET_NAME);
            }
            if (200 == response.getStatusLine().getStatusCode()) {
                if (StringUtils.isNotBlank(resultString)) {
                    InitializationConfiguration initializationConfiguration = gson.fromJson(resultString, InitializationConfiguration.class);
                    return initializationConfiguration;
                }
            } else {
                throw new RuntimeException(resultString);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            request.abort();
            IOUtils.closeQuietly(client);
            IOUtils.closeQuietly(response);
        }
        return null;
    }

    /**
     * 准备上传文件
     *
     * @param cookieStore
     * @param files
     * @return
     */
    private static Map<String, String> prepareUpload(CookieStore cookieStore, List<File> files) {
        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        HttpPost request  = new HttpPost(url + "/fs/largeUploader/prepareUpload");
        Map<String, String> prepareUploadMap = null;
        try {
            Map<String, String> prepareUploadReustMap = Maps.newHashMap();
            prepareUploadReustMap.putAll(requestMap());
            Gson gson = new Gson();
            List<PrepareUploadJson> prepareUploadJsons = getFileJson(files);
            PrepareUploadJson[] prepareUploadJsonsArr = prepareUploadJsons.toArray(new PrepareUploadJson[prepareUploadJsons.size()]);
            prepareUploadReustMap.put("newFiles", gson.toJson(prepareUploadJsonsArr));
            List<BasicNameValuePair> nameValuePairList = new ArrayList<>();
            for (String key : prepareUploadReustMap.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, prepareUploadReustMap.get(key)));
            }
            UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(nameValuePairList, CHARSET_NAME);
            request.setEntity(paramsEntity);
            client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            response = client.execute(request);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                HttpEntity entity = response.getEntity();
                String jsonString = null;
                if (entity != null) {
                    jsonString = EntityUtils.toString(entity, CHARSET_NAME);
                }
                prepareUploadMap = gson.fromJson(jsonString, Map.class);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            request.abort();
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(client);
        }
        return prepareUploadMap;
    }


    /**
     * 获取文件信息
     *
     * @param files
     * @return
     * @throws Exception
     */
    public static List<PrepareUploadJson> getFileJson(List<File> files) throws Exception {
        List<PrepareUploadJson> prepareUploadJsons = Lists.newArrayList();
        Integer i = 0;
        for (File file : files) {
            PrepareUploadJson prepareUploadJson = new PrepareUploadJson();
            prepareUploadJson.setFileName(file.getName());
            prepareUploadJson.setSize(file.length());
            String crc = doChecksum(file);
            prepareUploadJson.setCrc(crc);
            prepareUploadJson.setTempId(i++);
            prepareUploadJsons.add(prepareUploadJson);
        }
        return prepareUploadJsons;
    }

    /**
     * 获取文件夹下的所有的文件
     *
     * @param strPath
     * @return
     */
    private static List<File> getFileList(String strPath) {
        File dir = new File(strPath);
        List<File> filelist = Lists.newArrayList();
        if (dir.isFile()) {
            if (!dir.exists()) {
                throw new RuntimeException("file not found");
            }
            filelist.add(dir);
            return filelist;
        }
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        File file;
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                file = files[i];
                if (file.isDirectory()) { // 判断是文件还是文件夹
                    List<File> files1 = getFileList(file.getAbsolutePath()); // 获取文件绝对路径
                    if (CollectionUtils.isNotEmpty(files1)) {
                        filelist.addAll(files1);
                    }
                } else { // 判断文件名是否以.avi结尾
                    filelist.add(file);
                }
            }
        }
        return filelist;
    }

    /**
     * 上传文件
     */

    public static void uploadFile(CookieStore cookieStore) {
        logger.info(" ........ begin upload file to FS.  ........");
        InitializationConfiguration initializationConfiguration;

        List<File> files = getFileList(filePath);
        Map<String, String> prepareUploadMap = null;
        try {
            //1.上传文件信息
            logger.info("1.上传文件信息....." + files.size());
            initializationConfiguration = getConfig(cookieStore);

            //2.上传文件
            logger.info("2.准备上传文件.....");
            prepareUploadMap = prepareUpload(cookieStore, files);

            if (prepareUploadMap == null) {
                return;
            }
            logger.info("3.上传文件....." + prepareUploadMap.size());
            aysnUploadFileToFs(prepareUploadMap, files, cookieStore, null, (int) initializationConfiguration.getInByte());

            logger.info("4.模拟异步获取上传的信息");
            ExecutorService service = Executors.newCachedThreadPool();
            ConfigEntity config = new ConfigEntity(service, cookieStore);
            service.execute(config);

            //3.获取上传信息
            logger.info("5.获取上传信息.....");
            errorCheckAndNomalCheck(cookieStore, files, prepareUploadMap);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //发生异常再次检查，如果再次错误就不检查了
            errorCheckAndNomalCheck(cookieStore, files, prepareUploadMap);
        }
    }

    /**
     * 检查
     * @param cookieStore
     * @param files
     * @param prepareUploadMap
     */
    private static void errorCheckAndNomalCheck(CookieStore cookieStore, List<File> files, Map<String, String> prepareUploadMap) {
        while (true) {
            try {
                if (checkFileUploadResult(prepareUploadMap, files, cookieStore)) {
                    break;
                }
                Thread.sleep(2000);
            } catch (Exception e1) {
                logger.error(e1.getMessage(), e1);
            }
        }
    }


    /**
     * 上传是否完成
     *
     * @param largeFileUploadResult
     * @return
     */
    private static Boolean uploadOver(LargeFileUploadResult largeFileUploadResult) {
        if (largeFileUploadResult.getStatus().get()) {
            return true;
        }
        return false;
    }

    /**
     * 检查文件是否上传完成
     *
     * @param prepareUploadMap
     * @param files
     * @param cookieStore
     * @return
     * @throws Exception
     */
    private static boolean checkFileUploadResult(Map<String, String> prepareUploadMap, List<File> files, CookieStore cookieStore) throws Exception {
        InitializationConfiguration initializationConfiguration;
        Map<String, LargeFileUploadResult> fileStateJsonMap;
        initializationConfiguration = getConfig(cookieStore);
        int successSum = 0;
        if (initializationConfiguration != null) {
            logger.info("检查是否上传完成\n\n" + new Gson().toJson(initializationConfiguration) + "\n\n");
            fileStateJsonMap = initializationConfiguration.getLargeFileUploadResultMap();
            for (String file : fileStateJsonMap.keySet()) {
                LargeFileUploadResult fileStateJson = fileStateJsonMap.get(file);
                if (!uploadOver(fileStateJson)) {
                    aysnUploadFileToFs(prepareUploadMap, files, cookieStore, fileStateJson, (int) initializationConfiguration.getInByte());
                } else {
                    successSum += 1;
                }
            }
            //上传完成咯，可以啦
            if (successSum == fileStateJsonMap.size() && successSum != 0) {
                //再次获取数据，显示出来看看
                initializationConfiguration = getConfig(cookieStore);
                logger.info(" 上传信息是：\n\n\t" + new Gson().toJson(initializationConfiguration) + "\n\n");
                return true;
            }
        }
        return false;
    }

    /**
     * 多线程上传文件
     *
     * @param prepareUploadMap
     * @param files
     * @param cookieStore
     * @param inByte
     */
    private static void aysnUploadFileToFs(Map<String, String> prepareUploadMap, List<File> files, CookieStore cookieStore, LargeFileUploadResult largeFileUploadResult, int inByte) {
        Uploader uploader;
        try {
            Integer i = 0;
            String fileId;
            for (File file : files) {
                fileId = prepareUploadMap.get(String.valueOf(i++));
                Set<FileEntity> fileEntities = SplitFileUtils.splitFile(file.getAbsolutePath(), inByte);
                LargeFileUploadChunkResult largeFileUploadChunkResult;
                for (FileEntity fileEntity : fileEntities) {
                    if (largeFileUploadResult != null) {
                        largeFileUploadChunkResult = largeFileUploadResult.getLargeFileUploadChunkResultMap().get(fileId + "@" + String.valueOf(String.valueOf(fileEntity.getFileChunk())));
                        if (largeFileUploadChunkResult != null && largeFileUploadChunkResult.getStatus()) {
                            continue;
                        }
                    }
                    uploader = new Uploader(fileEntity, fileId,clinetId, cookieStore, file.getName());
                    executorService.execute(uploader);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws Exception {
        CookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie basicClientCookie = new BasicClientCookie("clientId",clinetId);
        basicClientCookie.setDomain(url);

        cookieStore.addCookie(basicClientCookie);

        uploadFile(cookieStore);

        executorService.shutdown();
        //判断是否所有的线程已经运行完

        while (!executorService.isTerminated()) {

        }
        System.exit(0);
    }
}
