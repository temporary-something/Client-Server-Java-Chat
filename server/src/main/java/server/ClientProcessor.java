package server;

import com.sun.istack.internal.NotNull;
import pojo.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class ClientProcessor implements Services, Runnable {

    private Socket sock;
    private ObjectOutputStream writer = null;
    private ObjectInputStream reader = null;
    private final Collection<User> users = Collections.synchronizedList(new LinkedList<>());
    private User user;
    private Map<Long, List<FileContent>> mapFileContent = new HashMap<>();
    private Map<Long, String> mapFileNames = new HashMap<>();

    private boolean isRunning = true;

    ClientProcessor(Socket pSock){
        sock = pSock;
    }

    private Response buildResponse(ResponseType type, List<User> users) {
        return Response.newInstance(type, users, null, null);
    }

    private Response buildResponse(ResponseType type, MessageContent message) {
        return Response.newInstance(type, null, message, null);
    }

    private Response buildResponse(ResponseType type, FileContent fileContent) {
        return Response.newInstance(type, null, null, fileContent);
    }

    private Response buildResponse(ResponseType type) {
        return Response.newInstance(type, null, null, null);
    }

    private Request getRequest() throws IOException, ClassNotFoundException {
        Object obj = reader.readObject();
        System.out.println(obj);
        if (obj instanceof Request) return (Request)obj;
        return null;
    }

    //May be called from this thread or other threads (when a user connects/disconnects).
    private synchronized boolean sendResponse(@NotNull final Response response) throws IOException {
        //If we already closed the socket but the method is called from another thread.
        if (writer != null) {
            writer.writeObject(response);
            writer.flush();
            return true;
        }
        return false;
    }

    public void run() {
        try {
            writer = new ObjectOutputStream(new BufferedOutputStream(sock.getOutputStream()));
            writer.writeBoolean(true);
            writer.flush();
            reader = new ObjectInputStream(new BufferedInputStream(sock.getInputStream()));
            while(!sock.isClosed() && isRunning) {
                final Request request = getRequest();
                if (!isRunning) {
                    System.err.println("Connexion ended from client ...");
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
        } catch(SocketException e){
            System.err.println("Connexion Interrupted.");
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.err.println("Connexion Lost.");
        //Treat it as a disconnection.
        try {
            this.removeConnection();
        } catch (IOException e) {
            //If an IOException occurred it was when attempting to close the socket, so we have to remove the user
            //from the server's list.
            this.removeUser();
        }
    }

    public void acceptConnection(final Request request) throws IOException {
        System.err.println("Connexion Accepted.");

        //Creating a User corresponding to the current connexion and adding it to the global list of Users.
        user = User.newInstance(sock.getInetAddress().getHostAddress(), request.getContent());
        users.addAll(Server.addUser(user, this));

        //Responding to the user that requested a connexion.
        final Response response = buildResponse(ResponseType.CONNECTED, (List<User>)users);
        System.err.println("Sending Response : " + response);
        this.sendResponse(response);

        //Sending a Response to all the known users so that they know about the current user
        final MessageContent msg = new MessageContent(user, null);
        final Response newUserResponse = buildResponse(ResponseType.ADD_USER, msg);
        ClientProcessor client;
        long id = user.getId();
        synchronized (this.users) {
            for (User u : users) {
                System.err.println("Sending Response (ADD) to User : " + u.getId() + " From : " +id);
                client = Server.findClient(u.getId());
                if (client != null) {
                    client.addUser(user, newUserResponse);
                } else {
                    System.err.println("Couldn't find Client for User : " + u.getId() + " From : " + id);
                }
            }
        }
    }

    public void removeConnection() throws IOException {
        System.err.println("Disconnection from User : " + user.getId());
        //To avoid other threads calling methods before everything is done.
        synchronized (this) {
            writer = null;
            reader = null;
            if (!sock.isClosed()) sock.close();
        }
        this.removeUser();
    }

    private void removeUser() {
        //Removing from the server's list of Users and Clients.
        Server.removeUser(user.getId());

        //Sending a Response to all the known users so that they remove the current user
        final MessageContent msg = new MessageContent(user, null);
        final Response newUserResponse = buildResponse(ResponseType.REMOVE_USER, msg);
        ClientProcessor client;
        long id = user.getId();
        synchronized (this.users) {
            for (User u : users) {
                System.err.println("Sending Response (REMOVE) to User : " + u.getId() + " From : " + id);
                client = Server.findClient(u.getId());
                if (client != null) {
                    try {
                        client.removeUser(user, newUserResponse);
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

    public void sendMessage(final Request request) throws IOException {
        System.err.println("Sending Message.");

        //Sending the Message to the destination.
        final MessageContent content = new MessageContent(user, request.getContent());
        final Response message = buildResponse(ResponseType.MESSAGE, content);
        if (request.getDestination() == null) {
            sendResponse(buildResponse(ResponseType.WRONG_PARAMETERS));
            return;
        }
        final ClientProcessor client = Server.findClient(request.getDestination().getId());
        Response response;

        System.err.println("Sending Message : " + message);
        if (client != null && client.sendResponse(message)) {
            //Sending Success Response to the User.
            response = buildResponse(ResponseType.SUCCESS);
        } else {
            //Sending Failure Response to the User.
            response = buildResponse(ResponseType.DESTINATION_NOT_FOUND);
        }
        System.err.println("Sending Response : " + response);
        this.sendResponse(response);
    }

    public void handleFile(Request request) throws IOException {
        System.out.println("Handling File ...");
        final FileContent fileContent = request.getFileContent();
        if (fileContent == null) {
            System.err.println("HandleFile : Wrong Parameter ...");
            sendResponse(buildResponse(ResponseType.WRONG_PARAMETERS));
            return;
        }
        if (!mapFileNames.containsKey(fileContent.getFileId())) {
            mapFileNames.put(fileContent.getFileId(), fileContent.getFileName());
            mapFileContent.put(fileContent.getFileId(), new LinkedList<>());
        }
        final List<FileContent> list = mapFileContent.get(fileContent.getFileId());
        list.add(fileContent);

        if (list.size() == fileContent.getFragmentTotalNumber()) {
            //If all the parts are received.
            System.err.println("All the parts of the file are received ...");
            final MessageContent content = new MessageContent(user, request.getFileContent().getFileName(), request.getFileContent().getFileId());
            final Response message = buildResponse(ResponseType.FILE_MESSAGE, content);
            if (request.getDestination() == null) {
                sendResponse(buildResponse(ResponseType.WRONG_PARAMETERS));
                return;
            }
            final ClientProcessor client = Server.findClient(request.getDestination().getId());
            Response response;

            System.err.println("Sending Message : " + message);
            if (client != null && client.sendResponse(message)) {
                //Sending Success Response to the User.
                response = buildResponse(ResponseType.FILE_SENT, new MessageContent(request.getDestination(), fileContent.getFileName()));
            } else {
                //Sending Failure Response to the User.
                response = buildResponse(ResponseType.DESTINATION_NOT_FOUND);
            }
            System.err.println("Sending Response : " + response);
            this.sendResponse(response);
        }
    }

    public void sendFile(final Request request) throws IOException {
        System.err.println("Sending File ...");
        final FileContent fileContent = request.getFileContent();
        if (fileContent == null || request.getDestination() == null) {
            //No destination or no data.
            System.err.println("HandleFile : Wrong Parameter ...");
            sendResponse(buildResponse(ResponseType.WRONG_PARAMETERS));
            return;
        }

        final ClientProcessor client = Server.findClient(request.getDestination().getId());
        if (!client.mapFileNames.containsKey(fileContent.getFileId())) {
            //File does not exist.
            System.err.println("HandleFile : Wrong Parameter ...");
            sendResponse(buildResponse(ResponseType.WRONG_PARAMETERS));
            return;
        }
        final List<FileContent> elements = client.mapFileContent.get(fileContent.getFileId());
        Response response;
        for (FileContent fc : elements) {
            response = buildResponse(ResponseType.FILE, fc);
            System.err.println("Sending Response : " + response);
            this.sendResponse(response);
        }
    }

    //Method called from other threads to add a user.
    public synchronized void addUser(final User from, final Response jsonResponse) throws IOException {
        synchronized (this.users) {
            this.users.add(from);
        }
        this.sendResponse(jsonResponse);
    }

    //Method called from other threads to remove a user.
    public synchronized void removeUser(final User from, final Response jsonResponse) throws IOException {
        synchronized (this.users) {
            this.users.remove(from);
        }
        this.sendResponse(jsonResponse);
    }

    public void handleError(final ResponseType type) throws IOException {
        if (!sock.isClosed()) {
            this.sendResponse(this.buildResponse(type));
        }
    }
}
