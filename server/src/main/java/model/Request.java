package model;

import model.enums.RequestType;

public class Request extends BasePojo {

    private static final long serialVersionUID = -3207514589798729948L;

    private final RequestType type;
    private Content content;
    private final User destination;

    public static Request newInstance(RequestType type, Content content, User destination) {
        return new Request(type, content, destination);
    }

    private Request(RequestType type, Content content, User destination) {
        super();
        this.type = type;
        this.content = content;
        this.destination = destination;
    }

    public RequestType getType() {
        return type;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public User getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type=" + type +
                ", content=" + content +
                ", destination=" + destination +
                '}';
    }
}
