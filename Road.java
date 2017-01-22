public class Road {
    static Lane[] lanes;

    public Road(int n) {
        lanes = new Lane[n];
    }

    public void insert(int n, Lane l) {
        lanes[n] = l;
    }

}
