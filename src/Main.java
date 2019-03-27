import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import vector.*;
import javafx.scene.input.MouseEvent;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

//TODO:clean up code and comment everything

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("app.fxml"));
        primaryStage.setTitle("4D Rotation");
        primaryStage.setScene(new Scene(root, 980, 720));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
