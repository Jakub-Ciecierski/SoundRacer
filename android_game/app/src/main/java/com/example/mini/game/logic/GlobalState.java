package com.example.mini.game.logic;

import android.util.Log;

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
    /**
     * Constants for audio
     */
    static private int SAMPLE_RATE = 44100;
    static private int SAMPLE_SIZE = 1024;
    static private int FLUX_SAMPLE_RATE = 400;

    static final public float FLUX_LENGTH = SAMPLE_SIZE / 2 * AudioAnalyser.SAMPLE_LENGTH_MS;

    /**
     * Keeps track of all songs to be played
     */
    static private List<String> playList = new ArrayList<String>();
    static private int currentPlayListIndexAudioPlayer = 0;
    static private int currentPlayListIndexAudioAnalyser = 0;

    static private List<Song> songList = new ArrayList<Song>();
    static private AudioPlayer audioPlayer;
    static private AudioAnalyser audioAnalyser;

    static public Thread loadingThread;
    static public Object loadingMutex = new Object();

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
    }

    public static void addFile(String filePath) {
        playList.add(filePath);
    }

    public static void addSong(Song song) {
        songList.add(song);
    }

    public static boolean createNextAudioAnalyser(){
        if(currentPlayListIndexAudioAnalyser == playList.size())
            return false;
        if(audioAnalyser != null && !audioAnalyser.isDoneAnalysing())
            return false;
        String file = playList.get(currentPlayListIndexAudioAnalyser);
        currentPlayListIndexAudioAnalyser++;
        audioAnalyser = new AudioAnalyser(file, SAMPLE_SIZE, SAMPLE_RATE, FLUX_SAMPLE_RATE);
        audioAnalyser.startAnalyzing();
        return true;
    }

    public static boolean createNextAudioPlayer() {
        if(currentPlayListIndexAudioPlayer == playList.size())
            return false;
        if(audioPlayer != null && !audioPlayer.isDonePlaying())
            return false;
        String file = playList.get(currentPlayListIndexAudioPlayer);
        currentPlayListIndexAudioPlayer++;
        audioPlayer = new AudioPlayer(file, SAMPLE_SIZE, SAMPLE_RATE);

        return true;
    }

    public static void startAudio() {
        audioPlayer.startDecoding();
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
