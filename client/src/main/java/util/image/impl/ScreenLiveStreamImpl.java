package util.image.impl;

import com.google.inject.Inject;
import controller.ChatFunctionalities;
import model.IFrame;
import model.ScreenInformation;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.Utils;
import util.image.ScreenLiveStream;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ScreenLiveStreamImpl implements ScreenLiveStream {

    private static final Logger logger = LogManager.getLogger(ScreenLiveStreamImpl.class);

    private static final String IMAGE_FORMAT = "gif";

    @Inject
    private ChatFunctionalities controller;

    private boolean isStreaming = false;


    @Override
    public void startStreaming(User destination, ScreenInformation screenInformation) throws IOException, AWTException {
        logger.info("Streaming started");
        isStreaming = true;
        final Robot robot = new Robot();
        BufferedImage bi;
        Rectangle screen = new Rectangle(Utils.getScreenWidth(), Utils.getScreenHeight());

        boolean resize = !(screenInformation.getWidth() == screen.width &&
                screenInformation.getHeight() == screen.height);

        while (isStreaming) {
            bi = robot.createScreenCapture(screen);
            if (resize) {
                bi = Utils.resize(bi, screenInformation.getWidth(), screenInformation.getHeight());
            }
            controller.sendFrame(destination, IFrame.newInstance(Utils.toByteArray(bi, IMAGE_FORMAT)));

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }

            System.gc();
        }
    }

    @Override
    public void stopStreaming() {
        isStreaming = false;
    }
}
