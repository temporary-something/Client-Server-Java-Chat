package view;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import model.Frame;

public interface ScreenView extends Initializable {

    void updateScreen(Frame frame);
    void clickEvent(ActionEvent event);
    void moveEvent(ActionEvent event);
}
