package org.poem.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/largeUploader")
public class LargeFileUploaderController {

    private static final Logger logger = LoggerFactory.getLogger(LargeFileUploaderController.class);

    @Autowired

    /**
     * get config
     * @return
     */
    public String getConfig(){
        return  null;
    };
}
