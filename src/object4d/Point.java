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

    private boolean selected;

    public Point() {
        this.values = new Vector4D();
        this.color = Color.BLACK;
        this.selected = false;
    }

    public Point(Point point) {
        this.values = point.values;
        this.color = point.color;
        this.selectable = point.selectable;
        this.selected = point.selected;
    }

    public Point(Vector4D values, Color color, boolean selectable, boolean selected) {
        this.values = new Vector4D(values);
        this.color = color;
        this.selectable = selectable;
        this.selected = selected;
    }

    public Vector2D get2DContext(ProjectionHandler ph, double w, double h) {
        Vector3D inThirdDim = ph.project4DTo3D(this.values);
        Vector2D inSecondDim = ph.project3DTo2D(inThirdDim, w, h);
        return inSecondDim;
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

    public Point rotateByVector(Vector4D axis, double angle) {
        return new Point(this.values.rotateByVector(axis, angle), this.color, this.selectable, this.selected);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void select() {
        if (this.selectable && !this.selected) {
            System.out.println("selected");
            this.selected = true;
        } else {
            this.deselect();
        }
    }

    public void deselect() {
        this.selected = false;
    }
}
