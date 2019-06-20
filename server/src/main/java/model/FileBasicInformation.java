package model;

public class FileBasicInformation implements Content {

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
}
