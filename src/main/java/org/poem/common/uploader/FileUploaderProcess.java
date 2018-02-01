package org.poem.common.uploader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 上传的进程管理
 */
public class FileUploaderProcess {

    /**
     * 执行的线程
     */
    private static final ExecutorService executorService = Executors.newFixedThreadPool(16);


}
