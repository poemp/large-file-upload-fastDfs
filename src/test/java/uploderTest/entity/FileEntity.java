package uploderTest.entity;

import java.util.Arrays;

public class FileEntity {

    private long fileOffset;
    private long fileEnd;
    private Integer partNumber;
    private byte[] fileChunk;
    private String fileCheckNum;
    private int length ;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileEntity that = (FileEntity) o;

        if (fileOffset != that.fileOffset) return false;
        if (fileEnd != that.fileEnd) return false;
        if (!partNumber.equals(that.partNumber)) return false;
        if (!Arrays.equals(fileChunk, that.fileChunk)) return false;
        return fileCheckNum.equals(that.fileCheckNum);
    }

    @Override
    public int hashCode() {
        int result = (int) (fileOffset ^ (fileOffset >>> 32));
        result = 31 * result + (int) (fileEnd ^ (fileEnd >>> 32));
        result = 31 * result + partNumber.hashCode();
        result = 31 * result + Arrays.hashCode(fileChunk);
        result = 31 * result + fileCheckNum.hashCode();
        return result;
    }

    public String getFileCheckNum() {
        return fileCheckNum;
    }

    public void setFileCheckNum(String fileCheckNum) {
        this.fileCheckNum = fileCheckNum;
    }

    public long getFileOffset() {
        return fileOffset;
    }

    public void setFileOffset(long fileOffset) {
        this.fileOffset = fileOffset;
    }

    public long getFileEnd() {
        return fileEnd;
    }

    public void setFileEnd(long fileEnd) {
        this.fileEnd = fileEnd;
    }

    public Integer getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(Integer partNumber) {
        this.partNumber = partNumber;
    }

    public byte[] getFileChunk() {
        return fileChunk;
    }

    public void setFileChunk(byte[] fileChunk) {
        this.fileChunk = fileChunk;
    }
}
