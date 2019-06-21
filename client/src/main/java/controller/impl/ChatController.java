package controller.impl;

import com.google.inject.Inject;
import controller.ChatFunctionalities;
import model.*;
import network.ServerServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.ChatView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatController implements ChatFunctionalities {

    private static final Logger logger = LogManager.getLogger(ChatController.class);

    @Inject private ChatView chatView;
    @Inject private ServerServices serverServices;

    private Map<Long, String> toSaveFilePaths = new HashMap<>();

    @Override
    public MessageContainer sendMessage(final User destination, final String message) {
        final MessageContainer messageContainer = MessageContainer.newInstance(destination, MessageContent.newInstance(message));
        try {
            serverServices.sendMessage(messageContainer);
            return messageContainer;
        } catch (IOException e) {
            logger.error(e);
            return null;
        }
    }

    @Override
    public void sendFile(final User destination, final File file) {
        try {
            serverServices.checkSendFile(destination, file);
        } catch (IOException e) {
            logger.error(e);
        }
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
    public void requestFile(User source, long fileId, String absolutePath) {
        try {
            toSaveFilePaths.put(fileId, absolutePath);
            serverServices.requestFile(source, fileId);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public void receiveFile(FileDescriptor fileDescriptor, List<FileContent> list) {
        logger.info("File Received : " + fileDescriptor);
        Collections.sort(list);
        try {
            final File file = new File(toSaveFilePaths.get(fileDescriptor.getFileId()));
            final FileOutputStream os = new FileOutputStream(file);
            for (FileContent fc: list) {
                os.write(fc.getData());
            }
            os.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
