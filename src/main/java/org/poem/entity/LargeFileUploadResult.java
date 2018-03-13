package org.poem.entity;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 文件整体信息
 */
public class LargeFileUploadResult implements Serializable {

    /**
     * 原始文件名.
     */
    private String originalFileName;

    /**
     * 原始文件的大小
     */
    private Long originalFileSizeInBytes;

    /**
     * Amount of bytes that were correctly validated.<br>
     * When resuming an upload, all bytes in the file that have not been validated are revalidated.
     * 上传了的文件中验证了的大小
     */
    private AtomicLong crcedBytes;

    /**  */
    private String firstChunkCrc;

    /**
     * is finish upload file
     */
    private AtomicBoolean status = new AtomicBoolean(false);

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 是否完成
     */
    private Boolean fileComplete = false;

    /**
     * 是否发生错误
     */
    private Boolean error = false;
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
    private AtomicLong originalFileSize = new AtomicLong(0);

    /**
     * 上传文件块的信息
     */
    private ConcurrentHashMap<String, LargeFileUploadChunkResult> largeFileUploadChunkResultMap = new ConcurrentHashMap<>();

    /**
     * 使用现成安全map
     *
     * @return
     */
    public ConcurrentHashMap<String, LargeFileUploadChunkResult> getLargeFileUploadChunkResultMap() {
        return largeFileUploadChunkResultMap;
    }

    public void setLargeFileUploadChunkResultMap(ConcurrentHashMap<String, LargeFileUploadChunkResult> largeFileUploadChunkResultMap) {
        this.largeFileUploadChunkResultMap = largeFileUploadChunkResultMap;
    }

    public LargeFileUploadResult(AtomicBoolean status) {
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
    public LargeFileUploadResult(AtomicBoolean status, String uploadFileId, String uploadId, String bucketName) {
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


    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Long getOriginalFileSizeInBytes() {
        return originalFileSizeInBytes;
    }

    public void setOriginalFileSizeInBytes(Long originalFileSizeInBytes) {
        this.originalFileSizeInBytes = originalFileSizeInBytes;
    }
    public AtomicLong getCrcedBytes() {
        return crcedBytes;
    }

    public void setCrcedBytes(AtomicLong crcedBytes) {
        this.crcedBytes = crcedBytes;
    }

    public String getFirstChunkCrc() {
        return firstChunkCrc;
    }

    public void setFirstChunkCrc(String firstChunkCrc) {
        this.firstChunkCrc = firstChunkCrc;
    }


    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Boolean getFileComplete() {
        return fileComplete;
    }

    public void setFileComplete(Boolean fileComplete) {
        this.fileComplete = fileComplete;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
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
