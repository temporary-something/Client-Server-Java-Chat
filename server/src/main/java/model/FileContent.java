package model;

public class FileContent implements Content, Comparable<FileContent> {

    private static final long serialVersionUID = 902393113162323186L;

    public static final transient int MAX_BYTE_SIZE = 1024;

    private long fileId;
    private long chunkNumber;
    private byte[] data;

    public static FileContent newInstance(long fileId, long chunkNumber, byte[] data) {
        return new FileContent(fileId, chunkNumber, data);
    }

    private FileContent(long fileId, long chunkNumber, byte[] data) {
        super();
        this.fileId = fileId;
        this.chunkNumber = chunkNumber;
        this.data = data;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public long getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(long chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public int compareTo(FileContent o) {
        return Long.compare(chunkNumber, o.chunkNumber);
    }

    @Override
    public String toString() {
        return "FileContent{" +
                "fileId=" + fileId +
                ", chunkNumber=" + chunkNumber +
                '}';
    }
}
