package util.image.impl;

import com.google.inject.Inject;
import controller.ChatFunctionalities;
import model.*;
import model.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.Utils;
import util.image.ScreenLiveStream;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ScreenLiveStreamImpl implements ScreenLiveStream {

    private static final Logger logger = LogManager.getLogger(ScreenLiveStreamImpl.class);

    private static final String IMAGE_FORMAT = "gif";

    @Inject private ChatFunctionalities controller;

    private ScreenInformation destinationScreen;
    private boolean isStreaming = false;


    @Override
    public void startStreaming(User destination, ScreenInformation screenInformation) throws IOException, AWTException {
        logger.info("Streaming started");
        isStreaming = true;
        this.destinationScreen = screenInformation;
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
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }

            System.gc();
        }
    }

    @Override
    public void provokeEvent(Event event) {
        if (event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent)event;
            int mouseX = (int)mouseEvent.getPosX()*Utils.getScreenWidth()/destinationScreen.getWidth();
            int mouseY = (int)mouseEvent.getPosY()*Utils.getScreenHeight()/destinationScreen.getHeight();
            System.err.println("MouseX : " + mouseX +", MouseY : " + mouseY);
            try {
                Robot robot = new Robot();
                switch (mouseEvent.getEventType()) {
                    case MOUSE_MOVE: {
                        robot.mouseMove(mouseX, mouseY);
                        break;
                    }
                    case MOUSE_LEFT_CLICK: {
                        robot.mouseMove(mouseX, mouseY);
                        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                        break;
                    }
                    case MOUSE_RIGHT_CLICK: {
                        robot.mouseMove(mouseX, mouseY);
                        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                        break;
                    }
                }
            } catch (AWTException e) {

            }
        }
    }

    @Override
    public void stopStreaming() {
        isStreaming = false;
    }
}
