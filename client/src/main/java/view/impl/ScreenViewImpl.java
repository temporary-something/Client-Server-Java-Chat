package view.impl;

import com.google.inject.Inject;
import controller.ScreenFunctionalities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.Event;
import model.Frame;
import model.IFrame;
import model.enums.EventType;
import util.Utils;
import view.ScreenView;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class ScreenViewImpl implements ScreenView {

    @FXML private ImageView imageScreen;
    @Inject private ScreenFunctionalities screenController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageScreen.setPreserveRatio(true);
        imageScreen.setSmooth(true);
        imageScreen.minWidth(Utils.getScreenWidth());
        imageScreen.minHeight(Utils.getScreenHeight());
    }

    @Override
    public void updateScreen(Frame frame) {
        if (frame instanceof IFrame) {
            imageScreen.setImage(new Image(new ByteArrayInputStream(((IFrame)frame).getImage())));
        }
    }

    @Override
    public void clickEvent(MouseEvent event) {
        MouseButton mb = event.getButton();
        EventType type = EventType.MOUSE_LEFT_CLICK;
        switch (mb) {
            case MIDDLE: {
                type = EventType.MOUSE_WHEEL_CLIck;
                break;
            }
            case PRIMARY: {
                type = EventType.MOUSE_LEFT_CLICK;
                break;
            }
            case SECONDARY: {
                type = EventType.MOUSE_RIGHT_CLICK;
                break;
            }
        }
        screenController.sendEvent(
                model.MouseEvent.newInstance(type,
                        event.getSceneX(),
                        event.getSceneY()));
    }

    @Override
    public void moveEvent(MouseEvent event) {
        System.err.println("EventX : "+event.getSceneX() +", EventY : "+event.getSceneY());
//        screenController.sendEvent(
//                model.MouseEvent.newInstance(EventType.MOUSE_MOVE,
//                        event.getScreenX(),
//                        event.getScreenY()));
    }
}
