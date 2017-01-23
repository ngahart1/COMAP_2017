import java.lang.StringBuilder;

public class Car {
    static final int PAY_TOLL_TIME = 15; //takes 15 seconds to pay cash toll
    static final double ACCELERATION = 11.5; //ft/s
    static final double DEFAULT_SPEED = 88.0; //60 mph in ft/s
    static final double CAR_LENGTH = 15.0;
    static final double DECELERATION = Math.pow(DEFAULT_SPEED, 2)/(2*TollSimulator.DISTANCE_TO_PLAZA);

    private int number;
    private Lane lane;
    private double position;
    private double speed;
    private boolean hasEZPass;
    private double time;
    private double timeAtPlaza;
    private int boothSelected;
    private double startTime;
    private double atBoothTime;
    private double leaveBoothTime;


    public Car(boolean hasPass, int num, int start) {
        this.hasEZPass = hasPass;
        this.position = 0;
        this.time = 0;
        this.speed = DEFAULT_SPEED;
        this.number = num;
        this.startTime = start;
        this.lane = Road.lanes[this.chooseLane()]; 
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

    /**Prints vital information in csv-format for ease of data analysis.*/
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.number + ",");
        s.append(this.startTime + ",");
        s.append(this.atBoothTime + ",");
        s.append(this.leaveBoothTime + ",");
        s.append(this.time + ",");
        s.append(this.hasEZPass + ",");
        s.append(this.boothSelected);
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
            if (this.speed < 60) {
                this.speed += ACCELERATION * TollSimulator.TIME_STEP;
            }
        } else if (!this.lane.forEZPass() && this.position < TollSimulator.DISTANCE_TO_PLAZA) {
            speed -= DECELERATION;
            if (this.speed < 0) {
                this.position = TollSimulator.DISTANCE_TO_PLAZA;
                this.speed = 0;
                this.lane.enter(this);
                this.atBoothTime = this.time;
            }
        }
        this.time += TollSimulator.TIME_STEP;
        this.position += this.speed * TollSimulator.TIME_STEP;
    } 
    
    /*private double deceleration() {
        int carsAhead = this.lane.getLength();
        double distanceUntilStop = TollSimulator.DISTANCE_UNTIL_PLAZA - 15.0*carsAhead;
        // use formula: v_f^2 = v_o^2 + 2*a*d
        return Math.pow(this.speed, 2) / (2.0 * distanceUntilStop);
    }*/

    public void changeLane(Lane l) {
        this.lane = l;
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
