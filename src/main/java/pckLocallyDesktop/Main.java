package pckLocallyDesktop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/layoutMain.fxml"));
        primaryStage.setTitle("Locally Client Desktop");
        Scene scene = new Scene(root, 350, 290);
        scene.getStylesheets().add(getClass().getResource("/styleMain.css").toExternalForm());

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                //Communication.getInstance().closeCommunication();
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
