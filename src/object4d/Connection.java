package object4d;

import javafx.scene.paint.Color;
/**
 * Represents a connection of two points in an array with their index's.
 * @author Lucas Engelmann
 */
public class Connection {
    /**
     * Index of point one.
     */
    private int indexOne;

    /**
     * Index of point two.
     */
    private int indexTwo;

    /**
     * Color of the connection.
     */
    private Color color;

    /**
     * Initialized the connection with index one and two and a color.
     * @param indexOne index of point one
     * @param indexTwo index of point two
     * @param color color of the connection
     */
    public Connection(int indexOne, int indexTwo, Color color) {
        this.indexOne = indexOne;
        this.indexTwo = indexTwo;
        this.color = color;
    }

    /**
     * Connection represented as string.
     * @return String with index one and two and the color
     */
    public String toStringSer() {
        return indexOne + " " + indexTwo + " " + color;
    }

    /**
     * Connection represented as string with rgb-values.
     * @return String with index one and two and the color as rgb value
     */
    public String toString() {
        String rgb = "(" + (int)(color.getRed()*255) + "," + (int)(color.getGreen()*255) + "," + (int)(color.getBlue()*255) + ")";
        return " " + indexOne + "\t  " + indexTwo + "\t   " + rgb;
    }

    /**
     * @return index of point one
     */
    public int getIndexOne() {
        return indexOne;
    }

    /**
     * @return index of point two
     */
    public int getIndexTwo() {
        return indexTwo;
    }

    /**
     * Set's index of point one.
     * @param index index of point one
     */
    public void setIndexOne(int index) {
        this.indexOne = index;
    }

    /**
     * Set's index of point two.
     * @param index index of point two
     */
    public void setIndexTwo(int index) {
        this.indexTwo = index;
    }

    /**
     * @return color of the connection
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set's the color of the connection.
     * @param color color of the connection
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Checks the connection contains the index of the point
     * @param index index of the point
     * @return index from point in the connection
     */
    public int containsPoint(int index) {
        int out = 0;
        if (indexOne == index) out++;
        if (indexTwo == index) out = out + 2;
        return out;
    }
}
