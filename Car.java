import java.lang.StringBuilder;

public class Car {
    private static final int PAY_TOLL_TIME = 10; //takes 10 seconds to pay toll
    private static final int DISTANCE_TO_PLAZA = 1000;
    private static final int TIME_STEP = 1; //simulation constant
    private static final int ACCELERATION = 11.5; //ft/s
    private static final double DEFAULT_SPEED = 88.0; //60 mph in ft/s

    private int number;
    private Lane lane;
    private double position;
    private double speed;
    private boolean hasEZPass;
    private int time;
    private int timeAtPlaza;
    private int boothSelected;
    private int startTime;

    public Car(boolean hasPass, int num, int start) {
        this.hasEZPass = hasPass;
        this.position = 0;
        this.time = 0;
        this.speed = DEFAULT_SPEED;
        this.number = num;
        this.startTime = start;
    }

    public int getNumber() {
        return this.number;
    }

    public int getTime() {
        return this.time;
    }

    public int boothSelected() {
        return this.boothSelected;
    }

    /**Prints vital information in csv-format for ease of data analysis.*/
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.number + ',');
        s.append(this.startTime + ','); 
        s.append(this.time + ',');
        s.append(this.hasEZPass + ',');
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
    public act() {
        // Instead, will choose lane in main
        /*if (this.position == 0) {
            int lane_number = this.chooseLane();
        }*/
        if (!this.lane.forEZPass && this.position == DISTANCE_TO_PLAZA) {
            //i.e. at tollbooth
            this.timeAtPlaza += TIME_STEP;
            if (this.timeAtPlaza == PAY_TOLL_TIME) {
                this.lane.setMoving(true);
                this.position += 0.1; //so that if statement fails
            }
        } else if (!this.lane.forEZPass && this.position > DISTANCE_TO_PLAZA) {
            //starting to speed back up again
            if (speed < 60) {
                speed += ACCELERATION * TIME_STEP;
            }
        } else if (!this.lane.forEZPass && this.position < DISTANCE_TO_PLAZA) {

        }
        // the other condition is obviously if 
        this.time += TIME_STEP;
        this.position += this.speed * TIME_STEP;
    } 
    
    public void changeLane(Lane l) {
        this.lane = l;
    }

    /** Method to choose lane at beginning of approach */
    public int chooseLane() {
        int lane_num;
        if (this.hasEZPass) {
            lane_num = lanes[0]; //the leftmost, EZPass lane
        } else {
            lane_num = this.shorter(); 
        }
        this.boothSelected = lane_num;
        return lane_num;
    }

    /** Returns either lane 1 or 2, whichever has the shorter line.
     * Choose randomly in event of tie.
     */
    public Lane shorter() {
        if (lanes[1].length() > lanes[2].length()) {
            return lanes[1];
        } else if (lanes[1].length() < lanes[2].length()) {
            return lanes[2]'
        } else {
            return (int) (Math.random()*2) + 1;
        }
    }




    
