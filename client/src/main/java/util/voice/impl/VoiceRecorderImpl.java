package util.voice.impl;

import com.google.inject.Inject;
import model.User;
import network.ServerServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.Utils;
import util.voice.VoiceRecorder;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Original code from :
 * @author Dominic
 * @since 16-Oct-16
 * Website: www.dominicheal.com
 * Github: www.github.com/DomHeal
 */
public class VoiceRecorderImpl implements VoiceRecorder {

    private static final Logger logger = LogManager.getLogger(VoiceRecorderImpl.class);

    @Inject
    private ServerServices services;

    private boolean isRecording = false;

    /**
     * Captures the audio in a new Thread and sends it to the given destination via the server.
     */
    @Override
    public void captureAudio(User destination) {
        try {
            final AudioFormat format = Utils.getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            Runnable runner = new Runnable() {
                int bufferSize = (int)format.getSampleRate() * format.getFrameSize();
                byte[] buffer = new byte[bufferSize];

                public void run() {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    isRecording = true;
                    try {
                        while (isRecording) {
                            int count = line.read(buffer, 0, buffer.length);
                            if (count > 0) {
                                out.write(buffer, 0, count);
                            }
                        }
                    } finally {
                        try {
                            out.close();
                            out.flush();
                            line.close();
                            line.flush();
                            //TODO: replace with a call to server#sendVoiceMessage
                            Files.write(Paths.get("C:\\Downloads\\huhu.wav"), out.toByteArray());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            Thread captureThread = new Thread(runner);
            captureThread.start();

        } catch (LineUnavailableException e) {
            logger.error("Line unavailable: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void endRecording() {
        isRecording = false;
    }
}
