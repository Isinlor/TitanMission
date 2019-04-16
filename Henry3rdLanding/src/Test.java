public class Test {

    public static void main(String[] args) {

        double auToM = 1.496e11;
        double dayToSecond = 1.0 / 86400.0;

        Body titan=new Body(
                "Titan",
                new Vector(2.340010336367727E+00, -9.765469982604248E+00, 7.300390210773734E-02).product(auToM),
                new Vector(1.947323954493107E-03, 1.729638118992622E-03, -1.417867687071718E-04).product(auToM*dayToSecond),
                134.5e21
        );

        double titanRadius=2574.7;
        Vector positionTitan=new Vector(0,0,0);
        Vector position=new Vector(positionTitan.x,positionTitan.y,positionTitan.z);
        position.x = positionTitan.x+600+titanRadius;
        System.out.println(position);
        System.out.println(positionTitan);
        Vector velocity=new Vector(-2,0,0);
        Spaceship henry3rd=new Spaceship("henry3rd",position,velocity,500,new Force(0,0,0),titan);

        System.out.println(henry3rd.getPosition().euclideanDistance(positionTitan));
        for (int i=0; i<60*24;i++){
            if (henry3rd.getPosition().euclideanDistance(positionTitan)<titanRadius){
                System.out.println("Landed"+i + henry3rd.getVelocity());
            }
            henry3rd.iterate(1);
        }
        /*
        Second coordinate system
         */
    }


}
