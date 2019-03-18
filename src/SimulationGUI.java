import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class SimulationGUI extends Application {
    private PerspectiveCamera camera;

    public Parent createContent() throws Exception {
        double auToM = 1.496e11;
        double dayToSecond = 1.0 / 86400.0;

        LinkedList<Planet> planets = CSVReader.readPlanets();

        Map<String, Body> mapOfBodies = new HashMap<>();
        for (Planet planet: planets) mapOfBodies.put(planet.getName(), planet);
        Bodies bodies = new Bodies(mapOfBodies);

        List<Sphere> planetSpheres = new LinkedList<>();
        for (Planet planet: planets) planetSpheres.add(planet.getSphere());

        for (Sphere planetSphere: planetSpheres) planetSphere.setRadius(planetSphere.getRadius()*1000);
        System.out.println(bodies);



        /*
        earthSphere.setRadius(earthSphere.getRadius()*2000);
        sunSphere.setRadius(sunSphere.getRadius()*50);
        */

        // Create and position camera
        camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(new Translate(0, 0, -1.489e12));
        camera.setFarClip(1e100);
        camera.setNearClip(1);

        // Build the Scene Graph
        Group root = new Group();
        root.getChildren().add(camera);
        for (Sphere planetSphere: planetSpheres) root.getChildren().add(planetSphere);

        final Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.millis( 5 ),
                        event -> {
                            bodies.iterate(100*25);
                            // camera.getTransforms().addAll(new Translate(camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ() - sunSphere.getRadius() ));

                            System.out.println(bodies);
                        }
                )
        );
        timeline.setCycleCount( Animation.INDEFINITE );
        timeline.play();

        // Use a SubScene
        SubScene subScene = new SubScene(root, 1000, 1000);
        subScene.setFill(Color.WHITE);
        subScene.setCamera(camera);
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
