package parser;

import javafx.scene.paint.Color;

public class StringUtils {

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

    public static double[] parseDoubleList(int numDoubles, String list) {
        double[] output = new double[numDoubles];
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
            output[returnArrayCount++] = Double.parseDouble(new String(charList, itemStart, itemLength));
            if (returnArrayCount >= numDoubles) {
                break;
            }

            count = itemEnd;
        }
        return output;
    }

    public static Connection parseConnection(String list){
        int[] values = StringUtils.parseIntList(2, list);
        Color color = StringUtils.parseColor(list);
        Connection output = new Connection(values[0], values[1], color);
        return output;
    }

    public static int[] parseIntList(int numInts, String list) {
        int[] output = new int[numInts];
        double[] doubles = parseDoubleList(numInts, list);
        for (int i = 0; i < output.length; i++) {
            output[i] = (int)doubles[i];
        }
        return output;
    }

    public static Color parseColor(String list) {
        Color output;

        String colorShort = "";

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
            colorShort = new String(charList, itemStart, itemLength);

            count = itemEnd;
        }
        switch (colorShort){
            case "b":
                output = Color.BLUE;
                break;
            case "r":
                output = Color.RED;
                break;
            case "g":
                output = Color.GREEN;
                break;
            default:
                output = Color.BLACK;
        }

        return output;
    }
}
