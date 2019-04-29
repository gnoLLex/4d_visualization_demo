import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
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
    private Slider sdrX, sdrY, sdrZ, sdrXW, sdrYW, sdrZW;

    @FXML
    private CheckBox cbX, cbY, cbZ, cbXW, cbYW, cbZW;

    private Slider[] sliders;
    private CheckBox[] checkBoxes;
    private String[] planes;

    private GraphicsContext gc;
    private MatrixVectorHandler mvh;

    // constant for how fast the rotation will be done
    private static double rSpeed = Math.PI / 4;

    // vectors for coordinate system
    private Vector4D[] coord = new Vector4D[4];
    private Vector4D[] coordCam = new Vector4D[coord.length];
    private Vector3D[] coord3D = new Vector3D[coord.length];
    private Vector2D[] coord2D = new Vector2D[coord.length];
    // arrays containing of points in n space
    private Vector4D[] points = new Vector4D[16];
    private Vector4D[] camera = new Vector4D[points.length];
    private Vector3D[] proj3D = new Vector3D[points.length];
    private Vector2D[] proj2D = new Vector2D[points.length];

    //is called to initialize a controller after its root element has been completely processed.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // filling array with the sliders
        sliders = new Slider[]{sdrX, sdrY, sdrZ, sdrXW, sdrYW, sdrZW};

        // giving the axes/planes a corresponding name
        planes = new String[]{"X", "Y", "Z", "XW", "YW", "ZW"};

        checkBoxes = new CheckBox[]{cbX, cbY, cbZ, cbXW, cbYW, cbZW};

        // getting the GraphicsContext2D
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1.4);

        // initializing the matrixhandler
        mvh = new MatrixVectorHandler();
        // generate initial projection matrix from 3D to 2D
        mvh.calcProj3DTo2D(canvas.getHeight() / canvas.getWidth());

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
                new Vector4D(0, 0, 1 ,0), // z-axis
        };

        points = new Vector4D[]{
                new Vector4D(-1, -1, -1, -1), new Vector4D(1, -1, -1, -1), new Vector4D(1, 1, -1, -1), new Vector4D(-1, 1, -1, -1),
                new Vector4D(-1, -1, 1, -1), new Vector4D(1, -1, 1, -1), new Vector4D(1, 1, 1, -1), new Vector4D(-1, 1, 1, -1),
                new Vector4D(-1, -1, -1, 1), new Vector4D(1, -1, -1, 1), new Vector4D(1, 1, -1, 1), new Vector4D(-1, 1, -1, 1),
                new Vector4D(-1, -1, 1, 1), new Vector4D(1, -1, 1, 1), new Vector4D(1, 1, 1, 1), new Vector4D(-1, 1, 1, 1)
        };

        for(int i = 0; i < points.length; i++) {
            camera[i] = new Vector4D();
            proj3D[i] = new Vector3D();
            proj2D[i] = new Vector2D();
        }
        mvh.zDisplacement = 4.5;

        redraw();
    }

    // rotates a set of 4D vectors and projects them after
    private void rotate(Vector4D[] v, String axis, double theta) {
        for (int i = 0; i < v.length; i++) {
            v[i] = mvh.rot(v[i], theta, axis);
        }
    }

    // projects a set of 4D vectors down to 2D and drawPoints's them
    private void project(Vector4D[] v1, Vector3D[] v2, Vector2D[] v3) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        for (int i = 0; i < v1.length; i++) {
            v2[i] = mvh.project4DTo3D(v1[i]);
            v3[i] = mvh.project3DTo2D(v2[i], w, h);
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

        project(coordCam, coord3D, coord2D);
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
        mvh.calcProj3DTo2D((canvas.getHeight() / canvas.getWidth()));
        rotate(points, "X", 0);
        rotate(coord, "X", 0);
        rotateTo(coord, coordCam);
        rotateTo(points , camera);
        project(camera, proj3D, proj2D);
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
                double newD = new_val.doubleValue();
                double oldD = old_val.doubleValue();
                double theta = rSpeed * (newD - oldD);
                rotate(points, planes[finalI], theta);
                rotateTo(points, camera);
                project(camera, proj3D, proj2D);
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

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        e -> {
                            for (int i = 0; i < checkBoxes.length; i++) {
                                if (checkBoxes[i].isSelected()) {
                                    rotate(points, planes[i], 0.0005);
                                }
                            }
                            redraw();
                        }
                ),
                new KeyFrame(Duration.millis(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
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

        rotate(coord, "X", verTheta);
        rotate(coord, "Y", horTheta);

        rotateTo(points, camera);
        project(camera, proj3D, proj2D);
        drawPoints(proj2D);
    }

    public void zoom(ScrollEvent s) {
        if (mvh.zDisplacement - s.getDeltaY() / 100 >= 2) {
            mvh.zDisplacement -= s.getDeltaY() / 100;
            redraw();
        }
    }

    private void rotateTo(Vector4D[] v1, Vector4D[] v2){

        Vector4D[] world = new Vector4D[]{
                new Vector4D(),
                new Vector4D(1, 0, 0 ,0),
                new Vector4D(0, 1, 0 ,0),
                new Vector4D(0, 0, 1 ,0)
        };

        double Xangle = Math.cos(world[1].dotProd(coord[1])/(world[1].magnitude() * coord[1].magnitude()));

        for (int i = 0; i < v1.length; i++) {
            v2[i] = mvh.rot(v1[i], 0, "X");
            v2[i] = mvh.rot(v2[i], 0, "Y");

        }
    }
}
