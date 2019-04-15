import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import vector.Vector2D;
import vector.Vector3D;
import vector.Vector4D;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Pane canvasPane;
    @FXML
    private Canvas canvas;

    @FXML
    private Slider sdrX;
    @FXML
    private Slider sdrY;
    @FXML
    private Slider sdrZ;
    @FXML
    private Slider sdrXW;
    @FXML
    private Slider sdrYW;
    @FXML
    private Slider sdrZW;

    private Slider[] sliders;
    private String[] planes;

    private GraphicsContext gc;
    private MatrixHandler mh;

    // constant for how fast the rotation will be done
    private static double rSpeed = 0.8;

    // vectors for coordinate system
    private  Vector4D[] coord = new Vector4D[4];
    private Vector3D[] coord3D = new Vector3D[coord.length];
    private Vector2D[] coord2D = new Vector2D[coord.length];
    // arrays containing of points in n space
    private Vector4D[] points = new Vector4D[16];
    private Vector3D[] proj3D = new Vector3D[points.length];
    private Vector2D[] proj2D = new Vector2D[points.length];

    //is called to initialize a controller after its root element has been completely processed.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // filling array with the sliders
        sliders = new Slider[]{sdrX, sdrY, sdrZ, sdrXW, sdrYW, sdrZW};

        // giving the axes/planes a corresponding name
        planes = new String[]{"X", "Y", "Z", "XW", "YW", "ZW"};

        // getting the GraphicsContext2D
        gc = canvas.getGraphicsContext2D();

        // initializing the matrixhandler
        mh = new MatrixHandler();
        // generate initial projection matrix from 3D to 2D
        mh.calcProj3DTo2D(canvas.getHeight() / canvas.getWidth());

        // resets first time as initialization for the arrays points, proj3D and proj2D
        // and to drawPoints the tesseract for the first time
        reset();
        addListeners();
    }

    public void reset() {
        resetSliders();

        coord = new Vector4D[]{
                new Vector4D(),
                new Vector4D(1, 0, 0 ,0), // x-axis
                new Vector4D(0, 1, 0 ,0), // y-axis
                new Vector4D(0, 0, 1 ,0)  // z-axis
        };

        points = new Vector4D[]{
                new Vector4D(-1, -1, -1, -1), new Vector4D(1, -1, -1, -1), new Vector4D(1, 1, -1, -1), new Vector4D(-1, 1, -1, -1),
                new Vector4D(-1, -1, 1, -1), new Vector4D(1, -1, 1, -1), new Vector4D(1, 1, 1, -1), new Vector4D(-1, 1, 1, -1),
                new Vector4D(-1, -1, -1, 1), new Vector4D(1, -1, -1, 1), new Vector4D(1, 1, -1, 1), new Vector4D(-1, 1, -1, 1),
                new Vector4D(-1, -1, 1, 1), new Vector4D(1, -1, 1, 1), new Vector4D(1, 1, 1, 1), new Vector4D(-1, 1, 1, 1)
        };
        for(int i = 0; i < points.length; i++) {
            proj3D[i] = new Vector3D();
            proj2D[i] = new Vector2D();
        }
        mh.zDisplacement = 4.5;

        redraw();
    }

    // rotates a set of 4D vectors and projects them after
    private void rotate(Vector4D[] v, String axis, double theta) {
        for (int i = 0; i < v.length; i++) {
            v[i] = mh.rot(v[i], theta, axis);
        }
    }

    // projects a set of 4D vectors down to 2D and drawPoints's them
    private void project(Vector4D[] v1, Vector3D[] v2, Vector2D[] v3) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        for (int i = 0; i < v1.length; i++) {
            v2[i] = mh.project4DTo3D(v1[i]);
            v3[i] = mh.project3DTo2D(v2[i], w, h);
        }
    }

    // clear's the screen
    private void clear() {
        // clear's a rectangle shape on the screen which in this case is the whole canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    // drawPoints's line from point to another point
    private void line(Vector2D[] p, int i, int j) {
        gc.strokeLine( p[i].x,  p[i].y,  p[j].x,  p[j].y);
    }

    // drawPoints's a set of 2D vectors by connecting 2 points
    private void drawPoints(Vector2D[] v) {
        // clear canvas before drawing on it
        clear();

        project(coord, coord3D, coord2D);
        drawCoord(coord2D);

        // connects the "outer" cube
        for(int i = 0; i < 4; i++) {
            line(v, i, ( i + 1 ) % 4);
            line(v, i + 4, ( ( i + 1 ) % 4 ) + 4);
            line(v, i, i + 4);
        }

        // connects the "inner" cube
        for(int i = 0; i < 4; i++) {
            line(v, i + 8, ( ( i + 1 ) % 4 ) + 8);
            line(v, i + 12, ( ( i + 1 ) % 4 ) + 12);
            line(v, i + 8, i + 12);
        }

        // connects first and second cube
        for(int i = 0; i < 8; i++) {
            line(v, i, i + 8);
        }
    }

    private void drawCoord(Vector2D[] v) {
        gc.setStroke(Color.BLUE);
        line(v, 0, 1);
        gc.setStroke(Color.RED);
        line(v, 0, 2);
        gc.setStroke(Color.GREEN);
        line(v, 0, 3);
        gc.setStroke(Color.BLACK);
    }

    private void redraw() {
        mh.calcProj3DTo2D((canvas.getHeight() / canvas.getWidth()));
        rotate(points, "X", 0.0);
        rotate(coord, "X", 0.0);
        project(points, proj3D, proj2D);
        drawPoints(proj2D);
    }


    //resetting all sliders4
    private void resetSliders() {
        for(Slider slider: sliders) {
            slider.setValue(0);
        }
    }

    //adding the listeners to certain application elements
    private void addListeners() {
        for(int i = 0; i < sliders.length; i++) {
            int finalI = i;
            sliders[i].valueProperty().addListener((ov, old_val, new_val) -> {
                double theta = rSpeed * (new_val.doubleValue() - old_val.doubleValue());
                rotate(points, planes[finalI], theta);
                project(points, proj3D, proj2D);
                drawPoints(proj2D);
            });
        }


        // binding the width and height property to the ones of the pane
        // a cheaty little solution to get a resizeable canvas
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        // adds a listener to the width and height property
        // when changed it recalculates the projection matrix for the 3D to 2D projection
        canvas.heightProperty().addListener((e) -> redraw());
        canvas.widthProperty().addListener((e) -> redraw());
    }

    private Vector2D currentVector = new Vector2D();

    public void setVector(MouseEvent e) {
        currentVector = new Vector2D(e.getX(), e.getY());
    }

    public void updateVector(MouseEvent e) {
        Vector2D oldVector = new Vector2D(currentVector.x, currentVector.y);
        currentVector = new Vector2D(e.getX(), e.getY());
        double horTheta = (currentVector.x - oldVector.x) / 100;
        double verTheta = (currentVector.y - oldVector.y) / 100;
        rotate(points, "X", verTheta);
        rotate(points, "Y", horTheta);
        rotate(coord, "X", verTheta);
        rotate(coord, "Y", horTheta);
        project(points, proj3D, proj2D);
        drawPoints(proj2D);
    }

    public void zoom(ScrollEvent s) {
        if (mh.zDisplacement - s.getDeltaY() / 100 >= 2) {
            mh.zDisplacement -= s.getDeltaY() / 100;
            System.out.println(mh.zDisplacement);
            rotate(points, "X", 0.0);
            rotate(coord, "X", 0.0);
            project(points, proj3D, proj2D);
            drawPoints(proj2D);
        }
    }
}
