package org.poem.entity.upload;

import java.io.InputStream;
import java.util.UUID;

/**
 * 文件的上传结果信息，信息的组装
 */
public class FileUploadConfiguration {

    /**
     * 文件id
     */
    private UUID fileId;
    /**
     * 文件验证码
     */
    private String crc;
    /**
     * 文件的输入
     */
    private InputStream inputStream;
    /**
     * 文件的输入流大小
     */
    private long inputStreamSize;

    /**
     * 分块的编号
     */
    private int partNumber;
    /**
     * 文件的开始位置
     */
    private long fileOffset;
    /**
     * 文件的结束位置
     */
    private long fileEnd;

    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public long getInputStreamSize() {
        return inputStreamSize;
    }

    public void setInputStreamSize(long inputStreamSize) {
        this.inputStreamSize = inputStreamSize;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public long getFileOffset() {
        return fileOffset;
    }

    public void setFileOffset(long fileOffset) {
        this.fileOffset = fileOffset;
    }

    public long getFileEnd() {
        return fileEnd;
    }

    public void setFileEnd(long fileEnd) {
        this.fileEnd = fileEnd;
    }
}
