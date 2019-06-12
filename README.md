# 4d_visualization_demo
Visualization demo of a 4D-object to get a better understanding of higher dimensional space.

Download the 4D_Demo.jar and execute it with:
```
java -jar 4D-Demo.jar
```
Recommended jre-version is **1.8**

## Usage
Tutorial in how to use the application.

### Screen
Panel in the center.

Provides a projected view the 4D-object.
You can rotate it by dragging the mouse.
Select a point by clicking it. To unselect click the point again.

### Editor
Panel on the left side.

#### Saving/Loading
There are two samples provided, a tesseract and a simplex, which can be loaded with the corresponding buttons on the top.

You can also save/load your custom 4D-objects.

#### Point
Adding a point will generate a point at the coordinates with the color from the color-selector. Initial coordinate-values are 0 and the intial color is black.

When a point is selected it is highlighted with a red circle on the screen. You can edit the coordinate-values and color of the point.
Recommended coordinate-values are between -1 and 1.
Also you can remove a point, which also removes all connections that contain the point.

#### Connection
The list contains all connection in the 4D-object that contain the selected point, if no is selected it lists all connections.

When selecting a connection in the list it will highlight both contained points and you can change its color.

To add a connection you must select a point first, then press the add button and select the second point last.

### Rotation
Panel on the right side.

If you want to get the full glory of your 4D-object you can also uncheck the checkbox for viewing the coordinate system, which is the center of rotation for this menu.

Rotation can be done around the x-, y- and z-axis and xw-, yw- and zw-plane.
You can rotate the 4D-object by adjusting the sliders or letting it automatically rotate by selecting the checkboxes.

Resetting will set everything back to the last state
of save.
