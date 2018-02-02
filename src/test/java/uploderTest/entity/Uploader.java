package uploderTest.entity;

import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uploderTest.uploadTest.FileUploaderTest;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;

public class Uploader implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(Uploader.class);


    private FileEntity fileEntity;
    String fileId;
    String fileSrc;
    CookieStore cookieStore;
    DefaultHttpClient client;
    String fileName;

    public Uploader(FileEntity fileEntity, String fileId, String fileSrc, CookieStore cookieStore, DefaultHttpClient client, String fileName) {
        this.fileEntity = fileEntity;
        this.fileId = fileId;
        this.fileSrc = fileSrc;
        this.cookieStore = cookieStore;
        this.client = client;
        this.fileName = fileName;
    }

    @Override
    public void  run() {
        HttpPost request = null;
        CloseableHttpResponse response = null;
        try {
            client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 100000);
            client.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 100000);
            String uploadUrl = FileUploaderTest.url+  "/fs/largeUploader/asyncFileUploader?timestamp="+new Date().getTime();
            request = new HttpPost(uploadUrl);
                /*表单提交*/
            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.addPart("fileId",new StringBody(fileId, Charset.forName("UTF-8")));
            multipartEntity.addPart("crc", new StringBody(fileEntity.getFileCheckNum(),Charset.forName("UTF-8")));
            multipartEntity.addPart("file", new ByteArrayBody(fileEntity.getFileChunk(),fileName));
            Map<String, String> requestMap = FileUploaderTest.requestMap();
            for (String key : requestMap.keySet()) {
                multipartEntity.addPart(key,new StringBody(requestMap.get(key),Charset.forName("UTF-8")));
            }
            request.setEntity(multipartEntity);
            request.setHeader("Origin-Rang", fileEntity.getFileOffset()+"-" +fileEntity.getFileEnd() + "-"+fileEntity.getPartNumber());
            this.client.setCookieStore(cookieStore);
            response = this.client.execute(request);
            if(200 == response.getStatusLine().getStatusCode()){
                logger.info(Thread.currentThread().getName() + "\t开始上传"+fileId+"第:"+(fileEntity.getPartNumber()+1)+"个文件 over.");
            }else{
                HttpEntity entity = response.getEntity();
                String resultString = "";
                if (entity != null) {
                    resultString = EntityUtils.toString(entity, FileUploaderTest.CHARSET_NAME);
                }
                logger.error(resultString);
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }finally {
            if(request != null){
                request.abort();
            }
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
