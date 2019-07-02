package controller.impl;

import com.google.inject.Inject;
import com.google.inject.Injector;
import controller.ChatFunctionalities;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.*;
import model.Frame;
import network.ServerServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.Utils;
import util.image.ScreenLiveStream;
import util.voice.VoicePlayback;
import util.voice.VoiceRecorder;
import view.ChatView;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ChatController implements ChatFunctionalities {

    private static final Logger logger = LogManager.getLogger(ChatController.class);

    @Inject private Injector injector;
    @Inject private ChatView chatView;
    @Inject private ServerServices serverServices;
    @Inject private VoiceRecorder voiceRecorder;
    @Inject private VoicePlayback voicePlayback;
    @Inject private ScreenLiveStream screenLiveStream;

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

    @Override
    public void requestControl(User destination) {
        logger.info("Requesting control from user : " + destination);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/Screen.fxml"));
            fxmlLoader.setControllerFactory(injector::getInstance);
            Parent root = (Pane)fxmlLoader.load();
            Scene scene = new Scene(root, Utils.getScreenWidth(), Utils.getScreenHeight());
            Stage stage = new Stage();
            stage.setMaximized(true);
            stage.setOnCloseRequest(event -> {
                Thread t = new Thread(() -> {
                    try {
                        serverServices.cancelControl(destination);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                });
                t.start();
            });
            stage.setTitle("Stream");
            stage.setScene(scene);
            stage.show();

            serverServices.requestControl(destination);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void stopGivingControl() {
        screenLiveStream.stopStreaming();
    }

    @Override
    public void startGivingControl(User destination, ScreenInformation screenInformation) {
        logger.info("Starting giving control to : " + destination);
        try {
            screenLiveStream.startStreaming(destination, screenInformation);
        } catch (IOException | AWTException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void sendFrame(User destination, Frame frame) {
        logger.info("Sending frame to : " + destination);
        try {
            serverServices.sendFrame(destination, frame);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
