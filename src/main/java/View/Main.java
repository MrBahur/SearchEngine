package View;

import Model.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static Stage primaryStage;
    public static Model model;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        model = new Model();
        Parent root = FXMLLoader.load(getClass().getResource("/View/View.fxml"));
        stage.setTitle("Search Engine");
        stage.setScene(new Scene(root, 300, 275));
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
