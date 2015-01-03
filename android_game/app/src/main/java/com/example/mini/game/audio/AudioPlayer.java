package com.example.mini.game.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.shapes.complex.Road;

import java.io.File;
import java.util.concurrent.Semaphore;

/**
 * Created by Kuba on 04/12/2014.
 */
public class AudioPlayer {
    // Handle to the mp3 decoder that is responsible for writing to audio buffer
    private static final int WRITE_HANDLE = 0;

    // synchronization tool
    private final Semaphore audioSemaphore = new Semaphore(1);

    // Tells us if the audio has finished playing
    private boolean donePlaying = false;

    // path to the audio file
    private String filePath;
    // audio device to write decoded audio to
    private AudioTrack audioTrack;

    // the sample size
    private int sampleSize;

    private boolean isPlaying = false;

    // 1 frame takes about FRAME_LENGTH_MS in miliseconds
    public static final float FRAME_LENGTH_MS = (1152f/44100f) * 1000f;
    // milliseconds per one byte in Mp3
    public static final float SAMPLE_LENGTH_MS = FRAME_LENGTH_MS / 1152f;

    private float currentTimeMs = 0;

    private int bytesDecoded = 0;

    public static int fluxCounter = 0;

    private boolean doneDecoding = false;

    /**
     *
     * @param filePath
     * @param sampleSize
     * @param sampleRate
     */
    public AudioPlayer(String filePath, int sampleSize, int sampleRate) {
        File file = new File(filePath);
        if(!file.exists()) {
            Log.w("AudioPlayer", "File: " + filePath + " does not exist");
        } else {
            Log.i("AudioPlayer","Creating instance of AudioPlayer");
        }

        // path to audio file
        this.filePath = filePath;

        // size of sample
        this.sampleSize = sampleSize;

        // create audio track
        int audioBufferSize = 32768;
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate,  // sample rate in Hz
                AudioFormat.CHANNEL_CONFIGURATION_STEREO, // channel config
                AudioFormat.ENCODING_PCM_16BIT, // audio format
                audioBufferSize, // buffer size in bytes
                AudioTrack.MODE_STREAM); // mode
    }

    /**
     * Decodes, writes to audio buffer
     */
    public void startDecoding() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("AudioPlayer","Starting decoding audio");
                // load up the mp3
                NativeMP3Decoder.loadMP3(filePath, WRITE_HANDLE);
                boolean done = false;
                while (!done) {
                    try {
                        audioSemaphore.acquire();
                        short[] sample = new short[sampleSize];

                        //currentTimeMs = SAMPLE_LENGTH_MS * audioTrack.getPlaybackHeadPosition();

                        //Log.i("AudioPlayer", Integer.toString(audioTrack.getPlaybackHeadPosition()));
                        //Log.i("AudioPlayer","time: " + currentTimeMs);

                        // decode, sampleSize*2 becouse sizeof(short) = sizeof(byte)*2
                        int ret = NativeMP3Decoder.decodeMP3(sampleSize * 2, sample, WRITE_HANDLE);

                        if (ret == -1 || ret == -12) { // -1 ERR, -12 Track Done
                            done = true;
                            audioSemaphore.release();
                            continue;
                        } else {
                            // write sample to audio tracker
                            audioTrack.write(sample, 0, sample.length);
                        }
                        bytesDecoded += sampleSize / 2;
                        audioSemaphore.release();
                    }catch(InterruptedException e) {e.printStackTrace();}
                }
                doneDecoding = true;
                // cleanup the mp3 and release audio resources
                NativeMP3Decoder.cleanupMP3(WRITE_HANDLE);

                Log.i("AudioPlayer","Finished decoding audio");
            }
        }).start();
    }

    /**
     * Rewinds audio back rewindTime milliseconds
     * and play a reversed audio,
     *
     * TODO Method under construction
     *
     * @param rewindTime
     *      rewindTime in milliseconds to rewind the audio
     */
    public void rewindAudio(int rewindTime) {
        try {
            Log.i("Rewind", "Rewind Pressed");
            this.pauseAudio();

            audioSemaphore.acquire();
            Log.i("Rewind","Rewind Pressed after acquire");

            //Thread.sleep(100);

            // number of samples to rewind
            int rewindSamples = (int) (rewindTime / SAMPLE_LENGTH_MS);

            // get current frame position
            int currentSample = this.audioTrack.getPlaybackHeadPosition();

            // if position is bellow 0, set it to 0
            int setPosition = currentSample - rewindSamples;
            if (setPosition < 0) {
                setPosition = 0;
            }

            Log.i("Rewind","AudioTrack Rewinding frames: " + Integer.toString(rewindSamples));
            Log.i("Rewind","AudioTrack Current: " + Integer.toString(currentSample));
            Log.i("Rewind", "AudioTrack Rewinding to: " + Integer.toString(setPosition));

            // discard the data that has been decoded so far
            this.audioTrack.flush();

            // seek to desired position
            int seekTo = setPosition;
            NativeMP3Decoder.seekTo(seekTo, WRITE_HANDLE);

            // decode the audio that we wish to replay
            int bufferSize = rewindSamples*2;
            short[] sample = new short[bufferSize];
            int ret = NativeMP3Decoder.decodeMP3(bufferSize * 2, sample, WRITE_HANDLE);

            // reverse the order for reverse audio playback
            short[] reservedSample = new short[bufferSize];
            for(int i = 0; i < bufferSize;i++) {
                reservedSample[i] = sample[bufferSize-i-1];
            }

            // remember current playback rate
            int playBackRate = this.audioTrack.getPlaybackRate();

            // lower playback rate
            this.audioTrack.setPlaybackRate(38000);

            // write it to audio buffer
            this.playAudio();

            this.audioTrack.write(reservedSample, 0, reservedSample.length);

            // wait until all frames have been played out
            int current = this.audioTrack.getPlaybackHeadPosition();
            while(current < rewindSamples) {
                current = this.audioTrack.getPlaybackHeadPosition();
            }
            Log.i("Rewind","Finishing");

            // seek back to desired position
            NativeMP3Decoder.seekTo(seekTo, WRITE_HANDLE);

            // set play back rate to normal
            this.audioTrack.pause();
            this.audioTrack.setPlaybackRate(playBackRate);
            this.audioTrack.play();

            audioSemaphore.release();
        }catch (InterruptedException e){}
    }

    public void timeStamp() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                long pos = audioTrack.getPlaybackHeadPosition();
                while(pos < bytesDecoded || !doneDecoding ) {
                    float time = SAMPLE_LENGTH_MS * pos;
                    //Log.i("","time: " + time);
                    //Log.i("","Bytes decoded: " + bytesDecoded);
                    //Log.i("","Pos: " + pos);
                    pos = audioTrack.getPlaybackHeadPosition();
                    if(fluxCounter * GameBoard.TIME_UNIT_LENGTH < time) { //AudioSampleActivity.FLUX_LENGTH
                        /*float value = AudioAnalyser.s_spectralFlux.get(fluxCounter);
                        if(value >= 250.f)
                            Log.i("AudioPlayer","" + AudioAnalyser.freqSpectrumValues.get(fluxCounter));*/

                        Flux flux = AudioAnalyser.s_spectralFluxes.get(fluxCounter);
                        float value = flux.getValue();

                        Log.i("AudioPlayer","" + flux.getSpectrumBand().toString());

                        fluxCounter++;

                        // add vertex to gameboard.
                        Road.nextVertexRoad();
                    }
                 //   Log.i("AudioPlayer", "still getting playback head position...");
                }
                Log.i("AudioPlayer","Audio finished playing with pos: " + pos  + " and BytesDecoded: " + bytesDecoded);
                Log.i("AudioPlayer","Audio finished playing with: " + fluxCounter  + " fluxes");
                //Log.i("","Audio length: " + fluxCounter * GameBoard.TIME_UNIT_LENGTH + " ms");
                //NativeMP3Decoder.cleanupLib();
            }
        }).start();
    }

    /**
     * Starts playing audio
     */
    public void playAudio() {
        this.audioTrack.play();
        this.isPlaying = true;
        timeStamp();
        Log.i("AudioPlayer","Playing audio");
    }

    /**
     * Pauses the audio
     */
    public void pauseAudio() {
        this.audioTrack.pause();
        this.isPlaying = false;
        Log.i("AudioPlayer","Paused playing audio");
    }

    /**
     * Stopping the audio thus discarding
     * any buffered data
     */
    public void stopAudio() {
        this.audioTrack.stop();
        this.isPlaying = false;
        audioTrack.release();
        Log.i("AudioPlayer","Stopped playing audio");
    }

    public boolean isPlaying() {
        return isPlaying;
    }



    /**
     * Computes current time of playback audio in milliseconds
     * TODO allow backtracking
     * @return
     *      Current time in milliseconds
     */
    public long getCurrentTime() {
        long time = 0;

//        try {
//            audioSemaphore.acquire();
//            float t = SAMPLE_LENGTH_MS * this.audioTrack.getPlaybackHeadPosition();
//            time = (long)t;
//            audioSemaphore.release();
//        }catch (InterruptedException e){e.printStackTrace();
//        }
        float t = SAMPLE_LENGTH_MS * this.audioTrack.getPlaybackHeadPosition();

        time = (long)t;
        return time;
    }
}
