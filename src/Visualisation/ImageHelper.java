package Visualisation;

import Utilities.FileSystem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * See: https://stackoverflow.com/questions/4156518/rotate-an-image-in-java/4156760#4156760
 * See: https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
 */
public class ImageHelper {

    public static BufferedImage getImageResource(String resource) {
        try {
            return ImageIO.read(FileSystem.getFileResource(resource));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage rotate(BufferedImage image, double radians) {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = copy.createGraphics();
        g.rotate(radians, image.getWidth() / 2, image.getHeight() / 2);
        g.drawRenderedImage(image, null);
        g.dispose();
        return copy;
    }

}
