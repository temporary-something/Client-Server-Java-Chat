package view.impl;

import com.google.inject.Inject;
import controller.LoginFunctionalities;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Credentials;
import view.LoginView;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginViewImpl implements LoginView {

    @FXML private TextField txtUsername;
    @FXML private TextField txtIPAddress;
    @FXML private TextField txtPort;

    @Inject private LoginFunctionalities loginController;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

    }

    @Override
    public void connect(final Event actionEvent) {
        loginController.connect(Credentials.newInstance(txtUsername.getText()),
                txtIPAddress.getText(), Integer.parseInt(txtPort.getText()));
    }

    @Override
    public Stage getStage() {
        return (Stage)txtUsername.getScene().getWindow();
    }
}
