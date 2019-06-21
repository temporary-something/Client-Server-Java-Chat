package client;

import model.*;
import model.enums.ResponseType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ClientProcessor extends Runnable {

    long FILE_SIZE_THRESHOLD = 100000;// 100 Mo

    boolean sendResponse(final Response response) throws IOException;
    void acceptConnection(final Request request) throws IOException;
    void removeConnection();
    void sendMessage(final Request request) throws IOException;
    void checkFile(final Request request) throws IOException;
    void handleFile(final Request request) throws IOException;
    void prepareSendFile(final Request request) throws IOException;
    void sendFile(final Request request) throws IOException;
    void addUser(final User from, final Response response) throws IOException;
    void removeUser(final User from, final Response response) throws IOException;
    void handleError(final ResponseType type) throws IOException;
    void close();

    //TODO: A ajouter au diagramme de classes.
    Map<Long, List<FileContent>> getFilesContent();
    Map<Long, FileDescriptor> getFilesDescription();
}
