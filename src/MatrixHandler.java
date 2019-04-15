import vector.Vector2D;
import vector.Vector3D;
import vector.Vector4D;

class MatrixHandler {

    private double[][] to2D = new double[4][4];
    private double[][] to3D = new double[4][4];

    public double zDisplacement = 4.5;

    void calcProj3DTo2D(double zAspectRatio) {
        double zNear = 0.1;
        double zFar = 1000.0;
        double zFov = 90.0;
        double zFovRad = 1.0 / Math.tan(zFov * 0.5 / 180.0 *  Math.PI);

        to2D[0][0] = zAspectRatio * zFovRad;
        to2D[1][1] = zFovRad;
        to2D[2][2] = (zFar + zNear) / (zFar - zNear);
    }

    /**
     * multiplies a matrix(3x3) with a vector(3)
     * @param m Matrix
     * @param v Vector
     * @return Vector4D(product of m * v)
     */
    private Vector3D multMatVec3D(double[][] m, Vector3D v) {
        double newX = v.x * m[0][0] + v.y * m[1][0] + v.z * m[2][0];
        double newY = v.x * m[0][1] + v.y * m[1][1] + v.z * m[2][1];
        double newZ = v.x * m[0][2] + v.y * m[1][2] + v.z * m[2][2];

        return new Vector3D(newX, newY, newZ);
    }

    /**
     * multiplies a matrix(4x4) with a vector(4)
     * @param m Matrix
     * @param v Vector
     * @return Vector4D(product of m * v)
     */
    public Vector4D multMatVec4D(double[][]m, Vector4D v) {
        double newX = v.x * m[0][0] + v.y * m[1][0] + v.z * m[2][0] + v.w * m[3][0];
        double newY = v.x * m[0][1] + v.y * m[1][1] + v.z * m[2][1] + v.w * m[3][1];
        double newZ = v.x * m[0][2] + v.y * m[1][2] + v.z * m[2][2] + v.w * m[3][2];
        double newW = v.x * m[0][3] + v.y * m[1][3] + v.z * m[2][3] + v.w * m[3][3];

        return new Vector4D(newX, newY, newZ, newW);
    }

    public double[][] multMatMat(double[][] m1, double[][] m2) {
        int l = m1.length;
        int m = m1[0].length;
        int n = m2[0].length;

        double[][] out = new double[l][n];

        for (int i = 0; i < l; i++) {
            for (int j = 0; j < n; j++) {
                out[i][j] = 0;
                for (int k = 0; k < m; k++) {
                    out[i][j] += m1[i][k] * m2[k][j];
                }
            }
        }
        return out;
    }

    Vector2D project3DTo2D(Vector3D v, double w, double h) {
        Vector3D t = new Vector3D(v.x, v.y, v.z);
        t.z += zDisplacement;
        double tempZ = t.z;

        t = multMatVec3D(to2D, t);

        t.x /= tempZ;
        t.y /= tempZ;

        t.x += 1.0;
        t.y += 1.0;
        t.x *= 0.5 * w;
        t.y *= 0.5 * h;

        return new Vector2D(t.x, t.y);
    }

    Vector4D rot(Vector4D v, double elapsedTime, String axis) {
        double[][] rM = new double[4][4];
        switch (axis){
            case "X":
                rM = this.rotX(elapsedTime);
                break;
            case "Y":
                rM = this.rotY(elapsedTime);
                break;
            case "Z":
                rM = this.rotZ(elapsedTime);
                break;
            case "XW":
                rM = this.rotXW(elapsedTime);
                break;
            case "YW":
                rM = this.rotYW(elapsedTime);
                break;
            case "ZW":
                rM = this.rotZW(elapsedTime);
                break;

        }
        return this.multMatVec4D(rM, v);
    }

    Vector3D project4DTo3D(Vector4D v) {
        Vector4D t = new Vector4D(v.x, v.y, v.z, v.w);
        double ok = 1 / (2.0 - v.w);

        to3D[0][0] = ok;
        to3D[1][1] = ok;
        to3D[2][2] = ok;

        t = multMatVec4D(to3D, t);

        return new Vector3D(t.x, t.y, t.z);
    }

    public double[][] rotX(double i) {
        double[][] m = new double[4][4];
        m[0][0] = 1.0;
        m[1][1] = Math.cos(i);
        m[1][2] = Math.sin(i);
        m[2][1] = -Math.sin(i);
        m[2][2] = Math.cos(i);
        m[3][3] = 1.0;
        return m;
    }

    public double[][] rotY(double i) {
        double[][] m = new double[4][4];
        m[0][0] = Math.cos(i);
        m[1][1] = 1.0;
        m[0][2] = Math.sin(i);
        m[2][0] = -Math.sin(i);
        m[2][2] = Math.cos(i);
        m[3][3] = 1.0;
        return m;
    }

    public double[][] rotZ(double i) {
        double[][] m = new double[4][4];
        m[0][0] = Math.cos(i);
        m[0][1] = Math.sin(i);
        m[1][0] = -Math.sin(i);
        m[1][1] = Math.cos(i);
        m[2][2] = 1.0;
        m[3][3] = 1.0;
        return m;
    }

    public double[][] rotXW(double i) {
        double[][] m = new double[4][4];
        m[0][0] = Math.cos(i);
        m[1][1] = 1.0;
        m[2][2] = 1.0;
        m[0][3] = Math.sin(i);
        m[3][0] = -Math.sin(i);
        m[3][3] = Math.cos(i);
        return m;
    }

    public double[][] rotYW(double i) {
        double[][] m = new double[4][4];
        m[0][0] = 1.0;
        m[1][1] = Math.cos(i);
        m[2][2] = 1.0;
        m[1][3] = Math.sin(i);
        m[3][1] = -Math.sin(i);
        m[3][3] = Math.cos(i);
        return m;
    }

    public double[][] rotZW(double i) {
        double[][] m = new double[4][4];
        m[0][0] = 1.0;
        m[1][1] = 1.0;
        m[2][2] = Math.cos(i);
        m[2][3] = Math.sin(i);
        m[3][2] = -Math.sin(i);
        m[3][3] = Math.cos(i);
        return m;
    }
}
