public class OpenLoopController {

    final private Vector distanceFromSurfaceAtStart = new Vector(0, 6e5);
    final private Force zeroForce = new Force(0,0);

    private Force slowingDownForce;
    private Body target;
    private double timetoRunthrough;
    private double timeStep;

    OpenLoopController(Force slowingDown, Body target, double timetoRunthrough, double timeStep){
        this.slowingDownForce = slowingDown;
        this.target = target;
        this.timetoRunthrough = timetoRunthrough;
        this.timeStep = timeStep;
    }

    public void freeFall(Spaceship henry3rd) {

        System.out.println("Position is: " + henry3rd.getPosition());
        for (int i=0; i < timetoRunthrough; i++) {
            if ((int)henry3rd.getPosition().y <= 0){
                System.out.println("Position is: " + (int)henry3rd.getPosition().y);
                System.out.println("Landed in " + i + " seconds, with velocity: " + henry3rd.getVelocity() + "\n");
                break;
            }
            henry3rd.iterate(target, slowingDownForce, timeStep);
        }

    }

    public void simulateFreeFallWithImpulseAtTheEnd(Spaceship henry3rd) {

        System.out.println("Position is: " + henry3rd.getPosition());
        for (int i = 0; i < timetoRunthrough; i++) {

            if ((int) henry3rd.getPosition().y <= 100) {
                slowingDownForce = new Force(henry3rd.computeAttraction(target).product(-4818.085));
            }

            if ((int) henry3rd.getPosition().y <= 0) {
                System.out.println("Position is: " + (int)henry3rd.getPosition().y);
                System.out.println("Landed in " + i + " seconds, with velocity: " + henry3rd.getVelocity() + "\n");
                break;
            }

            henry3rd.iterate(target, slowingDownForce, timeStep);

        }
    }

    public void simulateUnrealisticReallySlowLanding(Spaceship henry3rd) {

        slowingDownForce = new Force(henry3rd.computeAttraction(target).product(-0.999));
        System.out.println("Slowing down force is: " + slowingDownForce.toString());
        System.out.println("Position is: " + henry3rd.getPosition());
        for (int i=0; i < timetoRunthrough; i++) {
            if ((int)henry3rd.getPosition().y <= 0){
                System.out.println("Position is: " + (int)henry3rd.getPosition().y);
                System.out.println("Landed in " + i + " seconds, with velocity: " + henry3rd.getVelocity() + "\n");
                break;
            }
            henry3rd.iterate(target, slowingDownForce, timeStep);
        }
    }



}
