package org.poem.common.controller;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.fileupload.FileUploadException;
import org.poem.common.asyncListener.FileUploadAsyncListener;
import org.poem.common.exceptpion.ParameterMissException;
import org.poem.common.helper.LargeFileUploaderHelper;
import org.poem.entity.upload.FileUploadConfiguration;
import org.poem.entity.upload.PrepareUploadJson;
import org.poem.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/largeUploader")
public class LargeFileUploaderController {

    private static final Logger logger = LoggerFactory.getLogger(LargeFileUploaderController.class);

    @Autowired
    private LargeFileUploaderHelper largeFileUploaderHelper;
    /**
     * get config
     * @return
     */
    @RequestMapping("/getConfig")
    public Serializable getConfig(){
        return  largeFileUploaderHelper.getConfig();
    }

    /**
     * 准备上传
     * @param newFiles 新上传的文件的信息
     * @return
     */
    @RequestMapping("/prepareUpload")
    public Serializable prepareUpload(String newFiles){
        if(StringUtils.isNotEmpty(newFiles)){
            largeFileUploaderHelper.writeToResponse("文件为空");
        }
        PrepareUploadJson[] fromJson = new Gson().fromJson(newFiles, PrepareUploadJson[].class);
        Map<String,UUID> map = largeFileUploaderHelper.prepareUpload(fromJson);
        return Maps.newHashMap(Maps.transformValues(map, new Function<UUID, String>() {
            public String apply(UUID input) {
                return input.toString();
            }
        }));
    }

    /**
     * 异步上传
     * @param request
     * @param response
     */
    @RequestMapping("/asyncFileUploader")
    public void asyncFileUploader(final HttpServletRequest request , final HttpServletResponse response){
        try{
            FileUploadConfiguration fileUploadConfiguration = this.largeFileUploaderHelper.extractFileUploadConfiguration();
            /*开启同步策略*/
            final AsyncContext asyncContext = request.startAsync();
            asyncContext.setTimeout(TimeUnit.MICROSECONDS.toMillis(3000));
            asyncContext.addListener(new FileUploadAsyncListener(fileUploadConfiguration.getFileId()) {
                @Override
                public void clean(){

                }
            });
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParameterMissException e) {
            e.printStackTrace();
        }
    }
}
