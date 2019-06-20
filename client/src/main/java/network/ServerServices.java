package network;

import model.Credentials;
import model.MessageContainer;
import model.User;

import java.io.File;
import java.io.IOException;

public interface ServerServices {
    
    void connect(Credentials credentials, String host, int port) throws IOException;
    void disconnect();
    void sendMessage(MessageContainer message) throws IOException ;
    void sendFile(User destination, File file) throws IOException ;
    void requestFile(User source, long fileId) throws IOException ;
}
