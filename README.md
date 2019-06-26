# Quick documentation 

Current version available at: https://gitlab.com/titanmission/titan-mission

The demonstration can be run by:

```
javac -jar ./jar/TitanMission.jar
```

The other way to run the code is to compile it. The code requires Java 1.8 SDK.

The landing experiments can be run by running LandingTest.java.
The gradient descent of Hohmann transfer can be run by running HohmannTest.java.
The unit tests are located in tests directory and require classes from src directory.

All tests require providing a path to the resources directory.

Example command needed for compilation on Ubuntu 17.10 with InteliJ IDEA:

```
/usr/lib/jvm/java-8-openjdk-amd64/bin/java -javaagent:/snap/intellij-idea-ultimate/148/lib/idea_rt.jar=34175:/snap/intellij-idea-ultimate/148/bin -Dfile.encoding=UTF-8 -classpath /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/charsets.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/cldrdata.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/dnsns.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/icedtea-sound.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/jaccess.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/java-atk-wrapper.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/localedata.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/nashorn.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/sunec.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/sunjce_provider.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/sunpkcs11.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/zipfs.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/jce.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/jsse.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/management-agent.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/resources.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/jfxrt.jar:/home/isinlor/Projects/TitanMission/out/production/TitanMission LandingTest -enableassertions
```

We recommend using IntelliJ.

##### In other words: copy & paste + comments
Report link: (https://www.overleaf.com/6691112276qxmsgdtmcbtj)

Our current model is based on finite-differences naive O(n^2) n-body simulation. 

In mathematics, [finite-difference methods](https://en.wikipedia.org/wiki/Finite_difference_method) (FDM) are numerical methods for solving differential equations by approximating them with difference equations, in which finite differences approximate the derivatives.

As I understand it, instead of taking full limit of a difference quotient as difference approaches 0, we just take some small enough value.

In physics and astronomy, an [N-body simulation](https://en.wikipedia.org/wiki/N-body_simulation) is a simulation of a dynamical system of particles, usually under the influence of physical forces, such as gravity (see n-body problem). N-body simulations are widely used tools in astrophysics, from investigating the dynamics of few-body systems like the Earth-Moon-Sun system to understanding the evolution of the large-scale structure of the universe.

[There are two main approaches](http://www-inf.telecom-sudparis.eu/COURS/CSC5001/new_site/Supports/Projet/NBody/sujet.php):

- Brute-force is a naive algorithm O(n^2) that consists, for each time step, in computing the gravitational force between each pair of particle. For simulating Solar system with ~100 particles is perfectly doable.
- Barnes-Hut is another algorithm with a lower time complexity O(n*log(n)). Particles are stored in a tree based on their position and groups of particles can be viewed as one big particle in order to reduce the computation cost.

In order to simulate Solar system we need initial conditions for the simulation. This means we need a list of bodies (planets and moons) with their respective masses and vectors of position and velocity.

We can obtain this data from [NASA JPL](https://ssd.jpl.nasa.gov/?ephemerides). They provide the necessary data and [equations](https://ssd.jpl.nasa.gov/txt/aprx_pos_planets.pdf) to do the computations yourself. However, it overall it looks quite complex. They also provide an interface for obtaining already computed [ephemerides](https://ssd.jpl.nasa.gov/horizons.cgi#results). We have found also a [Java class](https://github.com/jmrozanec/nasa-horizons-rest/blob/master/src/main/java/com/neo/horizon/HorizonCoordinateRetriever.java) to read this data.

As mentioned, we can use NASA ephemerides for computing initial conditions that we could feed into our engine. This should be relatively simple conversion of vectors in polar coordinates to Cartesian.

Having this data in discrete steps allows us to also validate the simulation. In case our simulation will not work as expected we will need to investigate the source of issues. In case it will be some inherent issue of n-body simulation we could replace simulation of planets and moons with ephemerides. While it would fix issue of simulating Solar system we would still need to simulate spacecraft trajectory.

Other [Wikipedia comment on few-body simulation](https://en.wikipedia.org/wiki/N-body_problem):

> For a small number of bodies, an n-body problem can be solved using direct methods, also called particle–particle methods. These methods numerically integrate the differential equations of motion. Numerical integration for this problem can be a challenge for several reasons. 
>
> First, the gravitational potential is singular; it goes to infinity as the distance between two particles goes to zero. The gravitational potential may be softened to remove the singularity at small distances:
>
> Second, in general for n > 2, the n-body problem is chaotic, which means that even small errors in integration may grow exponentially in time. 
>
> Third, a simulation may be over large stretches of model time (e.g. millions of years) and numerical errors accumulate as integration time increases.
>
>There are a number of techniques to reduce errors in numerical integration. Local coordinate systems are used to deal with widely differing scales in some problems, for example an Earth–Moon coordinate system in the context of a solar system simulation. Variational methods and perturbation theory can yield approximate analytic trajectories upon which the numerical integration can be a correction. The use of a symplectic integrator ensures that the simulation obeys Hamilton's equations to a high degree of accuracy and in particular that energy is conserved.  

We have also paper ["On the Reliability of N-body Simulations"](https://arxiv.org/pdf/1411.6671.pdf).
> This research for the 3-body problem supports the assumption that results from conventional N-body simulations are valid in a statistical sense. (...) An example of a higher-N system where precision might play a role is  a  young  star  cluster  (without  gas)  going  through the  process  of  cold  collapse.  At  the  moment  of deepest  collapse,  a  fraction  of  stars  will  obtain  large accelerations, so that a small error in the acceleration can cause large errors in the position and velocity.

To summarise:

- issue of singularities; should not occur in Solar system unless we want to collide particles
- n-body problem is chaotic; this is a possible issue, to be validated with NASA data
- numerical errors accumulate as integration time increases; we will do simulation on scale of year and Wikipedia mentions millions of years; to be validated with NASA data
- a small error in the acceleration can cause large errors in the position and velocity; bodies in Solar system should not obtain relatively high accelerations; again  to be validated with NASA data

## Other approaches

Wikipedia on [n-body problem](https://en.wikipedia.org/wiki/N-body_problem) mentions:

> Hamilton's equations show that the n-body problem is a system of 6n first-order differential equations, with 6n initial conditions as 3n initial position coordinates and 3n initial momentum values. 

BTW - Is this what they mean by "Make a mathematical model of the solar, planetary and lunar orbits and the spacecraft motion"?

Wikipedia also mentions that:

> In the physical literature about the n-body problem (n ≥ 3), sometimes reference is made to the impossibility of solving the n-body problem (via employing the above approach).[citation needed] However, care must be taken when discussing the 'impossibility' of a solution, as this refers only to the method of first integrals (compare the theorems by Abel and Galois about the impossibility of solving algebraic equations of degree five or higher by means of formulas only involving roots). 

Apparently one option is to approach the n-body problem by Taylor series.

Hamilton's equations look scary enough and Taylor series approximation does not make it conceptually any simpler.

