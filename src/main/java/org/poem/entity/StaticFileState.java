package org.poem.entity;


import java.io.Serializable;


/**
 * Server-side entity representing a file.<br>
 * It contains the shared file information ({@link FileStateJsonBase}) and the url of the file.
 *
 * @author antoinem
 * 文件的状态
 */
public class StaticFileState implements Serializable {
    /**
     * 上传到的文件的相对路径
     */
    private FileStateJsonBase staticFileStateJson;
    /**
     * 文件上传信息
     */
    private LargeFileUploadResult largeFileUploadResult;

    public LargeFileUploadResult getLargeFileUploadResult() {
        return largeFileUploadResult;
    }

    public void setLargeFileUploadResult(LargeFileUploadResult largeFileUploadResult) {
        this.largeFileUploadResult = largeFileUploadResult;
    }

    /**
     * Default constructor.
     */
    public StaticFileState() {
        super();
    }

    public FileStateJsonBase getStaticFileStateJson() {
        return staticFileStateJson;
    }


    public void setStaticFileStateJson(FileStateJsonBase staticFileStateJson) {
        this.staticFileStateJson = staticFileStateJson;
    }
}
