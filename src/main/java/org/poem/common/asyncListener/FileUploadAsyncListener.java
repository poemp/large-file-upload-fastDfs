package org.poem.common.asyncListener;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import java.io.IOException;
import java.util.UUID;

public abstract class FileUploadAsyncListener implements AsyncListener {

    private UUID fileId ;

    protected abstract void clean(ServletRequest request);


    public FileUploadAsyncListener(UUID fileId){
        this.fileId = fileId;
    }

    @Override
    public void onComplete(AsyncEvent asyncEvent) throws IOException {
        clean(asyncEvent.getSuppliedRequest());
    }

    @Override
    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
        clean(asyncEvent.getSuppliedRequest());
    }

    @Override
    public void onError(AsyncEvent asyncEvent) throws IOException {
        clean(asyncEvent.getSuppliedRequest());
    }

    @Override
    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {

    }
}
