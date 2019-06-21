package client.impl;

import client.ClientProcessor;
import com.sun.istack.internal.NotNull;
import model.*;
import model.enums.ResponseType;
import server.ServerServices;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ClientProcessorImpl implements ClientProcessor {

    private final Socket socket;
    private final ServerServices server;
    private ObjectOutputStream writer;
    private ObjectInputStream reader;

    private final Collection<User> users = new LinkedList<>();

    private User user;

    private boolean isRunning = true;

    public ClientProcessorImpl(final Socket socket, final ServerServices server) {
        this.socket = socket;
        this.server = server;
    }

    private Response buildResponse(final ResponseType type, final Content content) {
        return Response.newInstance(type, content, user);
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
                    case SEND_FILE: {
                        handleFile(request);
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
        //Sending the Message to the destination.
        final Response message = buildResponse(ResponseType.MESSAGE, request.getContent());

        if (request.getDestination() == null) {
            handleError(ResponseType.WRONG_PARAMETERS);
            return;
        }

        final ClientProcessor client = server.findClient(request.getDestination().getId());

        if (client != null && client.sendResponse(message)) {
            //Sending Success Response to the User.
            sendResponse(buildResponse(ResponseType.MESSAGE_SENT, null));
        } else {
            //Sending Failure Response to the User.
            handleError(ResponseType.DESTINATION_NOT_FOUND);
        }
    }

    @Override
    public void handleFile(Request request) throws IOException {

    }

    @Override
    public void sendFile(Request request) throws IOException {

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
}
