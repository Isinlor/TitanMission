import java.util.Set;
import java.util.stream.Collectors;

class Vectors {

    private Set<Vector> vectors;

    Vectors(Set<Vector> vectors) {
        this.vectors = vectors;
    }

    public Set<Vector> getVectors() {
        return vectors;
    }

    Vectors rotateAroundAxisX(Vector center, double theta) {
        return new Vectors(
            vectors
                .stream()
                .map(vector -> vector.rotateAroundAxisX(center, theta))
                .collect(Collectors.toSet())
        );
    }

    Vectors rotateAroundAxisY(Vector center, double theta) {
        return new Vectors(
            vectors
                .stream()
                .map(vector -> vector.rotateAroundAxisY(center, theta))
                .collect(Collectors.toSet())
        );
    }

    Vectors rotateAroundAxisZ(Vector center, double theta) {
        return new Vectors(
            vectors
                .stream()
                .map(vector -> vector.rotateAroundAxisZ(center, theta))
                .collect(Collectors.toSet())
        );
    }

}
