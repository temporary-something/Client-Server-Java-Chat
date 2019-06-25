package model.model;

import model.FileDescriptor;
import model.MessageContent;

public class FileMessageContent extends MessageContent {

    private static final long serialVersionUID = 7716707288792569434L;

    private long fileId;

    public static model.FileMessageContent newInstance(FileDescriptor fileDescriptor) {
        return new model.FileMessageContent(fileDescriptor.getFileName(), fileDescriptor.getFileId());
    }

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
