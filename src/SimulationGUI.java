import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Set;

public class SimulationGUI extends Application {
    private PerspectiveCamera camera;

    public Parent createContent() throws Exception {
        double auToM = 1.496e11;
        double dayToSecond = 1.0 / 86400.0;
        Planet earth = new Planet(new Vector(-9.918696803493554E-01 * auToM, 9.679454643549934E-02 * auToM, -4.277240997129137E-05 * auToM),
                new Vector(-1.825836604899280E-03 * auToM * dayToSecond, -1.719621912926312E-02 * auToM * dayToSecond, 3.421794164900239E-07 * auToM * dayToSecond),
                5.9721986e24,
                6371008,
                "planettextures/8k_earth_daymap.jpg");

        Planet sun = new Planet(new Vector(-1.351343105506232E-03 * auToM, 7.549817138203992E-03 * auToM, -4.200718115315673E-05 * auToM),
                new Vector(-8.222950279730839E-06 * auToM * dayToSecond, 1.252598675779703E-06 * auToM * dayToSecond, 2.140020605610505E-07 * auToM * dayToSecond),
                1.988435e30,
                6.957e8,
                "planettextures/8k_sun.jpg");

        Set<Body> setOfBodies = new HashSet<>();
        setOfBodies.add(earth);
        setOfBodies.add(sun);
        Bodies bodies = new Bodies(setOfBodies);

        System.out.println(bodies);

        final Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.millis( 5 ),
                        event -> {
                            bodies.iterate(0.5);

                            System.out.println(bodies);
                        }
                )
        );
        timeline.setCycleCount( Animation.INDEFINITE );
        timeline.play();

        Sphere earthSphere = earth.getSphere();
        Sphere sunSphere = sun.getSphere();

        // Create and position camera
        camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(new Translate(earthSphere.getTranslateX(), earthSphere.getTranslateY(), earthSphere.getTranslateZ() - earthSphere.getRadius() * 5));
        camera.setFarClip(1e100);
        camera.setNearClip(1);

        // Build the Scene Graph
        Group root = new Group();
        root.getChildren().add(camera);
        root.getChildren().add(earthSphere);
        root.getChildren().add(sunSphere);

        // Use a SubScene
        SubScene subScene = new SubScene(root, 500, 500);
        subScene.setFill(Color.ALICEBLUE);
        subScene.setCamera(camera);
        subScene.addEventHandler(KeyEvent.ANY, event -> System.out.println("Hey"));
        subScene.setOnKeyPressed(event -> System.out.println("HeyR2"));
        Group group = new Group();
        group.getChildren().add(subScene);
        return group;
    }

    private void handleKeyboard(Scene scene) {
        scene.setOnKeyPressed(event -> {
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        Scene scene = new Scene(createContent());
        handleKeyboard(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Java main for when running without JavaFX launcher
     */
    public static void main(String[] args) {
        launch(args);
    }
}
