package handlers;

import javafx.scene.paint.Color;
import object4d.Point;

public class RotationHandler {
    /**
     * Rotates a vector by calculating a rotation-matrix and multiplying it with the vector
     * @param point     Point to be rotated
     * @param angle     Angle by how much the vector is rotated
     * @param around    Around what it rotates the vector
     * @return          Vector4D(result of rotating vector v)
     */
    public static Point rot(Point point, double angle, String around) {
        // stores rotation-matrix
        double[][] rM = new double[4][4];

        // setting rM to the calculated rotation-matrix around the specific axis/plane for a certain angle
        switch (around){
            case "X":
                rM = rotX(angle);
                break;
            case "Y":
                rM = rotY(angle);
                break;
            case "Z":
                rM = rotZ(angle);
                break;
            case "XW":
                rM = rotXW(angle);
                break;
            case "YW":
                rM = rotYW(angle);
                break;
            case "ZW":
                rM = rotZW(angle);
                break;
        }
        Point output = new Point(MatrixHandler.multMatVec(rM, point.getValues()), Color.BLACK);
        return output;
    }

    //region Rotation-Matrix calculation

    /**
     * Calculates rotation-matrix for rotation around x-axis
     * @param i angle for how much to rotate
     * @return  4x4 rotation matrix
     */
    private static double[][] rotX(double i) {
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
    private static double[][] rotY(double i) {
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
    private static double[][] rotZ(double i) {
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
    private static double[][] rotXW(double i) {
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
    private static double[][] rotYW(double i) {
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
    private static double[][] rotZW(double i) {
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
