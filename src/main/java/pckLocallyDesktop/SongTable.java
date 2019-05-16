package pckLocallyDesktop;

import javafx.beans.property.SimpleStringProperty;

public class SongTable {
    private SimpleStringProperty songName;
    private SimpleStringProperty songTime;
    private SimpleStringProperty songPath;

    public SongTable(String name, String time){
        songName= new SimpleStringProperty(name);
        songTime= new SimpleStringProperty(time);
    }
    public SongTable(String name, String time, String path){
        songName= new SimpleStringProperty(name);
        songTime= new SimpleStringProperty(time);
        songPath=new SimpleStringProperty(path);
    }
    public SongTable(Song song){
        songName= new SimpleStringProperty(song.getSongName());
        songTime= new SimpleStringProperty(song.getSongTime());
        songPath=new SimpleStringProperty(song.getSongPath());
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
