package uploderTest.utils;

import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uploderTest.entity.FileEntity;

import java.io.*;
import java.util.Set;

/**
 * 分割文件
 */
public class SplitFileUtils {

    private static final Logger logger = LoggerFactory.getLogger(SplitFileUtils.class);


    /**
     * 文件的路径
     * @param src
     * @return
     * @throws IOException
     */
    public static Set<FileEntity> splitFile(String src, int inByte)  throws Exception{
        Set<FileEntity> fileEntities = Sets.newHashSet();
        FileEntity fileEntity;
        File srcFile = new File(src);

        long srcSize = srcFile.length();
        int number = (int) (srcSize / inByte);

        number = srcSize % inByte == 0 ? number : number + 1;

        InputStream in = null;//输入字节流
        BufferedInputStream bis = null;//输入缓冲流
        int len;//每次读取的长度值
        try {
            in = new FileInputStream(srcFile);
            bis = new BufferedInputStream(in);

            long sum = 0;
            for (int i = 0; i < number; i++) {
                int count = 0;
                byte[] bytes = new byte[inByte];
                while ((len = bis.read(bytes)) != -1) {
                    fileEntity = new FileEntity();
                    fileEntity.setFileChunk(bytes);
                    fileEntity.setPartNumber(i);
                    fileEntity.setFileOffset(sum);
                    fileEntity.setFileCheckNum(FileCheckNumUtils.doChecksum(new ByteArrayInputStream(bytes)));
                    count += len;
                    sum += len;
                    fileEntity.setLength(len);
                    fileEntity.setFileEnd(sum);
                    fileEntities.add(fileEntity);
                    if (count >= inByte) {
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(bis);
        }
        return  fileEntities;
    }
}
