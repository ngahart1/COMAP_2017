import java.util.ArrayList;
import java.util.Iterator;

public final class TollSimulator {
    final static int SIMULATION_TIME = 1000;
    final static int TIME_STEP = 1;
    final static int DISTANCE_TO_PLAZA = 1000;
    final static int TOTAL_DISTANCE = 2000;
    static Road road;

    public static Road setupRoad() {
        Road r = new Road(3);
        for (int i = 0; i < 3; i++) {
            r.insert(i, new Lane(i));
        }
        return r;
    }

    /** A method to get a random poisson variable.
     * @param probInASecond the probability of a car entering
     * the toll area in any second.
     * @return a Poisson random variable, with appropriate lambda
     * parameter.
     */
    /*private static int getPoisson(double probInASecond) {
        int numberOfExponentials = 0;
	    for (int i = 0; i < SIMULATION_TIME; i += TIME_STEP){
		    double Current_Sum = 0;
		    while(Current_Sum < 1){
			    numberOfExponentials += 1;
			    Current_Sum += Math.random();
		    }
		    int Poisson_Value = numberOfExponentials;
	    }
        return Poisson_Value;
    }*/

    public static void main(String[] args) {
        System.out.println("CAR_NUMBER,START_TIME,TOTAL_TIME,HAS_EZ_PASS,BOOTH_SELECTED");
        ArrayList<Car> cars = new ArrayList<Car>();
        road = setupRoad();
        int carNumber = 0;
        for (int t = 0; t < SIMULATION_TIME; t += TIME_STEP) {
            // one new car created each time step
            cars.add(new Car(Math.random() > 0.5, carNumber++, t));
            Iterator<Car> iter = cars.iterator();
            while (iter.hasNext()) {
                Car c = iter.next();
                if (c != null) {
                    c.act();
                    if (c.getPosition() > TOTAL_DISTANCE) {
                        System.out.println(c);
                        iter.remove();
                    }
                }
            }
        }
    }
}

            


