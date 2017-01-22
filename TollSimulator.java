import java.util.ArrayList;

public final class TollSimulator {
    final static int SIMULATION_TIME = 1000;
    final static int TIME_STEP = 1;
    final static int TOTAL_DISTANCE = 2000;

    public Road setupRoad() {
        Road r = new Road(3);
        for (int i = 0; i < 3; i++) {
            r.insert(i, new Lane(i));
        }
        return r;
    }

    public static void main(String[] args) {
        System.out.println("CAR_NUMBER,START_TIME,TOTAL_TIME,HAS_EZ_PASS,BOOTH_SELECTED");
        ArrayList<Car> cars = new ArrayList<Car>();
        int carNumber = 0;
        for (int t = 0; t < SIMULATION_TIME; t += TIME_STEP) {
            // one new car created each time step
            cars.add(new Car(Math.random() > 0.5, carNumber++, t));
            for (Car c: cars) {
                if (c != null) {
                    c.act();
                    if (c.getPosition() > TOTAL_DISTANCE) {
                        System.out.println(c);
                        cars.remove(c);
                    }
                }
            }
        }
    }
}

            


