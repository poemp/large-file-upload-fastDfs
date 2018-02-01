package org.poem.entity.upload;

import java.io.Serializable;

/**
 * 新的文件信息
 */
public class PrepareUploadJson implements Serializable {

    /**
     * 文件的临时编号
     * 从客户端传过来
     */
    private Integer tempId;

    /**
     * 文件原始名称
     */
    private String fileName;

    /**
     * 文件的id
     */
    private Long size;

    /**
     * 文件验证码
     */
    private String crc;

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public Integer getTempId() {
        return tempId;
    }

    public void setTempId(Integer tempId) {
        this.tempId = tempId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
