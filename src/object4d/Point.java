package object4d;

import handlers.ProjectionHandler;
import javafx.scene.paint.Color;
import vector.Vector2D;
import vector.Vector3D;
import vector.Vector4D;
/**
 * Represents a point in the 4 dimensional-hyperspace.
 * @author Lucas Engelmann
 */
public class Point {
    /**
     * Values of the point stored as 4D vector.
     */
    private Vector4D values;

    /**
     * Color of the point.
     */
    private Color color;

    /**
     * Indicates if the vector can be selected.
     * By default every point is selectable.
     */
    private boolean selectable = true;

    /**
     * Initializes the point with values as 0 and color as black.
     */
    public Point() {
        this.values = new Vector4D();
        this.color = Color.BLACK;
    }

    /**
     * Initializes the point with values and color of another point.
     */
    public Point(Point point) {
        this.values = point.values;
        this.color = point.color;
        this.selectable = point.selectable;
    }

    /**
     * Initializes the point with values, color and if it's selectable.
     */
    public Point(Vector4D values, Color color, boolean selectable) {
        this.values = new Vector4D(values);
        this.color = color;
        this.selectable = selectable;
    }

    /**
     * Project's the point into 2D.
     * @param ph projectionhandler
     * @param w width of canvas
     * @param h height of canvas
     * @return projected point as 2D vector
     */
    public Vector2D get2DContext(ProjectionHandler ph, double w, double h) {
        Vector3D inThirdDim = ph.project4DTo3D(this.values);
        return ph.project3DTo2D(inThirdDim, w, h);
    }

    /**
     * @return values of the point in 4D hyperspace
     */
    public Vector4D getValues() {
        return values;
    }

    /**
     * @return color of the point
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set's the color of the point.
     * @param color color of the point
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Rotates a point around an custom axis for a certain angle.
     * @param axis axis to be rotated around
     * @param angle angle in radiant's to be rotated
     * @return rotated point with color and selectable-value of this point
     */
    public Point rotateByVector(Vector4D axis, double angle) {
        return new Point(this.values.rotateByVector(axis, angle), this.color, this.selectable);
    }


    /**
     * Set's if the point is selectable or not.
     * @param selectable selectable-value of point
     */
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    /**
     * @return selectable-value
     */
    public boolean isSelectable() {
        return selectable;
    }
}
