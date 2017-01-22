public class LaneTest {
    public static void main(String[] args) {
        Lane a = new Lane(0);
        System.out.println("lane a moving status: " + a.getMoving());
        changelane(a);
        System.out.println("lane a moving status: " + a.getMoving());
    }

    public static void changelane(Lane l) {
        l.setMoving(false);
    }
}
