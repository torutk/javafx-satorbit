/*
 * © 2017 TAKAHASHI,Toru
 */

package com.torutk.satorbit;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * 地球と衛星軌道の3D表示クラス。
 *
 * <ul>
 * <li>距離の単位は、1000kmを1.000とする。
 * </ul>
 */
public class SatelliteOrbits3dView {
    private static final double EARTH_RADIUS = 6.371; // 地球半径（WGS84を元にした短軸・長軸の平均値）
    private static final double GEO_ALTITUDE = 35.786; // 地球表面から静止軌道までの距離
    private static final double DISTANCE_EARTH_TO_SUN = 149_597.87; // 地球-太陽間の距離
    private static final String EARTH_TEXTURE_IMAGE = "earth.png"; // 地球のテクスチャマップ用画像ファイル名
    private static final int AXIS_LINE_LENGTH = 30; // XYZ座標軸の軸の長さ
    private static final int AXIS_SPHERE_RADIUS = 1; // XYZ座標軸の軸先頭に付ける球の半径
    private static final int CAMERA_DISTANCE = -45; // カメラの初期位置
    
    private final Rotate azimuthRotate = new Rotate(0, Rotate.Y_AXIS); // カメラの経度方向（赤道周囲）回転
    private final Rotate elevationRotate = new Rotate(0, Rotate.X_AXIS); // カメラの緯度方向（子午線周囲）回転
    private final Translate distanceTranslate = new Translate(0, 0, CAMERA_DISTANCE); // カメラの距離    
    private final Rotate earthRotate = new Rotate(0, Rotate.Y_AXIS); // 地球の自転方向の回転

    // カメラの位置・姿勢を定める座標変換列
    private final Transform[] cameraTransforms = new Transform[] {
        new Rotate(-90, Rotate.X_AXIS), // カメラをZ軸正方向が画面上になるよう回転
        azimuthRotate,
        elevationRotate,
        distanceTranslate
    };
    
    private SubScene subScene;
    private Group space;
    
    /**
     * 地球と衛星軌道を指定されたシーンの中に3次元表示するコンストラクタ。
     */
    public SatelliteOrbits3dView(int width, int height) {
        space = new Group();
        subScene = new SubScene(space, width, height, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.web("#201860"));
    }

    DoubleProperty azimuthProperty() {
        return azimuthRotate.angleProperty();
    }
    
    DoubleProperty elevationProperty() {
        return elevationRotate.angleProperty();
    }
    
    DoubleProperty distanceProperty() {
        return distanceTranslate.zProperty();
    }

    SubScene getSubScene() {
        return subScene;
    }

    void defaultCamera() {
        subScene.setCamera(createCamera(new Translate(0, 0, CAMERA_DISTANCE)));
    }

    void rotationCamera() {
        subScene.setCamera(createCamera(cameraTransforms));
    }
    void initialize() {
        // XYZ座標軸を作成
        Node xAxis = createAxisGroup(Rotate.Z_AXIS, 90, Color.RED);
        Node yAxis = createAxisGroup(Rotate.Z_AXIS, 180, Color.GREEN);
        Node zAxis = createAxisGroup(Rotate.X_AXIS, -90, Color.BLUE);
        space.getChildren().addAll(xAxis, yAxis, zAxis);

        // XY、YZ、ZX平面を作成
        Box xyPlane = createPlane(Rotate.Z_AXIS, 0);
        Box yzPlane = createPlane(Rotate.Y_AXIS, -90);
        Box zxPlane = createPlane(Rotate.X_AXIS, -90);
        space.getChildren().addAll(xyPlane, yzPlane, zxPlane);
        
        // 地球を生成
        final Sphere earth = createEarth();
        space.getChildren().add(earth);
        
        // 太陽光源を作成
        final PointLight sunLight = createPointLight();
        space.getChildren().add(sunLight);
        // 環境光源を作成
        space.getChildren().add(new AmbientLight(Color.rgb(64, 64, 128, 0.5)));
        
        // 静止軌道（円軌道）を作成
        Circle geoOrbit = new Circle(EARTH_RADIUS + GEO_ALTITUDE);
        geoOrbit.setFill(null);
        geoOrbit.setStroke(Color.WHITE);
        geoOrbit.setStrokeWidth(0.1);
        space.getChildren().add(geoOrbit);
        
        // カメラを初期位置で作成
        subScene.setCamera(createCamera(distanceTranslate));
    }
        
    // 3Dの座標軸形状を作成する。
    private Group createAxisGroup(Point3D rotateAxis, double rotate, Color diffuseColor) {
        Group group = new Group();
        PhongMaterial material = new PhongMaterial(diffuseColor);
        Cylinder axis = new Cylinder(0.1, AXIS_LINE_LENGTH);
        axis.setMaterial(material);
        Sphere top = new Sphere(AXIS_SPHERE_RADIUS);
        top.setMaterial(material);
        top.setTranslateY(-AXIS_LINE_LENGTH / 2);
        group.getChildren().addAll(axis, top);
        group.setRotationAxis(rotateAxis);
        group.setRotate(rotate);
        return group;
    }
    
    // カメラを作成する。
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
        Box plane = new Box(20, 20, 0.1);
        plane.setDrawMode(DrawMode.LINE);
        plane.setRotationAxis(rotateAxis);
        plane.setRotate(rotate);
        return plane;
    }

    // 地球を作成する。
    private Sphere createEarth() {
        final PhongMaterial material = new PhongMaterial();
        material.setSpecularColor(Color.rgb(240, 230, 229));
        material.setDiffuseMap(new Image(getClass().getResourceAsStream(EARTH_TEXTURE_IMAGE)));

        Sphere earth = new Sphere(EARTH_RADIUS);
        earth.setMaterial(material);
        // 地球をZ軸正方向が北極方向になるよう回転
        earth.setRotationAxis(Rotate.X_AXIS);
        earth.setRotate(-90);
        earth.getTransforms().add(earthRotate); // 地球の自転初期値
        return earth;
    }
    
    // 太陽光源を模した点光源を作成する。 地球ー太陽間の距離（X軸正方向）で白色とする。
    private PointLight createPointLight() {
        final PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(DISTANCE_EARTH_TO_SUN);
        pointLight.setTranslateY(0);
        pointLight.setTranslateZ(0);
        return pointLight;
    }
}
