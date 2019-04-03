import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
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

    @FXML
    private Button btnReset;

    private Slider[] sliders;
    private String[] planes;

    private GraphicsContext gc;
    private MatrixHandler mh;

    private static float rSpeed = 0.8f;

    private Vector4D[] points = new Vector4D[16];
    private Vector3D[] proj3D = new Vector3D[16];
    private Vector2D[] proj2D = new Vector2D[16];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sliders = new Slider[]{sdrX, sdrY, sdrZ, sdrXW, sdrYW, sdrZW};
        planes = new String[]{"X", "Y", "Z", "XW", "YW", "ZW"};

        gc = canvas.getGraphicsContext2D();

        mh = new MatrixHandler();
        mh.calcProjTo2D((float)(canvas.getHeight() / canvas.getWidth()));

        reset();
        addHandlers();
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
        foo(points, "X", 0.0f);
    }

    private void foo(Vector4D[] v, String axis, float theta) {
        clear();
        for (int i = 0; i < v.length; i++) {
            v[i] = mh.rot(v[i], theta, axis);
            proj3D[i] = mh.projectTo3D(v[i]);
            proj2D[i] = mh.projectTo2D(proj3D[i], canvas.getWidth(), canvas.getHeight());
        }
        drawPoints(proj2D);
    }

    private void clear() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void line(Vector2D[] p, int i, int j, int offset) {
        gc.strokeLine((double) p[i+offset].x, (double) p[i+offset].y, (double) p[j+offset].x, (double) p[j+offset].y);
    }

    private void drawPoints(Vector2D[] v) {
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

    private void resetSliders() {
        for(Slider slider: sliders) {
            slider.setValue(0);
        }
    }

    private void addHandlers() {
        for(int i = 0; i < sliders.length; i++) {
            int finalI = i;
            sliders[i].valueProperty().addListener((ov, old_val, new_val) -> {
                float theta = rSpeed * (new_val.floatValue() - old_val.floatValue());
                foo(points, planes[finalI], theta);
            });
        }

        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        canvas.heightProperty().addListener((e) -> {
            mh.calcProjTo2D((float)(canvas.getHeight() / canvas.getWidth()));
            foo(points, "X", 0.0f);
        });
        canvas.widthProperty().addListener((e) -> {
            mh.calcProjTo2D((float)(canvas.getHeight() / canvas.getWidth()));
            foo(points, "X", 0.0f);
        });
    }
}
