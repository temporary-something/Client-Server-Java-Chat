package pojo;

import java.io.Serializable;

public class Request implements Serializable {

    private static long count = 0;

    private long id;
    private RequestType type;
    private String content;
    private User destination;
    private FileContent fileContent;

    public static synchronized Request newInstance(RequestType type, String content, User destination, FileContent fileContent) {
        return new Request(++count, type, content, destination, fileContent);
    }

    private Request(long id, RequestType type, String content, User destination, FileContent fileContent) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.destination = destination;
        this.fileContent = fileContent;
    }

    public long getId() {
        return id;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getDestination() {
        return destination;
    }

    public void setDestination(User destination) {
        this.destination = destination;
    }

    public FileContent getFileContent() {
        return fileContent;
    }

    public void setFileContent(FileContent fileContent) {
        this.fileContent = fileContent;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", destination=" + destination +
                ", fileContentSize=" + fileContent +
                '}';
    }
}
