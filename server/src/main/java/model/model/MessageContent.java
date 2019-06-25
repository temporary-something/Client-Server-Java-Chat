package model.model;

import model.Content;

import java.util.Calendar;
import java.util.Date;

public class MessageContent implements Content {

    private String message;
    private Date creationDateTime;

    public static model.MessageContent newInstance(String message) {
        final model.MessageContent content = new model.MessageContent(message);
        content.creationDateTime = Calendar.getInstance().getTime();
        return content;
    }

    MessageContent(String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreationDateTime() {
        return creationDateTime;
    }

    @Override
    public String toString() {
        return "MessageContent{" +
                "message='" + message + '\'' +
                ", creationDateTime=" + creationDateTime +
                '}';
    }
}
