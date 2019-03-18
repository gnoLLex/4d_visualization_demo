package vector;

public class Vector3D extends Vector2D{
    public float z;

    public Vector3D(float x, float y, float z) {
        super(x, y);
        this.z = z;
    }
    public Vector3D() {
        super();
        this.z = 0;
    }
}
