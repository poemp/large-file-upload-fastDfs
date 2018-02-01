package org.poem.common.controller;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.poem.common.helper.LargeFileUploaderHelper;
import org.poem.entity.upload.PrepareUploadJson;
import org.poem.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

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
}
