package parser;

import javafx.scene.paint.Color;
import object4d.Connection;
import object4d.Object4D;
import object4d.Point;
import vector.Vector4D;

import java.io.*;
import java.util.ArrayList;

public class Object4DSerializer {
    private final static String POINT = "p";
    private final static String CONNECTION = "c";
    private final static String NAME = "n";

    public static Object4D loadObj4D(File obj4dFile) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new FileReader(obj4dFile));

        String name = "4D-Object";
        ArrayList<Point> pointList = new ArrayList<>();
        ArrayList<Connection> connectionList = new ArrayList<>();

        while (true) {
            String line = bufferedReader.readLine();
            if (null == line) {
                break;
            }

            line = line.trim();

            if (line.startsWith(POINT)) {
                Point point = StringUtils.parsePoint(line);
                pointList.add(point);
            } else if (line.startsWith(CONNECTION)) {
                Connection c = StringUtils.parseConnection(line);
                connectionList.add(c);
            } else if (line.startsWith(NAME)) {
                name = line.substring(StringUtils.skipWhiteSpace(NAME.length(), line.toCharArray()));
            }
        }
        bufferedReader.close();

        Point[] outputPoints = new Point[pointList.size()];
        outputPoints = pointList.toArray(outputPoints);

        Connection[] outputConnections = new Connection[connectionList.size()];
        outputConnections = connectionList.toArray(outputConnections);
        Object4D output = new Object4D(name, outputPoints, outputConnections);

        return output;
    }

    public static void saveObj4d(Object4D object4D, File destination) {
        try {
            PrintWriter out = new PrintWriter(destination);
            out.println("n " + object4D.getName());
            for (Point point: object4D.getPoints()) {
                out.println("p " + point.toString());
            }
            for (Connection connection: object4D.getConnections()) {
                out.println("c " + connection.toString());
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
