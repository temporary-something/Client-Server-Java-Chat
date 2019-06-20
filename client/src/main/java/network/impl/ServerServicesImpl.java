package network.impl;

import com.google.inject.Inject;
import com.sun.istack.internal.NotNull;
import controller.ChatFunctionalities;
import model.*;
import model.FileDescriptor;
import model.enums.RequestType;
import network.InputStreamListener;
import network.ServerServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class ServerServicesImpl implements ServerServices, InputStreamListener {

    private static final Logger logger = LogManager.getLogger(ServerServicesImpl.class);

    @Inject private ChatFunctionalities chatController;
    @Inject private InputStreamReaderImpl isReader;

    private Socket connection = null;
    private ObjectOutputStream writer = null;

    private Request buildRequest(final RequestType type, Content content, final User destination) {
        return Request.newInstance(type, content, destination);
    }

    private synchronized void sendRequest(@NotNull final Request request) throws IOException {
        if (writer != null) {
            writer.writeObject(request);
            writer.flush();
        }
    }

    @Override
    public void handleResponses(Response response) {
        logger.info("Handle Responses called.");
        logger.info(response);
        switch (response.getType()) {
            case CONNECTED: {
                chatController.initializeContext((ContextContent)response.getContent());
                break;
            }
            case ADD_USER: {
                chatController.addUser(response.getSource());
                break;
            }
            case REMOVE_USER: {
                chatController.removeUser(response.getSource());
                break;
            }
            case MESSAGE: {
                chatController.receiveMessage(
                        MessageContainer.newInstance(
                            response.getSource(),
                            (MessageContent)response.getContent()));
                break;
            }
            case FILE_MESSAGE: {
                break;
            }
            case MESSAGE_SENT: {
                break;
            }
            case FILE_SENT: {
                chatController.fileSent(
                        MessageContainer.newInstance(
                            response.getSource(),
                            (FileMessageContent)response.getContent()));
                break;
            }
            case DESTINATION_NOT_FOUND: {
                logger.error("Destination Not Found.");
                break;
            }
            case WRONG_PARAMETERS: {
                logger.error("Wrong Parameter.");
                break;
            }
            case INTERNAL_SERVER_ERROR: {
                logger.error("Internal Server Error.");
                break;
            }
        }
    }

    @Override
    public void connect(Credentials credentials, String host, int port) throws IOException {
        //Initialize the connection to the server.
        connection = new Socket(host, port);
        final ObjectInputStream reader = new ObjectInputStream(new BufferedInputStream(connection.getInputStream()));
        if (!reader.readBoolean()) {
            logger.error("An error occurred while initializing the connection.");
            return;
        }
        writer = new ObjectOutputStream(new BufferedOutputStream(connection.getOutputStream()));

        //Initialize the InputStreamReader and let it run in another Thread.
        isReader.open(reader);
        isReader.addListener(this);
        Thread t = new Thread(isReader);
        t.start();

        //Send the credentials to validate the connection.
        this.sendRequest(this.buildRequest(RequestType.CONNECT, credentials, null));
    }

    @Override
    public void disconnect() {
        try {
            this.sendRequest(this.buildRequest(RequestType.DISCONNECT, null, null));
            isReader.close();
            this.connection.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void sendMessage(MessageContainer message) throws IOException   {
        this.sendRequest(this.buildRequest(RequestType.SEND_MESSAGE, message.getMessageContent(), message.getUser()));
    }

    @Override
    public void sendFile(User destination, File file) throws IOException  {
        final FileDescriptor fileDescriptor = FileDescriptor.newInstance(
                file.length()/ FileContent.MAX_BYTE_SIZE + 1,
                file.getName());
        this.sendRequest(this.buildRequest(RequestType.SEND_FILE, fileDescriptor, destination));

        int read = 0, chunkNumber = 1;
        final FileInputStream fis = new FileInputStream(file);
        while (read != -1 && !connection.isClosed()) {
            byte[] bytes = new byte[FileContent.MAX_BYTE_SIZE];
            read = fis.read(bytes, 0, FileContent.MAX_BYTE_SIZE);
            if (read != -1) {
                final FileContent fileContent = FileContent.newInstance(
                        fileDescriptor.getFileId(),
                        chunkNumber,
                        bytes);
                this.sendRequest(this.buildRequest(RequestType.SEND_FILE, fileContent, destination));
            }
        }
    }

    @Override
    public void requestFile(User source, long fileId) throws IOException  {
        this.sendRequest(this.buildRequest(
                RequestType.REQUEST_FILE,
                FileDescriptor.newInstance(fileId),
                source));
    }
}
