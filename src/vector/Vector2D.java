package vector;

/**
 * 2 dimensional vector used as a structure (attributes are all public)
 */
public class Vector2D {

    /**
     * x and y coordinate stored as double
     */
    public double x, y;

    /**
     * initialize 2D vector with a x and y coordinate
     * @param x coordinate
     * @param y coordinate
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * initialize 2D vector with a x and y coordinate as 0
     */
    public Vector2D() {
        this.x = 0;
        this.y = 0;
    }

    public String toString() {
        return this.x + " " + this.y;
    }
}
