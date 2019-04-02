package vector;

/**
 * 2 dimensional vector used as a structure (attributes are all public)
 */
public class Vector2D {

    /**
     * x and y coordinate stored as float
     */
    public float x, y;

    /**
     * initialize 2D vector with a x and y coordinate
     * @param x coordinate
     * @param y coordinate
     */
    public Vector2D(float x, float y) {
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
}
