package org.poem.client;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerServer;
import org.csource.fastdfs.UploadCallback;

import java.io.IOException;

/**
 * override fastdfs method
 */
public class StorageClient extends org.csource.fastdfs.StorageClient {
    public StorageClient() {
        super();
    }

    public StorageClient(TrackerServer trackerServer, StorageServer storageServer) {
        super(trackerServer,storageServer);
    }
    @Override
    public  String[] do_upload_file(byte cmd, String group_name, String master_filename, String prefix_name, String file_ext_name, long file_size, UploadCallback callback, NameValuePair[] meta_list) throws IOException, MyException {
        return  super.do_upload_file((byte)23,group_name,master_filename,prefix_name,file_ext_name,file_size,callback,meta_list);
    }
}
