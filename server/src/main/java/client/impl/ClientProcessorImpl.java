package client.impl;

import client.ClientProcessor;
import com.sun.istack.internal.NotNull;
import model.*;
import model.FileDescriptor;
import model.enums.ResponseType;
import server.ServerServices;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class ClientProcessorImpl implements ClientProcessor {

    private final Socket socket;
    private final ServerServices server;
    private ObjectOutputStream writer;
    private ObjectInputStream reader;

    private final Collection<User> users = new LinkedList<>();
    private Map<Long, List<FileContent>> filesContent = new HashMap<>();
    private Map<Long, FileDescriptor> filesDescription = new HashMap<>();

    private User user;

    private boolean isRunning = true;

    public ClientProcessorImpl(final Socket socket, final ServerServices server) {
        this.socket = socket;
        this.server = server;
    }

    private Response buildResponse(final ResponseType type, final Content content) {
        return Response.newInstance(type, content, user);
    }

    private Response buildResponse(final ResponseType type, final Content content, final User newOrigin) {
        return Response.newInstance(type, content, newOrigin);
    }

    public synchronized boolean sendResponse(@NotNull final Response response) throws IOException {
        if (writer != null) {
            writer.writeObject(response);
            writer.flush();
            return true;
        }
        return false;
    }

    private Request getRequest() throws IOException, ClassNotFoundException {
        Object obj = reader.readObject();
        System.out.println(obj);
        if (obj instanceof Request) return (Request)obj;
        return null;
    }

    @Override
    public void run() {
        try {
            writer = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            writer.writeBoolean(true);
            writer.flush();
            reader = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            while(!socket.isClosed() && isRunning) {
                final Request request = getRequest();

                if (!isRunning) {
                    System.err.println("Connexion ended by Server ...");
                    break;
                }
                if (request == null) {
                    System.err.println("Request is empty.");
                    handleError(ResponseType.WRONG_PARAMETERS);
                } else switch (request.getType()) {
                    case CONNECT: {
                        acceptConnection(request);
                        break;
                    }
                    case SEND_MESSAGE: {
                        sendMessage(request);
                        break;
                    }
                    case PREPARE_SEND_FILE: {
                        checkFile(request);
                        break;
                    }
                    case SEND_FILE: {
                        handleFile(request);
                        break;
                    }
                    case PREPARE_REQUEST_FILE: {
                        prepareSendFile(request);
                        break;
                    }
                    case REQUEST_FILE: {
                        sendFile(request);
                        break;
                    }
                    case DISCONNECT: {
                        removeConnection();
                        return;
                    }
                    default: {
                        System.err.println("Unexpected value : " + request.getType());
                    }
                }
            }
        } catch(SocketException e) {
            System.err.println("Connexion Interrupted.");
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.err.println("Connexion Lost.");
        //Treat it as a disconnection.
        this.removeConnection();
    }

    @Override
    public void acceptConnection(Request request) throws IOException {
        System.err.println("Connexion Accepted.");

        //Creating a User corresponding to the current connexion and adding it to the global list of Users.
        user = User.newInstance(socket.getInetAddress().getHostAddress(), ((Credentials)request.getContent()).getUsername());
        server.addUser(user, this, users);

        System.out.println(buildResponse(ResponseType.CONNECTED, ContextContent.newInstance((List<User>)users)));
        this.sendResponse(buildResponse(ResponseType.CONNECTED, ContextContent.newInstance((List<User>)users)));

        //Sending a Response to all the known users so that they know about the current user
        final Response response = buildResponse(ResponseType.ADD_USER, null);

        ClientProcessor client;
        long id = user.getId();
        synchronized (this.users) {
            for (User u : users) {
                System.err.println("Sending Response (ADD) to User : " + u.getId() + " From : " + id);
                client = server.findClient(u.getId());
                if (client != null) {
                    client.addUser(user, response);
                } else {
                    System.err.println("Couldn't find Client for User : " + u.getId() + " From : " + id);
                }
            }
        }
    }

    @Override
    public void removeConnection() {
        System.err.println("Disconnection from User : " + user.getId());
        this.close();
        this.removeUser();
    }

    private void removeUser() {
        //Removing from the server's list of Users and Clients.
        server.removeUser(user.getId());

        //Sending a Response to all the known users so that they remove the current user
        final Response response = buildResponse(ResponseType.REMOVE_USER, null);

        ClientProcessor client;
        final long id = user.getId();
        synchronized (this.users) {
            for (User u : users) {
                System.err.println("Sending Response (REMOVE) to User : " + u.getId() + " From : " + id);
                client = server.findClient(u.getId());
                if (client != null) {
                    try {
                        client.removeUser(user, response);
                    } catch (IOException e) {
                        System.err.println("Exception while removing user ...");
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Couldn't find Client for User : " + u.getId() + " From : " + id);
                }
            }
        }
    }

    @Override
    public void sendMessage(Request request) throws IOException {
        if (request.getDestination() == null) {
            handleError(ResponseType.WRONG_PARAMETERS);
            return;
        }

        this.sendMessage(request.getContent(), request.getDestination(), ResponseType.MESSAGE, ResponseType.MESSAGE_SENT);
    }

    private void sendMessage(Content content, User destination, ResponseType type, ResponseType successResponseType) throws IOException {
        final ClientProcessor client = server.findClient(destination.getId());

        //Sending the Message to the destination.
        final Response message = buildResponse(type, content);

        if (client != null && client.sendResponse(message)) {
            //Sending Success Response to the User.
            sendResponse(buildResponse(successResponseType, content));
        } else {
            //Sending Failure Response to the User.
            handleError(ResponseType.DESTINATION_NOT_FOUND);
        }
    }

    @Override
    public void checkFile(Request request) throws IOException {
        final FileDescriptor descriptor = (FileDescriptor)request.getContent();
        if (descriptor.getChunksTotalNumber() < ClientProcessor.FILE_SIZE_THRESHOLD) {
            //File size inferior to the maximum authorized, tell the client to start sending the file.
            this.filesDescription.put(descriptor.getFileId(), descriptor);
            sendResponse(buildResponse(
                    ResponseType.CAN_SEND_FILE,
                    FileBasicInformation.newInstance(descriptor.getFileId()),
                    request.getDestination()));
        } else {
            //File is too big, tell the client to not send the file.
            sendResponse(buildResponse(ResponseType.INSUFFICIENT_MEMORY, null));
        }
    }

    @Override
    public void handleFile(Request request) throws IOException {
        final FileContent fileContent = (FileContent)request.getContent();
        if (fileContent == null || !filesDescription.containsKey(fileContent.getFileId())) {
            handleError(ResponseType.WRONG_PARAMETERS);
            return;
        }

        if (!filesContent.containsKey(fileContent.getFileId())) {
            filesContent.put(fileContent.getFileId(), new LinkedList<>());
        }

        final List<FileContent> list = filesContent.get(fileContent.getFileId());
        list.add(fileContent);

        final FileDescriptor fileDescriptor = filesDescription.get(fileContent.getFileId());
        if (list.size() == fileDescriptor.getChunksTotalNumber()) {
            //If all the parts are received, send a FileMessage to the destination, and tell
            // the sending user that the file has been sent.
            sendMessage(
                    FileMessageContent.newInstance(fileDescriptor),
                    request.getDestination(),
                    ResponseType.FILE_MESSAGE,
                    ResponseType.FILE_SENT);
        }
    }

    @Override
    public void prepareSendFile(Request request) throws IOException {
        final ClientProcessor client = (request.getDestination() == null)
                ? this
                : server.findClient(request.getDestination().getId());

        final FileDescriptor fileDescriptor = client.getFilesDescription().get(((FileBasicInformation)request.getContent()).getFileId());
        if (fileDescriptor == null) {
            handleError(ResponseType.WRONG_PARAMETERS);
            return;
        }

        sendResponse(buildResponse(
                ResponseType.PREPARE_RECEIVE_FILE,
                fileDescriptor,
                (request.getDestination() == null) ? user : request.getDestination()));
    }

    @Override
    public void sendFile(Request request) throws IOException {
        final FileBasicInformation fileBasicInformation = (FileBasicInformation)request.getContent();
        final ClientProcessor client = server.findClient(request.getDestination().getId());

        final FileDescriptor fileDescriptor = client.getFilesDescription().get(fileBasicInformation.getFileId());

        if (fileDescriptor == null || request.getDestination() == null) {
            handleError(ResponseType.WRONG_PARAMETERS);
            return;
        }

        final List<FileContent> list = client.getFilesContent().get(fileBasicInformation.getFileId());
        if (list.size() != fileDescriptor.getChunksTotalNumber()) {
            handleError(ResponseType.WRONG_PARAMETERS);
            return;
        }

        for (FileContent fc : list) {
            sendResponse(buildResponse(ResponseType.FILE_CHUNK, fc));
        }
    }

    @Override
    public void addUser(User from, Response response) throws IOException {
        synchronized (this.users) {
            this.users.add(from);
        }
        this.sendResponse(response);
    }

    @Override
    public void removeUser(User from, Response response) throws IOException {
        synchronized (this.users) {
            this.users.remove(from);
        }
        this.sendResponse(response);
    }

    @Override
    public void handleError(ResponseType type) throws IOException {
        if (!socket.isClosed()) {
            this.sendResponse(this.buildResponse(type, null));
        }
    }

    @Override
    public void close() {
        try {
            synchronized (this) {
                writer = null;
                reader = null;
                this.isRunning = false;
                this.socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Long, List<FileContent>> getFilesContent() {
        return filesContent;
    }

    public Map<Long, FileDescriptor> getFilesDescription() {
        return filesDescription;
    }
}
