package vector;

/** Represents a vector in the 2 dimensional-plane.
 * @author Lucas Engelmann
 */
public class Vector2D {

    /**
     * X coordinate stored as double.
     */
    public double x;
    /**
     * Y coordinate stored as double.
     */
    public double y;

    /**
     * Initialize 2D vector with x and y coordinate.
     * @param x vectors x component
     * @param y vectors y component
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Initialize 2D vector with x and y coordinate as 0.
     */
    public Vector2D() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Calculates the distance between two vectors
     * @param to vector to measure the distance to
     * @return distance between both vectors
     */
    public double dist(Vector2D to) {
        return Math.sqrt((to.x - this.x) * (to.x - this.x) + (to.y - this.y) * (to.y - this.y));
    }
}
