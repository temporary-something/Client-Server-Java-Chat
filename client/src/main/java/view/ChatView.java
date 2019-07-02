package view;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import model.ContextContent;
import model.MessageContainer;
import model.User;

public interface ChatView extends Initializable {

    void disconnect(ActionEvent event);
    void initializeContext(ContextContent context);

    void sendMessage(ActionEvent event);
    void receiveMessage(MessageContainer message);

    void addUser(User user);
    void removeUser(User user);

    void uploadFile(ActionEvent event);
    void fileSent(MessageContainer fileMessage);
    void requestFile(User source, long fileId);

    void startRecording(ActionEvent event);
    void stopRecording(ActionEvent event);
    void audioSent(MessageContainer audioMessage);
    void requestAudio(User source, long audioId);

    void requestControl(ActionEvent event);
}
