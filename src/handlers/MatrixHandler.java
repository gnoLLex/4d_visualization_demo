package handlers;

import vector.Vector3D;
import vector.Vector4D;

public class MatrixHandler {

    /**
     * Multiplies a matrix(4x4) with a vector(4d)
     * @param m     Matrix
     * @param v     Vector
     * @return      Vector4D(product of m * v)
     */
    public static Vector4D multMatVec(double[][] m, Vector4D v) {
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
    public static Vector3D multMatVec(double[][] m, Vector3D v) {
        double newX = v.x * m[0][0] + v.y * m[1][0] + v.z * m[2][0];
        double newY = v.x * m[0][1] + v.y * m[1][1] + v.z * m[2][1];
        double newZ = v.x * m[0][2] + v.y * m[1][2] + v.z * m[2][2];

        return new Vector3D(newX, newY, newZ);
    }

}
