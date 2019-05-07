package vector;

/** Represents a vector in the 2 dimensional-plane
 * @author Lucas Engelmann
 * @version 1.0
 * @since 1.0
 */
public class Vector2D {

    /**
     * x coordinate stored as double
     */
    public double x;
    /**
     * y coordinate stored as double
     */
    public double y;

    /**
     * initialize 2D vector with x and y coordinate
     * @param x vectors x component
     * @param y vectors y component
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * initialize 2D vector with x and y coordinate as 0
     */
    public Vector2D() {
        this.x = 0;
        this.y = 0;
    }

    public String toString() {
        return x + " " + y;
    }
}
