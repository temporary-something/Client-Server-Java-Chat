package model;

public class FileDescriptor extends FileBasicInformation {

    private long chunksTotalNumber;
    private String fileName;

    public static FileDescriptor newInstance(long chunksTotalNumber, String fileName) {
        return new FileDescriptor(chunksTotalNumber, fileName);
    }

    private FileDescriptor(long chunksTotalNumber, String fileName) {
        super();
        this.chunksTotalNumber = chunksTotalNumber;
        this.fileName = fileName;
    }

    public long getChunksTotalNumber() {
        return chunksTotalNumber;
    }

    public void setChunksTotalNumber(long chunksTotalNumber) {
        this.chunksTotalNumber = chunksTotalNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "FileDescriptor{" +
                "fileId=" + getFileId() +
                ", chunksTotalNumber=" + chunksTotalNumber +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
