class EulerODE implements ODESolver {
    public void iterate(Bodies bodies, double time) {
        bodies.iterate(time);
    }
}
