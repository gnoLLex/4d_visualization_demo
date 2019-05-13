package object4d;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class Connection implements Serializable {
    private int indexOne;
    private int indexTwo;
    private Color color;

    public Connection(int indexOne, int indexTwo, Color color) {
        this.indexOne = indexOne;
        this.indexTwo = indexTwo;
        this.color = color;
    }

    public Connection(Connection c) {
        this.indexOne = c.getIndexOne();
        this.indexTwo = c.getIndexTwo();
        this.color = c.getColor();
    }

    public String toString() {
        return indexOne + " " + indexTwo + " " + color;
    }

    public int getIndexOne() {
        return indexOne;
    }

    public int getIndexTwo() {
        return indexTwo;
    }

    public Color getColor() {
        return color;
    }
}
