package pckLocallyDesktop;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public ImageView playPauseImage;
    public Label statusLabel;
    public ImageView loopImage;
    public Slider timeSlider;
    public Slider volumeSlider;
    public ImageView connectImage;

    Communication communication = new Communication(this);
    boolean connected = false;

    public void connectButtonClicked(ActionEvent event) {
        try {
            //connected = communication.connect();
            connected = communication.initConnection();
            if (connected) {
                statusLabel.setText("Connected");
                communication.TCPConnection();
            } else {
                statusLabel.setText("NOT Connected");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playPauseButtonClicked(ActionEvent event) {
        try {
            communication.comPlayPause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nextButtonClicked(ActionEvent actionEvent) {
        if (connected)
            communication.comNext();
    }

    public void prevButtonClicked(ActionEvent actionEvent) {
        if (connected)
            communication.comPrev();
    }

    public void loopButtonClicked(ActionEvent actionEvent) {
        if (connected)
            communication.comLoop();
    }

    public void replayButtonClicked(ActionEvent actionEvent) {
        if (connected)
            communication.comReplay();
    }

    public void refreshInfo(PlayerStatus status) {
        if(status.played){
            playPauseImage.setImage(new Image("/icons/play.png"));
        }if(status.paused){
            playPauseImage.setImage(new Image("/icons/pause.png"));
        }

        if(status.loopType == PlayerStatus.LoopType.RepeatAll){
            loopImage.setImage(new Image("/icons/repeatAll.png"));
        }else if(status.loopType == PlayerStatus.LoopType.RepeatOne){
            loopImage.setImage(new Image("/icons/repeatOne.png"));
        }else if(status.loopType == PlayerStatus.LoopType.Random){
            loopImage.setImage(new Image("/icons/random.png"));
        }

        statusLabel.setText(status.title);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
