package main;

import handlers.ProjectionHandler;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import serializer.Object4DSerializer;
import vector.Vector2D;
import vector.Vector4D;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Controls the JavaFx Application
 * @author Lucas Engelmann
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
    public Label lblTipp;

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

    /**
     * CheckBox for whether the coordinatesystem is shown or not
     */
    public CheckBox cBshowCS;

    /**
     * Colorpicker for coloring the selected point or if nothing is selected the color for the point to add
     */
    public ColorPicker colorPickerPoint;

    /**
     * Colorpicker for coloring the selected connection or if nothing is selected the color for the connection to add
     */
    public ColorPicker colorPickerConnection;

    /**
     * Button to add a Connection to a point
     */
    public Button btnAddConnection;

    /**
     * Listview for listing all connections of the 4d object or those which contain the selected point
     */
    public ListView listViewConnections;
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

    /**
     * Path for the drawn 4D object
     */
    private String obj;

    /**
     * Path for the coordinate
     */
    private String co;

    /**
     * Coordinate System
     */
    private Object4D coordinateSystem;

    /**
     * Object4D with relative rotation
     */
    private Object4D obj4DToDraw;

    /**
     * Camera Object with total rotation
     */
    private Object4D camera;

    //endregion

    //region Constants
    private static final String PATH_TO_COORDINATESYSTEM = "/objects/.coordinateSystem.obj4d";

    private static final String PATH_TO_TESSERACT = "/objects/tesseract.obj4d";

    private static final String PATH_TO_SIMPLEX = "/objects/simplex.obj4d";

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
     * Diameter of the highlight circle
     */
    private static final double DIAMETER_HIGHLIGHT = 10.0;

    /**
     * Diameter of the points
     */
    private static final double DIAMETER_POINT = 5.0;

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

        //
        co = PATH_TO_COORDINATESYSTEM;
        obj = PATH_TO_TESSERACT;

        // initializing filechooser and adding a extensionfilter
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("4D Object Files", "*.obj4d"));

        // one time use in the beginning
        addListeners();

        // resets first time as initialization for the 4D objects
        // and to draw everything for the first time
        reset();
    }

    /**
     * Resets everything to the initial state
     */
    public void reset() {
        // reset the UI first because of the listeners, which change the object
        resetUI();
        try {
            coordinateSystem = Object4DSerializer.loadObj4D(co, this);
            for (Point point: coordinateSystem.getPoints()) {
                point.setSelectable(false);
            }
            if (!obj.equals(PATH_TO_TESSERACT) && !obj.equals(PATH_TO_SIMPLEX)) {
                obj4DToDraw = Object4DSerializer.loadObj4D(new File(obj));
            } else {
                obj4DToDraw = Object4DSerializer.loadObj4D(obj, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // setting the shown name according to the loaded object
        text4DObj.setText(obj4DToDraw.getName());

        // resetting zoom
        ph.zDisplacement = INIT_ZOOM;

        // rotating to initial position
        coordinateSystem.rotate("Y", INIT_Y_ANGLE);
        coordinateSystem.rotate("X", INIT_X_ANGLE);

        setListViewConnections();
        redraw();
    }

    /**
     * Resets the UI
     */
    public void resetUI() {
        for(Slider slider: sliders) {
            slider.setValue(0);
        }
        for(CheckBox cB: checkBoxes) {
            cB.setSelected(false);
        }
        // Setting colorpicker to Black
        colorPickerPoint.setValue(Color.BLACK);
        colorPickerConnection.setValue(Color.BLACK);
        // Disabling button for adding connections
        btnAddConnection.setDisable(true);
    }

    //region Drawing
    private void drawObject4D(Object4D obj4d) {
        Vector2D[] context2D = obj4d.project(canvas, ph);
        for (Connection con: obj4d.getConnections()) {
            gc.setStroke(con.getColor());
            line(context2D, con.getIndexOne(), con.getIndexTwo());
        }
        ArrayList<Point> points = obj4d.getPoints();
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).isSelectable()) {
                gc.setFill(points.get(i).getColor());
                double x = context2D[i].x - DIAMETER_POINT / 2;
                double y = context2D[i].y - DIAMETER_POINT / 2;
                gc.fillOval(x, y, DIAMETER_POINT, DIAMETER_POINT);
                if (selectedPointIndex != -1) {
                   highlightPoint(context2D, selectedPointIndex);
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
        if (cBshowCS.isSelected()) drawObject4D(coordinateSystem);
        drawObject4D(camera);
        highlightConnection();
    }

    //endregion

    //region File-Loading-Saving

    public void loadTesseract() {
        obj = PATH_TO_TESSERACT;
        reset();
        lblTipp.setText("Tesseract loaded");
    }

    public void loadSimplex() {
        obj = PATH_TO_SIMPLEX;
        reset();
        lblTipp.setText("Simplex loaded");
    }

    public void loadObj4DFile(Event e) {
        Node source = (Node) e.getSource();
        Window stage = source.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        lblTipp.setText("Loading...");
        if (file != null) {
            obj = file.getPath();
            reset();
            lblTipp.setText("Loading successful");
        } else {
            lblTipp.setText("Loading Failed. No file selected!");
        }
    }

    public void saveObj4DFile(Event e) {
        Node source = (Node) e.getSource();
        Window stage = source.getScene().getWindow();
        File destination = fileChooser.showSaveDialog(stage);
        lblTipp.setText("Saving...");
        if (destination != null) {
            Object4DSerializer.saveObj4d(obj4DToDraw, destination);
            lblTipp.setText("Saving successful");
        } else {
            lblTipp.setText("Saving Failed. No file name chosen!");
        }
    }
    //endregion

    //region Mouserotation

    private Vector2D mousePosition = new Vector2D();

    public void setVector(MouseEvent e) {
        mousePosition = new Vector2D(e.getX(), e.getY());
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

    public void selectPoint() {
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
        Vector4D vector;
        if (selectedPointIndex != -1) {
            Point point = obj4DToDraw.getPoints().get(selectedPointIndex);
            vector = point.getValues();
            colorPickerPoint.setValue(point.getColor());
            btnAddConnection.setDisable(false);

            if (addConnectionFlag && oldSelectedPointIndex != selectedPointIndex) {
                Color color = colorPickerConnection.getValue();
                Connection connection = new Connection(oldSelectedPointIndex, selectedPointIndex, color);
                obj4DToDraw.getConnections().add(connection);
            }
            lblTipp.setText("Selected Point-Index: " + selectedPointIndex);
        } else {
            vector = new Vector4D();
            colorPickerPoint.setValue(Color.BLACK);
            btnAddConnection.setDisable(true);
            lblTipp.setText("");
        }
        textXValue.setText(Double.toString(vector.x));
        textYValue.setText(Double.toString(vector.y));
        textZValue.setText(Double.toString(vector.z));
        textWValue.setText(Double.toString(vector.w));

        addConnectionFlag = false;
        setListViewConnections();
        redraw();
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

        cBshowCS.selectedProperty().addListener(e -> {redraw();});

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

        listViewConnections.getSelectionModel().selectedIndexProperty().addListener((ov, old_val, new_val) -> {
            redraw();
        });
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

    public void addPoint() {
        double outputX = Double.parseDouble(textXValue.getText());
        double outputY = Double.parseDouble(textYValue.getText());
        double outputZ = Double.parseDouble(textZValue.getText());
        double outputW = Double.parseDouble(textWValue.getText());
        Vector4D values = new Vector4D(outputX, outputY, outputZ, outputW);
        Color color = colorPickerPoint.getValue();
        obj4DToDraw.getPoints().add(new Point(values, color, true));
        selectedPointIndex = obj4DToDraw.getPoints().size() - 1;
        setListViewConnections();
        redraw();
    }

    public void removePoint() {
        obj4DToDraw.removePointOfIndex(selectedPointIndex);
        selectedPointIndex = -1;
        redraw();
    }

    public void changeColorPoint() {
        if (selectedPointIndex != -1) {
            obj4DToDraw.getPoints().get(selectedPointIndex).setColor(colorPickerPoint.getValue());
            redraw();
        }
    }

    private void highlightPoint(Vector2D[] context2D, int index) {
        gc.setStroke(Color.RED);
        double x = context2D[index].x - DIAMETER_HIGHLIGHT / 2;
        double y = context2D[index].y - DIAMETER_HIGHLIGHT / 2;
        gc.strokeOval(x, y, DIAMETER_HIGHLIGHT, DIAMETER_HIGHLIGHT);
    }
    //endregion

    //region Connection-Editor

    private int oldSelectedPointIndex;
    private boolean addConnectionFlag = false;

    public void addConnection() {
        oldSelectedPointIndex = selectedPointIndex;
        addConnectionFlag = true;
        lblTipp.setText("Select the second point for the connection.");
    }

    public void removeConnection() {
        obj4DToDraw.getConnections().remove(listViewConnections.getSelectionModel().getSelectedItem());
        setListViewConnections();
        redraw();
    }

    public void changeColorConnection() {
        Connection connection = (Connection) listViewConnections.getSelectionModel().getSelectedItem();
        if (connection != null) {
            connection.setColor(colorPickerConnection.getValue());
            redraw();
        }
    }

    private void highlightConnection() {
        Connection connection = (Connection) listViewConnections.getSelectionModel().getSelectedItem();
        if (connection != null) {
            colorPickerConnection.setValue(connection.getColor());
            Vector2D[] context2D = camera.project(canvas, ph);
            highlightPoint(context2D, connection.getIndexOne());
            highlightPoint(context2D, connection.getIndexTwo());
            setListViewConnections();
        }
    }

    private void setListViewConnections() {
        ArrayList<Connection> connectionsToView = new ArrayList<>(0);
        if (selectedPointIndex != -1) {
            ArrayList<Connection> connections = obj4DToDraw.getConnections();
            for (int i = 0; i < obj4DToDraw.getConnections().size(); i++) {
                Connection connection = connections.get(i);
                if (connection.containsPoint(selectedPointIndex) > 0) {
                    connectionsToView.add(connection);
                }
            }
        } else {
            connectionsToView.addAll(obj4DToDraw.getConnections());
        }
        ObservableList<Connection> obsConnections = FXCollections.observableArrayList(connectionsToView);
        listViewConnections.setItems(obsConnections);
    }

    //endregion
}
