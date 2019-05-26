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
    LabelRefresher labelRefresher = new LabelRefresher();
    //Communication communication = new Communication(this);
    boolean connected = false;
    private Thread updaterLabelThread;

    public void connectButtonClicked(ActionEvent event) {
        if(connected)
            return;
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("To connect input PIN");
        dialog.setHeaderText("PIN has 4 digits");
        dialog.setContentText("Enter PIN:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            System.out.println("PIN: " + result.get());
            String pin = result.get();
            if (pin.length() != 4) return;
            Communication.getInstance().setPin(pin);
        } else {
            return;
        }

        try {
            //connected = communication.connect();
            connected = Communication.getInstance().initConnection(this);
            if (connected) {
                //statusLabel.setText("Connected");
                labelRefresher.refresh("Connected");
                Communication.getInstance().TCPConnection();
            } else {
                //statusLabel.setText("NOT Connected");
                labelRefresher.refresh("NOT Connected");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playPauseButtonClicked(ActionEvent event) {
        try {
            Communication.getInstance().comPlayPause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nextButtonClicked(ActionEvent actionEvent) {
        if (connected)
            Communication.getInstance().comNext();
    }

    public void prevButtonClicked(ActionEvent actionEvent) {
        if (connected)
            Communication.getInstance().comPrev();
    }

    public void loopButtonClicked(ActionEvent actionEvent) {
        if (connected)
            Communication.getInstance().comLoop();
    }

    public void replayButtonClicked(ActionEvent actionEvent) {
        if (connected)
            Communication.getInstance().comReplay();
    }

    public void refreshInfo(PlayerStatus status) {
        //TODO napisac odswiezanie paska timeSlider & volumeSlider
        //System.out.println(status.played+" "+status.paused+" "+status.loopType);
        if (status == null) {
            labelRefresher.refresh("Disconnected..");
            volumeSlider.setValue(100);
            playPauseImage.setImage(new Image("/icons/play.png"));
            loopImage.setImage(new Image("/icons/repeatAll.png"));
            tableView.getItems().clear();
            return;
        }

        volumeSlider.setValue(status.volumeValue * 100);
        if (status.played) {
            playPauseImage.setImage(new Image("/icons/pause.png"));
        }
        if (status.paused) {
            playPauseImage.setImage(new Image("/icons/play.png"));
        }

        if (status.loopType == PlayerStatus.LoopType.RepeatAll) {
            loopImage.setImage(new Image("/icons/repeatAll.png"));
        } else if (status.loopType == PlayerStatus.LoopType.RepeatOne) {
            loopImage.setImage(new Image("/icons/repeatOne.png"));
        } else if (status.loopType == PlayerStatus.LoopType.Random) {
            loopImage.setImage(new Image("/icons/random.png"));
        }

        tableView.getItems().clear();
        for (Song s : status.currentPlaylist.getSongs()) {
            tableView.getItems().add(new SongTable(s));
        }

        //statusLabel.setText(status.title);
        labelRefresher.refresh(status.title);

        System.out.println(status.volumeValue * 100);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SongColumn.setCellValueFactory(new PropertyValueFactory<SongTable, String>("SongName"));
        volumeSlider.setValue(100);

        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable observable) {
                if (volumeSlider.isPressed() && connected) {
                    Communication.getInstance().comSetVolume(volumeSlider.getValue());
                }
            }
        });

        statusLabel.textProperty().bind(labelRefresher.messageProperty());
        updaterLabelThread = new Thread(labelRefresher);
        updaterLabelThread.start();
    }

    public void tableClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            String title = tableView.getSelectionModel().getSelectedItem().getSongName();
            String time = tableView.getSelectionModel().getSelectedItem().getSongTime();
            String path = tableView.getSelectionModel().getSelectedItem().getSongPath();
            System.out.println(title);

            Song song = new Song(time, time, path);
            Communication.getInstance().comSetSong(song);
        }
    }

    public void closeCommunication() {
        Communication.getInstance().resetCommunication();
        //labelRefresher.refresh("Disconnected..");
        refreshInfo(null);
        connected = false;
    }
}
