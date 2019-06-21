package view.impl;

import com.google.inject.Inject;
import controller.ChatFunctionalities;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.ContextContent;
import model.FileMessageContent;
import model.MessageContainer;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.ChatView;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ChatViewImpl implements ChatView {

    private static final Logger logger = LogManager.getLogger(ChatViewImpl.class);

    @FXML private Label lblConnected;
    @FXML private ListView<User> listUsers;
    @FXML private ListView<MessageContainer> listMessages;
    @FXML private TextArea txtMessage;

    @Inject private ChatFunctionalities chatController;

    private Stage stage;
    private FileChooser fileChooser;

    private User selectedUser;
    private Map<Long, ObservableList<MessageContainer>> conversations = new HashMap<>();


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        if (this.listUsers.getItems() == null) this.listUsers.setItems(FXCollections.observableArrayList());
        this.listMessages.setItems(FXCollections.observableArrayList());

        this.listUsers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedUser = newValue;
            if (newValue == null) return;
            if (!conversations.containsKey(newValue.getId())) conversations.put(newValue.getId(), FXCollections.observableArrayList());
            listMessages.setItems(conversations.get(newValue.getId()));
        });

        this.listMessages.setCellFactory(p -> new ChatListCell());
        this.listMessages.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.getMessageContent() instanceof FileMessageContent) {
                //Send Request to the server to start downloading the file.
                final Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() {
                        chatController.requestFile(selectedUser, ((FileMessageContent)newValue.getMessageContent()).getFileId());
                        return null;
                    }
                };
                task.setOnSucceeded(event -> logger.info("Sending Message Succeeded ..."));
                task.setOnFailed(event -> logger.info("Sending Message Failed ..."));

                Thread t = new Thread(task);
                t.setDaemon(true);
                t.start();
            }
        });

        fileChooser = new FileChooser();
    }

    @Override
    public void sendMessage(final ActionEvent event) {
        if (selectedUser == null) {
            logger.info("No user selected ...");
            return;
        }
        if (txtMessage.getText().trim().equals("")) {
            logger.info("Empty Message ...");
            return;
        }

        final Task<MessageContainer> task = new Task<MessageContainer>() {
            @Override
            protected MessageContainer call() {
                return chatController.sendMessage(selectedUser, txtMessage.getText());
            }
        };
        task.setOnSucceeded(event1 -> {
            txtMessage.clear();
            listMessages.getItems().add(task.getValue());
        });
        task.setOnFailed(event12 -> logger.info("Sending Message Failed ..."));

        this.startTask(task);
    }

    @Override
    public void uploadFile(final ActionEvent event) {
        //Prompt the user to select a File.
        final File file = fileChooser.showOpenDialog(stage);

        //If the user selected a File (file is not null) then send it to the selected user.
        if (file != null) {
            final Thread t = new Thread(() -> chatController.sendFile(selectedUser, file));
            t.start();
        }
    }

    @Override
    public void disconnect(final ActionEvent event) {
        //Call the disconnect method of the controller.
        Thread t = new Thread(chatController::disconnect);
        t.start();
        //Leave the application.
        Platform.exit();
    }

    @Override
    public void receiveMessage(final MessageContainer message) {
        final Task<MessageContainer> task = new Task<MessageContainer>() {
            @Override
            protected MessageContainer call() {
                return message;
            }
        };
        task.setOnSucceeded(event -> {
            final User user = task.getValue().getUser();
            //Adds the message to the conversation with the sender if he is connected.
            if (conversations.containsKey(user.getId())) conversations.get(user.getId()).add(task.getValue());
        });
        task.setOnFailed(event -> logger.info("Updating UI for Message Received Failed ..."));

        this.startTask(task);
    }

    @Override
    public void initializeContext(final ContextContent context) {
        System.err.println("Setting Users ...");
        final Task<ContextContent> task = new Task<ContextContent>() {
            @Override
            protected ContextContent call() {
                return context;
            }
        };

        task.setOnSucceeded(event -> {
            if (listUsers.getItems() == null) listUsers.setItems(FXCollections.observableArrayList());
            final List<User> users = listUsers.getItems();

            //Add the users if they don't already exist.
            for (User u : context.getUsers()) {
                if (!users.contains(u)) users.add(u);
            }

            //Update the number of connected users.
            lblConnected.setText(""+listUsers.getItems().size());

            //Select the first user (if there is) as the default chat.
            if (selectedUser == null && users.size() > 0) {
                selectedUser = users.get(0);
                listUsers.getSelectionModel().select(selectedUser);
                if (!conversations.containsKey(selectedUser.getId()))
                    conversations.put(selectedUser.getId(), FXCollections.observableArrayList());
                listMessages.setItems(conversations.get(selectedUser.getId()));
            }
        });
        task.setOnFailed(event -> logger.info("Setting Users Failed ..."));

        this.startTask(task);
    }

    @Override
    public void addUser(final User user) {
        final Task<User> task = new Task<User>() {
            @Override
            protected User call() {
                return user;
            }
        };
        task.setOnSucceeded(event -> {
            //Add the user if he doesn't already exist.
            if (!listUsers.getItems().contains(task.getValue())) {
                listUsers.getItems().add(task.getValue());

                //Update the number of connected users.
                lblConnected.setText(""+listUsers.getItems().size());

                //If it is the only connected user, select him for a conversation.
                if (listUsers.getItems().size() == 1) {
                    selectedUser = task.getValue();
                    listUsers.getSelectionModel().select(selectedUser);
                    if (!conversations.containsKey( task.getValue().getId()))
                        conversations.put(task.getValue().getId(), FXCollections.observableArrayList());
                    listMessages.setItems(conversations.get(task.getValue().getId()));
                }
            }
        });
        task.setOnFailed(event -> logger.info("Adding User Failed ..."));

        this.startTask(task);
    }

    @Override
    public void removeUser(final User user) {
        final Task<User> task = new Task<User>() {
            @Override
            protected User call() {
                return user;
            }
        };
        task.setOnSucceeded(event -> {
            //If the user to delete was the selected user, deselect him and leave the conversation.
            if (selectedUser != null && selectedUser.equals(task.getValue())) {
                selectedUser = null;
                listMessages.setItems(FXCollections.observableArrayList());
            }

            //Remove the user for the list.
            listUsers.getItems().remove(task.getValue());

            //Update the number of connected users.
            lblConnected.setText(""+listUsers.getItems().size());
        });
        task.setOnFailed(event -> logger.info("Removing User Failed ..."));

        this.startTask(task);
    }

    @Override
    public void fileSent(final MessageContainer fileMessage) {
        fileMessage.setUser(null);
        this.receiveMessage(fileMessage);
    }

    private void startTask(final Task task) {
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
}
