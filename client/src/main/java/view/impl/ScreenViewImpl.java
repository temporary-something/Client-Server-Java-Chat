package view.impl;

import com.google.inject.Inject;
import controller.ScreenFunctionalities;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Frame;
import model.IFrame;
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
    public void clickEvent(ActionEvent event) {

    }

    @Override
    public void moveEvent(ActionEvent event) {

    }
}
