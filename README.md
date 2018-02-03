# large-file-upload-fastDfs

实现大文件的断点续传，使用Redis做数据信息的缓存，不提供数据的删除。

代码更新下来，启动。
### 1、 创建并且指定ClientId，(可以指定或者不指定)

````
   CookieStore cookieStore = new BasicCookieStore();
   BasicClientCookie basicClientCookie = new BasicClientCookie("clientId",clinetId);
   basicClientCookie.setDomain(url);
   cookieStore.addCookie(basicClientCookie);
````

### 2、 获取文件的分块大小信息，之前上传文件的信息

````
    http://127.0.0.1:8082/fs/largeUploader/getConfig
````

返回的格式是（刚开始上传返回请求该接口返回的数据）：

``````
{
    "inByte":10485760,
    "largeFileUploadResultMap":{}
    }
}
``````

### 3、准备上传的信息，后端会生成相应的数据

````
    http://127.0.0.1:8082/fs/largeUploader/prepareUpload
````
* 需要组装响应的元数据

    >tempId 文件编号
    
    >fileName 文件名
    
    >size 文件的大小
    
    >crc 文件验证码
    
 * 组装成Json 
 
 ``````
 {
     "tempId":{
         "tempId":0,
         "fileName":"text.file",
         "size":123,
         "crc":""
     }
 }
 ``````
 * 返回数据格式
 
 ``````
{
    "0":"f75c64b3-200e-4c51-8ec1-4a483fd7819b"
}
 ``````
 
 ### 4、开始上传(post)
 ````
     http://127.0.0.1:8082/fs/fs/largeUploader/asyncFileUploader
 ````
 
 * 需要组装响应的元数据
 
     >fileId 文件id
     
     >crc 文件验证
     
     
 * 指定文件块的开始位置和结束位置
    
       当前值是放在请求的 header 之中
       
       eg: request.setHeader("Origin-Rang", fileEntity.getFileOffset() + "-" + fileEntity.getFileEnd() + "-" + fileEntity.getPartNumber());
       
  
  ### 5、获取文件的分块大小信息，之前上传文件的信息
  
  ````
      http://127.0.0.1:8082/fs/largeUploader/getConfig
  ````
  
  返回的格式是（刚开始上传返回请求该接口返回的数据）：
  
  ``````
{
    "inByte":10485760,
    "largeFileUploadResultMap":{
        "f75c64b3-200e-4c51-8ec1-4a483fd7819b":{
            "originalFileName":"R0011368.docx",
            "originalFileSizeInBytes":25555,
            "crcedBytes":10485760,
            "firstChunkCrc":"230546538",
            "status":true,
            "fileId":"f75c64b3-200e-4c51-8ec1-4a483fd7819b",
            "fileComplete":true,
            "error":false,
            "uploadFileId":"M00/08/68/CgYWNVp1aXOEYfNuAAAAAOoEwUg61.docx",
            "groupName":"group1",
            "originalFileSize":0,
            "largeFileUploadChunkResultMap":{
                "da47b1ab@0":{
                    "fileOffset":0,
                    "fileEnd":25555,
                    "partNumber":0,
                    "fileCheckNum":"da47b1ab",
                    "length":25555,
                    "status":true,
                    "error":false
                }
            }
        }
    }
}
  ``````
  
 在获取元数据的时候，如果文件分块没有上传完成或是报错，可以重新上传。
 
 ##### all
 >有问题请联系我xue_2013@sina.com 
 
 >期待相互交流
