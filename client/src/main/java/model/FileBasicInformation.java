package model;

public class FileBasicInformation implements Content {

    private static final long serialVersionUID = 4631869226054872647L;

    private static long count = 0;

    private final long fileId;

    public static FileBasicInformation newInstance(final long fileId) {
        return new FileBasicInformation(fileId);
    }

    private FileBasicInformation(long fileId) {
        this.fileId = fileId;
    }

    FileBasicInformation() {
        super();
        this.fileId = FileBasicInformation.count++;
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
