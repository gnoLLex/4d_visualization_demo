package object4d;

import handlers.ProjectionHandler;
import handlers.RotationHandler;
import javafx.scene.canvas.Canvas;
import vector.Vector2D;
import vector.Vector3D;
import vector.Vector4D;

import java.io.Serializable;

public class Object4D implements Serializable {
    private String name;
    private Point[] points;
    private Connection[] connections;

    public Object4D(String name, Point[] inputPoints, Connection[] inputConnections) {
        this.name = name;
        this.points = new Point[inputPoints.length];
        this.connections = new Connection[inputConnections.length];
        for (int i = 0; i < inputPoints.length; i++) {
            this.points[i] = new Point(inputPoints[i]);
        }
        for (int i = 0; i < inputConnections.length; i++) {
            this.connections[i] = new Connection(inputConnections[i]);
        }
    }

    public Object4D(Object4D obj4d) {
        this.name = obj4d.name;
        this.points = new Point[obj4d.points.length];

        for (int i = 0; i < obj4d.points.length; i++) {
            this.points[i] = new Point(obj4d.points[i]);
        }
        this.connections = new Connection[obj4d.connections.length];
        for (int i = 0; i < obj4d.connections.length; i++) {
            this.connections[i] = new Connection(obj4d.connections[i]);
        }
    }

    /**
     * Rotates a set of 4d vectors
     * @param around    Around what it rotates the vector
     * @param theta     Angle by how much the vector is rotated
     */
    public void rotate(String around, double theta) {
        for (int i = 0; i < this.getPoints().length; i++) {
            this.points[i] = RotationHandler.rot(this.points[i], theta, around);
        }
    }

    /**
     * projects a set of 4D vectors down to 2D
     */
    public Vector2D[] project(Canvas canvas, ProjectionHandler ph) {
        Vector2D[] inSecondDim = new Vector2D[this.points.length];

        // getting width and height
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        // for each point
        for (int i = 0; i < this.points.length; i++) {
            inSecondDim[i] = this.points[i].get2DContext(ph, w, h);
        }
        return inSecondDim;
    }

    public Object4D rotateToCoord(Object4D coord){

        Vector4D[] world = new Vector4D[]{
                new Vector4D(),
                new Vector4D(1, 0, 0 ,0),
                new Vector4D(0, 1, 0 ,0),
                new Vector4D(0, 0, 1 ,0)
        };

        Object4D output = new Object4D(this);

        double firstAngle = world[1].angle3DToVec(coord.points[1].getValues());
        Vector4D firstAxis = world[1].crossProd(coord.points[1].getValues());
        //System.out.println("Axis: " + firstAxis.toString() + "| Angle: " + firstAngle);

        for (int i = 0; i < world.length; i++) {
            world[i] = world[i].rotateByVector(firstAxis, firstAngle);

        }
        for (int i = 0; i < this.points.length; i++) {
            output.points[i] = output.points[i].rotateByVector(firstAxis, firstAngle);
        }

        double secondAngle = world[2].angle3DToVec(coord.points[2].getValues());
        Vector4D secondAxis = world[2].crossProd(coord.points[2].getValues());

        for (int i = 0; i < output.points.length; i++) {
            output.points[i] = output.points[i].rotateByVector(secondAxis, secondAngle);
        }

        return output;
    }

    public Point[] getPoints() {
        return points;
    }

    public Connection[] getConnections() {
        return connections;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
