package model.model;

import model.Content;

public class FileBasicInformation implements Content {

    private static final long serialVersionUID = 4631869226054872647L;

    private static long count = 0;

    private final long fileId;

    public static model.FileBasicInformation newInstance(final long fileId) {
        return new model.FileBasicInformation(fileId);
    }

    private FileBasicInformation(long fileId) {
        this.fileId = fileId;
    }

    FileBasicInformation() {
        super();
        this.fileId = model.FileBasicInformation.count++;
    }

    public long getFileId() {
        return fileId;
    }

    @Override
    public String toString() {
        return "FileBasicInformation{" +
                "fileId=" + fileId +
                '}';
    }
}
