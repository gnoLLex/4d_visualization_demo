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
    public ListView<Connection> listViewConnections;
    //endregion

    /**
     * GraphicsContext of the Canvas for global usage
     */
    private GraphicsContext graphicsContext;

    /**
     * RotationHandler for global usage
     */
    private ProjectionHandler projectionHandler;

    /**
     * FileChooser for loading in .obj4d files
     */
    private FileChooser fileChooser;

    /**
     * Path for the drawn 4D object
     */
    private String objFilePath;

    /**
     * Path for the coordinate
     */
    private String coordinateSystemFilePath;

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
    /**
     * relative path to the .obj4d-file for the coordinate-system
     */
    private static final String PATH_TO_COORDINATESYSTEM = "/objects/.coordinateSystem.obj4d";

    /**
     * relative path to the .obj4d-file for the tesseract
     */
    private static final String PATH_TO_TESSERACT = "/objects/tesseract.obj4d";

    /**
     * relative path to the .obj4d-file for the simplex
     */
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

    //region Initalize/Reset
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
        graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setLineWidth(LINE_WIDTH);

        // initializing the matrixhandler
        projectionHandler = new ProjectionHandler();

        coordinateSystemFilePath = PATH_TO_COORDINATESYSTEM;
        objFilePath = PATH_TO_TESSERACT;

        // initializing filechooser and adding a extensionfilter
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("4D Object Files", "*.obj4d"));

        listViewConnections.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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
            // loading coordinate-system
            coordinateSystem = Object4DSerializer.loadObj4D(coordinateSystemFilePath, this);

            // configuring the coordinate-system to not be selectable
            for (Point point: coordinateSystem.getPoints()) {
                point.setSelectable(false);
            }

            // checking if the 4D object is loaded from custom source
            if (!objFilePath.equals(PATH_TO_TESSERACT) && !objFilePath.equals(PATH_TO_SIMPLEX)) {
                // loading custom 4D object
                obj4DToDraw = Object4DSerializer.loadObj4D(objFilePath);
            } else {
                // loading sample 4D object
                obj4DToDraw = Object4DSerializer.loadObj4D(objFilePath, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // setting the shown name according to the loaded object
        text4DObj.setText(obj4DToDraw.getName());

        // resetting zoom
        projectionHandler.zDisplacement = INIT_ZOOM;

        // rotating to initial position
        coordinateSystem.rotate("Y", INIT_Y_ANGLE);
        coordinateSystem.rotate("X", INIT_X_ANGLE);

        selectedPointIndex = -1;

        setListViewConnections();
        redraw();
    }

    /**
     * Resets the UI
     */
    private void resetUI() {
        for(Slider slider: sliders) {
            slider.setValue(0);
        }
        for(CheckBox cB: checkBoxes) {
            cB.setSelected(false);
        }
        // setting colorpicker to Black
        colorPickerPoint.setValue(Color.BLACK);
        colorPickerConnection.setValue(Color.BLACK);
        // disabling button for adding connection (point needs to be selected first)
        btnAddConnection.setDisable(true);
    }

    //endregion

    //region Drawing

    /**
     * Draws a 4D object on the canvas of this application
     * @param obj4d 4D object to be drawn
     */
    private void drawObject4D(Object4D obj4d) {
        // projecting to 2D
        Vector2D[] context2D = obj4d.project(canvas, projectionHandler);

        // draw a line for every connection in the 4D object
        for (Connection con: obj4d.getConnections()) {
            graphicsContext.setStroke(con.getColor());
            line(context2D, con.getIndexOne(), con.getIndexTwo());
        }

        // if connection is selected highlight it
        for (Connection connection: listViewConnections.getSelectionModel().getSelectedItems()) {
            highlightConnection(connection);
        }

        // draw a circle for every point in the 4D object
        ArrayList<Point> points = obj4d.getPoints();
        for (int i = 0; i < points.size(); i++) {
            // only draw the point when it is selectable
            if (points.get(i).isSelectable()) {
                // drawing circle at points position
                // offsetting because fillOval does not draw from the center of the oval
                graphicsContext.setFill(points.get(i).getColor());
                double offset = DIAMETER_POINT / 2;
                double x = context2D[i].x - offset;
                double y = context2D[i].y - offset;
                graphicsContext.fillOval(x, y, DIAMETER_POINT, DIAMETER_POINT);

                // if a point is selected, highlight it
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
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * draws line between two vectors out of an array of vectors
     * @param vectors Array of Vector2D
     * @param i Index of vector one
     * @param j Index of vector two
     */
    private void line(Vector2D[] vectors, int i, int j) {
        graphicsContext.strokeLine( vectors[i].x,  vectors[i].y,  vectors[j].x,  vectors[j].y);
    }

    /**
     * clears the screen and draws everything again
     */
    private void redraw() {
        projectionHandler.calcProj3DTo2D((canvas.getHeight() / canvas.getWidth()));

        // getting the absolute rotated 4D object how its viewed
        camera = obj4DToDraw.rotateToCoord(coordinateSystem);
        clearCanvas();

        // drawing coordinate-system if wanted
        if (cBshowCS.isSelected()) drawObject4D(coordinateSystem);

        // drawing 4D object
        drawObject4D(camera);
    }

    //endregion

    //region File-Loading-Saving

    /**
     * Loads up the tesseract.
     */
    public void loadTesseract() {
        objFilePath = PATH_TO_TESSERACT;
        reset();
        lblTipp.setText("Tesseract loaded");
    }

    /**
     * Loads up the simplex.
     */
    public void loadSimplex() {
        objFilePath = PATH_TO_SIMPLEX;
        reset();
        lblTipp.setText("Simplex loaded");
    }

    /**
     * Opens up the file-chooser to the user.
     * @param e event
     */
    public void loadObj4DFile(Event e) {
        Node source = (Node) e.getSource();
        Window stage = source.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        lblTipp.setText("Loading...");
        if (file != null) {
            objFilePath = file.getPath();
            reset();
            lblTipp.setText("Loading successful");
        } else {
            lblTipp.setText("Loading Failed. No file selected!");
        }
    }

    /**
     * Opens up the file-chooser to the user to save a file.
     * @param e event
     */
    public void saveObj4DFile(Event e) {
        Node source = (Node) e.getSource();
        Window stage = source.getScene().getWindow();
        File destination = fileChooser.showSaveDialog(stage);
        lblTipp.setText("Saving...");
        if (destination != null) {
            Object4DSerializer.saveObj4d(obj4DToDraw, destination);
            objFilePath = destination.getPath();
            lblTipp.setText("Saving successful");
        } else {
            lblTipp.setText("Saving Failed. No file name chosen!");
        }
    }
    //endregion

    //region Mouserotation

    /**
     * Position of the mouse.
     */
    private Vector2D mousePosition;

    /**
     * Sets the mousePosition to the position of the mouse when it is pressed.
     * Also checks and selects for a point.
     * @param e mouse event where the position is get from
     */
    public void setVector(MouseEvent e) {
        mousePosition = new Vector2D(e.getX(), e.getY());
        checkSelectPoint();
        listViewConnections.getSelectionModel().select(-1);
    }

    /**
     * Updates the mousePosition and calculates for how much the coordinate system should be rotated.
     * @param e mouse event to get the position of the mouse
     */
    public void updateVector(MouseEvent e) {
        Vector2D oldVector = new Vector2D(mousePosition.x, mousePosition.y);
        mousePosition = new Vector2D(e.getX(), e.getY());

        double horTheta = (mousePosition.x - oldVector.x) / 100;
        double verTheta = (mousePosition.y - oldVector.y) / 100;

        coordinateSystem.rotate("X", verTheta);
        coordinateSystem.rotate("Y", horTheta);

        redraw();
    }

    /**
     * Changes the distance to the 4D object according to the scroll-wheel
     * @param s scroll event to get the position of the scroll-wheel
     */
    public void zoom(ScrollEvent s) {
        if (projectionHandler.zDisplacement - s.getDeltaY() / 100 >= 2) {
            projectionHandler.zDisplacement -= s.getDeltaY() / 100;
            redraw();
        }
    }

    //endregion

    //region Pointslection
    /**
     * Index of the selected point
     */
    private int selectedPointIndex = -1;

    /**
     * Checks if the distance of the mouse to a point is less than a certain threshold and selects the nearest point.
     * Also sets up the values for the point-editor and checks if a connection should be added.
     */
    private void checkSelectPoint() {
        // check if in range of the drawn point
        double maxDist = DIAMETER_POINT;
        Vector2D[] points = camera.project(canvas, projectionHandler);
        for (int i = 0; i < points.length; i++) {
            // getting distance in 2D plane
            double dist = points[i].dist(mousePosition);
            if ( dist < maxDist) {
                // if in range
                if (selectedPointIndex != i) {
                    // if not same point select
                    selectedPointIndex = i;
                    // this checks for the nearest point
                    maxDist = dist;
                } else {
                    // if same point unselect
                    selectedPointIndex = -1;
                }
            }
        }
        Vector4D values;
        if (selectedPointIndex != -1) {
            // set values according to the selected point
            Point point = obj4DToDraw.getPoints().get(selectedPointIndex);
            values = point.getValues();
            colorPickerPoint.setValue(point.getColor());
            lblTipp.setText("Selected Point-Index: " + selectedPointIndex);

            // enable button for adding connection
            btnAddConnection.setDisable(false);

            // if connection can be added and the points to connect are not the same add a connection to the 4D object
            if (addConnectionFlag && oldSelectedPointIndex != selectedPointIndex) {
                Color color = colorPickerConnection.getValue();
                Connection connection = new Connection(oldSelectedPointIndex, selectedPointIndex, color);
                obj4DToDraw.getConnections().add(connection);
            }
        } else {
            // set values to standard values
            values = new Vector4D();
            colorPickerPoint.setValue(Color.BLACK);
            btnAddConnection.setDisable(true);
            lblTipp.setText("no Point selected");
        }
        // set the point-editor UI to values
        textXValue.setText(Double.toString(values.x));
        textYValue.setText(Double.toString(values.y));
        textZValue.setText(Double.toString(values.z));
        textWValue.setText(Double.toString(values.w));

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
        addTextFieldListeners();

        for (int i = 0; i < sliders.length; i++) {
            int finalI = i;
            // when slider-value is changed rotate
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

        // adds a listener to the width and height property, when changed it redraw
        canvas.heightProperty().addListener((e) -> redraw());
        canvas.widthProperty().addListener((e) -> redraw());

        // when unselected redraw and therefore the coordinate-system will not be drawn
        cBshowCS.selectedProperty().addListener(e -> {redraw();});

        // timeline for automatic rotation
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        e -> {
                            for (int i = 0; i < checkBoxes.length; i++) {
                                if (checkBoxes[i].isSelected()) {
                                    obj4DToDraw.rotate(around[i], ANIMATION_ROTATION_SPEED);
                                    if (selectedPointIndex != -1) {
                                        Vector4D values = obj4DToDraw.getPoints().get(selectedPointIndex).getValues();
                                        textXValue.setText(Double.toString(values.x));
                                        textYValue.setText(Double.toString(values.y));
                                        textZValue.setText(Double.toString(values.z));
                                        textWValue.setText(Double.toString(values.w));
                                    }
                                    redraw();
                                }
                            }
                        }
                ),
                new KeyFrame(Duration.millis(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // when a connection is selected redraw and therefore highlight the connection
        listViewConnections.getSelectionModel().selectedIndexProperty().addListener((ov, old_val, new_val) -> {
            redraw();
        });
    }
    //endregion

    //region Point-Editor
    /**
     * Adds listeners for the the textfields.
     */
    private void addTextFieldListeners() {
        // setting name of the 4D object according to the value in the textfield
        text4DObj.textProperty().addListener((ov, old_val, new_val) -> obj4DToDraw.setName(new_val));

        // textformatter that only allows double-values in the textfield
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

        // adding textformatter to each textfield
        for (TextField t : textFields) {
            TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, filter);
            t.setTextFormatter(textFormatter);
        }

        // changing values of the selected point to the values in the textfields
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

    /**
     * Checks if a point is selected and if the string is just empty or just a minus.
     * @param value string to be validated
     * @return true if valid
     */
    private boolean isValid(String value) {
        return selectedPointIndex != -1 && !value.equals("") && !value.equals("-") && !value.equals(".");
    }

    /**
     * Adds a point to the 4D object according to the textfield-values.
     */
    public void addPoint() {
        // getting values from the textfields
        double outputX = Double.parseDouble(textXValue.getText());
        double outputY = Double.parseDouble(textYValue.getText());
        double outputZ = Double.parseDouble(textZValue.getText());
        double outputW = Double.parseDouble(textWValue.getText());
        Vector4D values = new Vector4D(outputX, outputY, outputZ, outputW);
        Color color = colorPickerPoint.getValue();
        obj4DToDraw.getPoints().add(new Point(values, color, true));

        // setting the added point as the selected
        selectedPointIndex = obj4DToDraw.getPoints().size() - 1;

        setListViewConnections();
        redraw();
    }

    /**
     * Removes the selected point from the 4D object.
     */
    public void removePoint() {
        obj4DToDraw.removePointOfIndex(selectedPointIndex);
        selectedPointIndex = -1;
        redraw();
    }

    /**
     * Changes the color of the selected point, if one is selected.
     */
    public void changeColorPoint() {
        if (selectedPointIndex != -1) {
            obj4DToDraw.getPoints().get(selectedPointIndex).setColor(colorPickerPoint.getValue());
            redraw();
        }
    }

    /**
     * Highlights the selected point.
     * @param context2D 2D values of the points
     * @param index index of the point to highlight
     */
    private void highlightPoint(Vector2D[] context2D, int index) {
        // drawing red oval around the point
        graphicsContext.setStroke(Color.RED);
        // offset for centering the oval to the point
        double offset = DIAMETER_HIGHLIGHT / 2;
        double x = context2D[index].x - offset;
        double y = context2D[index].y - offset;
        graphicsContext.strokeOval(x, y, DIAMETER_HIGHLIGHT, DIAMETER_HIGHLIGHT);
    }
    //endregion

    //region Connection-Editor

    /**
     * Index of the previously selected point.
     */
    private int oldSelectedPointIndex;

    /**
     * Flag for whether a connection should be added or not.
     */
    private boolean addConnectionFlag = false;

    /**
     * Makes everything ready for adding a connection.
     * Adding the connection happens in the pointselection.
     */
    public void addConnection() {
        oldSelectedPointIndex = selectedPointIndex;
        addConnectionFlag = true;
        lblTipp.setText("Select the second point for the connection.");
    }

    /**
     * Removes the selected connection from the 4D object.
     */
    public void removeConnection() {
        obj4DToDraw.getConnections().removeAll(listViewConnections.getSelectionModel().getSelectedItems());
        setListViewConnections();
        redraw();
    }

    /**
     * Changes the color of the selected connection, if one is selected.
     */
    public void changeColorConnection() {
        for (Connection connection: listViewConnections.getSelectionModel().getSelectedItems()) {
            connection.setColor(colorPickerConnection.getValue());
        }
        listViewConnections.refresh();
        redraw();
    }

    /**
     * Highlights a connection by highlighting the contained point.
     * @param connection connection to be highlighted
     */
    private void highlightConnection(Connection connection) {
        if (connection == null) return;
        colorPickerConnection.setValue(connection.getColor());
        Vector2D[] context2D = camera.project(canvas, projectionHandler);
        highlightPoint(context2D, connection.getIndexOne());
        highlightPoint(context2D, connection.getIndexTwo());
    }

    /**
     * Sets the listview-items.
     */
    private void setListViewConnections() {
        ArrayList<Connection> connectionsToView = new ArrayList<>(0);
        if (selectedPointIndex != -1) {
            // setting the items to all connection that contain the selected point
            for (Connection  connection: obj4DToDraw.getConnections()) {
                if (connection.containsPoint(selectedPointIndex) > 0) {
                    connectionsToView.add(connection);
                }
            }
        } else {
            // setting the items ti all connections
            connectionsToView.addAll(obj4DToDraw.getConnections());
        }
        ObservableList<Connection> obsConnections = FXCollections.observableArrayList(connectionsToView);
        listViewConnections.setItems(obsConnections);
    }
    //endregion
}
