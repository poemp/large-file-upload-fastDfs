package org.poem.common.exceptpion;

public class UploadFileNotFoundException extends Exception {

    public UploadFileNotFoundException() {
        super();
    }

    public UploadFileNotFoundException(String message) {
        super(message);
    }

    public UploadFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
