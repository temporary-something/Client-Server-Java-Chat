package model;

public class FileMessageContent extends MessageContent {

    private long fileId;

    private FileMessageContent(final String message, final long fileId) {
        super(message);
        this.fileId = fileId;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    @Override
    public String toString() {
        return "FileMessageContent{" +
                "fileId=" + fileId +
                '}';
    }
}
