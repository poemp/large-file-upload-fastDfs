package org.poem.common.helper;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.poem.common.enums.LargeFileUploadAction;
import org.poem.common.exceptpion.ParameterMissException;
import org.poem.common.exceptpion.UploadFileInfoNotFoundException;
import org.poem.common.exceptpion.UploadFileNotFoundException;
import org.poem.entity.InitializationConfiguration;
import org.poem.entity.LargeFileUploadResult;
import org.poem.entity.upload.FileUploadConfiguration;
import org.poem.entity.upload.PrepareUploadJson;
import org.poem.utils.RedisUtils;
import org.poem.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class LargeFileUploaderHelper {

    private static final String COOKIE_NAME = "largeFileCookieName";

    private static final Logger logger = LoggerFactory.getLogger(LargeFileUploaderHelper.class);

    @Autowired
    private SchoolThreadLocalContainer schoolThreadLocalContainer;

    /**
     * 获取配置
     *
     * @return
     */
    public InitializationConfiguration getConfig(Boolean force) throws ParameterMissException{
        HttpServletRequest request = schoolThreadLocalContainer.getRequest();
        HttpServletResponse response = schoolThreadLocalContainer.getResponse();
        Object result = RedisUtils.get(getClientId(request, response).toString());
        if(result == null && force){
            throw new ParameterMissException(LargeFileUploadAction.clientId.name());
        }
        if (null != result) {
            return (InitializationConfiguration) result;
        }
        //没有就写入
        InitializationConfiguration initializationConfiguration = new InitializationConfiguration();
        initializationConfiguration.setInByte(10 * 1024 * 1024);
        this.updateEntity(getClientId(request, response).toString(), initializationConfiguration);
        return initializationConfiguration;
    }

    /**
     * 全部的异常的处理
     */
    public void processException(UUID client, UUID fileId, Exception e) {
        Object o = getEntity(client.toString());
        if (o == null) {
            writeToResponse(new UploadFileInfoNotFoundException(client + " info not found."));
        }
        InitializationConfiguration initializationConfiguration = (InitializationConfiguration) o;
        LargeFileUploadResult largeFileUploadResult = initializationConfiguration.getLargeFileUploadResultMap().get(fileId.toString());
        largeFileUploadResult.setError(true);
        this.updateEntity(client.toString(), initializationConfiguration);
        writeToResponse(e);
    }

    /**
     * 把信息写入到Response中，发送给客户端
     *
     * @param jsonObject
     * @throws java.io.IOException
     */
    public void writeToResponse(Serializable jsonObject) {
        HttpServletResponse response = schoolThreadLocalContainer.getResponse();
        response.setContentType("application/json");
        try {
            response.setStatus(500);
            response.getOutputStream().print(jsonObject instanceof String ? jsonObject.toString():JSON.toJSONString(jsonObject));
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param clientId
     * @param object
     */
    public void updateEntity(String clientId, Serializable object) {
        logger.info(Thread.currentThread().getName() + "\t"+ clientId + " write data.");
        RedisUtils.set(clientId, object);
    }

    /**
     * 跟新数据
     * @param client
     * @param fileId
     * @param largeFileUploadResult
     */
    public void updateEntity(String client,String fileId,LargeFileUploadResult largeFileUploadResult){
        InitializationConfiguration initializationConfiguration = (InitializationConfiguration)getEntity(client);
        initializationConfiguration.getLargeFileUploadResultMap().put(fileId,largeFileUploadResult);
        updateEntity(client,initializationConfiguration);
    }
    /**
     * 获取数值
     *
     * @param clientId
     * @return
     */
    public Object getEntity(String clientId) {
        return RedisUtils.get(clientId);
    }

    /**
     * 获取文件目录信息
     *
     * @param fromJson
     * @return
     */
    public Map<String, UUID> prepareUpload(PrepareUploadJson[] fromJson) throws ParameterMissException {
        InitializationConfiguration initializationConfiguration = this.getConfig(true);
        Map<String, UUID> f = Maps.newHashMap();
        ConcurrentHashMap<String, LargeFileUploadResult> largeFileUploadResultMap = new ConcurrentHashMap<>();
        LargeFileUploadResult largeFileUploadResult;
        UUID fileId;
        for (PrepareUploadJson prepareUploadJson : fromJson) {
            fileId = UUID.randomUUID();
            largeFileUploadResult = this.prepareResult(fileId, prepareUploadJson);
            f.put(String.valueOf(prepareUploadJson.getTempId()), fileId);
            largeFileUploadResultMap.put(fileId.toString(), largeFileUploadResult);
        }
        initializationConfiguration.setLargeFileUploadResultMap(largeFileUploadResultMap);
        HttpServletRequest request = schoolThreadLocalContainer.getRequest();
        HttpServletResponse response = schoolThreadLocalContainer.getResponse();
        updateEntity(getClientId(request, response).toString(), initializationConfiguration);
        return f;
    }

    /**
     * 获取客户id
     *
     * @param request
     * @param response
     * @return
     */
    public UUID getClientId(HttpServletRequest request, HttpServletResponse response) throws ParameterMissException {
        //从session中获取数据
        UUID clientId = (UUID) request.getSession().getAttribute(COOKIE_NAME);
        //没有，就从cookie中获取
        if (clientId == null) {
            clientId = getClientFromCookie(request);
        }
        if(clientId == null){
            String id = this.getParamter(request,LargeFileUploadAction.clientId,false);
            if(StringUtils.isNotEmpty(id)){
                return  UUID.fromString(id);
            }
        }
        //还是没有，则随机创建一个
        if (clientId == null) {
            clientId = UUID.randomUUID();
        }
        writeToCookie(request, response, clientId);
        return clientId;
    }

    /**
     * 准备上传的信息
     *
     * @param fileId
     * @param prepareUploadJson
     * @return
     */
    private LargeFileUploadResult prepareResult(UUID fileId, PrepareUploadJson prepareUploadJson) {
        LargeFileUploadResult largeFileUploadResult = new LargeFileUploadResult();
        largeFileUploadResult.setFileComplete(false);
        largeFileUploadResult.setOriginalFileName(prepareUploadJson.getFileName());
        largeFileUploadResult.setOriginalFileSize(new AtomicLong(prepareUploadJson.getSize()));
        largeFileUploadResult.setCrcedBytes(new AtomicLong(0));
        largeFileUploadResult.setFirstChunkCrc(prepareUploadJson.getCrc());
        largeFileUploadResult.setStatus(new AtomicBoolean(false));
        largeFileUploadResult.setFileId(fileId.toString());
        largeFileUploadResult.setError(false);
        largeFileUploadResult.setOriginalFileSizeInBytes(prepareUploadJson.getSize());
        return largeFileUploadResult;
    }

    /**
     * 写入cookie
     *
     * @param request
     * @param response
     * @param client
     */
    private void writeToCookie(HttpServletRequest request, HttpServletResponse response, UUID client) {
        clearAllCookie(request, response);
        request.getSession().setAttribute(COOKIE_NAME, client);
        Cookie cookie = new Cookie(COOKIE_NAME, client.toString());
        cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(31));
        response.addCookie(cookie);
    }

    /**
     * cookie  中获取clientID
     *
     * @param request
     * @return
     */
    private UUID getClientFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (null != cookies && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    return UUID.fromString(cookie.getValue());
                }
            }
        }
        return null;
    }

    /**
     * 清除所有的数据
     */
    private void clearAllCookie(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute(COOKIE_NAME);
        Cookie[] cookies = request.getCookies();
        if (null != cookies && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }
    }

    /**
     * 组装上传的参数
     *
     * @return
     */
    public FileUploadConfiguration extractFileUploadConfiguration(HttpServletRequest request) throws ParameterMissException, FileUploadException, IOException, UploadFileNotFoundException {
        if (!FileUpload.isMultipartContent(request)) {
            throw new FileUploadException("not Multipart request");
        }
        FileUploadConfiguration fileUploadConfiguration = new FileUploadConfiguration();
        fileUploadConfiguration.setCrc(this.getParamter(request, LargeFileUploadAction.crc, true));
        fileUploadConfiguration.setFileId(UUID.fromString(this.getParamter(request, LargeFileUploadAction.fileId, true)));
        fileUploadConfiguration.setFileEnd(Long.valueOf(this.getParamter(request, LargeFileUploadAction.fileEnd, true)));
        fileUploadConfiguration.setPartNumber(Integer.valueOf(this.getParamter(request, LargeFileUploadAction.partNumber, true)));
        fileUploadConfiguration.setFileOffset(Long.valueOf(this.getParamter(request, LargeFileUploadAction.fileOffset, true)));
        /*提取请求中的文件的信息     解析上传文件流信息*/
        Map<String, MultipartFile> fileMap = ((MultipartHttpServletRequest) request).getFileMap();
        InputStream inputStream = null;
        long inputSteamSize = 1L;
        for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
            inputStream = entry.getValue().getInputStream();
            inputSteamSize = entry.getValue().getSize();
        }
            /*跑出异常*/
        if (inputStream == null) {
            throw new UploadFileNotFoundException("Multipart file bytes not found");
        }
        /*提取文件输出流*/
        fileUploadConfiguration.setInputStream(inputStream);
        fileUploadConfiguration.setInputStreamSize(inputSteamSize);
        return fileUploadConfiguration;
    }

    /**
     * 获取参数
     *
     * @param request               request
     * @param largeFileUploadAction action name
     * @param force                 true 强制存在 false 非强制存在
     * @return
     */
    private String getParamter(HttpServletRequest request, LargeFileUploadAction largeFileUploadAction, Boolean force) throws ParameterMissException {
        Object par = request.getParameter(largeFileUploadAction.name());
        if (par == null || StringUtils.isEmpty(String.valueOf(par))) {
            if (force) {
                throw new ParameterMissException(largeFileUploadAction.name());
            }
        }
        return (String) par;
    }
}
