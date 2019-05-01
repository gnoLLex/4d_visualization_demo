package vector;
/** Represents a vector in the 3 dimensional-space
 * @author Lucas Engelmann
 * @version 1.0
 * @since 1.0
 */
public class Vector3D extends Vector2D {
    /**
     * z coordinate stored as a double
     */
    public double z;

    /**
     * initialize 3D vector with x, y and z coordinate
     * @param x vectors x component
     * @param y vectors y component
     * @param z vectors z component
     */
    public Vector3D(double x, double y, double z) {
        super(x, y);
        this.z = z;
    }

    /**
     * initialize 3D vector with x, y and z coordinate as 0
     */
    public Vector3D() {
        super();
        this.z = 0;
    }
}
