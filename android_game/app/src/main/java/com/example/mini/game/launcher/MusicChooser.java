package com.example.mini.game.launcher;

import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba on 09/12/2014.
 */
public class MusicChooser {
    private Cursor cursor;

    public final static int ID_INDEX = 0;
    public final static int ARTIST_INDEX = 1;
    public final static int TITLE_INDEX = 2;
    public final static int PATH_INDEX = 3;
    public final static int NAME_INDEX = 4;
    public final static int DURATION_INDEX = 5;

    private List<Song> songs = new ArrayList<Song>();

    private int currentIndex = 0;

    public MusicChooser(Cursor cursor) {
        this.cursor = cursor;

        while(cursor.moveToNext()) {
            Song song = new Song(cursor.getString(ID_INDEX), cursor.getString(ARTIST_INDEX),
                                cursor.getString(TITLE_INDEX), cursor.getString(PATH_INDEX),
                                cursor.getString(NAME_INDEX), cursor.getString(DURATION_INDEX));
            songs.add(song);
        }
    }

    public Song getNextSong() {
        if(currentIndex >= songs.size())
            return null;
        Song song = songs.get(currentIndex);
        currentIndex++;
        return song;
    }

    public String getSongByName(String songName)
    {
        for(int i=0;i<songs.size();i++)
        {
             if(songs.get(i).getName()==songName){
                 return songs.get(i).getPath();
             }
        }
        return "";
    }

    public List<Song> getSongs() {
        return new ArrayList<Song>(songs);
    }

}
