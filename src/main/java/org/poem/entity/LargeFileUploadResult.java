package org.poem.entity;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class LargeFileUploadResult implements Serializable {
    /**
     * is finish upload file
     */
    private AtomicBoolean status = new AtomicBoolean(false);
    /**
     * upload file id
     */
    private String uploadFileId;
    /**
     * FastDfs group name
     */
    private String groupName;

    /**
     * 文件上传的结果
     */
    private AtomicLong originalFileSize;

    /**
     * 上传文件块的信息
     */
    private ConcurrentHashMap<String, LargeFileUploadChunkResult> largeFileUploadChunkResultMap =  new ConcurrentHashMap<>();

    /**
     * 使用现成安全map
     * @return
     */
    public ConcurrentHashMap<String, LargeFileUploadChunkResult> getLargeFileUploadChunkResultMap() {
        return largeFileUploadChunkResultMap;
    }

    public void setLargeFileUploadChunkResultMap(ConcurrentHashMap<String, LargeFileUploadChunkResult> largeFileUploadChunkResultMap) {
        this.largeFileUploadChunkResultMap = largeFileUploadChunkResultMap;
    }

    public LargeFileUploadResult(AtomicBoolean status){
        this.status = status;
    }

    public LargeFileUploadResult(AtomicBoolean status, String uploadFileId) {
        this.status = status;
        this.uploadFileId = uploadFileId;
    }

    /**
     * ;to oss
     *
     * @param status
     * @param uploadFileId
     * @param uploadId
     */
    public LargeFileUploadResult(AtomicBoolean status, String uploadFileId, String uploadId,  String bucketName) {
        this.status = status;
        this.uploadFileId = uploadFileId;
    }

    /**
     * to FastDfs
     *
     * @param status
     * @param uploadFileId
     * @param groupName
     */
    public LargeFileUploadResult(AtomicBoolean status, String uploadFileId, String groupName) {
        this.status = status;
        this.uploadFileId = uploadFileId;
        this.groupName = groupName;
    }

    public LargeFileUploadResult() {
        
    }

    public AtomicLong getOriginalFileSize() {
        return originalFileSize;
    }

    public void setOriginalFileSize(AtomicLong originalFileSize) {
        this.originalFileSize = originalFileSize;
    }


    public AtomicBoolean getStatus() {
        return status;
    }

    public void setStatus(AtomicBoolean status) {
        this.status = status;
    }


    public String getUploadFileId() {
        return uploadFileId;
    }

    public void setUploadFileId(String uploadFileId) {
        this.uploadFileId = uploadFileId;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
