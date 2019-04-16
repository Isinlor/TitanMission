public class Test {

    public static void main(String[] args) {

        System.out.println("Free fall");
        simulateSlowedDownFreeFall(getNewSpaceship());
        System.out.println("Free fall with impulse");
        simulateFreeFallWithImpulseAtTheEnd(getNewSpaceship());

    }

    private static void simulateFreeFallWithImpulseAtTheEnd(Spaceship henry3rd) {

        System.out.println(henry3rd.getPosition());
        for (int i=0; i < 24*60*60*1000; i++) {

            if((int)henry3rd.getPosition().y <= 100) {
                henry3rd.slowingDown = new Force(henry3rd.computeAttraction(henry3rd.target).product(-4818.085));
            }

            if ((int)henry3rd.getPosition().y <= 0){
                System.out.println((int)henry3rd.getPosition().y);
                System.out.println("Landed"+i + henry3rd.getVelocity());
                break;
            }

            henry3rd.iterate(1 / 1000.0);

        }
    }

    private static void simulateSlowedDownFreeFall(Spaceship henry3rd) {
//        henry3rd.slowingDown = new Force(henry3rd.computeAttraction(henry3rd.target).product(-0.99));

        System.out.println(henry3rd.getPosition());
        for (int i=0; i < 24*60*60*1000; i++) {
            if ((int)henry3rd.getPosition().y <= 0){
                System.out.println((int)henry3rd.getPosition().y);
                System.out.println("Landed"+i + henry3rd.getVelocity());
                break;
            }
            henry3rd.iterate(1 / 1000.0);
        }
    }

    private static Spaceship getNewSpaceship() {
        double titanRadius = 2500*1000;

        Body titan=new Body(
                "Titan",
                new Vector(0, -titanRadius),
                new Vector(0, 0),
                134.5e21
        );

        Vector position=new Vector(0, 600*1000);
        Vector velocity=new Vector(0,0);
        Force force = new Force();
        return new Spaceship("henry3rd",position,velocity,500,force,titan);
    }


}
