package object4d;

import handlers.ProjectionHandler;
import javafx.scene.paint.Color;
import vector.Vector2D;
import vector.Vector3D;
import vector.Vector4D;


public class Point {
    private Vector4D values;
    private Color color;

    private static final double diameter = 5.0;

    /**
     * indicates if the vector can be selected
     */
    private boolean selectable = true;

    private boolean selected;

    public Point(Point point) {
        this.values = point.values;
        this.color = point.color;
        this.selected = point.selected;
    }

    public Point(Vector4D values, Color color, boolean selected) {
        this.values = new Vector4D(values);
        this.color = color;
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
        return new Point(this.values.rotateByVector(axis, angle), this.color, this.selected);
    }

    public double getDiameter() {
        return diameter;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public void select() {
        if (this.selectable && !this.selected) {
            this.selected = true;
        } else {
            this.selected = false;
        }
    }

    public void deselect() {
        this.selected = false;
    }
}
