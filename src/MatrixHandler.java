import vector.Vector2D;
import vector.Vector3D;
import vector.Vector4D;

public class MatrixHandler {

    private float[][] to2D = new float[4][4];
    private float[][] to3D = new float[4][4];

    MatrixHandler(float aR)
    {
        calcProjTo2D(aR);
    }

    public void calcProjTo2D(float aR)
    {
        float zNear = 0.1f;
        float zFar = 1000.0f;
        float zFov = 90.0f;
        float zAspectRatio = aR;
        float zFovRad = (float) (1.0f / Math.tan(zFov * 0.5f / 180.0f * (float) Math.PI));

        to2D[0][0] = zAspectRatio * zFovRad;
        to2D[1][1] = zFovRad;
        to2D[2][2] = (zFar + zNear) / (zFar - zNear);
    }

    public Vector3D multMatVec3D(float[][] m, Vector3D v)
    {
        float newX = v.x * m[0][0] + v.y * m[1][0] + v.z * m[2][0];
        float newY = v.x * m[0][1] + v.y * m[1][1] + v.z * m[2][1];
        float newZ = v.x * m[0][2] + v.y * m[1][2] + v.z * m[2][2];

        return new Vector3D(newX, newY, newZ);
    }

    public Vector2D projectTo2D(Vector3D v, double w, double h)
    {
        Vector3D t = new Vector3D(v.x, v.y, v.z);
        t.z += 5.0f;
        float tempZ = t.z;

        t = multMatVec3D(to2D, t);

        //t.z += zScale;

        t.x /= tempZ;
        t.y /= tempZ;
        t.z /= tempZ;

        t.x += 1.0f; t.y += 1.0f;
        t.x *= 0.5f * w;
        t.y *= 0.5f * h;

        return new Vector2D(t.x, t.y);
    }

    public Vector4D rot(Vector4D v, float elapsedTime, String axis)
    {
        float rM[][] = new float[4][4];
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

    public Vector4D multMatVec4D(float[][]m, Vector4D v){
        float newX = v.x * m[0][0] + v.y * m[1][0] + v.z * m[2][0] + v.w * m[3][0];
        float newY = v.x * m[0][1] + v.y * m[1][1] + v.z * m[2][1] + v.w * m[3][1];
        float newZ = v.x * m[0][2] + v.y * m[1][2] + v.z * m[2][2] + v.w * m[3][2];
        float newW = v.x * m[0][3] + v.y * m[1][3] + v.z * m[2][3] + v.w * m[3][3];

        return new Vector4D(newX, newY, newZ, newW);
    }

    public Vector3D projectTo3D(Vector4D v) {
        Vector4D t = new Vector4D(v.x, v.y, v.z, v.w);
        float ok = 1 / (2.0f - v.w);

        to3D[0][0] = ok;
        to3D[1][1] = ok;
        to3D[2][2] = ok;

        t = multMatVec4D(to3D, t);

        return new Vector3D(t.x, t.y, t.z);
    }

    private float[][] rotX(float i) {
        float m[][] = new float[4][4];
        m[0][0] = 1.0f;
        m[1][1] = (float)Math.cos(i);
        m[1][2] = (float)Math.sin(i);
        m[2][1] = -(float)Math.sin(i);
        m[2][2] = (float)Math.cos(i);
        m[3][3] = 1.0f;
        return m;
    }

    private float[][] rotY(float i) {
        float m[][] = new float[4][4];
        m[0][0] = (float)Math.cos(i);
        m[1][1] = 1.0f;
        m[0][2] = (float)Math.sin(i);
        m[2][0] = -(float)Math.sin(i);
        m[2][2] = (float)Math.cos(i);
        m[3][3] = 1.0f;
        return m;
    }

    private float[][] rotZ(float i) {
        float m[][] = new float[4][4];
        m[0][0] = (float)Math.cos(i);
        m[0][1] = (float)Math.sin(i);
        m[1][0] = -(float)Math.sin(i);
        m[1][1] = (float)Math.cos(i);
        m[2][2] = 1.0f;
        m[3][3] = 1.0f;
        return m;
    }

    private float[][] rotXW(float i) {
        float m[][] = new float[4][4];
        m[0][0] = (float)Math.cos(i);
        m[1][1] = 1.0f;
        m[2][2] = 1.0f;
        m[0][3] = (float)Math.sin(i);
        m[3][0] = -(float)Math.sin(i);
        m[3][3] = (float)Math.cos(i);
        return m;
    }

    private float[][] rotYW(float i) {
        float m[][] = new float[4][4];
        m[0][0] = 1.0f;
        m[1][1] = (float)Math.cos(i);
        m[2][2] = 1.0f;
        m[1][3] = (float)Math.sin(i);
        m[3][1] = -(float)Math.sin(i);
        m[3][3] = (float)Math.cos(i);
        return m;
    }

    private float[][] rotZW(float i) {
        float m[][] = new float[4][4];
        m[0][0] = 1.0f;
        m[1][1] = 1.0f;
        m[2][2] = (float)Math.cos(i);
        m[2][3] = (float)Math.sin(i);
        m[3][2] = -(float)Math.sin(i);
        m[3][3] = (float)Math.cos(i);
        return m;
    }
}
