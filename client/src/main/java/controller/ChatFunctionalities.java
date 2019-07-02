package controller;

import model.*;

import java.io.File;
import java.util.List;

public interface ChatFunctionalities {

    MessageContainer sendMessage(User destination, String message);
    void sendFile(User destination, File file);
    void disconnect();
    void receiveMessage(MessageContainer message);
    void fileSent(MessageContainer fileMessage);
    void addUser(User user);
    void removeUser(User user);
    void initializeContext(ContextContent context);
    void requestFile(User source, long fileId, String absolutePath);
    void receiveFile(FileDescriptor fileDescriptor, List<FileContent> list);
    void startRecording();
    void stopRecording(User destination);
    void sendAudio(User destination, byte[] audio);
    void audioSent(MessageContainer message);
    void requestAudio(User source, long audioId);
    void receiveAudio(AudioDescriptor descriptor, List<AudioContent> list);
    void playAudio(byte[] audio);
    void requestControl(User destination);
    void startGivingControl(User destination, ScreenInformation screenInformation);
    void stopGivingControl();
    void sendFrame(User destination, Frame frame);
}
