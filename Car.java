import java.lang.StringBuilder;
import java.util.Random;

public class Car {
    static final double ACCELERATION = 11.5; //ft/s
    static final double DEFAULT_SPEED = 95.3333; //65 mph in ft/s
    static final double CAR_LENGTH = 15.0;
    static final double MERGE_TIME = 0.5;
    static final double MEAN_TOLL_TIME = 15.0;

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
    private double payTollTime;
    private int boothPaidAt;

    public Car(boolean hasPass, int num, double start) {
        this.hasEZPass = hasPass;
        this.position = 0;
        this.time = 0;
        this.number = num;
        this.startTime = start;
        this.lane = Road.lanes[this.chooseLane()];
        this.lane.enterLane(this);
        this.payTollTime = this.getGaussian(MEAN_TOLL_TIME, 3);
        this.aggressiveness = this.getGaussian(1, .1);
        this.speed = DEFAULT_SPEED * this.aggressiveness;
        this.deceleration = Math.pow(this.speed, 2)/(2.0 * TollSimulator.DISTANCE_TO_PLAZA);
    }

    /**
     * @return random value taken from a normal(mean,sigma)
     */
    private double getGaussian(double mean, double sigma) {
        Random r = new Random();
        double norm = r.nextGaussian();
        return (norm*sigma) + mean;
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

    public double getAggressiveness() {
        return this.aggressiveness;
    }

    public double getStartTime() {
        return this.startTime;
    }

    public double getAtBoothTime() {
        return this.atBoothTime;
    }

    public double getLeaveBoothTime() {
        return this.leaveBoothTime;
    }

    public boolean getHasEZPass() {
        return this.hasEZPass;
    }

    public double getPayTollTime() {
        return this.payTollTime;
    }

    public int getBoothPaidAt() {
        return this.boothPaidAt;
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
            //i.e. at toll plaza
            this.timeAtPlaza += TollSimulator.TIME_STEP;
            if (this.timeAtPlaza >= this.payTollTime) {
                this.boothPaidAt = this.lane.getNumber();
                this.lane.setMoving(true);
                this.position += 0.1; //so that if statement fails
                this.leaveBoothTime = this.time;
                this.lane.leave(); 
            }
        } else if (!this.lane.forEZPass() && this.position == TollSimulator.DISTANCE_TO_PLAZA) {
            int lenHere = this.lane.getLength();
            for (Lane l: Road.lanes) {
                if (!l.forEZPass() && l.getLength() == 2 + lenHere) {
                    System.out.println("In here");
                    this.changeLane(l);
                }
            }
        } else if (!this.lane.forEZPass() && this.position > TollSimulator.DISTANCE_TO_PLAZA) {
            //starting to speed back up again
            if (this.speed < DEFAULT_SPEED * this.aggressiveness) {
                this.speed += this.aggressiveness * ACCELERATION * TollSimulator.TIME_STEP;
            }
            int laneToLeft = this.lane.getNumber() - 1;    
            // want to merge into lane 1 when possible
            double dist = TollSimulator.DISTANCE_TO_PLAZA;
            if (this.position - dist > 10) {
                if (this.position - dist <= 30) {
                    if (Road.lanes[laneToLeft].carInRange(dist + .05, this.position) == null) {
                        this.changeLane(Road.lanes[laneToLeft]);
                    } 
                } else {
                    Car cr = Road.lanes[laneToLeft].closestBehind(this.position);
                    if (cr != null && this.position >= this.distanceRequired(cr)) {
                        this.changeLane(Road.lanes[laneToLeft]);
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
    private double distanceRequired(Car c) {
        return (2 - this.aggressiveness) * c.getSpeed() * 2;
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
