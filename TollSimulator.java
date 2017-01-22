import java.util.ArrayList;

public final class TollSimulator {
    final int SIMULATION_TIME = 1000;
    final int TIME_STEP = 1;
    final String filename = "Simulation.csv";


    public void print_vitals(Car c) {
        System.out.print(
    
    public static void main(String[] args) {
        System.out.println("CAR_NUMBER,START_TIME,TOTAL_TIME,HAS_EZ_PASS,BOOTH_SELECTED");
        ArrayList<Car> cars = new ArrayList<Car>();
        int carNumber = 0;
        for (int t = 0; t < SIMULATION_TIME; t += TIME_STEP) {
            // one new car created each time step
            cars.add(new Car(Math.random() > 0.5), carNumber++, t);
            for (Car c: cars) {
                c.act();
                if (c.distance > TOTAL_DISTANCE) {
                    printVitals(c);
                    cars.remove(c);
                }
            }
        }
    }

            


