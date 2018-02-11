package L03;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Team 2
 */
public class app extends Application{
    public static Scene scene;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/L03/gui.fxml"));
        this.scene = new Scene(root);
        stage.setTitle("Connecting to a web service");
        stage.setScene(scene);
        //stage.setWidth(650);
        //stage.setHeight(600);
        stage.show();
    }
}