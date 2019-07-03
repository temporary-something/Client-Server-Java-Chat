package view;

import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import model.Frame;

public interface ScreenView extends Initializable {

    void updateScreen(Frame frame);
    void clickEvent(MouseEvent event);
    void moveEvent(MouseEvent event);
}
