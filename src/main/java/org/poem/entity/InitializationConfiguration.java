package org.poem.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * 返回信息
 */
public class InitializationConfiguration implements Serializable {

    private long  inByte = 10 * 1024 * 1024;

    private Map<String,LargeFileUploadResult> largeFileUploadResultMap ;

    public long getInByte() {
        return inByte;
    }

    public void setInByte(long inByte) {
        this.inByte = inByte;
    }

    public Map<String, LargeFileUploadResult> getLargeFileUploadResultMap() {
        return largeFileUploadResultMap;
    }

    public void setLargeFileUploadResultMap(Map<String, LargeFileUploadResult> largeFileUploadResultMap) {
        this.largeFileUploadResultMap = largeFileUploadResultMap;
    }
}
