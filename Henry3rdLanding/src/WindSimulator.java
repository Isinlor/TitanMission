import java.util.Random;

public class WindSimulator {

    public static Force getWind(){
        Random generator=new Random();
        double random = generator.nextInt(10);
        double x=0;
        double y=0;

        System.out.println(random);

        if (random<5){
            x = generator.nextInt(30);
            y = generator.nextInt(30);
        }
        else if(random>=5 && random<8){
            x = generator.nextInt(50);
            y = generator.nextInt(50);
        }
        else {
            x = generator.nextInt(72);
            y = generator.nextInt(72);
        }

        Force wind = new Force(x,y);
        System.out.println("Wind force: " + wind.toString() + "\nLength: " + wind.getLength());
        return wind;
    }

    public static void main(String[] args) {
        getWind();
    }

}
