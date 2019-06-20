package controller;

import model.*;

import java.io.File;

public interface ChatFunctionalities {

    MessageContainer sendMessage(User destination, String message);

    /**
     * Breaks a {@link File} into multiple chunks and sends them to the given {@link User}
     * using an implementation of {@link network.ServerServices#sendFile(User, File)}.
     * @param destination An instance of {@link User}, the user that shall receive the file.
     * @param file A {@link File} to send.
     */
    void sendFile(User destination, File file);
    void disconnect();
    void receiveMessage(MessageContainer message);
    void fileSent(MessageContainer fileMessage);
    void addUser(User user);
    void removeUser(User user);
    void initializeContext(ContextContent context);
    void requestFile(User source, long fileId);
}
