package com.sunproject.mineboot.installer.ui;

import com.sunproject.mineboot.installer.Installer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class InstUi extends Application {

    private static Stage stInstaller;

    public static String getInstName() {
        return Installer.instName;
    }

    public static void cancel() {
        Alert alrt = new Alert(AlertType.CONFIRMATION);
        alrt.setHeaderText("Cancel installation ?");
        alrt.showAndWait();
        if (alrt.getResult().equals(ButtonType.OK)) {
            stopApp();
        }
    }

    public static void stopApp() {
        stInstaller.setIconified(true);
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.exit();
    }

    @Override
    public void start(Stage _stInstaller) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/rsrc/UIs/UiInstaller.fxml"));
        Scene scInstaller = new Scene(root, 700, 500);
        setUserAgentStylesheet(STYLESHEET_MODENA);
        scInstaller.getStylesheets().add(getClass().getResource("/rsrc/UIs/ui.css").toExternalForm());
        stInstaller = _stInstaller;
        stInstaller.setScene(scInstaller);
        stInstaller.setResizable(false);
        stInstaller.setTitle(getInstName());
        stInstaller.setOnCloseRequest(e -> {
            if (!InstUiController.isAborted) {
                cancel();
                e.consume();
            } else {
                stopApp();
            }
        });
        stInstaller.show();
        stInstaller.setIconified(true);
        Thread.sleep(1000L);
        stInstaller.setIconified(false);

    }

}
