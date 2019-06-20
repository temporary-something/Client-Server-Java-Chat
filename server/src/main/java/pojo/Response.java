package pojo;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {

    private static long count = 0;

    private long id;
    private ResponseType type;
    private List<User> users;
    private MessageContent content;
    private FileContent fileContent;

    public static synchronized Response newInstance(ResponseType type, List<User> users, MessageContent content, FileContent fileContent) {
        return new Response(++count, type, users, content, fileContent);
    }

    private Response(long id, ResponseType type, List<User> users, MessageContent content, FileContent fileContent) {
        this.id = id;
        this.type = type;
        this.users = users;
        this.content = content;
        this.fileContent = fileContent;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public MessageContent getContent() {
        return content;
    }

    public void setContent(MessageContent content) {
        this.content = content;
    }

    public FileContent getFileContent() {
        return fileContent;
    }

    public void setFileContent(FileContent fileContent) {
        this.fileContent = fileContent;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", type=" + type +
                ", users=" + users +
                ", content=" + content +
                ", fileContent=" + fileContent +
                '}';
    }
}
