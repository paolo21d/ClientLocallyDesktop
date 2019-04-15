package pckLocallyDesktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Controller {
    Communication communication = new Communication();
    boolean connected=false;

    @FXML
    private Label labelInfo;
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button connectButton;
    @FXML
    private Label labelConnection;
    @FXML
    void connectButtonClick(ActionEvent event) {
        try {
            connected = communication.connect();
            if(connected){
                labelConnection.setText("Connected");
            }else{
                labelConnection.setText("NOT Connected");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void pauseButtonClick(ActionEvent event) {
        try {
            communication.comPause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void playButtonClick(ActionEvent event) {
        try {
            communication.comPlay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
