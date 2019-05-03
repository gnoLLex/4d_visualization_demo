package main;

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

/** Controls the JavaFx Application
 * @author Lucas Engelmann
 * @version 1.0
 * @since 1.0
 */
public class Controller implements Initializable {
    //region Global variables

    //region JavaFX UI Elements

    /**
     * Pane containing the canvas
     * necessary to resize the canvas
     */
    @FXML
    private Pane canvasPane;

    /**
     * Canvas to draw tesseract on
     */
    @FXML
    private Canvas canvas;

    /**
     * Slider to control the rotation around the x-axis
     */
    @FXML
    private Slider sdrX;

    /**
     * Slider to control the rotation around the y-axis
     */
    @FXML
    private Slider sdrY;

    /**
     * Slider to control the rotation around the z-axis
     */
    @FXML
    private Slider sdrZ;

    /**
     * Slider to control the rotation around the xw-plane
     */
    @FXML
    private Slider sdrXW;

    /**
     * Slider to control the rotation around the yw-plane
     */
    @FXML
    private Slider sdrYW;

    /**
     * Slider to control the rotation around the zw-plane
     */
    @FXML
    private Slider sdrZW;

    /**
     * CheckBox to control if the tesseract should rotate around the x-axis automatically
     */
    @FXML
    private CheckBox cbX;

    /**
     * CheckBox to control if the tesseract should rotate around the y-axis automatically
     */
    @FXML
    private CheckBox cbY;

    /**
     * CheckBox to control if the tesseract should rotate around the z-axis automatically
     */
    @FXML
    private CheckBox cbZ;

    /**
     * CheckBox to control if the tesseract should rotate around the xw-plane automatically
     */
    @FXML
    private CheckBox cbXW;

    /**
     * CheckBox to control if the tesseract should rotate around the yw-plane automatically
     */
    @FXML
    private CheckBox cbYW;

    /**
     * CheckBox to control if the tesseract should rotate around the zw-plane automatically
     */
    @FXML
    private CheckBox cbZW;

    /**
     * array containing all sliders
     */
    private Slider[] sliders;

    /**
     * array containing all checkboxes
     */
    private CheckBox[] checkBoxes;

    /**
     * array containing all names for axis or planes to rotate around
     */
    private String[] around;
    //endregion

    /**
     * GraphicsContext of the Canvas for global usage
     */
    private GraphicsContext gc;

    /**
     * RotationHandler for global usage
     */
    private RotationHandler rh;

    //region Vector Arrays

    /**
     * array to store coordinate system
     */
    private Vector4D[] coord = new Vector4D[4];

    /**
     * array to store coordinate system projected to 3d-space
     */
    private Vector3D[] coord3D = new Vector3D[coord.length];

    /**
     * array to store coordinate system projected to 2d-plane
     */
    private Vector2D[] coord2D = new Vector2D[coord.length];

    /**
     * array to store the points of the tesseract
     */
    private Vector4D[] tesseract = new Vector4D[16];

    /**
     * array to store the points of the tesseract which are rotated by mouse-control
     */
    private Vector4D[] camera = new Vector4D[tesseract.length];

    /**
     * array to store the points of the tesseract projected to 3d-space
     */
    private Vector3D[] proj3D = new Vector3D[tesseract.length];

    /**
     * array to store the points of the tesseract projected to 2d-plane
     */
    private Vector2D[] proj2D = new Vector2D[tesseract.length];

    //endregion

    //endregion

    //region Constants

    /**
     * Constant for how fast the rotation will be done
     */
    private static final double ROTATION_SPEED = Math.PI / 4;

    /**
     * Constant for how fast the automatic rotation is happening
     */
    private static final double ANIMATION_ROTATION_SPEED = 0.0005;

    /**
     * Thickness of the lines
     */
    private static final double LINE_WIDTH = 1.4;


    /**
     * Initial rotation-angle for y-axis
     */
    private static final double INIT_Y_ANGLE = Math.PI / 5;

    /**
     * Initial rotation-angle for x-axis
     */
    private static final double INIT_X_ANGLE = Math.PI / 20;

    /**
     * Initial zoom-factor
     */
    private static final double INIT_ZOOM = 4.5;
    //endregion

    /**
     * Is called to initialize the controller after its root element has been completely processed.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // filling slider-array with all sliders
        sliders = new Slider[]{sdrX, sdrY, sdrZ, sdrXW, sdrYW, sdrZW};

        // filling checkbox-array with all checkboxes
        checkBoxes = new CheckBox[]{cbX, cbY, cbZ, cbXW, cbYW, cbZW};

        // giving the axes/planes a corresponding name
        around = new String[]{"X", "Y", "Z", "XW", "YW", "ZW"};

        // getting the GraphicsContext2D
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(LINE_WIDTH);

        // initializing the matrixhandler
        rh = new RotationHandler();
        // generate initial projection matrix from 3D to 2D
        rh.calcProj3DTo2D(canvas.getHeight() / canvas.getWidth());

        // one time use in the beginning
        addListeners();

        // resets first time as initialization for the vector-arrays
        // and to draw everything for the first time
        reset();
    }

    /**
     * Resets everything to the initial state
     */
    public void reset() {
        resetUI();

        // all points for a coordinate system
        coord = new Vector4D[]{
                new Vector4D(),                      // center-point
                new Vector4D(1, 0, 0 ,0), // x-axis
                new Vector4D(0, 1, 0 ,0), // y-axis
                new Vector4D(0, 0, 1 ,0), // z-axis
        };

        // all points in a tesseract(can also be represented as the binary-numbers from 0 to 15
        tesseract = new Vector4D[]{
                new Vector4D(-1, -1, -1, -1), new Vector4D(1, -1, -1, -1), new Vector4D(1, 1, -1, -1), new Vector4D(-1, 1, -1, -1),
                new Vector4D(-1, -1, 1, -1), new Vector4D(1, -1, 1, -1), new Vector4D(1, 1, 1, -1), new Vector4D(-1, 1, 1, -1),
                new Vector4D(-1, -1, -1, 1), new Vector4D(1, -1, -1, 1), new Vector4D(1, 1, -1, 1), new Vector4D(-1, 1, -1, 1),
                new Vector4D(-1, -1, 1, 1), new Vector4D(1, -1, 1, 1), new Vector4D(1, 1, 1, 1), new Vector4D(-1, 1, 1, 1)
        };

        // resetting zoom
        rh.zDisplacement = INIT_ZOOM;

        // rotating to initial position
        rotate(coord, "Y", INIT_Y_ANGLE);
        rotate(coord, "X", INIT_X_ANGLE);

        redraw();
    }

    /**
     * Rotates a set of 4d vectors
     * @param v         Set of 4d vectors to be rotated
     * @param around    Around what it rotates the vector
     * @param theta     Angle by how much the vector is rotated
     */
    private void rotate(Vector4D[] v, String around, double theta) {
        for (int i = 0; i < v.length; i++) {
            v[i] = rh.rot(v[i], theta, around);
        }
    }

    /**
     * projects a set of 4D vectors down to 2D
     * @param toBeProjected Array of points
     * @param inThirdDim    Array to store the points projected to 3d
     * @param inSecondDim   Array to store the points projected to 2d
     */
    private void project(Vector4D[] toBeProjected, Vector3D[] inThirdDim, Vector2D[] inSecondDim) {
        // getting width and height
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        // for each point
        for (int i = 0; i < toBeProjected.length; i++) {
            inThirdDim[i] = rh.project4DTo3D(toBeProjected[i]);
            inSecondDim[i] = rh.project3DTo2D(inThirdDim[i], w, h);
        }
    }

    private void drawPoints(Vector2D[] v) {
        clearCanvas();

        project(coord, coord3D, coord2D);
        drawCoord();

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

    private void drawCoord() {
        gc.setStroke(Color.BLUE);
        line(coord2D, 0, 1);
        gc.setStroke(Color.RED);
        line(coord2D, 0, 2);
        gc.setStroke(Color.GREEN);
        line(coord2D, 0, 3);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(coord2D[0].x, coord2D[0].y, 1, 1);
    }

    /**
     * clears the canvas
     */
    private void clearCanvas() {
        // clear's a rectangle shape on the screen which in this case is the whole canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * draws line between two vectors out of an array of vectors
     * @param v Array of Vector2D
     * @param i Index of vector one
     * @param j Index of vector two
     */
    private void line(Vector2D[] v, int i, int j) {
        gc.strokeLine( v[i].x,  v[i].y,  v[j].x,  v[j].y);
    }

    private void redraw() {
        rh.calcProj3DTo2D((canvas.getHeight() / canvas.getWidth()));
        rotate(tesseract, "X", 0);
        rotate(coord, "X", 0);
        rotateToCoord(tesseract , camera);
        project(camera, proj3D, proj2D);
        drawPoints(proj2D);
    }


    /**
     * Resets all sliders and checkboxes in the UI
     */
    private void resetUI() {
        for(Slider slider: sliders) {
            slider.setValue(0);
        }
        for(CheckBox cB: checkBoxes) {
            cB.setSelected(false);
        }
    }


    /**
     * Adds the listeners to certain UI elements
     * and in addition a timeline for automatic rotation
     */
    private void addListeners() {
        for(int i = 0; i < sliders.length; i++) {
            int finalI = i;
            sliders[i].valueProperty().addListener((ov, old_val, new_val) -> {
                double newD = new_val.doubleValue();
                double oldD = old_val.doubleValue();
                double theta = ROTATION_SPEED * (newD - oldD);
                rotate(tesseract, around[finalI], theta);
                rotateToCoord(tesseract, camera);
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
                                    rotate(tesseract, around[i], ANIMATION_ROTATION_SPEED);
                                    redraw();
                                }
                            }
                        }
                ),
                new KeyFrame(Duration.millis(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    //region Mouserotation

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

        rotateToCoord(tesseract, camera);
        project(camera, proj3D, proj2D);
        drawPoints(proj2D);
    }

    public void zoom(ScrollEvent s) {
        if (rh.zDisplacement - s.getDeltaY() / 100 >= 2) {
            rh.zDisplacement -= s.getDeltaY() / 100;
            redraw();
        }
    }

    private void rotateToCoord(Vector4D[] input, Vector4D[] output){

        Vector4D[] world = new Vector4D[]{
                new Vector4D(),
                new Vector4D(1, 0, 0 ,0),
                new Vector4D(0, 1, 0 ,0),
                new Vector4D(0, 0, 1 ,0)
        };

        double firstAngle = world[1].angle3DToVec(coord[1]);
        Vector4D firstAxis = world[1].crossProd(coord[1]);

        for (int i = 0; i < world.length; i++) {
            world[i] = world[i].rotateByVector(firstAxis, firstAngle);
        }
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i].rotateByVector(firstAxis, firstAngle);
        }

        double secondAngle = world[2].angle3DToVec(coord[2]);
        Vector4D secondAxis = world[2].crossProd(coord[2]);

        for (int i = 0; i < output.length; i++) {
            output[i] = output[i].rotateByVector(secondAxis, secondAngle);
        }
    }
    //endregion
}
