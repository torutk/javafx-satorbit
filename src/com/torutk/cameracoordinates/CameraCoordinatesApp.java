/*
 * © 2017 TAKAHASHI,Toru
 */
package com.torutk.cameracoordinates;

import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 3次元座標系の座標軸とカメラの向き（姿勢）の関係を可視化するデモ・プログラム。
 * <p>
 * <li>画面レイアウトはFXMLファイルで定義
 * <li>色の設定はCSSファイルで定義
 * <li>画面に表示するテキストは国際化プロパティファイルで定義
 */
public class CameraCoordinatesApp extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("com.torutk.cameracoordinates.CameraCoordinatesView");
        Parent root = FXMLLoader.load(getClass().getResource("CameraCoordinatesView.fxml"), bundle);

        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle(bundle.getString("window.title"));
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
