package pckLocallyDesktop;

import javafx.beans.property.SimpleStringProperty;

public class Song {
    private SimpleStringProperty songName;
    private SimpleStringProperty songTime;
    private SimpleStringProperty songPath;

    public Song(String name, String time){
        songName= new SimpleStringProperty(name);
        songTime= new SimpleStringProperty(time);
    }
    public Song(String name, String time, String path){
        songName= new SimpleStringProperty(name);
        songTime= new SimpleStringProperty(time);
        songPath=new SimpleStringProperty(path);
    }

    public String getSongName() {
        return songName.get();
    }

    public void setSongName(String songName) {
        this.songName = new SimpleStringProperty(songName);
    }

    public String getSongTime() {
        return songTime.get();
    }

    public void setSongTime(String songTime) {
        this.songTime = new SimpleStringProperty(songTime);
    }

    public String getSongPath() {
        return songPath.get();
    }

    public void setSongPath(String songPath) {
        this.songPath = new SimpleStringProperty(songPath);
    }
}
