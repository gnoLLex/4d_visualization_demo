import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
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
    private static double rSpeed = 0.6;

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
        // and to draw the tesseract for the first time
        reset();
        addListeners();
    }

    @FXML
    public void reset() {
        resetSliders();
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
        project(points, "X", 0.0);
    }

    //TODO: split up into projection and rotation
    private void project(Vector4D[] v, String axis, double theta) {
        for (int i = 0; i < v.length; i++) {
            v[i] = mh.rot(v[i], theta, axis);
            proj3D[i] = mh.project4DTo3D(v[i]);
            proj2D[i] = mh.project3DTo2D(proj3D[i], canvas.getWidth(), canvas.getHeight());
        }
        draw(proj2D);
    }

    private void clear() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void line(Vector2D[] p, int i, int j, int offset) {
        gc.strokeLine( p[i+offset].x,  p[i+offset].y,  p[j+offset].x,  p[j+offset].y);
    }

    private void draw(Vector2D[] v) {
        clear();
        for(int i = 0; i < 4; i++) {
            line(v, i, ( i + 1 ) % 4, 8);
            line(v, i + 4, ( ( i + 1 ) % 4 ) + 4, 8);
            line(v, i, i + 4, 8);
        }
        for(int i = 0; i < 4; i++) {
            line(v, i, ( i + 1 ) % 4, 0);
            line(v, i + 4, ( ( i + 1 ) % 4 ) + 4, 0);
            line(v, i, i + 4, 0);
        }
        for(int i = 0; i < 8; i++) {
            line(v, i, i + 8, 0);
        }
    }

    /**
     * resetting all sliders4
     */
    private void resetSliders() {
        for(Slider slider: sliders) {
            slider.setValue(0);
        }
    }

    /**
     * adding the listeners to certain application elements
     */
    private void addListeners() {
        for(int i = 0; i < sliders.length; i++) {
            int finalI = i;
            sliders[i].valueProperty().addListener((ov, old_val, new_val) -> {
                double theta = rSpeed * (new_val.doubleValue() - old_val.doubleValue());
                project(points, planes[finalI], theta);
            });
        }


        // binding the width and height property to the ones of the pane
        // a cheaty little solution to get a resizeable canvas
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        // adds a listener to the width and height property
        // when changed it recalculates the projection matrix for the 3D to 2D projection
        canvas.heightProperty().addListener((e) -> {
            mh.calcProj3DTo2D((canvas.getHeight() / canvas.getWidth()));
            project(points, "X", 0.0);
        });
        canvas.widthProperty().addListener((e) -> {
            mh.calcProj3DTo2D((canvas.getHeight() / canvas.getWidth()));
            project(points, "X", 0.0);
        });
    }
}
