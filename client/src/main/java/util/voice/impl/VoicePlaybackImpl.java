package util.voice.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.Utils;
import util.voice.VoicePlayback;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Original code from :
 * @author Dominic
 * @since 16-Oct-16
 * Website: www.dominicheal.com
 * Github: www.github.com/DomHeal
 */
public class VoicePlaybackImpl implements VoicePlayback {

    private static final Logger logger = LogManager.getLogger(VoicePlaybackImpl.class);

    /**
     * Plays the audio corresponding to the given byte array in a new Thread.
     * @param audio A byte array containing an audio sound.
     */
    @Override
    public void playAudio(byte[] audio) {
        try {
            InputStream input = new ByteArrayInputStream(audio);
            final AudioFormat format = Utils.getAudioFormat();
            final AudioInputStream ais = new AudioInputStream(input, format, audio.length / format.getFrameSize());
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            Runnable runner = new Runnable() {
                int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
                byte[] buffer = new byte[bufferSize];

                public void run() {
                    try {
                        int count;
                        while ((count = ais.read(buffer, 0, buffer.length)) != -1) {
                            if (count > 0) {
                                line.write(buffer, 0, count);
                            }
                        }
                    } catch (IOException e) {
                        logger.error("IO Error while playing audio : " + e.getMessage());
                    } finally {
                        line.drain();
                        line.close();
                    }
                }
            };

            Thread playThread = new Thread(runner);
            playThread.start();
        } catch (LineUnavailableException e) {
            logger.error("Line unavailable: " + e.getMessage());
        }
    }
}
