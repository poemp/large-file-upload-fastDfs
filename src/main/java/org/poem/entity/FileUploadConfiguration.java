package org.poem.entity;

import java.io.InputStream;
import java.util.UUID;

public class FileUploadConfiguration {
    private UUID fileId;
    private String crc;
    private InputStream inputStream;
    private long inputStreamSize;
    private int partNumber;//分块的编号
    private long fileOffset;//文件的开始位置
    private long fileEnd; //文件的结束位置

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
