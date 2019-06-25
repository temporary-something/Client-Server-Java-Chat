package model;

public class FileContent extends ChunkContent implements Comparable<FileContent> {

    private static final long serialVersionUID = 902393113162323186L;

    public static final transient int MAX_BYTE_SIZE = 1024;

    private long fileId;

    public static FileContent newInstance(long fileId, long chunkNumber, byte[] data) {
        return new FileContent(fileId, chunkNumber, data);
    }

    private FileContent(long fileId, long chunkNumber, byte[] data) {
        super(chunkNumber, data);
        this.fileId = fileId;

    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }


    @Override
    public int compareTo(FileContent o) {
        int sameFile = Long.compare(fileId, o.fileId);
        if (sameFile != 0) return sameFile;
        return Long.compare(getChunkNumber(), o.getChunkNumber());
    }

    @Override
    public String toString() {
        return "FileContent{" +
                "fileId=" + fileId +
                ", chunkNumber=" + getChunkNumber() +
                '}';
    }
}
