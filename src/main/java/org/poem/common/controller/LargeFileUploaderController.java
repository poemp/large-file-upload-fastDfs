package org.poem.common.controller;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.poem.common.exceptpion.ParameterMissException;
import org.poem.common.helper.LargeFileUploaderHelper;
import org.poem.common.helper.SchoolThreadLocalContainer;
import org.poem.entity.upload.PrepareUploadJson;
import org.poem.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/largeUploader")
public class LargeFileUploaderController {

    /**
     * 默认文件大小
     */
    public static final int default_size = 10 * 1024 * 1024;

    /**
     * 上传优化效果
     */
    @Autowired
    private LargeFileUploaderHelper largeFileUploaderHelper;

    @Autowired
    SchoolThreadLocalContainer container;

    /**
     * get config
     *
     * @return
     */
    @RequestMapping("/getConfig")
    public Serializable getConfig() throws ParameterMissException {
        return largeFileUploaderHelper.getConfig(false);
    }

    /**
     * 准备上传
     *
     * @param newFiles 新上传的文件的信息
     * @return
     */
    @RequestMapping("/prepareUpload")
    public Serializable prepareUpload(String newFiles) throws ParameterMissException {
        if (StringUtils.isEmpty(newFiles)) {
            largeFileUploaderHelper.writeToResponse(new ParameterMissException("newFiles parameter is null."));
        }
        PrepareUploadJson[] fromJson = new Gson().fromJson(newFiles, PrepareUploadJson[].class);
        Map<String, UUID> map = largeFileUploaderHelper.prepareUpload(fromJson);
        return Maps.newHashMap(Maps.transformValues(map, new Function<UUID, String>() {
            public String apply(UUID input) {
                return input.toString();
            }
        }));
    }
}
