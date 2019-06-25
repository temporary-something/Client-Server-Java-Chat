package model.model;

import model.FileBasicInformation;

public class FileDescriptor extends FileBasicInformation {

    private static final long serialVersionUID = -7809127402086776713L;

    private long chunksTotalNumber;
    private String fileName;

    public static model.FileDescriptor newInstance(long chunksTotalNumber, String fileName) {
        return new model.FileDescriptor(chunksTotalNumber, fileName);
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
