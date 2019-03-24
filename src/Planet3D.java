import javafx.scene.shape.Sphere;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

public class Planet3D extends Planet {
    private String texturePath;
    private Sphere sphere;

    Planet3D(String name, double mass, double radius, String texturePath, Vector position, Vector velocity) {
        super(name, mass, radius, position, velocity);

        this.texturePath = texturePath;

        sphere = new Sphere();
        PhongMaterial material = new PhongMaterial(Color.WHITE);
        material.setDiffuseMap(new Image(getClass().getClassLoader().getResource(texturePath).toString(), true));
        sphere.setMaterial(material);
        sphere.setRadius(radius);
        sphere.setTranslateX(getPosition().x);
        sphere.setTranslateY(getPosition().y);
        sphere.setTranslateZ(getPosition().z);
    }


    public Sphere getSphere() {return sphere;}


    @Override
    void applyForce(Force force, double time) {
        super.applyForce(force, time);
        sphere.setTranslateX(getPosition().x);
        sphere.setTranslateY(getPosition().y);
        sphere.setTranslateZ(getPosition().z);
    }
}
