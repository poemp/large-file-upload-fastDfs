package uploderTest.utils;

import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class FileCheckNumUtils {

    /**
     * @param fileinputstream file input stream
     * @return
     * @throws Exception
     */
    public static String doChecksum(InputStream fileinputstream) throws Exception {
        CRC32 crc32 = new CRC32();
        for (CheckedInputStream checkedinputstream = new CheckedInputStream(fileinputstream, crc32);
             checkedinputstream.read() != -1;
                ) {
        }
        String fileCheckNum = Long.toHexString(crc32.getValue());
        return fileCheckNum;
    }
}
