package controller.impl;

import com.google.inject.Inject;
import controller.ChatFunctionalities;
import model.*;
import network.ServerServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.voice.VoicePlayback;
import util.voice.VoiceRecorder;
import view.ChatView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ChatController implements ChatFunctionalities {

    private static final Logger logger = LogManager.getLogger(ChatController.class);

    @Inject private ChatView chatView;
    @Inject private ServerServices serverServices;
    @Inject private VoiceRecorder voiceRecorder;
    @Inject private VoicePlayback voicePlayback;

    private Map<Long, String> toSaveFilePaths = new HashMap<>();

    @Override
    public MessageContainer sendMessage(final User destination, final String message) {
        final MessageContainer messageContainer = MessageContainer.newInstance(destination, MessageContent.newInstance(message));
        try {
            serverServices.sendMessage(messageContainer);
            return messageContainer;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public void sendFile(final User destination, final File file) {
        try {
            serverServices.checkSendFile(destination, file);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        serverServices.disconnect();
    }

    @Override
    public void receiveMessage(final MessageContainer message) {
        chatView.receiveMessage(message);
    }

    @Override
    public void fileSent(final MessageContainer fileMessage) {
        chatView.fileSent(fileMessage);
    }

    @Override
    public void addUser(final User user) {
        chatView.addUser(user);
    }

    @Override
    public void removeUser(final User user) {
        chatView.removeUser(user);
    }

    @Override
    public void initializeContext(final ContextContent context) {
        chatView.initializeContext(context);
    }

    @Override
    public void requestFile(User source, long fileId, String absolutePath) {
        try {
            toSaveFilePaths.put(fileId, absolutePath);
            serverServices.requestFile(source, fileId);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void receiveFile(FileDescriptor fileDescriptor, List<FileContent> list) {
        logger.info("File Received : " + fileDescriptor);
        Collections.sort(list);
        try {
            final File file = new File(toSaveFilePaths.get(fileDescriptor.getFileId()));
            final FileOutputStream os = new FileOutputStream(file);
            for (FileContent fc: list) {
                os.write(fc.getData());
            }
            os.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public void startRecording() {
        voiceRecorder.captureAudio();
    }

    @Override
    public void stopRecording(User destination) {
        voiceRecorder.endRecording(destination);
    }

    @Override
    public void sendAudio(User destination, byte[] audio) {
        try {
            serverServices.checkSendAudio(destination, audio);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void audioSent(MessageContainer message) {
        chatView.audioSent(message);
    }

    @Override
    public void requestAudio(User source, long audioId) {
        try {
            serverServices.requestAudio(source, audioId);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void receiveAudio(AudioDescriptor descriptor, List<AudioContent> list) {
        logger.info("Audio Received : " + descriptor);
        Collections.sort(list);
        final byte[] audio = new byte[AudioContent.MAX_BYTE_SIZE*(list.size()-1)
                + list.get(list.size()-1).getData().length];
        byte[] temps;
        for (int i = 0; i < list.size(); i++) {
            temps = list.get(i).getData();
            System.arraycopy(temps, 0, audio, i * AudioContent.MAX_BYTE_SIZE, temps.length);
        }
        this.playAudio(audio);
    }

    @Override
    public void playAudio(byte[] audio) {
        voicePlayback.playAudio(audio);
    }
}
