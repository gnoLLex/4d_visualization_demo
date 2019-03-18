import vector.*;
import javafx.scene.input.MouseEvent;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

//TODO:clean up code and comment everything

public class Main extends Application {

    private GUI gui;
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
    public void start(Stage primaryStage) throws Exception{
        gui = new GUI();
        mh = new MatrixHandler(gui);
        Scene scene = new Scene(gui);
        scene.getRoot().requestFocus();
        primaryStage.setTitle("4D Rotation");
        primaryStage.setScene(scene);
        primaryStage.show();

        reset();
        addHandlers();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void foo(Vector4D[] v, String axis, float theta){
        gui.clear();
        for (int i = 0; i < v.length; i++){
            v[i] = mh.rot(v[i], theta, axis);
            proj3D[i] = mh.projectTo3D(v[i]);
            proj2D[i] = mh.projectTo2D(proj3D[i]);
        }
        gui.drawPoints(proj2D);
    }

    private void addHandlers() {
        gui.rotationX.valueProperty().addListener((ov, old_val, new_val) -> {
            float theta = rSpeed * (new_val.floatValue() - old_val.floatValue());
            foo(points, "X", theta);
        });
        gui.rotationY.valueProperty().addListener((ov, old_val, new_val) -> {
            float theta = rSpeed * (new_val.floatValue() - old_val.floatValue());
            foo(points, "Y", theta);
        });
        gui.rotationZ.valueProperty().addListener((ov, old_val, new_val) -> {
            float theta = rSpeed * (new_val.floatValue() - old_val.floatValue());
            foo(points, "Z", theta);
        });
        gui.rotationXW.valueProperty().addListener((ov, old_val, new_val) -> {
            float theta = rSpeed * (new_val.floatValue() - old_val.floatValue());
            foo(points, "XW", theta);
        });
        gui.rotationYW.valueProperty().addListener((ov, old_val, new_val) -> {
            float theta = rSpeed * (new_val.floatValue() - old_val.floatValue());
            foo(points, "YW", theta);
        });
        gui.rotationZW.valueProperty().addListener((ov, old_val, new_val) -> {
            float theta = rSpeed * (new_val.floatValue() - old_val.floatValue());
            foo(points, "ZW", theta);
        });

        gui.resetBtn.addEventHandler( MouseEvent.MOUSE_CLICKED, e -> this.reset() );
    }

    public void reset(){
        gui.resetSliders();
        Vector4D[] points =
                {
                        new Vector4D(-1, -1, -1, -1), new Vector4D(1, -1, -1, -1), new Vector4D(1, 1, -1, -1), new Vector4D(-1, 1, -1, -1),
                        new Vector4D(-1, -1, 1, -1), new Vector4D(1, -1, 1, -1), new Vector4D(1, 1, 1, -1), new Vector4D(-1, 1, 1, -1),
                        new Vector4D(-1, -1, -1, 1), new Vector4D(1, -1, -1, 1), new Vector4D(1, 1, -1, 1), new Vector4D(-1, 1, -1, 1),
                        new Vector4D(-1, -1, 1, 1), new Vector4D(1, -1, 1, 1), new Vector4D(1, 1, 1, 1), new Vector4D(-1, 1, 1, 1)
                };
        Vector3D[] proj3D =
                {
                        new Vector3D(), new Vector3D(), new Vector3D(), new Vector3D(),
                        new Vector3D(), new Vector3D(), new Vector3D(), new Vector3D(),
                        new Vector3D(), new Vector3D(), new Vector3D(), new Vector3D(),
                        new Vector3D(), new Vector3D(), new Vector3D(), new Vector3D()
                };
        Vector2D[] proj2D =
                {
                        new Vector2D(), new Vector2D(), new Vector2D(), new Vector2D(),
                        new Vector2D(), new Vector2D(), new Vector2D(), new Vector2D(),
                        new Vector2D(), new Vector2D(), new Vector2D(), new Vector2D(),
                        new Vector2D(), new Vector2D(), new Vector2D(), new Vector2D()
                };
        foo(points, "X", 0.0f);
    }
}
