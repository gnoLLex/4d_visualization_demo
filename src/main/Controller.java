package main;

import handlers.ProjectionHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.util.StringConverter;
import object4d.Object4D;
import object4d.Connection;
import object4d.Point;
import parser.Object4DSerializer;
import vector.Vector2D;
import vector.Vector4D;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

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
     * Label for the Name of the 4D Object
     */
    @FXML
    public TextField text4DObj;

    /**
     * Textfields containing coordinates of a Point
     */
    @FXML
    private TextField textXValue, textYValue, textZValue, textWValue;

    /**
     * Array containing all Textfields
     */
    private TextField[] textFields;

    /**
     * Slider to control the rotation around the an axis
     */
    @FXML
    private Slider sdrX, sdrY, sdrZ, sdrXW, sdrYW, sdrZW;

    /**
     * array containing all sliders
     */
    private Slider[] sliders;

    /**
     * CheckBoxes to control if the obj4DToDraw should rotate around an axis automatically
     */
    @FXML
    private CheckBox cbX, cbY, cbZ, cbXW, cbYW, cbZW;

    /**
     * array containing all checkboxes
     */
    private CheckBox[] checkBoxes;

    /**
     * array containing all names for axis or planes to rotate around
     */
    private String[] around;

    public ColorPicker colorPickerPoint;
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
     * Object4D
     */
    private Object4D obj4DToDraw;

    /**
     * Camera Object with total rotation
     */
    private Object4D camera;

    //endregion

    //region Constants

    private static final File INITIAL_DIRECTORY = new File("src/objects");

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
        // filling textfield-array with textfields
        textFields = new TextField[]{textXValue, textYValue, textZValue, textWValue};

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
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("4D Object Files", "*.obj4d"));
        fileChooser.setInitialDirectory(INITIAL_DIRECTORY);

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
            coordinateSystem = Object4DSerializer.loadObj4D(co);
            for (Point point: coordinateSystem.getPoints()) {
                point.setSelectable(false);
            }
            obj4DToDraw = Object4DSerializer.loadObj4D(obj);
            text4DObj.setText(obj4DToDraw.getName());
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

    private void drawObject4D(Object4D obj4d) {
        Vector2D[] context2D = obj4d.project(canvas, ph);
        for (Connection con: obj4d.getConnections()) {
            gc.setStroke(con.getColor());
            line(context2D, con.getIndexOne(), con.getIndexTwo());
        }
        ArrayList<Point> points = obj4d.getPoints();
        for (int i = 0; i < points.size(); i++) {
            //TODO: function
            if (points.get(i).isSelectable()) {
                gc.setFill(points.get(i).getColor());
                double diameter = 5.0;
                double x = context2D[i].x - diameter / 2;
                double y = context2D[i].y - diameter / 2;
                gc.fillOval(x, y, diameter, diameter);
                if (selectedPointIndex != -1) {
                    gc.setStroke(Color.RED);
                    diameter = 10.0;
                    x = context2D[selectedPointIndex].x - diameter / 2;
                    y = context2D[selectedPointIndex].y - diameter / 2;
                    gc.strokeOval(x, y, diameter, diameter);
                }
            }
        }
    }

    /**
     * clears the canvas
     */
    private void clearCanvas() {
        // clear's a rectangle shape on the screen which in this case is the whole canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    //region File-Loading-Saving
    public void loadObj4DFile(Event e) {
        Node source = (Node) e.getSource();
        Window stage = source.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        lblLoading.setText("Loading...");
        String loadingMessage;
        if (file != null) {
            obj = file;
            reset();
            loadingMessage = "Loading successful";
        } else {
            loadingMessage = "Loading Failed. No file selected!";
        }
        lblLoading.setText(loadingMessage);
    }

    public void saveObj4DFile(Event e) {
        Node source = (Node) e.getSource();
        Window stage = source.getScene().getWindow();
        File destination = fileChooser.showSaveDialog(stage);
        if (destination != null) {
            Object4DSerializer.saveObj4d(obj4DToDraw, destination);
        }
    }
    //endregion

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
        camera = obj4DToDraw.rotateToCoord(coordinateSystem);
        clearCanvas();
        drawObject4D(coordinateSystem);
        drawObject4D(camera);
    }


    /**
     * Resets all sliders and checkboxes in the UI
     */
    public void resetUI() {
        for(Slider slider: sliders) {
            slider.setValue(0);
        }
        for(CheckBox cB: checkBoxes) {
            cB.setSelected(false);
        }
    }

    //region Mouserotation

    private Vector2D mousePosition = new Vector2D();

    public void setVector(MouseEvent e) {
        mousePosition = new Vector2D(e.getX(), e.getY());
        //TODO: on mouse released
        selectPoint();
    }

    public void updateVector(MouseEvent e) {
        Vector2D oldVector = new Vector2D(mousePosition.x, mousePosition.y);
        mousePosition = new Vector2D(e.getX(), e.getY());

        double horTheta = (mousePosition.x - oldVector.x) / 100;
        double verTheta = (mousePosition.y - oldVector.y) / 100;

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

    //region Pointslection
    private int selectedPointIndex = -1;

    private void selectPoint() {
        Vector2D[] points = camera.project(canvas, ph);
        double smallestDist = 5;
        selectedPointIndex = -1;
        for (int i = 0; i < points.length; i++) {
            double dist = points[i].dist(mousePosition);
            if ( dist < smallestDist) {
                smallestDist = dist;
                selectedPointIndex = i;
            }
        }
        Vector4D point;
        if (selectedPointIndex != -1) {
            Point p = obj4DToDraw.getPoints().get(selectedPointIndex);
            point = p.getValues();
            colorPickerPoint.setValue(p.getColor());
        } else {
            point = new Vector4D();
            colorPickerPoint.setValue(Color.BLACK);
        }
        textXValue.setText(Double.toString(point.x));
        textYValue.setText(Double.toString(point.y));
        textZValue.setText(Double.toString(point.z));
        textWValue.setText(Double.toString(point.w));

        redraw();
    }

    public void addPoint() {
        double outputX = Double.parseDouble(textXValue.getText());
        double outputY = Double.parseDouble(textYValue.getText());
        double outputZ = Double.parseDouble(textZValue.getText());
        double outputW = Double.parseDouble(textWValue.getText());
        Vector4D values = new Vector4D(outputX, outputY, outputZ, outputW);
        Color color = colorPickerPoint.getValue();
        obj4DToDraw.getPoints().add(new Point(values, color, true));
        selectedPointIndex = obj4DToDraw.getPoints().size() - 1;
        redraw();
    }

    public void removePoint() {
        if (selectedPointIndex != -1) {
            obj4DToDraw.getPoints().remove(selectedPointIndex);
            ArrayList<Connection> con = obj4DToDraw.getConnections();
            for (int i = 0; i < con.size(); i++) {
                if (con.get(i).containsPoint(selectedPointIndex)) {
                    con.remove(i);
                }
            }
            redraw();
        }
    }

    public void changeColor() {
        if (selectedPointIndex != -1) {
            obj4DToDraw.getPoints().get(selectedPointIndex).setColor(colorPickerPoint.getValue());
            redraw();
        }
    }
    //endregion

    //region Ugly ListenerStuff
    /**
     * Adds the listeners to certain UI elements
     * and in addition a timeline for automatic rotation
     */
    private void addListeners() {
        text4DObj.textProperty().addListener((ov, old_val, new_val) -> obj4DToDraw.setName(new_val));

        addTextFieldListeners();

        for (int i = 0; i < sliders.length; i++) {
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
    //endregion

    //region Point-Editor

    private void addTextFieldListeners() {
        // credits to https://stackoverflow.com/a/45981297
        Pattern validEditingState;
        validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };

        StringConverter<Double> converter = new StringConverter<Double>() {
            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0;
                } else {
                    return Double.valueOf(s);
                }
            }

            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };
        for (TextField t : textFields) {
            TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, filter);
            t.setTextFormatter(textFormatter);
        }

        textXValue.textProperty().addListener((ov, old_val, new_val) -> {
            if (isValid(new_val)) {
                obj4DToDraw.getPoints().get(selectedPointIndex).getValues().x = Double.parseDouble(new_val);
                redraw();
            }
        });
        textYValue.textProperty().addListener((ov, old_val, new_val) -> {
            if (isValid(new_val)) {
                obj4DToDraw.getPoints().get(selectedPointIndex).getValues().y = Double.parseDouble(new_val);
                redraw();
            }
        });
        textZValue.textProperty().addListener((ov, old_val, new_val) -> {
            if (isValid(new_val)) {
                obj4DToDraw.getPoints().get(selectedPointIndex).getValues().z = Double.parseDouble(new_val);
                redraw();
            }
        });
        textWValue.textProperty().addListener((ov, old_val, new_val) -> {
            if (isValid(new_val)) {
                obj4DToDraw.getPoints().get(selectedPointIndex).getValues().w = Double.parseDouble(new_val);
                redraw();
            }
        });
    }

    private boolean isValid(String value) {
        return selectedPointIndex != -1 && !value.equals("") && !value.equals("-");
    }
    //endregion
}
