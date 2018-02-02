package org.poem.common.exceptpion;

/**
 * 文件没有得到的错误
 */
public class UploadFileInfoNotFoundException extends Exception {
    public UploadFileInfoNotFoundException() {
        super();
    }

    public UploadFileInfoNotFoundException(String message) {
        super(message);
    }

    public UploadFileInfoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
