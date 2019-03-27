import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import vector.*;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML public Pane canvasPane;
    @FXML public Canvas canvas;

    @FXML public Slider sdrX;
    @FXML public Slider sdrY;
    @FXML public Slider sdrZ;
    @FXML public Slider sdrXW;
    @FXML public Slider sdrYW;
    @FXML public Slider sdrZW;

    @FXML public Button btnReset;

    private GraphicsContext gc;
    private MatrixHandler mh;

    private static float rSpeed = 0.8f;
    private Vector4D[] points =
            {
                    new Vector4D(-1, -1, -1, -1), new Vector4D(1, -1, -1, -1), new Vector4D(1, 1, -1, -1), new Vector4D(-1, 1, -1, -1),
                    new Vector4D(-1, -1, 1, -1), new Vector4D(1, -1, 1, -1), new Vector4D(1, 1, 1, -1), new Vector4D(-1, 1, 1, -1),
                    new Vector4D(-1, -1, -1, 1), new Vector4D(1, -1, -1, 1), new Vector4D(1, 1, -1, 1), new Vector4D(-1, 1, -1, 1),
                    new Vector4D(-1, -1, 1, 1), new Vector4D(1, -1, 1, 1), new Vector4D(1, 1, 1, 1), new Vector4D(-1, 1, 1, 1)
            };
    private Vector3D[] proj3D =
            {
                    new Vector3D(), new Vector3D(), new Vector3D(), new Vector3D(),
                    new Vector3D(), new Vector3D(), new Vector3D(), new Vector3D(),
                    new Vector3D(), new Vector3D(), new Vector3D(), new Vector3D(),
                    new Vector3D(), new Vector3D(), new Vector3D(), new Vector3D()
            };
    private Vector2D[] proj2D =
            {
                    new Vector2D(), new Vector2D(), new Vector2D(), new Vector2D(),
                    new Vector2D(), new Vector2D(), new Vector2D(), new Vector2D(),
                    new Vector2D(), new Vector2D(), new Vector2D(), new Vector2D(),
                    new Vector2D(), new Vector2D(), new Vector2D(), new Vector2D()
            };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        mh = new MatrixHandler((float)(canvas.getHeight() / canvas.getWidth()));

        reset();
        addHandlers();
    }

    @FXML
    public void reset()
    {
        resetSliders();
        points = new Vector4D[]{
                new Vector4D(-1, -1, -1, -1), new Vector4D(1, -1, -1, -1), new Vector4D(1, 1, -1, -1), new Vector4D(-1, 1, -1, -1),
                new Vector4D(-1, -1, 1, -1), new Vector4D(1, -1, 1, -1), new Vector4D(1, 1, 1, -1), new Vector4D(-1, 1, 1, -1),
                new Vector4D(-1, -1, -1, 1), new Vector4D(1, -1, -1, 1), new Vector4D(1, 1, -1, 1), new Vector4D(-1, 1, -1, 1),
                new Vector4D(-1, -1, 1, 1), new Vector4D(1, -1, 1, 1), new Vector4D(1, 1, 1, 1), new Vector4D(-1, 1, 1, 1)
        };
        for(int i = 0; i < points.length; i++)
        {
            proj3D[i] = new Vector3D();
            proj2D[i] = new Vector2D();
        }
        foo(points, "X", 0.0f);
    }

    private void foo(Vector4D[] v, String axis, float theta)
    {
        clear();
        for (int i = 0; i < v.length; i++){
            v[i] = mh.rot(v[i], theta, axis);
            proj3D[i] = mh.projectTo3D(v[i]);
            proj2D[i] = mh.projectTo2D(proj3D[i], canvas.getWidth(), canvas.getHeight());
        }
        drawPoints(proj2D);
    }

    private void clear()
    {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void line(Vector2D[] p, int i, int j, int offset) {
        gc.strokeLine((double) p[i+offset].x, (double) p[i+offset].y, (double) p[j+offset].x, (double) p[j+offset].y);
    }

    private void drawPoints(Vector2D[] v)
    {
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

    private void resetSliders(){
        sdrX.setValue(0);
        sdrY.setValue(0);
        sdrZ.setValue(0);
        sdrXW.setValue(0);
        sdrYW.setValue(0);
        sdrZW.setValue(0);
    }

    private void addHandlers() {
        sdrX.valueProperty().addListener((ov, old_val, new_val) -> {
            float theta = rSpeed * (new_val.floatValue() - old_val.floatValue());
            foo(points, "X", theta);
        });
        sdrY.valueProperty().addListener((ov, old_val, new_val) -> {
            float theta = rSpeed * (new_val.floatValue() - old_val.floatValue());
            foo(points, "Y", theta);
        });
        sdrZ.valueProperty().addListener((ov, old_val, new_val) -> {
            float theta = rSpeed * (new_val.floatValue() - old_val.floatValue());
            foo(points, "Z", theta);
        });
        sdrXW.valueProperty().addListener((ov, old_val, new_val) -> {
            float theta = rSpeed * (new_val.floatValue() - old_val.floatValue());
            foo(points, "XW", theta);
        });
        sdrYW.valueProperty().addListener((ov, old_val, new_val) -> {
            float theta = rSpeed * (new_val.floatValue() - old_val.floatValue());
            foo(points, "YW", theta);
        });
        sdrZW.valueProperty().addListener((ov, old_val, new_val) -> {
            float theta = rSpeed * (new_val.floatValue() - old_val.floatValue());
            foo(points, "ZW", theta);
        });

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
