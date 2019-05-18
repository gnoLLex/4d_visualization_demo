package main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
/** Main Class for launching the JavaFX-Application.
 * @author Lucas Engelmann
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("app.fxml"));
        primaryStage.setTitle("4D_Demo");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(720);
        primaryStage.setMinWidth(1180);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
