package pojo;

import java.io.Serializable;

public class FileContent implements Serializable {

    private static final long serialVersionUID = -2661875755612260125L;

    public static final transient int MAX_BYTE_SIZE = 1024;
    private static long count = 0;

    private long fileId;
    private String fileName;
    private long fragmentNumber;
    private long fragmentTotalNumber;
    private byte[] data;

    public static long getNewFileId() {
        return ++count;
    }

    public FileContent(long fileId, String fileName, long fragmentNumber, long fragmentTotalNumber, byte[] data) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fragmentNumber = fragmentNumber;
        this.fragmentTotalNumber = fragmentTotalNumber;
        this.data = data;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFragmentNumber() {
        return fragmentNumber;
    }

    public void setFragmentNumber(long fragmentNumber) {
        this.fragmentNumber = fragmentNumber;
    }

    public long getFragmentTotalNumber() {
        return fragmentTotalNumber;
    }

    public void setFragmentTotalNumber(long fragmentTotalNumber) {
        this.fragmentTotalNumber = fragmentTotalNumber;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FileContent{" +
                "fileId=" + fileId +
                ", fileName='" + fileName + '\'' +
                ", fragmentNumber=" + fragmentNumber +
                ", fragmentTotalNumber=" + fragmentTotalNumber +
                ", data=" + ((data == null) ? -1 : data.length)+
                '}';
    }
}
