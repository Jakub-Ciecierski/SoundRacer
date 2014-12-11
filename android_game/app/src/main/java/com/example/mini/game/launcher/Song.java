package com.example.mini.game.launcher;


/**
 * Created by Kuba on 09/12/2014.
 */
public class Song {
    private String id;
    private String artist;
    private String title;
    private String path;
    private String name;
    private String duration;

    public Song(String id, String artist, String title,
                String path, String name, String duration) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.path = path;
        this.name = name;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }
}
