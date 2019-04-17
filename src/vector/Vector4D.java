package vector;

/**
 * Vector4D extends Vector3D with a w-value
 */
public class Vector4D extends Vector3D {
    /**
     * w coordinate stored as a double
     */
    public double w;

    /**
     * initialize 3D vector with a x, y, z and w coordinate
     * @param x coordinate
     * @param y coordinate
     * @param z coordinate
     * @param w coordinate
     */
    public Vector4D(double x, double y, double z, double w) {
        super(x, y, z);
        this.w = w;
    }

    /**
     * initialize 3D vector with a x, y, z and w coordinate as 0
     */
    public Vector4D() {
        super();
        this.w = 0;
    }

    public static Vector4D sub(Vector4D v1, Vector4D v2) {
        Vector4D o = new Vector4D();

        o.x = v1.x - v2.x;
        o.y = v1.y - v2.y;
        o.z = v1.z - v2.z;
        o.w = v1.w - v2.w;

        return o;
    }
}
