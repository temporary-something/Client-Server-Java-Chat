package controller.impl;

import com.google.inject.Inject;
import controller.ScreenFunctionalities;
import model.Frame;
import model.MouseEvent;
import model.User;
import network.ServerServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.ScreenView;

import java.io.IOException;

public class ScreenController implements ScreenFunctionalities {

    private static final Logger logger = LogManager.getLogger(ScreenController.class);

    @Inject private ScreenView screenView;
    @Inject private ServerServices serverServices;

    private User source;

    @Override
    public void updateScreen(User source, Frame frame) {
        this.source = source;
        screenView.updateScreen(frame);
    }

    @Override
    public void sendEvent(MouseEvent event) {
        try {
            serverServices.sendEvent(source, event);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
