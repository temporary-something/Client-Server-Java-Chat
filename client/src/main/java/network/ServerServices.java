package network;

import model.*;

import java.io.File;
import java.io.IOException;

public interface ServerServices {

    void connect(Credentials credentials, String host, int port) throws IOException;
    void disconnect();
    void sendMessage(MessageContainer message) throws IOException ;
    //File methods
    void checkSendFile(User destination, File file) throws IOException ;
    void sendFile(User destination, long fileId) throws IOException ;
    void requestFile(User source, long fileId) throws IOException ;
    void prepareReceiveFile(FileDescriptor fileDescriptor, User source) throws IOException;
    void receiveFile(User source, FileContent fileContent);
    //Audio methods
    void checkSendAudio(User destination, byte[] audio) throws IOException ;
    void sendAudio(User destination, long audioId) throws IOException ;
    void requestAudio(User source, long audioId) throws IOException ;
    void prepareReceiveAudio(AudioDescriptor audioDescriptor, User source) throws IOException;
    void receiveAudio(User source, AudioContent audioContent);

    void requestControl(User destination) throws IOException;
    void cancelControl(User destination) throws IOException;
    void sendFrame(User destination, Frame frame) throws IOException;
    void sendEvent(User destination, Event event) throws IOException;
}
