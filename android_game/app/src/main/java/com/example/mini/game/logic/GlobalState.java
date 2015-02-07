package com.example.mini.game.logic;

import android.util.Log;
import android.widget.ImageView;

import com.example.mini.game.GameActivity;
import com.example.mini.game.GameRenderer;
import com.example.mini.game.audio.AudioAnalyser;
import com.example.mini.game.audio.AudioPlayer;
import com.example.mini.game.audio.NativeMP3Decoder;
import com.example.mini.game.launcher.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuba on 1/4/15.
 */
public class GlobalState {
    /*
    type of ship steering
     */
    static public boolean isTouch=false
            ;
    static public boolean isOnMove=false;
    // Current song for displaying title
    static public Song currentSong;
    static public boolean displaySongName=false;
    /**
     * GameActivity object for turning off imageView
     */
    static public GameActivity gameActivity;
    /**
     * Constants for audio
     */
    static private int SAMPLE_RATE = 44100;
    static private int SAMPLE_SIZE = 1024;
    static private int FLUX_SAMPLE_RATE = 400;

    static final public float FLUX_LENGTH = SAMPLE_SIZE / 2 * AudioAnalyser.SAMPLE_LENGTH_MS;

    /**
     * Keeps track of all songs to be played
     */;
    static private List<Song> songList = new ArrayList<Song>();
    static private int currentPlayListIndexAudioPlayer = 0;
    static private int currentPlayListIndexAudioAnalyser = 0;

    static private AudioPlayer audioPlayer;
    static private AudioAnalyser audioAnalyser;

    static public Thread loadingThread;
    static public Object loadingMutex = new Object();

    static public Object audioPlayerMutex = new Object();

    /**
     * Includes everything that needs to be initiated before starting game
     */
    public static void initSystem() {
        NativeMP3Decoder.initLib();
    }

    /**
     * Cleans up all resources that were used for running the game
     */
    public static void shutDownSystem() {
        NativeMP3Decoder.cleanupLib();

        songList = new ArrayList<Song>();
        currentPlayListIndexAudioPlayer = 0;
        currentPlayListIndexAudioAnalyser = 0;

        audioPlayer = null;
        audioAnalyser = null;

    }

    public static void addSong(Song song) {
        songList.add(song);
    }

    public static boolean createNextAudioAnalyser(){
        if(currentPlayListIndexAudioAnalyser == songList.size())
            return false;
        if(audioAnalyser != null && !audioAnalyser.isDoneAnalysing())
            return false;
        Song song = songList.get(currentPlayListIndexAudioAnalyser);
        currentPlayListIndexAudioAnalyser++;
        audioAnalyser = new AudioAnalyser(song.getPath(), SAMPLE_SIZE, SAMPLE_RATE, FLUX_SAMPLE_RATE);
        audioAnalyser.startAnalyzing();
        return true;
    }

    public static boolean createNextAudioPlayer() {
        if(currentPlayListIndexAudioPlayer == songList.size())
            return false;
        if(audioPlayer != null && !audioPlayer.isDonePlaying())
            return false;
        Song song = songList.get(currentPlayListIndexAudioPlayer);
        currentPlayListIndexAudioPlayer++;
        audioPlayer = new AudioPlayer(song.getPath(), SAMPLE_SIZE, SAMPLE_RATE);
        currentSong = song;
        return true;
    }

    public static void startAudio() {
        final String name  = GlobalState.currentSong.getName();
        GlobalState.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GlobalState.gameActivity.changeSongNameText(name);
                GlobalState.displaySongName=true;
            }
        });
        audioPlayer.startDecoding();
        audioPlayer.playAudio();
    }

    public static void playAudio() {
        final String name  = GlobalState.currentSong.getName();
        GlobalState.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GlobalState.gameActivity.changeSongNameText(name);
                GlobalState.displaySongName=true;
            }
        });
        audioPlayer.playAudio();
    }

    public static void pauseAudio() {
        audioPlayer.pauseAudio();
    }

    public static boolean isAnalyserReadyToGo() {
        return audioAnalyser.isReadyToGo();
    }

    public static boolean isAnalyserDone() {
        return audioAnalyser.isDoneAnalysing();
    }

    public static boolean isPlayerDone() {
        return audioPlayer.isDonePlaying();
    }

    public static boolean isGameDoneLoading() {
        if(isAnalyserReadyToGo())
            return true;
        return false;
    }

    public static void loadGraphics() {
        GameRenderer.initGameBoard();
    }
}
