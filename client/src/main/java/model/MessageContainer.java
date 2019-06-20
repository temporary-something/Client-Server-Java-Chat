package model;

public class MessageContainer extends BasePojo {

    private static final long serialVersionUID = 135433767732892798L;

    private User user;
    private final MessageContent messageContent;

    public static MessageContainer newInstance(User user, MessageContent messageContent) {
        return new MessageContainer(user, messageContent);
    }

    private MessageContainer(User user, MessageContent messageContent) {
        this.user = user;
        this.messageContent = messageContent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MessageContent getMessageContent() {
        return messageContent;
    }

    @Override
    public String toString() {
        return "MessageContainer{" +
                "user=" + user +
                ", messageContent=" + messageContent +
                '}';
    }
}
