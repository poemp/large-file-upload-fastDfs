package org.poem.entity;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 返回信息
 */
public class InitializationConfiguration implements Serializable {

    private long  inByte;

    private ConcurrentHashMap<String,LargeFileUploadResult> largeFileUploadResultMap = new ConcurrentHashMap<>() ;

    public long getInByte() {
        return inByte;
    }

    public void setInByte(long inByte) {
        this.inByte = inByte;
    }

    public ConcurrentHashMap<String, LargeFileUploadResult> getLargeFileUploadResultMap() {
        return largeFileUploadResultMap;
    }

    public void setLargeFileUploadResultMap(ConcurrentHashMap<String, LargeFileUploadResult> largeFileUploadResultMap) {
        this.largeFileUploadResultMap = largeFileUploadResultMap;
    }
}
