/*
 * © 2017 TAKAHASHI,Toru
 */
package com.torutk.cameracoordinates;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 *
 */
public class CameraCoordinatesViewController implements Initializable {
    private static final int AXIS_LINE_LENGTH = 100;
    private static final int AXIS_SPHERE_RADIUS = 5;
    private static final int CAMERA_DISTANCE = -150;
    private static final Translate TRANSLATE_DISTANCE = new Translate(0, 0, CAMERA_DISTANCE);
            
    private final Rotate azimuthRotate = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate elevationRotate = new Rotate(0, Rotate.X_AXIS);
    
    private final Transform[] cameraTransforms = new Transform[] {
        new Rotate(-90, Rotate.X_AXIS),
        azimuthRotate,
        elevationRotate,
        TRANSLATE_DISTANCE
    };
            
    @FXML
    private BorderPane root;
    @FXML
    private SubScene subScene;
    @FXML
    private RadioButton rotationsButton;
    @FXML
    private VBox rotationsVBox;
    @FXML
    private Slider azimuthSlider;
    @FXML
    private Slider elevationSlider;
    
    @FXML
    public void defaultCamera() {
        subScene.setCamera(createCamera(TRANSLATE_DISTANCE));
    }
    
    @FXML
    public void rotationsCamera() {
        subScene.setCamera(createCamera(cameraTransforms));
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeSubScene();
        initializeRightPane();
    }    

    private void initializeSubScene() {
        subScene = new SubScene(root, 320, 320, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.ANTIQUEWHITE);
        root.setCenter(subScene);
        
        Group space = new Group();
        
        Node xAxis = createAxisGroup(Rotate.Z_AXIS, 90, Color.RED);
        Node yAxis = createAxisGroup(Rotate.Z_AXIS, 180, Color.GREEN);
        Node zAxis = createAxisGroup(Rotate.X_AXIS, -90, Color.BLUE);
        space.getChildren().addAll(xAxis, yAxis, zAxis);

        Box xyPlane = createPlane(Rotate.Z_AXIS, 0);
        Box yzPlane = createPlane(Rotate.Y_AXIS, -90);
        Box zxPlane = createPlane(Rotate.X_AXIS, -90);
        space.getChildren().addAll(xyPlane, yzPlane, zxPlane);
        
        subScene.setCamera(createCamera(TRANSLATE_DISTANCE));
        subScene.setRoot(space);

    }    

    private void initializeRightPane() {
        rotationsVBox.disableProperty().bind(Bindings.not(rotationsButton.selectedProperty()));
        azimuthRotate.angleProperty().bind(Bindings.subtract(-90, azimuthSlider.valueProperty()));
        elevationRotate.angleProperty().bind(Bindings.multiply(-1, elevationSlider.valueProperty()));
    }
    
    private Group createAxisGroup(Point3D rotateAxis, double rotate, Color diffuseColor) {
        Group group = new Group();
        PhongMaterial material = new PhongMaterial(diffuseColor);
        Cylinder axis = new Cylinder(1, AXIS_LINE_LENGTH);
        axis.setMaterial(material);
        Sphere top = new Sphere(AXIS_SPHERE_RADIUS);
        top.setMaterial(material);
        top.setTranslateY(-AXIS_LINE_LENGTH / 2);
        group.getChildren().addAll(axis, top);
        group.setRotationAxis(rotateAxis);
        group.setRotate(rotate);
        return group;
    }
    
    private PerspectiveCamera createCamera(Transform... args) {
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFieldOfView(45.0);
        camera.setFarClip(400);
        camera.getTransforms().addAll(args);
        return camera;
    }

    // 大きさ：100x100、厚さ：1の薄い箱を作成。原点中心に指定軸、指定角度回転させる。
    // XY平面、YZ平面、ZX平面を作る用途で作成した。
    private Box createPlane(Point3D rotateAxis, double rotate) {
        Box plane = new Box(100, 100, 1);
        plane.setDrawMode(DrawMode.LINE);
        plane.setRotationAxis(rotateAxis);
        plane.setRotate(rotate);
        return plane;
    }
    
}
