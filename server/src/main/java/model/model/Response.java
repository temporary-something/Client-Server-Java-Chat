package model.model;

import model.BasePojo;
import model.Content;
import model.User;
import model.enums.ResponseType;

public class Response extends BasePojo {

    private static final long serialVersionUID = -5799093094096485671L;

    private final ResponseType type;
    private Content content;
    private final User source;

    public static model.Response newInstance(ResponseType type, Content content, User source) {
        return new model.Response(type, content, source);
    }

    private Response(ResponseType type, Content content, User source) {
        this.type = type;
        this.content = content;
        this.source = source;
    }

    public ResponseType getType() {
        return type;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public User getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "Response{" +
                "type=" + type +
                ", content=" + content +
                ", source=" + source +
                '}';
    }
}
