package controller.impl;

import com.google.inject.Inject;
import controller.ChatFunctionalities;
import model.*;
import network.ServerServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.ChatView;

import java.io.File;
import java.io.IOException;

public class ChatController implements ChatFunctionalities {

    private static final Logger logger = LogManager.getLogger(ChatController.class);

    @Inject private ChatView chatView;
    @Inject private ServerServices serverServices;


    @Override
    public MessageContainer sendMessage(final User destination, final String message) {
        final MessageContainer messageContainer = MessageContainer.newInstance(destination, MessageContent.newInstance(message));
        try {
            serverServices.sendMessage(messageContainer);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return messageContainer;
    }

    @Override
    public void sendFile(final User destination, final File file) {

    }

    @Override
    public void disconnect() {
        serverServices.disconnect();
    }

    @Override
    public void receiveMessage(final MessageContainer message) {
        chatView.receiveMessage(message);
    }

    @Override
    public void fileSent(final MessageContainer fileMessage) {
        chatView.fileSent(fileMessage);
    }

    @Override
    public void addUser(final User user) {
        chatView.addUser(user);
    }

    @Override
    public void removeUser(final User user) {
        chatView.removeUser(user);
    }

    @Override
    public void initializeContext(final ContextContent context) {
        chatView.initializeContext(context);
    }

    @Override
    public void requestFile(User source, long fileId) {
        try {
            serverServices.requestFile(source, fileId);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
