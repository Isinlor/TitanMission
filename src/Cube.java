import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

class Cube {

    private HashMap<Integer, Vector> vertices = new HashMap<>();

    /**
     * Creates a cube.
     *
     * @param A The corner of the cube.
     * @param a Side length of the cube.
     */
    Cube(Vector A, int a) {

        double Aax = A.x+a;
        double Aay = A.y+a;
        double Aaz = A.z+a;

        vertices.put(0, new Vector(A.x, A.y, A.z));
        vertices.put(1, new Vector(Aax, A.y, A.z));
        vertices.put(2, new Vector(A.x, Aay, A.z));
        vertices.put(3, new Vector(A.x, A.y, Aaz));
        vertices.put(4, new Vector(Aax, Aay, A.z));
        vertices.put(5, new Vector(Aax, A.y, Aaz));
        vertices.put(6, new Vector(A.x, Aay, Aaz));
        vertices.put(7, new Vector(Aax, Aay, Aaz));

    }

    Vectors getVectors() {
        return new Vectors(new HashSet<Vector>(vertices.values()));
    }

    void rotateAroundAxisX(Vector center, double theta) {
        for (int i = 0; i < vertices.size(); i++) {
            vertices.replace(i, vertices.get(i).rotateAroundAxisX(center, theta));
        }
    }

    void rotateAroundAxisY(Vector center, double theta) {
        for (int i = 0; i < vertices.size(); i++) {
            vertices.replace(i, vertices.get(i).rotateAroundAxisY(center, theta));
        }
    }

    void rotateAroundAxisZ(Vector center, double theta) {
        for (int i = 0; i < vertices.size(); i++) {
            vertices.replace(i, vertices.get(i).rotateAroundAxisZ(center, theta));
        }
    }

    void draw(Graphics g) {
        
        // Colors are added in order to allow keeping track of directions
        Color oldColor = g.getColor();
        g.setColor(Color.RED);
        drawLine(g, vertices.get(0), vertices.get(1));
        g.setColor(Color.GREEN);
        drawLine(g, vertices.get(0), vertices.get(2));
        g.setColor(Color.BLUE);
        drawLine(g, vertices.get(0), vertices.get(3));
        g.setColor(oldColor);
        drawLine(g, vertices.get(1), vertices.get(4));
        drawLine(g, vertices.get(1), vertices.get(5));
        drawLine(g, vertices.get(2), vertices.get(4));
        drawLine(g, vertices.get(2), vertices.get(6));
        drawLine(g, vertices.get(3), vertices.get(5));
        drawLine(g, vertices.get(3), vertices.get(6));
        drawLine(g, vertices.get(4), vertices.get(7));
        drawLine(g, vertices.get(5), vertices.get(7));
        drawLine(g, vertices.get(6), vertices.get(7));

    }

    private void drawLine(Graphics g, Vector a, Vector b) {
        g.drawLine(
            (int)Math.round(a.x), (int)Math.round(a.y),
            (int)Math.round(b.x), (int)Math.round(b.y)
        );
    }

}
