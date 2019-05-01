package vector;
/** Represents a vector in the 4 dimensional-hyperspace
 * @author Lucas Engelmann
 * @version 1.0
 * @since 1.0
 */
public class Vector4D extends Vector3D {
    /**
     * w coordinate stored as a double
     */
    public double w;

    /**
     * initialize 4D vector with x, y, z and w coordinate
     * @param x vectors x component
     * @param y vectors y component
     * @param z vectors z component
     * @param w vectors w component
     */
    public Vector4D(double x, double y, double z, double w) {
        super(x, y, z);
        this.w = w;
    }

    /**
     * initialize 4D vector with x, y, z and w coordinate as 0
     */
    public Vector4D() {
        super();
        this.w = 0;
    }

    private Vector4D add(Vector4D v) {
        Vector4D o = new Vector4D();
        o.x = this.x + v.x;
        o.y = this.y + v.y;
        o.z = this.z + v.z;
        o.w = this.w + v.w;
        return o;
    }

    private Vector4D sub(Vector4D v) {
        Vector4D o = new Vector4D();
        o.x = this.x - v.x;
        o.y = this.y - v.y;
        o.z = this.z - v.z;
        o.w = this.w - v.w;
        return o;
    }

    private Vector4D times(double d) {
        Vector4D o = new Vector4D();
        o.x = this.x * d;
        o.y = this.y * d;
        o.z = this.z * d;
        o.w = this.w;
        return o;
    }

    public double dotProd(Vector4D v) {
        double o = 0;
        o += this.x * v.x;
        o += this.y * v.y;
        o += this.z * v.z;
        return o;
    }

    public double magnitude() {
        double X = this.x * this.x;
        double Y = this.y * this.y;
        double Z = this.z * this.z;
        return Math.sqrt(X + Y + Z);
    }

    public Vector4D crossProd(Vector4D v) {
        Vector4D o = new Vector4D();
        o.x = this.y * v.z - this.z * v.y;
        o.y = this.z * v.x - this.x * v.z;
        o.z = this.x * v.y - this.y * v.x;
        return o;
    }

    public Vector4D rotateByVector(Vector4D v, double angle) {
        Vector4D aDirB = v.times(this.dotProd(v) / v.dotProd(v));
        Vector4D aOrthB = this.sub(aDirB);
        Vector4D W = v.crossProd(aOrthB);

        double x1 = Math.cos(angle) / aOrthB.magnitude();
        double x2 = Math.sin(angle) / W.magnitude();

        Vector4D aOrthBRot = (aOrthB.times(x1).add(W.times(x2))).times(aOrthB.magnitude());

        return aDirB.add(aOrthBRot);
    }
}
