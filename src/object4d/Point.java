package object4d;

import handlers.ProjectionHandler;
import javafx.scene.paint.Color;
import vector.Vector2D;
import vector.Vector3D;
import vector.Vector4D;


public class Point {
    private Vector4D values;
    private Color color;

    /**
     * indicates if the vector can be selected
     */
    private boolean selectable = true;

    public Point() {
        this.values = new Vector4D();
        this.color = Color.BLACK;
    }

    public Point(Point point) {
        this.values = point.values;
        this.color = point.color;
        this.selectable = point.selectable;
    }

    public Point(Vector4D values, Color color, boolean selectable) {
        this.values = new Vector4D(values);
        this.color = color;
        this.selectable = selectable;
    }

    public Vector2D get2DContext(ProjectionHandler ph, double w, double h) {
        Vector3D inThirdDim = ph.project4DTo3D(this.values);
        return ph.project3DTo2D(inThirdDim, w, h);
    }

    public String toString() {
        return values.toString() + " " + color;
    }

    public Vector4D getValues() {
        return values;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Point rotateByVector(Vector4D axis, double angle) {
        return new Point(this.values.rotateByVector(axis, angle), this.color, this.selectable);
    }


    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public boolean isSelectable() {
        return selectable;
    }
}
