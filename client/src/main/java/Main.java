import com.google.inject.Guice;
import com.google.inject.Injector;
import guice.GuiceModule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Injector injector = Guice.createInjector(new GuiceModule());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/Login.fxml"));
        fxmlLoader.setControllerFactory(injector::getInstance);
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Java Chat Login");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}