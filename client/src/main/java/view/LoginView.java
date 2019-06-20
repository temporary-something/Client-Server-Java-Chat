package view;

import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

public interface LoginView extends Initializable {

    void connect(Event actionEvent);
    Stage getStage();
}
