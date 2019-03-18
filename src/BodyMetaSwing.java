import java.awt.*;

/**
 * Metadata for swing visualization;
 */
public class BodyMetaSwing implements BodyMeta {

    private Color color;

    BodyMetaSwing(Color color) {
        this.color = color;
    }

    Color getColor() {
        return color;
    }

    public BodyMeta copy() {
        return new BodyMetaSwing(
            new Color(color.getRGB())
        );
    }

}
