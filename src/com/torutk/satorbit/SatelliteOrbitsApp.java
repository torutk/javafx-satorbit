/*
 * © 2017 TAKAHASHI,Toru
 */
package com.torutk.satorbit;

import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 地球の衛星軌道を可視化するプログラム。
 * <ul>
 * <li>画面レイアウトはFXMLファイルで定義
 * <li>色の設定はCSSファイルで定義
 * <li>画面に表示するテキストは国際化プロパティファイルで定義
 * </ul>
 */
public class SatelliteOrbitsApp extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("com.torutk.satorbit.SatelliteOrbitsView");
        Parent root = FXMLLoader.load(getClass().getResource("SatelliteOrbitsView.fxml"), bundle);

        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle(bundle.getString("window.title"));
        stage.show();
    }

}
