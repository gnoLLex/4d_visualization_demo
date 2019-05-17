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

    public String toString() {
        return indexOne + "\t" + indexTwo;
    }

    public int getIndexOne() {
        return indexOne;
    }

    public int getIndexTwo() {
        return indexTwo;
    }

    public void setIndexOne(int index) {
        this.indexOne = index;
    }

    public void setIndexTwo(int index) {
        this.indexTwo = index;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int containsPoint(int index)
    {
        int out = 0;
        if (indexOne == index) out++;
        if (indexTwo == index) out = out + 2;
        return out;
    }
}
