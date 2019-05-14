package parser;

import javafx.scene.paint.Color;
import object4d.Connection;
import object4d.Point;
import vector.Vector4D;

public class StringUtils {

    //region Constants

    private static final int NUM_ARGS_POINT = 5;
    private static final int NUM_ARGS_CONNECTION = 3;

    //endregion

    public static int skipWhiteSpace(int mCount, char messageChars[]) {
        //Skip whitespace
        while (mCount < messageChars.length) {
            if (messageChars[mCount] == ' ' || messageChars[mCount] == '\n' || messageChars[mCount] == '\t') {
                mCount++;
            } else {
                break;
            }
        }
        return mCount;
    }

    public static String[] parseString(int numStrings, String list) {
        String[] output = new String[numStrings];

        int returnArrayCount = 0;
        // Copy list into a char array.
        char[] charList;
        charList = new char[list.length()];
        list.getChars(0, list.length(), charList, 0);
        int listLength = charList.length;

        int count = 1;
        int itemStart;
        int itemLength;

        while (count < listLength) {
            // Skip any leading whitespace
            int itemEnd = skipWhiteSpace(count, charList);
            count = itemEnd;
            if (count >= listLength) {
                break;
            }
            itemStart = count;
            itemEnd = itemStart;
            while (itemEnd < listLength) {
                if ((charList[itemEnd] != ' ') && (charList[itemEnd] != '\n') && (charList[itemEnd] != '\t')) {
                    itemEnd++;
                } else {
                    break;
                }
            }
            itemLength = itemEnd - itemStart;
            output[returnArrayCount++] = new String(charList, itemStart, itemLength);
            if (returnArrayCount >= numStrings) {
                break;
            }

            count = itemEnd;
        }

        return output;
    }

    public static double[] parseDoubleList(int numDoubles, String list) {
        double[] output = new double[numDoubles];

        String[] outputString = parseString(numDoubles, list);

        for (int i = 0; i < output.length; i++) {
            output[i] = Double.parseDouble(outputString[i]);
        }

        return output;
    }

    public static Connection parseConnection(String list) {
        int[] values = StringUtils.parseIntList(2, list);
        Color color = StringUtils.parseColor(NUM_ARGS_CONNECTION, list);
        return new Connection(values[0], values[1], color);
    }

    public static Point parsePoint(String list) {
        double[] values = StringUtils.parseDoubleList(4, list);
        Color color = StringUtils.parseColor(NUM_ARGS_POINT, list);
        return new Point(new Vector4D(values[0], values[1], values[2], values[3]), color, false);
    }

    public static int[] parseIntList(int numInts, String list) {
        int[] output = new int[numInts];
        double[] doubles = parseDoubleList(numInts, list);
        for (int i = 0; i < output.length; i++) {
            output[i] = (int)doubles[i];
        }
        return output;
    }

    public static Color parseColor(int start, String list) {
        Color output;

        String[] lineArray = parseString(start, list);
        String colorShort = lineArray[lineArray.length-1];
        if (colorShort != null) {
            output = Color.valueOf(colorShort);
        } else {
            output = Color.BLACK;
        }

        return output;
    }
}
