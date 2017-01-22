/**A class to model a lane object
 * Member variables: 
 *  queue, a Queue object containing a list of all cars
 *      in line at the time
 *  number, a signifier (e.g. left-most lane is lane 0, then to
 *      its right is lane 1, etc.)
 *  moving, a boolean that is true iff the line is moving (because
 *      a car just finished paying and is leaving)
 *  length, the number of cars in the queue
 * Methods:
 *  enter, when a car enters the back of the queue
 *  leave, when a car proceeds to the end of the toll booth and leaves
 *  getNumber, setNumber, getMoving, setMoving, getLength
*/

import java.util.LinkedList;

public class Lane {

    private LinkedList<Car> queue;
    private int number;
    private boolean moving;
    private int length;
    private boolean forEZPass;

    public Lane(int lane_num) {
        this.queue = new LinkedList<Car>();
        this.number = lane_num;
        this.length = 0;
        this.moving = true;
        this.forEZPass = true; //arbitrary
    }

    public Lane(int lane_num, boolean pass) {
        this(lane_num);
        this.forEZPass = pass;
    }

    public void enter(Car car) {
        this.queue.addLast(car);
        this.length = this.length + 1;
    }

    public Car leave() {
        this.length = this.length - 1;
        return this.queue.pollFirst();
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int new_num) {
        this.number = new_num;
    }

    public boolean getMoving() {
        return this.moving;
    }

    public void setMoving(boolean is_moving) {
        this.moving = is_moving;
    }

    public int getLength() {
        return this.length;
    }

    public boolean forEZPass() {
        return this.forEZPass;
    }
}
