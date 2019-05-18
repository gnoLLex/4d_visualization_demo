package object4d;

import handlers.ProjectionHandler;
import handlers.RotationHandler;
import javafx.scene.canvas.Canvas;
import vector.Vector2D;
import vector.Vector4D;
import java.util.ArrayList;
/**
 * Represents an object in the 4 dimensional-hyperspace.
 * @author Lucas Engelmann
 */
public class Object4D {
    /**
     * Name of the 4D object as String.
     */
    private String name;

    /**
     * Points of the 4D object in an arraylist.
     */
    private ArrayList<Point> points;

    /**
     * Connections of the 4D object in an arraylist.
     */
    private ArrayList<Connection> connections;

    /**
     * Initialize 4D object with name, points and connections.
     * @param name name for the 4D object
     * @param inputPoints set of points in the 4D object
     * @param inputConnections set of connections in the 4D object
     */
    public Object4D(String name, ArrayList<Point> inputPoints, ArrayList<Connection> inputConnections) {
        this.name = name;
        this.points = new ArrayList<>(inputPoints);
        this.connections = new ArrayList<>(inputConnections);
    }

    /**
     * Initialize 4D object with name, points and connections copied from another 4D object.
     * @param obj4d 4D object to be copied
     */
    public Object4D(Object4D obj4d) {
        this.name = obj4d.name;
        this.points = new ArrayList<>(obj4d.points);
        this.connections = new ArrayList<>(obj4d.connections);
    }

    /**
     * Rotates a set of 4d vectors.
     * @param around    Around what it rotates the vector
     * @param theta     Angle by how much the vector is rotated
     */
    public void rotate(String around, double theta) {
        // rotate every point of the 4D object
        for (int i = 0; i < this.points.size(); i++) {
            this.points.set(i, RotationHandler.rot(this.points.get(i), theta, around));
        }
    }

    /**
     * Projects a set of 4D vectors down to 2D.
     * @param canvas canvas to be drawn on
     * @param ph projectionhandler for further passing
     * @return projected set of vector's
     */
    public Vector2D[] project(Canvas canvas, ProjectionHandler ph) {
        Vector2D[] inSecondDim = new Vector2D[this.points.size()];

        // getting width and height
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        // for each point
        for (int i = 0; i < this.points.size(); i++) {
            inSecondDim[i] = this.points.get(i).get2DContext(ph, w, h);
        }
        return inSecondDim;
    }

    /**
     * Rotates the 4D object by calculating the rotational difference between a rotated coordinate-system and a
     * normalized coordinate-system. These differences are then applied to the 4D object.
     * @param coord reference to the coordinate system which this 4D object should be rotated to
     * @return rotated 4D object
     */
    public Object4D rotateToCoord(Object4D coord){

        // normalized coordinate-system
        Vector4D[] world = new Vector4D[]{
                new Vector4D(),
                new Vector4D(1, 0, 0 ,0),
                new Vector4D(0, 1, 0 ,0),
                new Vector4D(0, 0, 1 ,0)
        };

        Object4D output = new Object4D(this);

        // calculating first axis and angle
        Vector4D firstAxis = world[1].crossProd3D(coord.points.get(1).getValues());
        double firstAngle = world[1].angle3DToVec(coord.points.get(1).getValues());

        // apply first rotation to the normalized coordinate-system
        for (int i = 0; i < world.length; i++) {
            world[i] = world[i].rotateByVector(firstAxis, firstAngle);

        }
        // apply first rotation to the 4D object
        for (int i = 0; i < this.points.size(); i++) {
            output.points.set(i, output.points.get(i).rotateByVector(firstAxis, firstAngle));
        }

        // calculating second axis and angle
        double secondAngle = world[2].angle3DToVec(coord.points.get(2).getValues());
        Vector4D secondAxis = world[2].crossProd3D(coord.points.get(2).getValues());

        // apply second rotation to the 4D object
        for (int i = 0; i < output.points.size(); i++) {
            output.points.set(i, output.points.get(i).rotateByVector(secondAxis, secondAngle));
        }

        return output;
    }

    /**
     * Removes the point with the index from the points of the 4D object, removes all connections which contain the
     * point and shifts all other connections.
     * @param index index of the point to be removed
     */
    public void removePointOfIndex(int index) {
        if (index != -1) {
            ArrayList<Point> points = this.points;
            ArrayList<Connection> connections = this.connections;

            // remove all no longer needed connections
            for (int i = 0; i < connections.size(); i++) {
                testAndRemoveConnection(i, index);
            }

            // change either first or second index of connection
            for (Connection con: connections) {
                for (int i = index; i < points.size(); i++) {
                    int pointIndex = con.containsPoint(i);
                    switch (pointIndex) {
                        case 1:
                            con.setIndexOne(con.getIndexOne() - 1);
                            break;
                        case 2:
                            con.setIndexTwo(con.getIndexTwo() - 1);
                            break;
                    }
                }
            }

            // finally remove the point
            points.remove(index);
        }
    }

    /**
     * Tests if the point is contained in the connection and remove if so.
     * @param indexConnection index of the connection to be tested
     * @param indexPoint index of the point
     */
    public void testAndRemoveConnection(int indexConnection, int indexPoint) {
        if (indexConnection < connections.size()) {
            if (connections.get(indexConnection).containsPoint(indexPoint) != 0) {
                // remove connection
                connections.remove(connections.get(indexConnection));
                // do it again for the same index's.
                testAndRemoveConnection(indexConnection, indexPoint);
            }
        }
    }

    /**
     * @return points of the 4D object
     */
    public ArrayList<Point> getPoints() {
        return points;
    }

    /**
     * @return connections of the 4D object
     */
    public ArrayList<Connection> getConnections() {
        return connections;
    }

    /**
     * @return name of the 4D object
     */
    public String getName() {
        return name;
    }


    /**
     * Set's the name of the 4D object.
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }
}
