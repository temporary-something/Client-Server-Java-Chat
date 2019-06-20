package pojo;

import java.io.Serializable;

public class MessageContent implements Serializable {

    private User user;
    private String message;
    private long fileId;

    public MessageContent() {
    }

    public MessageContent(User user, String message) {
        this.user = user;
        this.message = message;
        this.fileId = -1;
    }

    public MessageContent(User user, String message, long fileId) {
        this.user = user;
        this.message = message;
        this.fileId = fileId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    @Override
    public String toString() {
        return "MessageContent{" +
                "user=" + user +
                ", message='" + message + '\'' +
                ", fileId=" + fileId +
                '}';
    }
}
