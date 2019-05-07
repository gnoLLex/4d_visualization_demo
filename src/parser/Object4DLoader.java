package parser;

import objects.Object4D;
import vector.Vector4D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Object4DLoader {
    private final static String VECTOR = "v";
    private final static String CONNECTION = "c";

    public static Object4D parseObj4D(File obj4dFile) throws Exception{

        FileReader fileReader = new FileReader(obj4dFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        ArrayList<Vector4D> pointList = new ArrayList<>();
        ArrayList<Connection> connectionList = new ArrayList<>();

        String line;

        while (true) {
            line = bufferedReader.readLine();
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
            }
        }
        bufferedReader.close();

        Vector4D[] outputPoints = new Vector4D[pointList.size()];
        outputPoints = pointList.toArray(outputPoints);

        Connection[] outputConnections = new Connection[connectionList.size()];
        outputConnections = connectionList.toArray(outputConnections);
        Object4D output = new Object4D(outputPoints, outputConnections);

        return output;
    }
}
