import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.*;

public class Simulation extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception{

        Scene scene = simulationScene(stage);
        stage.setScene(scene);
        stage.show();

    }

    public Scene simulationScene(Stage stage){

        stage.setTitle("Landing simulation");

        Button freeFall = new Button("Free fall");
//        freeFall.setActionCommand();
        AnchorPane anchorPane = new AnchorPane();
        Scene scene = new Scene(anchorPane, 800,600);
        return scene;
    }

}
