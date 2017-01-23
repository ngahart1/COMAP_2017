import java.util.ArrayList;
import java.util.Iterator;

public final class TollSimulator {
    final static int SIMULATION_TIME = 100000;
    final static int TIME_STEP = 1;
    final static int DISTANCE_TO_PLAZA = 500;
    final static int TOTAL_DISTANCE = 1500;
    static Road road;

    public static Road setupRoad() {
        Road r = new Road(3);
        r.insert(0, new Lane(0, true));
        r.insert(1, new Lane(1, false));
        r.insert(2, new Lane(2, false));
        return r;
    }

    /** A method to get a random poisson variable.
     * @param probInASecond the probability of a car entering
     * the toll area in any second.
     * @return a Poisson random variable, with appropriate lambda
     * parameter.
     */
    private static int getPoisson(double probInASecond) {
        int numberOfExponentials = 0;
	    double Current_Product = 1;
	    double lambda = probInASecond*TIME_STEP;
	    double threshold = Math.exp(-lambda);
        while(Current_Product >= threshold){
            numberOfExponentials += 1;
            Current_Product *= Math.random();
        }
        int Poisson_Value = numberOfExponentials;
        return Poisson_Value;
    }

    public static void main(String[] args) {
        System.out.print("CAR_NUMBER,START_TIME,ENTER_QUEUE_TIME,");
        System.out.println("LEAVE_TOLL_TIME,TOTAL_TIME,HAS_EZ_PASS,BOOTH_SELECTED");
        ArrayList<Car> cars = new ArrayList<Car>();
        road = setupRoad();
        int carNumber = 0;
        for (int t = 0; t < SIMULATION_TIME; t += TIME_STEP) {
            int numCarsToAdd = getPoisson(.25);
            //for (int n = 0; n < numCarsToAdd; n++) {    
            if (Math.random() > .75) {
                cars.add(new Car(Math.random() > 0.5, carNumber++, t));
            }
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
