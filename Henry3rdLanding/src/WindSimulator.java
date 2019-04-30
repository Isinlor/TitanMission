import java.util.Random;

public class WindSimulator {

    private Force wind;

    public static Force getWind(){
        Random generator=new Random();
        double x = generator.nextInt(72);
        double y = generator.nextInt(72);
        Force wind = new Force(x,y);
        System.out.println("Wind force: " + wind.toString() + "Length: " + wind.getLength());
        return wind;
    }

    public static void main(String[] args) {
        getWind();
    }

}
