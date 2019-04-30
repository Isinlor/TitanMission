public class OpenLoopController {

    final private Vector distanceFromSurfaceAtStart = new Vector(0, 6e5);
    final private Force zeroForce = new Force(0,0);

    private Force slowingDownForce;
    private Body target;
    private double timetoRunthrough;
    private double timeStep;
    private int IMPULSE_AT_END_TIME = 1095641;
    private int TIME_TO_DEPLOY_PARACHUTE = 5000;

    OpenLoopController(Force slowingDown, Body target, double timetoRunthrough, double timeStep){
        this.slowingDownForce = slowingDown;
        this.target = target;
        this.timetoRunthrough = timetoRunthrough;
        this.timeStep = timeStep;
    }

    public void simulateFreeFall(Spaceship henry3rd) {

        System.out.println("Position is: " + henry3rd.getPosition());
        for (int i=0; i < timetoRunthrough; i++) {
            if ((int)henry3rd.getPosition().y <= 0){
                System.out.println("Position is: " + (int)henry3rd.getPosition().y);
                System.out.println("Landed in " + i + " seconds, with velocity: " + henry3rd.getVelocity() + "\n");
                break;
            }
            henry3rd.iterate(target, timeStep);
        }

    }

    public void simulateFreeFallWithImpulseAtTheEnd(Spaceship henry3rd) {

        System.out.println("Position is: " + henry3rd.getPosition());
        for (int i = 0; i < timetoRunthrough; i++) {

            if (i >= IMPULSE_AT_END_TIME) {
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

        //here we can put almost exactly the same amount of opposing force but still land at around 520m/s
        slowingDownForce = new Force(henry3rd.computeAttraction(target).product(-0.999999));
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

    public void simulateParachuteAndThenImpulseAtTheEnd(Spaceship henry3rd){

        slowingDownForce = new Force(0,0,0);
        System.out.println("Position is: " + henry3rd.getPosition());
        for (int i = 0; i < timetoRunthrough; i++) {
            //deploy parachute at 200km above target, reduces the gravitational force
            if (i <= TIME_TO_DEPLOY_PARACHUTE) {
                slowingDownForce = new Force(henry3rd.computeAttraction(target).product(-0.1));
            }

            //start impulse to slow down fast
            if (i <= IMPULSE_AT_END_TIME) {
                slowingDownForce = slowingDownForce.sum(new Force(henry3rd.computeAttraction(target).product(-0.000001202)));
            }

            if ((int) henry3rd.getPosition().y <= 0) {
                System.out.println("Position is: " + (int)henry3rd.getPosition().y);
                System.out.println("Landed in " + i + " seconds, with velocity: " + henry3rd.getVelocity() + "\n");
                break;
            }

            henry3rd.iterate(target, slowingDownForce, timeStep);

        }
    }
}
