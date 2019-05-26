package EffectSystem;

import Simulation.Force;

import java.util.Random;

public class Wind extends Force{

    private Force wind;

    public Wind(){
        this.wind=getRandomWind();
    }

    public void setWind(Force wind) {
        this.wind = wind;
    }

    public Force getRandomWind(){
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
        return wind;
    }

    public void getStrongWInd(){
        Random generator=new Random();
        x = generator.nextInt(75);
        y = generator.nextInt(75);
        Force wind = new Force(x,y);
        setWind(wind);
    }

    public void changeWind(){

        double xDelta=wind.x*getPercentage();
        double yDelta=wind.y*getPercentage();
        //double zDelta=wind.z*getPercentage();

        double xNew=wind.x+xDelta;
        double yNew=wind.y+yDelta;
        //double zNew=wind.z+zDelta;

        if (Math.random()<0.5&&wind.x>1)
            xNew=xNew-2*xDelta;
        if (Math.random()<0.5&&wind.y>1)
            yNew=yNew-2*yDelta;
        //if (Math.random()<0.5)
            //zNew=zNew-2*zDelta;

        wind.x=xNew;
        wind.y=yNew;
    }

    public void decreaseWind(){
        double xDelta=wind.x*getPercentage();
        double yDelta=wind.y*getPercentage();
        //double zDelta=wind.z*getPercentage();

        double xNew=wind.x+xDelta;
        double yNew=wind.y+yDelta;
        //double zNew=wind.z+zDelta;

        if (Math.random()<0.8&&wind.x>1)
            xNew=xNew-2*xDelta;
        if (Math.random()<0.8&&wind.y>1)
            yNew=yNew-2*yDelta;
        //if (Math.random()<0.5)
        //zNew=zNew-2*zDelta;

        wind.x=xNew;
        wind.y=yNew;
    }

    public void keepWindStrong(){
        double xDelta=wind.x*getPercentage();
        double yDelta=wind.y*getPercentage();

        double xNew=wind.x+xDelta;
        double yNew=wind.y+yDelta;

        if (Math.random()<0.3&&wind.x>1)
            xNew=xNew-2*xDelta;
        if (Math.random()<0.3&&wind.y>1)
            yNew=yNew-2*yDelta;

        wind.x=xNew;
        wind.y=yNew;
    }

    public double getPercentage(){
        Random random=new Random();
        double p=0;
        while (p==0||p>0.05){
            p=random.nextDouble();
        }
        return p;
    }

    public Force getWind() {
        return wind;
    }
}
