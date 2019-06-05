package serializer;

import object4d.Connection;
import object4d.Object4D;
import object4d.Point;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Utility-Class for serializing an 4D object.
 */
public class Object4DSerializer {
    /**
     * Constant that indicates that a point should be loaded.
     */
    private final static String POINT = "p";

    /**
     * Constant that indicates that a connection should be loaded.
     */
    private final static String CONNECTION = "c";

    /**
     * Constant that indicates that the name should be loaded.
     */
    private final static String NAME = "n";

    /**
     * Loads a 4D object with path located in the jar.
     * @param path path to the .obj4d-file which should be loaded
     * @param source where it is called from
     * @return loaded 4D object
     * @throws Exception ignored
     */
    public static Object4D loadObj4D(String path, Object source) throws Exception{
        InputStreamReader isr = new InputStreamReader(source.getClass().getResourceAsStream(path), Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(isr);
        return loadFromBufferedReader(bufferedReader);
    }

    /**
     * Loads a 4D object from file.
     * @param path path to .obj4d-file to be loaded
     * @return loaded 4D object
     * @throws Exception ignored
     */
    public static Object4D loadObj4D(String path) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)));
        return loadFromBufferedReader(bufferedReader);
    }

    /**
     * Loads a 4D object.
     * @param bufferedReader bufferedreader to be read from
     * @return loaded 4D object
     * @throws Exception ignored
     */
    private static Object4D loadFromBufferedReader(BufferedReader bufferedReader) throws Exception{
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

        return new Object4D(name, pointList, connectionList);
    }

    /**
     * Saves a 4D object.
     * @param object4D 4D object to be saved
     * @param destination file, where the 4D object should be saved in
     */
    public static void saveObj4d(Object4D object4D, File destination) {
        try {
            PrintWriter out = new PrintWriter(destination);
            out.println("n " + object4D.getName());
            for (Point point: object4D.getPoints()) {
                out.println("p " + point.toString());
            }
            for (Connection connection: object4D.getConnections()) {
                out.println("c " + connection.toStringSer());
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
