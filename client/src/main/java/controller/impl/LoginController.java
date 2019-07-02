package controller.impl;

import com.google.inject.Inject;
import com.google.inject.Injector;
import controller.LoginFunctionalities;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Credentials;
import network.ServerServices;
import view.LoginView;

import java.io.IOException;

public class LoginController implements LoginFunctionalities {

    @Inject private LoginView loginView;

    @Inject private ServerServices serverServices;

    @Inject private Injector injector;

    @Override
    public void connect(final Credentials credentials, final String host, final int port) {

        Platform.runLater(new Runnable() {
            public void run() {
                try {
                    Stage primaryStage = loginView.getStage();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/Chat.fxml"));
                    fxmlLoader.setControllerFactory(injector::getInstance);
                    Parent window = (Pane)fxmlLoader.load();
                    primaryStage.setTitle("Java Chat");
                    primaryStage.setResizable(false);
                    primaryStage.setScene(new Scene(window, 800, 600));

                    serverServices.connect(credentials, host, port);

                    primaryStage.setOnCloseRequest(event1 -> {
                        Thread t = new Thread(serverServices::disconnect);
                        t.start();
                    });
                    primaryStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
