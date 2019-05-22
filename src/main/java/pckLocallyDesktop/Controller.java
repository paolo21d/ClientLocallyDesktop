package pckLocallyDesktop;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public ImageView playPauseImage;
    public Label statusLabel;
    public Label volumeLabel;
    public ImageView loopImage;
    public Slider timeSlider;
    public Slider volumeSlider;
    public ImageView connectImage;

    public TableColumn<SongTable, String> SongColumn;
    public TableView<SongTable> tableView;


    Communication communication = new Communication(this);
    boolean connected = false;

    public void connectButtonClicked(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("To connect input PIN");
        dialog.setHeaderText("PIN has 4 digits");
        dialog.setContentText("Enter PIN:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            System.out.println("Your name: " + result.get());
            String pin = result.get();
            if(pin.length() != 4) return;
            communication.setPin(pin);
        }else {
            return;
        }

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
        //TODO napisac odswiezanie paska timeSlider & volumeSlider
        //System.out.println(status.played+" "+status.paused+" "+status.loopType);
        volumeSlider.setValue(status.volumeValue*100);
        if(status.played){
            playPauseImage.setImage(new Image("/icons/pause.png"));
        }if(status.paused){
            playPauseImage.setImage(new Image("/icons/play.png"));
        }

        if(status.loopType == PlayerStatus.LoopType.RepeatAll){
            loopImage.setImage(new Image("/icons/repeatAll.png"));
        }else if(status.loopType == PlayerStatus.LoopType.RepeatOne){
            loopImage.setImage(new Image("/icons/repeatOne.png"));
        }else if(status.loopType == PlayerStatus.LoopType.Random){
            loopImage.setImage(new Image("/icons/random.png"));
        }

        tableView.getItems().clear();
        for(Song s:status.currentPlaylist.getSongs()){
            tableView.getItems().add(new SongTable(s));
        }

        statusLabel.setText(status.title);

        System.out.println(status.volumeValue*100);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SongColumn.setCellValueFactory(new PropertyValueFactory<SongTable, String>("SongName"));
        volumeSlider.setValue(100);

        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable observable) {
                if (volumeSlider.isPressed() && connected) {
                    communication.comSetVolume(volumeSlider.getValue());
                }
            }
        });
    }

    public void tableClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            String title = tableView.getSelectionModel().getSelectedItem().getSongName();
            String time = tableView.getSelectionModel().getSelectedItem().getSongTime();
            String path = tableView.getSelectionModel().getSelectedItem().getSongPath();
            System.out.println(title);

            Song song = new Song(time, time, path);
            communication.comSetSong(song);
        }
    }
}
