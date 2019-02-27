package vector;

public class Vector4D extends Vector3D{
    public float w;
    protected float iw;

    public Vector4D(float x, float y, float z, float w) {
        super(x, y, z);
        this.w = w;
        this.iw = w;
    }
    public Vector4D(){
        super();
        this.w = 0;
    }
    public void initialValues(){
        this.x = this.ix;
        this.y = this.iy;
        this.z = this.iz;
        this.w = this.iw;
    }
}
