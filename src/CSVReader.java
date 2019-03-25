import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class CSVReader {
    public static LinkedList<Body> readPlanets() {
        String line = "";
        String cvsSplitBy = ",";

        LinkedList<Body> planets = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSVReader.class.getClassLoader().getResource("planets.csv").getFile()))) {
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (!firstLine) {
                    String[] parts = line.split(cvsSplitBy);

                    double auToM = 1.496e11;
                    double dayToSecond = 1.0 / 86400.0;

                    Body planet = new Body(parts[0], // parts[3],
                            new Vector(Double.valueOf(parts[5])*auToM, Double.valueOf(parts[6])*auToM, Double.valueOf(parts[7])*auToM),
                            new Vector(Double.valueOf(parts[8])*auToM*dayToSecond, Double.valueOf(parts[9])*auToM*dayToSecond, Double.valueOf(parts[10])*auToM*dayToSecond),
                        Double.valueOf(parts[1]), Double.valueOf(parts[2])
                    );
                    planets.add(planet);
                }
                firstLine = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return planets;
    }

    public static LinkedList<Planet3D> readPlanets3D() {
        String line = "";
        String cvsSplitBy = ",";

        LinkedList<Planet3D> planets = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSVReader.class.getClassLoader().getResource("planets.csv").getFile()))) {
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (!firstLine) {
                    String[] parts = line.split(cvsSplitBy);

                    double auToM = 1.496e11;
                    double dayToSecond = 1.0 / 86400.0;

                    Planet3D planet = new Planet3D(parts[0], Double.valueOf(parts[1]), Double.valueOf(parts[2]), parts[3],
                            new Vector(Double.valueOf(parts[5])*auToM, Double.valueOf(parts[6])*auToM, Double.valueOf(parts[7])*auToM),
                            new Vector(Double.valueOf(parts[8])*auToM*dayToSecond, Double.valueOf(parts[9])*auToM*dayToSecond, Double.valueOf(parts[10])*auToM*dayToSecond));
                    planets.add(planet);
                }
                firstLine = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return planets;
    }
}