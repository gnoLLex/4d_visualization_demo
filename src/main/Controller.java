package main;

import handlers.ProjectionHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import objects.Object4D;
import parser.Connection;
import parser.Object4DLoader;
import vector.Vector2D;

import java.io.File;
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
     * Canvas to draw everything on
     */
    @FXML
    private Canvas canvas;

    /**
     * Label for loading status
     */
    @FXML
    public Label lblLoading;

    /**
     * Button for opening the File-Chooser
     */
    @FXML
    public Button btnLoadObj4d;

    /**
     * Label for the Name of the 4D Object
     */
    @FXML
    public Label lbl4DObj;

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
     * CheckBox to control if the obj4DToDraw should rotate around the x-axis automatically
     */
    @FXML
    private CheckBox cbX;

    /**
     * CheckBox to control if the obj4DToDraw should rotate around the y-axis automatically
     */
    @FXML
    private CheckBox cbY;

    /**
     * CheckBox to control if the obj4DToDraw should rotate around the z-axis automatically
     */
    @FXML
    private CheckBox cbZ;

    /**
     * CheckBox to control if the obj4DToDraw should rotate around the xw-plane automatically
     */
    @FXML
    private CheckBox cbXW;

    /**
     * CheckBox to control if the obj4DToDraw should rotate around the yw-plane automatically
     */
    @FXML
    private CheckBox cbYW;

    /**
     * CheckBox to control if the obj4DToDraw should rotate around the zw-plane automatically
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
    private ProjectionHandler ph;

    /**
     * FileChooser for loading in .obj4d files
     */
    private FileChooser fileChooser;

    private File obj;
    private File co;

    /**
     * Coordinate System
     */
    private Object4D coordinateSystem;

    /**
     * Tesseract
     */
    private Object4D obj4DToDraw;

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
        ph = new ProjectionHandler();

        co = new File("src/objects/.coordinateSystem.obj4d");
        obj = new File("src/objects/tesseract.obj4d");

        fileChooser = new FileChooser();

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
        try {
            coordinateSystem = Object4DLoader.parseObj4D(co);
            obj4DToDraw = Object4DLoader.parseObj4D(obj);
            lbl4DObj.setText(obj4DToDraw.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // resetting zoom
        ph.zDisplacement = INIT_ZOOM;

        // rotating to initial position
        coordinateSystem.rotate("Y", INIT_Y_ANGLE);
        coordinateSystem.rotate("X", INIT_X_ANGLE);

        redraw();
    }

    public void drawObject4D(Object4D obj4d) {
        for (Connection con: obj4d.getConnections()) {
            gc.setStroke(con.getColor());
            line(obj4d.project(canvas, ph), con.getIndexOne(), con.getIndexTwo());
        }
    }

    /**
     * clears the canvas
     */
    public void clearCanvas() {
        // clear's a rectangle shape on the screen which in this case is the whole canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void loadObj4DFile(Event e) {
        Node source = (Node) e.getSource();
        Window stage = source.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        lblLoading.setText("Loading...");
        String loadingMessage;
        if (file != null) {
            String path = file.toString();
            String fileType = path.substring(path.lastIndexOf("."));
            if (fileType.equals(".obj4d")) {
                obj = file;
                reset();
                loadingMessage = "Loading successful";
            } else {
                loadingMessage = "Loading failed. Not .obj4d file!";
            }
        } else {
            loadingMessage = "Loading Failed. No file selected!";
        }
        lblLoading.setText(loadingMessage);
    }

    /**
     * draws line between two vectors out of an array of vectors
     * @param v Array of Vector2D
     * @param i Index of vector one
     * @param j Index of vector two
     */
    private void line(Vector2D[] v, int i, int j) {
        //System.out.println(i + " " + j);
        //System.out.println(v[i].toString() + " " + v[j].toString());
        gc.strokeLine( v[i].x,  v[i].y,  v[j].x,  v[j].y);
    }

    private void redraw() {
        ph.calcProj3DTo2D((canvas.getHeight() / canvas.getWidth()));
        Object4D camera = obj4DToDraw.rotateToCoord(coordinateSystem);
        clearCanvas();
        drawObject4D(coordinateSystem);
        drawObject4D(camera);
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
                obj4DToDraw.rotate(around[finalI], theta);
                redraw();
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
                                    obj4DToDraw.rotate(around[i], ANIMATION_ROTATION_SPEED);
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

        coordinateSystem.rotate("X", verTheta);
        coordinateSystem.rotate("Y", horTheta);

        redraw();
    }

    public void zoom(ScrollEvent s) {
        if (ph.zDisplacement - s.getDeltaY() / 100 >= 2) {
            ph.zDisplacement -= s.getDeltaY() / 100;
            redraw();
        }
    }

    //endregion
}
