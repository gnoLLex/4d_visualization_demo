package object4d;

import javafx.scene.paint.Color;

public class Connection {
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