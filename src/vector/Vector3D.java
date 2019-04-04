package vector;

/**
 * Vector3D extends Vector2D with a z-value
 */
public class Vector3D extends Vector2D {
    /**
     * z coordinate stored as a double
     */
    public double z;

    /**
     * initialize 3D vector with a x, y and z coordinate
     * @param x coordinate
     * @param y coordinate
     * @param z coordinate
     */
    public Vector3D(double x, double y, double z) {
        super(x, y);
        this.z = z;
    }

    /**
     * initialize 3D vector with a x, y and z coordinate as 0
     */
    public Vector3D() {
        super();
        this.z = 0;
    }
}
