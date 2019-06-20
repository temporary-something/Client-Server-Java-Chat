package server;

import pojo.Request;
import pojo.Response;
import pojo.ResponseType;
import pojo.User;

import java.io.IOException;

public interface Services {

    void acceptConnection(final Request request) throws IOException;
    void removeConnection() throws IOException;
    void sendMessage(final Request request) throws IOException;
    void handleFile(final Request request) throws IOException;
    void sendFile(final Request request) throws IOException;
    void addUser(final User from, final Response response) throws IOException;
    void removeUser(final User from, final Response response) throws IOException;
    void handleError(final ResponseType type) throws IOException;
}
