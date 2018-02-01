package org.poem.entity;

import java.io.Serializable;

/**
 * 文件块信息
 */
public class LargeFileUploadChunkResult implements Serializable{

    /**
     * 文件块开始位置
     */
    private long fileOffset;
    /**
     * 文件块结束位置
     */
    private long fileEnd;
    /**
     * 文件块编号
     */
    private Integer partNumber;
    /**
     * 文件多项式验证码编码
     */
    private String fileCheckNum;
    /**
     * 文件的长度
     */
    private int length = 0 ;

    /**
     * 上传的结果
     */
    private Boolean status = false;
    /**
     * 文件是否发生失败
     */
    private Boolean error = false;

    public LargeFileUploadChunkResult() {
    }

    /**
     * constructor
     * @param fileOffset begin of the this file chunk
     * @param fileEnd end of the this file chunk
     * @param partNumber the part number
     * @param fileCheckNum the check number
     * @param length length
     * @param status upload status
     */

    public LargeFileUploadChunkResult(long fileOffset, long fileEnd, Integer partNumber, String fileCheckNum, int length, Boolean status) {
        this.fileOffset = fileOffset;
        this.fileEnd = fileEnd;
        this.partNumber = partNumber;
        this.fileCheckNum = fileCheckNum;
        this.length = length;
        this.status = status;
        this.error = false;
    }
    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public Integer getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(Integer partNumber) {
        this.partNumber = partNumber;
    }

    public String getFileCheckNum() {
        return fileCheckNum;
    }

    public void setFileCheckNum(String fileCheckNum) {
        this.fileCheckNum = fileCheckNum;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }


    @Override
    public String toString() {
        return "文件块信息:" +
                "文件块开始位置：" + fileOffset +
                ", 文件块结束位置：" + fileEnd +
                ", 文件块编号：" + partNumber +
                ", 文件块编码：" + fileCheckNum + '\'' +
                ", 文件长度：" + length +
                ", 上传状态：" + (status?"已完成":"没有完成") +
                ", 是否发生错误：" + (error?"发生错误":"没有发生错误") +
                '}';
    }
}
