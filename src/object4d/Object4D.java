package object4d;

import handlers.ProjectionHandler;
import handlers.RotationHandler;
import javafx.scene.canvas.Canvas;
import vector.Vector2D;
import vector.Vector4D;

import java.util.ArrayList;

public class Object4D {
    private String name;
    private ArrayList<Point> points;
    private ArrayList<Connection> connections;

    public Object4D(String name, ArrayList<Point> inputPoints, ArrayList<Connection> inputConnections) {
        this.name = name;
        this.points = new ArrayList<>(inputPoints);
        this.connections = new ArrayList<>(inputConnections);
    }

    public Object4D(Object4D obj4d) {
        this.name = obj4d.name;
        this.points = new ArrayList<>(obj4d.points);
        this.connections = new ArrayList<>(obj4d.connections);
    }

    /**
     * Rotates a set of 4d vectors
     * @param around    Around what it rotates the vector
     * @param theta     Angle by how much the vector is rotated
     */
    public void rotate(String around, double theta) {
        for (int i = 0; i < this.points.size(); i++) {
            this.points.set(i, RotationHandler.rot(this.points.get(i), theta, around));
        }
    }

    /**
     * projects a set of 4D vectors down to 2D
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

    public Object4D rotateToCoord(Object4D coord){

        Vector4D[] world = new Vector4D[]{
                new Vector4D(),
                new Vector4D(1, 0, 0 ,0),
                new Vector4D(0, 1, 0 ,0),
                new Vector4D(0, 0, 1 ,0)
        };

        Object4D output = new Object4D(this);

        double firstAngle = world[1].angle3DToVec(coord.points.get(1).getValues());
        Vector4D firstAxis = world[1].crossProd(coord.points.get(1).getValues());
        //System.out.println("Axis: " + firstAxis.toString() + "| Angle: " + firstAngle);

        for (int i = 0; i < world.length; i++) {
            world[i] = world[i].rotateByVector(firstAxis, firstAngle);

        }
        for (int i = 0; i < this.points.size(); i++) {
            output.points.set(i, output.points.get(i).rotateByVector(firstAxis, firstAngle));
        }

        double secondAngle = world[2].angle3DToVec(coord.points.get(2).getValues());
        Vector4D secondAxis = world[2].crossProd(coord.points.get(2).getValues());

        for (int i = 0; i < output.points.size(); i++) {
            output.points.set(i, output.points.get(i).rotateByVector(secondAxis, secondAngle));
        }

        return output;
    }

    public void removePointFrom(int index) {
        if (index != -1) {
            ArrayList<Point> points = this.points;
            ArrayList<Connection> connections = this.connections;

            for (int i = 0; i < connections.size(); i++) {
                testAndRemoveConnection(i, index);
            }

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
            points.remove(index);
        }
    }

    public void testAndRemoveConnection(int index, int indexToCompare) {
        if (index < connections.size()) {
            if (connections.get(index).containsPoint(indexToCompare) != 0) {
                connections.remove(connections.get(index));
                testAndRemoveConnection(index, indexToCompare);
            }
        }
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
