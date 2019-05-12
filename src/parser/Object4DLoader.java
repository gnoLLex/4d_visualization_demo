package parser;

import object4d.Connection;
import object4d.Object4D;
import vector.Vector4D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Object4DLoader {
    private final static String VECTOR = "v";
    private final static String CONNECTION = "c";
    private final static String NAME = "n";

    public static Object4D parseObj4D(File obj4dFile) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new FileReader(obj4dFile));

        String name = "4D-Object";
        ArrayList<Vector4D> pointList = new ArrayList<>();
        ArrayList<Connection> connectionList = new ArrayList<>();

        while (true) {
            String line = bufferedReader.readLine();
            if (null == line) {
                break;
            }

            line = line.trim();

            if (line.startsWith(VECTOR)) {
                double[] values = StringUtils.parseDoubleList(4, line);
                Vector4D v = new Vector4D(values[0], values[1], values[2], values[3]);
                pointList.add(v);
            } else if (line.startsWith(CONNECTION)) {
                Connection c = StringUtils.parseConnection(line);
                connectionList.add(c);
            } else if (line.startsWith(NAME)) {
                name = line.substring(StringUtils.skipWhiteSpace(NAME.length(), line.toCharArray()));
            }
        }
        bufferedReader.close();

        Vector4D[] outputPoints = new Vector4D[pointList.size()];
        outputPoints = pointList.toArray(outputPoints);

        Connection[] outputConnections = new Connection[connectionList.size()];
        outputConnections = connectionList.toArray(outputConnections);
        Object4D output = new Object4D(name, outputPoints, outputConnections);

        return output;
    }
}
