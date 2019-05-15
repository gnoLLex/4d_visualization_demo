package handlers;

import vector.Vector2D;
import vector.Vector3D;
import vector.Vector4D;

public class ProjectionHandler {

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
    private static final double DIST_TO_LIGHT = -2.0;
    //endregion

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

        t = MatrixHandler.multMatVec(to3D, t);

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
        Vector3D temp = new Vector3D(v.x, v.y, v.z);

        // translating vector further back
        temp.z += zDisplacement;
        double tempZ = temp.z;

        // projection via multiplication with projection matrix
        temp = MatrixHandler.multMatVec(to2D, temp);

        // normalizing
        temp.x /= tempZ;
        temp.y /= tempZ;

        // translating the vector into the center of the canvas
        temp.x += 1.0;
        temp.y += 1.0;
        temp.x *= 0.5 * w;
        temp.y *= 0.5 * h;

        return new Vector2D(temp.x, temp.y);
    }

    /**
     * Calculates components of the 3d to 2d projection
     * @param aR Aspectratio
     */
    public void calcProj3DTo2D(double aR) {
        final double zNear = 0.1;
        final double zFar = 1000.0;
        final double fov = 90.0;
        final double fovRad = 1.0 / Math.tan(fov * 0.5 / 180.0 *  Math.PI);

        to2D[0][0] = aR * fovRad;
        to2D[1][1] = fovRad;
        to2D[2][2] = (zFar + zNear) / (zFar - zNear);
    }
}
