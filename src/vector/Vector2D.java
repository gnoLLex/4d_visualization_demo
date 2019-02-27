package vector;

public class Vector2D {
    public float x, y;
    protected float ix, iy;

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
        this.ix = x;
        this.iy = y;
    }
    public Vector2D() {
        this.x = 0;
        this.y = 0;
    }
}
