package client;

import model.Request;
import model.Response;
import model.User;
import model.enums.ResponseType;

import java.io.IOException;

public interface ClientProcessor extends Runnable {

    void acceptConnection(final Request request) throws IOException;
    void removeConnection() throws IOException;
    void sendMessage(final Request request) throws IOException;
    void handleFile(final Request request) throws IOException;
    void sendFile(final Request request) throws IOException;
    void addUser(final User from, final Response response) throws IOException;
    void removeUser(final User from, final Response response) throws IOException;
    void handleError(final ResponseType type) throws IOException;
}
