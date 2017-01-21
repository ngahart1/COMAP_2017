import java.util.Random;

public class Car {
    private static final int PAY_TOLL_TIME = 10; //takes 10 seconds to pay toll

    private Lane lane;
    private double position;
    private double speed;
    private boolean hasEZPass;
    private int time;

    public Car(boolean hasPass) {
        this.hasEZPass = hasPass;
    }

    /** The catch-all method for the car continuing through the process
     *  Will allow car to enter toll plaza area, pay toll, merge, etc.
     *  If distance = 0 (i.e. not going anywhere), will choose a lane
     *  Will proceed down the lane assuming it can travel
     *  Will slow down as necessary
     *  Will enter queue as necessary
     *  Will speed back up, then depart
     */
    public act() {
        if (this.position == 0) {
            int lane_number = this.chooseLane();
        }
    
    public void changeLane(Lane l) {
        this.lane = l;
    }

    /** Method to choose lane at beginning of approach */
    public int chooseLane() {
        if (this.hasEZPass) {
            lane = 0;
        } else {
            // choose randomly between lane 1 and 2
            lane = (int)(Math.random() * 2) + 1;
        }
        return lane;
    }


