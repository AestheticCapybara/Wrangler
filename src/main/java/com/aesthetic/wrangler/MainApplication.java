package com.aesthetic.wrangler;

import com.aesthetic.wrangler.core.LogHandler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });

        stage.setTitle("Wrangler - Java Based File Encryptor");
        stage.sizeToScene();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        LogHandler.log("This software is a prototype! BACK YOUR THINGS UP BEFORE ENCRYPTING!");
    }
}