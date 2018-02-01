package org.poem.common.enums;

public enum LargeFileUploadAction {

    /**
     * 文件的ID
     */
    fileId,

    /**
     * 文件源
     */
    crc,

    /**
     * 新的文件
     */
    newFiles,

    /**
     * 客户端的ID
     */
    clientId,

    /**
     * 分块的快标号
     */
    partNumber,

    /**
     * 开始位置
     */
    fileOffset,

    /**
     * 结束位置
     */
    fileEnd;
}
