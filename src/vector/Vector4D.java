package vector;
/** Represents a vector in the 4 dimensional-hyperspace.
 * @author Lucas Engelmann
 */
public class Vector4D extends Vector3D {
    /**
     * W coordinate stored as a double.
     */
    public double w;

    /**
     * Initialize 4D vector with x, y, z and w coordinate.
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
     * Initialize 4D vector with x, y, z and w coordinate copied from another 4D vector.
     * @param input 4D vector to be copied
     */
    public Vector4D(Vector4D input) {
        super(input.x, input.y, input.z);
        this.w = input.w;
    }

    /**
     * Initialize 4D vector with x, y, z and w coordinate as 0.
     */
    public Vector4D() {
        super();
        this.w = 0;
    }

    /**
     * @return vector as string
     */
    public String toString() {
        return x + " " + y + " " + z + " " + w;
    }

    /**
     * Calculates the sum of this 4D vector and the 4D vector to be added.
     * @param toBeAdded vector to be added to this vector
     * @return sum of the two 4D vectors
     */
    private Vector4D add(Vector4D toBeAdded) {
        Vector4D output = new Vector4D();
        output.x = this.x + toBeAdded.x;
        output.y = this.y + toBeAdded.y;
        output.z = this.z + toBeAdded.z;
        output.w = this.w + toBeAdded.w;
        return output;
    }

    /**
     * Calculates the difference of this 4D vector and the 4D vector to be subtracted.
     * @param toBeSubtracted 4D vector to be subtracted from this 4D vector
     * @return difference of the two 4D vectors
     */
    private Vector4D sub(Vector4D toBeSubtracted) {
        Vector4D output = new Vector4D();
        output.x = this.x - toBeSubtracted.x;
        output.y = this.y - toBeSubtracted.y;
        output.z = this.z - toBeSubtracted.z;
        output.w = this.w - toBeSubtracted.w;
        return output;
    }

    /**
     * Calculates the product of this 4D vector and a double in 3D context.
     * @param amount by what to multiply
     * @return product of the multiplication
     */
    private Vector4D times3D(double amount) {
        Vector4D output = new Vector4D();
        output.x = this.x * amount;
        output.y = this.y * amount;
        output.z = this.z * amount;
        output.w = this.w;
        return output;
    }

    /**
     * Calculates the dot-product of this 4D vector and another 4D vector in 3D context.
     * @param other other vector to calculate with
     * @return dot-product
     */
    private double dotProd3D(Vector4D other) {
        double output = 0;
        output += this.x * other.x;
        output += this.y * other.y;
        output += this.z * other.z;
        return output;
    }

    /**
     * Calculates the magnitude of this 4D vector in 3D context.
     * @return magnitude
     */
    private double magnitude3D() {
        double X = this.x * this.x;
        double Y = this.y * this.y;
        double Z = this.z * this.z;
        return Math.sqrt(X + Y + Z);
    }

    /**
     * Calculates the cross-product of two 4D vectors in 3D context.
     * @param other other vector
     * @return cross-product of both 4D vectors
     */
    public Vector4D crossProd3D(Vector4D other) {
        Vector4D o = new Vector4D();
        o.x = this.y * other.z - this.z * other.y;
        o.y = this.z * other.x - this.x * other.z;
        o.z = this.x * other.y - this.y * other.x;
        return o;
    }

    /**
     * Calculates the angle between two 4D vectors in 3D context.
     * @param other other vector
     * @return angle between both 4D vectors
     */
    public double angle3DToVec(Vector4D other) {
        return Math.acos(this.dotProd3D(other)/(this.magnitude3D() * other.magnitude3D()));
    }

    /**
     * Rotates a 4D vector around an custom axis for a certain angle.
     * @see <a target="blank" href="https://math.stackexchange.com/a/1432182">Solution inspired and explained by MNKY.</a>
     * @param axis axis to be rotated around
     * @param angle angle in radiant's to be rotated
     * @return rotated 4D vector
     */
    public Vector4D rotateByVector(Vector4D axis, double angle) {
        if (axis.magnitude3D() <= 0.000001) {
            if (angle != 0) return this.times3D(-1);
            return this;
        }
        Vector4D aDirB;
        double dotWithOther = this.dotProd3D(axis);
        double dotWithSelf = axis.dotProd3D(axis);
        if (Double.isNaN(dotWithOther) || dotWithSelf == 0) {
            return new Vector4D( 0, 0, 0, this.w);
        } else {
            aDirB = axis.times3D(dotWithOther / dotWithSelf);
            Vector4D aOrthB = this.sub(aDirB);
            Vector4D W = axis.crossProd3D(aOrthB);

            double x1 = Math.cos(angle) / aOrthB.magnitude3D();
            double x2 = Math.sin(angle) / W.magnitude3D();

            Vector4D aOrthBRot = (aOrthB.times3D(x1).add(W.times3D(x2))).times3D(aOrthB.magnitude3D());
            return aDirB.add(aOrthBRot);
        }
    }
}
