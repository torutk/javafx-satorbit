/*
 * © 2017 TAKAHASHI,Toru
 */
package com.torutk.satorbit;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SubScene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * 衛星軌道可視化プログラムの表示制御（View Controller）を担うクラス。
 */
public class SatelliteOrbitsViewController implements Initializable {

    private SatelliteOrbits3dView orbits3dView;
            
    @FXML
    private BorderPane root;
    @FXML
    private SubScene subScene; // 3D描画系を収容するシーン
    @FXML
    private RadioButton rotationsButton;
    @FXML
    private VBox rotationsVBox;
    @FXML
    private Slider azimuthSlider;
    @FXML
    private Slider elevationSlider;
    @FXML
    private Slider distanceSlider;
    @FXML
    private CheckBox axesCheckBox;
    @FXML
    private CheckBox planesCheckBox;
    
    /**
     * デフォルト姿勢・距離のカメラを3D描画シーンに設定する。
     */
    @FXML
    void defaultCamera() {
        orbits3dView.defaultCamera();
    }
    
    /**
     * スライダで方位・仰角・距離を制御可能なカメラを3D描画シーンに設定する。
     */
    @FXML
    void rotationsCamera() {
        orbits3dView.rotationCamera();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeSubScene();
        initializeRightPane();
    }    

    // 3D描画系のシーンを初期化する。
    private void initializeSubScene() {
        root.getChildren().remove(subScene);
        orbits3dView = new SatelliteOrbits3dView(320, 320);
        orbits3dView.initialize();
        root.setCenter(orbits3dView.getSubScene());
    }    

    // 画面右端のコントロール用ペインを初期化（バインド設定）する。
    private void initializeRightPane() {
        rotationsVBox.disableProperty().bind(Bindings.not(rotationsButton.selectedProperty()));
        orbits3dView.azimuthProperty().bind(Bindings.subtract(-90, azimuthSlider.valueProperty()));
        orbits3dView.elevationProperty().bind(Bindings.multiply(-1, elevationSlider.valueProperty()));
        orbits3dView.distanceProperty().bind(Bindings.multiply(-1, distanceSlider.valueProperty()));
        axesCheckBox.setOnAction(ev -> orbits3dView.setAxesVisible(axesCheckBox.isSelected()));
        planesCheckBox.setOnAction(ev -> orbits3dView.setPlanesVisible(planesCheckBox.isSelected()));
    }

}
