package view;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import model.ContextContent;
import model.MessageContainer;
import model.User;

public interface ChatView extends Initializable {

    void sendMessage(ActionEvent event);
    void uploadFile(ActionEvent event);
    void disconnect(ActionEvent event);
    void receiveMessage(MessageContainer message);
    void initializeContext(ContextContent context);
    void addUser(User user);
    void removeUser(User user);
    void fileSent(MessageContainer fileMessage);
    void requestFile(User user, long fileId);
}
