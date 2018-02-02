package org.poem.common.uploader;

public interface WriteChunkCompletionListener {

    public void start();

    public void error(Exception e);

    public void success();
}
