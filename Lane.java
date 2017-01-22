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

public class Lane {

    private PriorityQueue queue;
    private int number;
    private boolean moving;
    private int length;

    public Lane(lane_num) {
        this.queue = new PriorityQueue<Car>() 
        this.number = lane_num
        this.length = 0
        this.moving = True
    }

    public void enter(Car car) {
        this.queue.add(car)
    }

    public Car leave() {
        car_out = this.queue.poll()
    }

    public int getNumber() {
        return this.number
    }

    public void setNumber(new_num) {
        this.number = new_num
    }

    public boolean getMoving() {
        return this.moving
    }

    public void setMoving(is_moving) {
        this.moving = is_moving
    }

    public int getLength() {
        return this.length
    } 
}
