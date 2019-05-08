package objects;

import handlers.ProjectionHandler;
import handlers.RotationHandler;
import javafx.scene.canvas.Canvas;
import parser.Connection;
import vector.Vector2D;
import vector.Vector3D;
import vector.Vector4D;

public class Object4D {
    private String name;
    private Vector4D[] points;
    private Connection[] connections;

    public Object4D(int pLength, int cLength) {
        points = new Vector4D[pLength];
        connections = new Connection[cLength];
    }

    public Object4D(String name, Vector4D[] inputPoints, Connection[] inputConnections) {
        this.name = name;
        this.points = new Vector4D[inputPoints.length];
        this.connections = new Connection[inputConnections.length];
        for (int i = 0; i < inputPoints.length; i++) {
            this.points[i] = new Vector4D(inputPoints[i]);
        }
        for (int i = 0; i < inputConnections.length; i++) {
            this.connections[i] = new Connection(inputConnections[i]);
        }
    }

    public Object4D(Object4D obj4d) {
        this.name = obj4d.name;


        this.points = new Vector4D[obj4d.points.length];

        for (int i = 0; i < obj4d.points.length; i++) {
            this.points[i] = new Vector4D(obj4d.points[i]);
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

        Vector3D[] inThirdDim = new Vector3D[this.points.length];
        Vector2D[] inSecondDim = new Vector2D[this.points.length];

        // getting width and height
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        // for each point
        for (int i = 0; i < this.points.length; i++) {
            inThirdDim[i] = ph.project4DTo3D(this.points[i]);
            inSecondDim[i] = ph.project3DTo2D(inThirdDim[i], w, h);
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

        double firstAngle = world[1].angle3DToVec(coord.getPoints()[1]);
        Vector4D firstAxis = world[1].crossProd(coord.getPoints()[1]);

        for (int i = 0; i < world.length; i++) {
            world[i] = world[i].rotateByVector(firstAxis, firstAngle);
        }
        for (int i = 0; i < this.points.length; i++) {
            output.getPoints()[i] = this.points[i].rotateByVector(firstAxis, firstAngle);
        }

        double secondAngle = world[2].angle3DToVec(coord.getPoints()[2]);
        Vector4D secondAxis = world[2].crossProd(coord.getPoints()[2]);

        for (int i = 0; i < output.points.length; i++) {
            output.getPoints()[i] = output.getPoints()[i].rotateByVector(secondAxis, secondAngle);
        }

        return output;
    }

    public Vector4D[] getPoints() {
        return points;
    }

    public Connection[] getConnections() {
        return connections;
    }

    public String getName() {
        return name;
    }
}
