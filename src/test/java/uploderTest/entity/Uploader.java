package uploderTest.entity;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uploderTest.uploadTest.FileUploaderTest;

import java.io.IOException;
import java.util.concurrent.Callable;

public class Uploader implements Callable<Void> {

    private static final Logger logger = LoggerFactory.getLogger(Uploader.class);


    private FileEntity fileEntity;
    private String fileId;
    private String clientId;
    private CookieStore cookieStore;
    private String fileName;

    public Uploader(FileEntity fileEntity, String fileId, String clientId, CookieStore cookieStore, String fileName) {
        this.fileEntity = fileEntity;
        this.fileId = fileId;
        this.clientId = clientId;
        this.cookieStore = cookieStore;
        this.fileName = fileName;
    }

    @Override
    public Void call() {
        HttpPost request = null;
        CloseableHttpResponse response = null;
        CloseableHttpClient client = null;
        try {
            String uploadUrl = FileUploaderTest.url + "/fs/largeUploader/asyncFileUploader?fileId=" + fileId + "&crc="+fileEntity.getFileCheckNum() + "&clientId="+clientId;
            request = new HttpPost(uploadUrl);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout((int) FileUploaderTest.timeout).setConnectTimeout((int) FileUploaderTest.timeout).build();
            request.setConfig(requestConfig);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file",this.fileEntity.getFileChunk(), ContentType.DEFAULT_BINARY, this.fileName);
            client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            request.setEntity(builder.build());
            request.setHeader("Origin-Rang", fileEntity.getFileOffset() + "-" + fileEntity.getFileEnd() + "-" + fileEntity.getPartNumber());

            response = client.execute(request);
            if (200 == response.getStatusLine().getStatusCode()) {
                logger.info(Thread.currentThread().getName() + "\t开始上传" + fileId + "第:" + (fileEntity.getPartNumber() + 1) + "个文件 over.");
            } else {
                HttpEntity entity = response.getEntity();
                String resultString = "";
                if (entity != null) {
                    resultString = EntityUtils.toString(entity, FileUploaderTest.CHARSET_NAME);
                }
                logger.error(Thread.currentThread().getName() + "\t" + resultString);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (request != null) {
                request.abort();
            }
            IOUtils.closeQuietly(client);
            IOUtils.closeQuietly(response);
        }
        return null;
    }
}
