package client.impl;

import client.ClientProcessor;
import com.sun.istack.internal.NotNull;
import model.Content;
import model.Request;
import model.Response;
import model.User;
import model.enums.ResponseType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientProcessorImpl implements ClientProcessor {

    private Socket socket;
    private ObjectOutputStream writer;
    private ObjectInputStream reader;

    private User user;

    public ClientProcessorImpl(Socket socket) {
        this.socket = socket;
    }

    private Response buildResponse(final ResponseType type, final Content content) {
        return Response.newInstance(type, content, user);
    }

    private void sendResponse(@NotNull final Response response) {

    }

    private Request getRequest() {
        return null;
    }

    @Override
    public void run() {

    }

    @Override
    public void acceptConnection(Request request) throws IOException {

    }

    @Override
    public void removeConnection() throws IOException {

    }

    private void removeUser() {

    }

    @Override
    public void sendMessage(Request request) throws IOException {

    }

    @Override
    public void handleFile(Request request) throws IOException {

    }

    @Override
    public void sendFile(Request request) throws IOException {

    }

    @Override
    public void addUser(User from, Response response) throws IOException {

    }

    @Override
    public void removeUser(User from, Response response) throws IOException {

    }

    @Override
    public void handleError(ResponseType type) throws IOException {

    }
}
