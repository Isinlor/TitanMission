public class Test {

    public static void main(String[] args) {

        OpenLoopController openController = getOpenController();

        System.out.println("Free fall");
        openController.freeFall(getNewSpaceship());
        System.out.println("Free fall impulse at the end");
        openController.simulateFreeFallWithImpulseAtTheEnd(getNewSpaceship());
        System.out.println("Unrealistically slow descent");
        openController.simulateUnrealisticReallySlowLanding(getNewSpaceship());

    }

    private static Spaceship getNewSpaceship() {
        Vector position=new Vector(0, 600*1000);
        Vector velocity=new Vector(0,0);
        return new Spaceship("henry3rd",position,velocity,500);
    }

    private static OpenLoopController getOpenController() {
        Force force = new Force();
        double titanRadius = 2500*1000;

        Body titan=new Body(
                "Titan",
                new Vector(0, -titanRadius),
                new Vector(0, 0),
                134.5e21
        );
        return new OpenLoopController(force, titan, 24 * 60 * 60 * 1000, 1 / 1000.0);
    }

}
