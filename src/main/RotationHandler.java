package main;

import vector.Vector2D;
import vector.Vector3D;
import vector.Vector4D;

public class RotationHandler {

    //region Global variable
    /**
     * Stores 3x3 projection matrix for 3d to 2d projection
     */
    private double[][] to2D = new double[3][3];

    /**
     * Stores 4x4 projection matrix for 4d to 3d projection
     */
    private double[][] to3D = new double[4][4];

    /**
     * Globally stored zoom-factor
     */
    public double zDisplacement;
    //endregion

    //region Constants

    /**
     * Distance to the lightsource for 4d to 3d projection
     */
    private static final double DIST_TO_LIGHT = 2.0;
    //endregion

    //region Matrix-Vector multiplication

    /**
     * Multiplies a matrix(4x4) with a vector(4d)
     * @param m     Matrix
     * @param v     Vector
     * @return      Vector4D(product of m * v)
     */
    private Vector4D multMatVec(double[][] m, Vector4D v) {
        double newX = v.x * m[0][0] + v.y * m[1][0] + v.z * m[2][0] + v.w * m[3][0];
        double newY = v.x * m[0][1] + v.y * m[1][1] + v.z * m[2][1] + v.w * m[3][1];
        double newZ = v.x * m[0][2] + v.y * m[1][2] + v.z * m[2][2] + v.w * m[3][2];
        double newW = v.x * m[0][3] + v.y * m[1][3] + v.z * m[2][3] + v.w * m[3][3];

        return new Vector4D(newX, newY, newZ, newW);
    }

    /**
     * Multiplies a matrix(3x3) with a vector(3d)
     * @param m     Matrix
     * @param v     Vector
     * @return      Vector4D(product of m * v)
     */
    private Vector3D multMatVec(double[][] m, Vector3D v) {
        double newX = v.x * m[0][0] + v.y * m[1][0] + v.z * m[2][0];
        double newY = v.x * m[0][1] + v.y * m[1][1] + v.z * m[2][1];
        double newZ = v.x * m[0][2] + v.y * m[1][2] + v.z * m[2][2];

        return new Vector3D(newX, newY, newZ);
    }

    //endregion

    //region Projection

    /**
     * Projects a 4d vector down to a 3d vector via stereographic projection
     * @param v Vector4D to be projected
     * @return  projected Vector3D
     */
    public Vector3D project4DTo3D(Vector4D v) {
        Vector4D t = new Vector4D(v.x, v.y, v.z, v.w);
        double ok = 1 / (DIST_TO_LIGHT - v.w);

        to3D[0][0] = ok;
        to3D[1][1] = ok;
        to3D[2][2] = ok;

        t = multMatVec(to3D, t);

        return new Vector3D(t.x, t.y, t.z);
    }

    /**
     * Projects a 3d vector down to a 2d vector via perspective projection
     * @param v Vector3D to be projected
     * @param w Width of the canvas
     * @param h Height of the canvas
     * @return  projected Vector2D
     */
    public Vector2D project3DTo2D(Vector3D v, double w, double h) {
        Vector3D t = new Vector3D(v.x, v.y, v.z);

        // translating vector further back
        t.z += zDisplacement;
        double tempZ = t.z;

        // projection via multiplication with projection matrix
        t = multMatVec(to2D, t);

        // normalizing
        t.x /= tempZ;
        t.y /= tempZ;

        // translating the vector into the center of the canvas
        t.x += 1.0;
        t.y += 1.0;
        t.x *= 0.5 * w;
        t.y *= 0.5 * h;

        return new Vector2D(t.x, t.y);
    }
    //endregion

    //region Projection-Matrix calculation

    /**
     * Calculates components of the 3d to 2d projection
     * @param aR Aspectratio
     */
    public void calcProj3DTo2D(double aR) {
        double zNear = 0.1;
        double zFar = 1000.0;
        double zFov = 90.0;
        double zFovRad = 1.0 / Math.tan(zFov * 0.5 / 180.0 *  Math.PI);

        to2D[0][0] = aR * zFovRad;
        to2D[1][1] = zFovRad;
        to2D[2][2] = (zFar + zNear) / (zFar - zNear);
    }
    //endregion

    //region Rotation

    /**
     * Rotates a vector by calculating a rotation-matrix and multiplying it with the vector
     * @param v         Vector to be rotated
     * @param angle     Angle by how much the vector is rotated
     * @param around    Around what it rotates the vector
     * @return          Vector4D(result of rotating vector v)
     */
    public Vector4D rot(Vector4D v, double angle, String around) {
        // stores rotation-matrix
        double[][] rM = new double[4][4];

        // setting rM to the calculated rotation-matrix around the specific axis/plane for a certain angle
        switch (around){
            case "X":
                rM = this.rotX(angle);
                break;
            case "Y":
                rM = this.rotY(angle);
                break;
            case "Z":
                rM = this.rotZ(angle);
                break;
            case "XW":
                rM = this.rotXW(angle);
                break;
            case "YW":
                rM = this.rotYW(angle);
                break;
            case "ZW":
                rM = this.rotZW(angle);
                break;
        }
        return this.multMatVec(rM, v);
    }
    //endregion

    //region Rotation-Matrix calculation

    /**
     * Calculates rotation-matrix for rotation around x-axis
     * @param i angle for how much to rotate
     * @return  4x4 rotation matrix
     */
    private double[][] rotX(double i) {
        double[][] m = new double[4][4];
        m[0][0] = 1.0;
        m[1][1] = Math.cos(i);
        m[1][2] = Math.sin(i);
        m[2][1] = -Math.sin(i);
        m[2][2] = Math.cos(i);
        m[3][3] = 1.0;
        return m;
    }

    /**
     * Calculates rotation-matrix for rotation around y-axis
     * @param i angle for how much to rotate
     * @return  4x4 rotation matrix
     */
    private double[][] rotY(double i) {
        double[][] m = new double[4][4];
        m[0][0] = Math.cos(i);
        m[1][1] = 1.0;
        m[0][2] = Math.sin(i);
        m[2][0] = -Math.sin(i);
        m[2][2] = Math.cos(i);
        m[3][3] = 1.0;
        return m;
    }

    /**
     * Calculates rotation-matrix for rotation around z-axis
     * @param i angle for how much to rotate
     * @return  4x4 rotation matrix
     */
    private double[][] rotZ(double i) {
        double[][] m = new double[4][4];
        m[0][0] = Math.cos(i);
        m[0][1] = Math.sin(i);
        m[1][0] = -Math.sin(i);
        m[1][1] = Math.cos(i);
        m[2][2] = 1.0;
        m[3][3] = 1.0;
        return m;
    }

    /**
     * Calculates rotation-matrix for rotation around xw-plane
     * @param i angle for how much to rotate
     * @return  4x4 rotation matrix
     */
    private double[][] rotXW(double i) {
        double[][] m = new double[4][4];
        m[0][0] = Math.cos(i);
        m[1][1] = 1.0;
        m[2][2] = 1.0;
        m[0][3] = Math.sin(i);
        m[3][0] = -Math.sin(i);
        m[3][3] = Math.cos(i);
        return m;
    }

    /**
     * Calculates rotation-matrix for rotation around yw-plane
     * @param i angle for how much to rotate
     * @return  4x4 rotation matrix
     */
    private double[][] rotYW(double i) {
        double[][] m = new double[4][4];
        m[0][0] = 1.0;
        m[1][1] = Math.cos(i);
        m[2][2] = 1.0;
        m[1][3] = Math.sin(i);
        m[3][1] = -Math.sin(i);
        m[3][3] = Math.cos(i);
        return m;
    }

    /**
     * Calculates rotation-matrix for rotation around zw-plane
     * @param i angle for how much to rotate
     * @return  4x4 rotation matrix
     */
    private double[][] rotZW(double i) {
        double[][] m = new double[4][4];
        m[0][0] = 1.0;
        m[1][1] = 1.0;
        m[2][2] = Math.cos(i);
        m[2][3] = Math.sin(i);
        m[3][2] = -Math.sin(i);
        m[3][3] = Math.cos(i);
        return m;
    }
    //endregion
}
