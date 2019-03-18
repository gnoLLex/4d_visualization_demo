package vector;

public class Vector4D extends Vector3D{
    public float w;

    public Vector4D(float x, float y, float z, float w) {
        super(x, y, z);
        this.w = w;
    }
    public Vector4D(){
        super();
        this.w = 0;
    }
}
