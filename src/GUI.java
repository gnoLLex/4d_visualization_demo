import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import vector.Vector2D;

public class GUI extends BorderPane {
    private float w = 980.0f; //width
    private float h = 720.0f; //height

    private Canvas canvas;
    private GraphicsContext gc;

    public Slider rotationX, rotationY, rotationZ, rotationXW, rotationYW, rotationZW;
    public Button resetBtn;

    GUI() {
        this.setStyle("-fx-background-color: black;");

        Pane pane = new Pane();
        pane.setPrefSize(w, h);

        canvas = new Canvas(w, h);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        pane.getChildren().add(canvas);
        setCenter(pane);

        VBox menue = new VBox();
        menue.setMinWidth(400);

        Label X = new Label("Rotation around X-Axis:");
        rotationX = new Slider();
        rotationX.setMin(-4);
        rotationX.setMax(4);
        rotationX.setValue(0);

        Label Y = new Label("Rotation around Y-Axis:");
        rotationY = new Slider();
        rotationY.setMin(-4);
        rotationY.setMax(4);
        rotationY.setValue(0);

        Label Z = new Label("Rotation around Z-Axis:");
        rotationZ = new Slider();
        rotationZ.setMin(-4);
        rotationZ.setMax(4);
        rotationZ.setValue(0);

        Label XW = new Label("Rotation around XW-Plane:");
        rotationXW = new Slider();
        rotationXW.setMin(-10);
        rotationXW.setMax(10);
        rotationXW.setValue(0);

        Label YW = new Label("Rotation around YW-Plane:");
        rotationYW = new Slider();
        rotationYW.setMin(-10);
        rotationYW.setMax(10);
        rotationYW.setValue(0);

        Label ZW = new Label("Rotation around ZW-Plane:");
        rotationZW = new Slider();
        rotationZW.setMin(-10);
        rotationZW.setMax(10);
        rotationZW.setValue(0);

        resetBtn = new Button("Reset");

        menue.setSpacing(10);
        menue.getChildren().addAll(X, rotationX, Y, rotationY, Z, rotationZ, XW, rotationXW, YW, rotationYW, ZW, rotationZW, resetBtn);
        setRight(menue);

        gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
    }

    public GraphicsContext getGC() {
        return gc;
    }

    //clear canvas
    public void clear() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public float getW() {
        return (float)canvas.getWidth();
    }

    public float getH() { return (float)canvas.getHeight(); }

    public float getAspectRatio() {
        return h / w;
    }

    public void line(Vector2D[] p, int i, int j, int offset) {
        gc.strokeLine((double) p[i+offset].x, (double) p[i+offset].y, (double) p[j+offset].x, (double) p[j+offset].y);
    }

    public void drawPoints(Vector2D[] v) {
        for(int i = 0; i < 4; i++) {
            line(v, i, ( i + 1 ) % 4, 0);
            line(v, i + 4, ( ( i + 1 ) % 4 ) + 4, 0);
            line(v, i, i + 4, 0);
        }
        for(int i = 0; i < 4; i++) {
            line(v, i, ( i + 1 ) % 4, 8);
            line(v, i + 4, ( ( i + 1 ) % 4 ) + 4, 8);
            line(v, i, i + 4, 8);
        }
        for(int i = 0; i < 8; i++) {
            line(v, i, i + 8, 0);
        }
    }

    public void resetSliders(){
        rotationX.setValue(0);
        rotationY.setValue(0);
        rotationZ.setValue(0);
        rotationXW.setValue(0);
        rotationYW.setValue(0);
        rotationZW.setValue(0);
    }
}