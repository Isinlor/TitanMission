import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

/**
 * Representation of a body with position velocity and mass.
 */
class Planet extends Body {
    private String texturePath;
    private double radius;
    private Sphere sphere;

    Planet(Vector position, Vector velocity, double mass, double radius, String texturePath) {
        super(position, velocity, mass);
        this.radius = radius;
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

    double getRadius() {
        return radius;
    }

    @Override
    void applyForce(Force force, double time) {
        super.applyForce(force, time);
        sphere.setTranslateX(getPosition().x);
        sphere.setTranslateY(getPosition().y);
        sphere.setTranslateZ(getPosition().z);
    }

    public Sphere getSphere() {return sphere;}

}
