import java.lang.StringBuilder;
import java.util.Random;

public class Car {
    static final double PAY_TOLL_TIME = 15.0; //takes 15 seconds to pay cash toll
    static final double ACCELERATION = 11.5; //ft/s
    static final double DEFAULT_SPEED = 95.3333; //65 mph in ft/s
    static final double CAR_LENGTH = 15.0;
    static final double MERGE_TIME = 1.0;

    private int number;
    private int boothSelected;
    private Lane lane;
    private double position;
    private double speed;
    private boolean hasEZPass;
    private double time;
    private double timeAtPlaza;
    private double startTime;
    private double atBoothTime;
    private double leaveBoothTime;
    private double aggressiveness;
    private double deceleration;

    public Car(boolean hasPass, int num, double start) {
        this.hasEZPass = hasPass;
        this.position = 0;
        this.time = 0;
        this.number = num;
        this.startTime = start;
        this.lane = Road.lanes[this.chooseLane()];
        this.lane.enterLane(this);
        this.aggressiveness = this.setAggressiveness();
        this.speed = DEFAULT_SPEED * this.aggressiveness;
        this.deceleration = Math.pow(this.speed, 2)/(2.0 * TollSimulator.DISTANCE_TO_PLAZA);
    }

    /** Determines the aggressiveness factor for a car.
     * Assume aggressiveness is distributed as a normal,
     * with mean of 1 and st. dev of .1. A driver one standard
     * deviation above mean is 10% more aggressive than average,
     * etc.
     * @return random value taken from a normal(1,.1)
     */
    private double setAggressiveness() {
        Random r = new Random();
        double norm = r.nextGaussian();
        return (norm*.1) + 1;
    }

    public int getNumber() {
        return this.number;
    }

    public double getTime() {
        return this.time;
    }

    public int boothSelected() {
        return this.boothSelected;
    }

    public double getPosition() {
        return this.position;
    }

    public Lane getLane() {
        return this.lane;
    }

    public double getSpeed() {
        return this.speed;
    }

    /**Prints vital information in csv-format for ease of data analysis.*/
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.number + ",");
        s.append(this.aggressiveness + ",");
        s.append(this.startTime + ",");
        s.append(this.atBoothTime + ",");
        s.append(this.leaveBoothTime + ",");
        s.append(this.time + ",");
        s.append(this.hasEZPass + ",");
        s.append(this.boothSelected + ",");
        s.append(this.lane.getNumber());
        return s.toString();
    }

    /** The catch-all method for the car continuing through the process
     *  Will allow car to enter toll plaza area, pay toll, merge, etc.
     *  If distance = 0 (i.e. not going anywhere), will choose a lane
     *  Will proceed down the lane assuming it can travel
     *  Will slow down as necessary
     *  Will enter queue as necessary
     *  Will speed back up, then depart
     */
    public void act() {
        if (!this.lane.forEZPass() && this.lane.firstInLine(this)) {
            //i.e. at tollbooth
            this.timeAtPlaza += TollSimulator.TIME_STEP;
            if (this.timeAtPlaza == PAY_TOLL_TIME) {
                this.lane.setMoving(true);
                this.position += 0.1; //so that if statement fails
                this.leaveBoothTime = this.time;
                this.lane.leave(); 
            }
        } else if (!this.lane.forEZPass() && this.position > TollSimulator.DISTANCE_TO_PLAZA) {
            //starting to speed back up again
            if (this.speed < DEFAULT_SPEED * this.aggressiveness) {
                this.speed += this.aggressiveness * ACCELERATION * TollSimulator.TIME_STEP;
            }
            if (this.lane.getNumber() == 2) {
                // want to merge into lane 1 when possible
                double dist = TollSimulator.DISTANCE_TO_PLAZA;
                if (this.position - dist > 10) {
                    if (this.position - dist <= 30) {
                        if (Road.lanes[1].carInRange(dist + .05, this.position) == null) {
                            this.changeLane(Road.lanes[1]);
                        } else {
                            Car cr = Road.lanes[1].carInRange(this.position - this.distanceRequired(), this.position + 20);
                            if (cr == null) {
                                this.changeLane(Road.lanes[1]);
                            }
                        }
                    }
                }
            }
        } else if (!this.lane.forEZPass() && this.position < TollSimulator.DISTANCE_TO_PLAZA) {
            speed -= this.deceleration;
            if (this.speed < 0.0) {
                this.position = TollSimulator.DISTANCE_TO_PLAZA;
                this.speed = 0.0;
                this.lane.enter(this);
                this.atBoothTime = this.time;
            }
        } else if (this.lane.forEZPass()) {
           Car cr = this.lane.carInRange(this.position, this.position + 2*CAR_LENGTH);
           if (cr != null) {
               // i.e. there is a car very close ahead of you
               this.speed = cr.getSpeed();
           }
        }
        this.time += TollSimulator.TIME_STEP;
        this.position += this.speed * TollSimulator.TIME_STEP;
    } 

    /** How far ahead of car in intended merge lane you must be, given the
     * "two second rule" and assuming your speed is their speed
     * @return the distance required between you and next car in order to
     * change lanes, based on your speed.
     */
    private double distanceRequired() {
        return (2 - this.aggressiveness) * this.speed * 2;
    }

    /*private double deceleration() {
        int carsAhead = this.lane.getLength();
        double distanceUntilStop = TollSimulator.DISTANCE_UNTIL_PLAZA - 15.0*carsAhead;
        // use formula: v_f^2 = v_o^2 + 2*a*d
        return Math.pow(this.speed, 2) / (2.0 * distanceUntilStop);
    }*/

    public void changeLane(Lane l) {
        this.lane.leaveLane(this);
        this.lane = l;
        this.lane.enterLane(this);
        this.time += MERGE_TIME;
    }

    /** Method to choose lane at beginning of approach */
    public int chooseLane() {
        int lane_num;
        if (this.hasEZPass) {
            lane_num = Road.lanes[0].getNumber(); //the leftmost, EZPass lane
        } else {
            lane_num = this.shorter().getNumber(); 
        }
        this.boothSelected = lane_num;
        return lane_num;
    }

    /** Returns either lane 1 or 2, whichever has the shorter line.
     * Choose randomly in event of tie.
     */
    public Lane shorter() {
        if (Road.lanes[1].getLength() < Road.lanes[2].getLength()) {
            return Road.lanes[1];
        } else if (Road.lanes[2].getLength() < Road.lanes[1].getLength()) {
            return Road.lanes[2];
        } else {
            return Road.lanes[(int) (Math.random()*2) + 1];
        }
    }
}
