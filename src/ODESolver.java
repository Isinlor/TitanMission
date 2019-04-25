public interface ODESolver {
    void iterate(Bodies bodies, double time);
    String getName();
}
