package com.example.mini.game.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.util.Log;

import com.example.mini.game.audio.analysis.FFT;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by Kuba on 26/11/2014.
 */
public class AudioAnalyser {

    private final Semaphore semaphore = new Semaphore(1);

    // path to the audio file
    private String filePath;

    private AudioTrack audioTrack;

    // the sample size
    private int sampleSize;
    private FFT fft;
    private List<Float> spectralFlux;

    // 1 frame takes about frameTimeMs in miliseconds
    public static final float frameTimeMs = (1000f/44100f) * 1152f;

    // miliseconds per one byte in Mp3
    public static final float MsPerSample = frameTimeMs / 1152f;

    /**
     * TODO check if file exists
     *
     * @param filePath
     * @param sampleSize
     * @param sampleRate
     */
    AudioAnalyser(String filePath, int sampleSize, int sampleRate) {
        File file = new File(filePath);
        if(!file.exists()) {
            Log.w("AudioAnalyser","File: " + filePath + " does not exist");
        } else {
            Log.i("AudioAnalyser","Creating instance of AudioAnalyser");
        }

        // path to audio file
        this.filePath = filePath;

        // size of sample
        this.sampleSize = sampleSize;

        // spectral flux
        this.spectralFlux = new ArrayList<Float>( );

        // create instance of fourier transform
        this.fft = new FFT(sampleSize, sampleRate);

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
     * Decodes, computes spectral flux and writes to audio buffer
     */
    public void analyzeAndWrite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("AudioAnalyser","Starting analyzing buffered audio");
                // load up the mp3
                loadMP3();
                boolean done = false;
                while (!done) {
                    try {
                        semaphore.acquire();
                        short[] sample = new short[sampleSize];

                        // decode, sampleSize*2 becouse sizeof(short) = sizeof(byte)*2
                        int ret = decodeMP3(sampleSize * 2, sample);

                        if (ret == -1 || ret == -12) { // -1 ERR, -12 Track Done
                            //done = true;
                            //continue;
                        } else {
                            // write sample to audio tracker
                            audioTrack.write(sample, 0, sample.length);

                            // TODO spectral performance
                            /*
                            // compute spectral flux
                            float[] samples = new float[sampleSize];
                            // convert to float
                            for (int i = 0; i < sample.length; i++) {
                                samples[i] = sample[i];
                            }
                            float[] spectrum = new float[sampleSize / 2 + 1];
                            float[] lastSpectrum = new float[sampleSize / 2 + 1];

                            fft.forward(samples);
                            System.arraycopy(spectrum, 0, lastSpectrum, 0, spectrum.length);
                            System.arraycopy(fft.getSpectrum(), 0, spectrum, 0, spectrum.length);

                            float flux = 0;
                            for (int i = 0; i < spectrum.length; i++)
                                flux += (spectrum[i] - lastSpectrum[i]);
                            spectralFlux.add(flux);
                            */
                        }
                        semaphore.release();
                    }catch(InterruptedException e) {e.printStackTrace();}
                }
                // cleanup the mp3 and release audio resources
                cleanupMP3();
                audioTrack.release();
                Log.i("Analyze","Finishing analyzing audio");
            }
        }).start();
    }

    /**
     * Rewinds audio back rewindTime miliseconds
     * and play a reversed audio
     *
     * @param rewindTime
     *      rewindTime in miliseconds to rewind the audio
     */
    public void rewindAudio(int rewindTime) {
        try {
            Log.i("Rewind", "Rewind Pressed");
            this.pauseAudio();

            semaphore.acquire();
            Log.i("Rewind","Rewind Pressed after acquire");

            //Thread.sleep(100);

            // number of samples to rewind
            int rewindSamples = (int) (rewindTime / MsPerSample);

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
            seekTo(seekTo);

            // decode the audio that we wish to replay
            int bufferSize = rewindSamples*2;
            short[] sample = new short[bufferSize];
            int ret = decodeMP3(bufferSize * 2, sample);

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
            seekTo(seekTo);

            // set play back rate to normal
            this.audioTrack.pause();
            this.audioTrack.setPlaybackRate(playBackRate);
            this.audioTrack.play();

            semaphore.release();
        }catch (InterruptedException e){}
    }

    public void playAudio() {
        this.audioTrack.play();
    }

    public void pauseAudio() {
        this.audioTrack.pause();
    }

    public void stopAudio() {
        this.audioTrack.stop();
    }

    public float getFluxAt(int i) {
        //float time = i*(bufferSize/2) * AudioAnalyser.MsPerSample;
        float flux = 0f;
        try {
            semaphore.acquire();
            flux = this.spectralFlux.get(i);
            semaphore.release();
        }catch (InterruptedException e){e.printStackTrace();}

        return flux;
    }

    /**
     * Decodes and created spectral flux for entire audio.
     * Use only for Test cases with short audio samples !
     *
     * @return
     *      Spectral flux
     */
    public List<Float> analyzeEntireAudio() {
        this.cleanupMP3();
        this.loadMP3();
        boolean done = false;

        Log.i("AudioAnalyser","Starting analyzing entire audio...");
        long startTime = System.currentTimeMillis();

        List<Float> spectralFlux = new ArrayList<Float>( );
        FFT fft = new FFT(sampleSize, 44100);
        while(!done) {
            short[] sample = new short[sampleSize];

            // decode, sampleSize*2 becouse sizeof(short) = sizeof(byte)*2
            int ret = decodeMP3(sampleSize * 2, sample);

            if (ret == -1 || ret == -12) { // -1 ERR, -12 Track Done
                done = true;
                continue;
            }

            // FFT
            float[] samples = new float[sampleSize];
            // convert to float
            for (int i = 0; i < sample.length; i++) {
                samples[i] = sample[i];
            }
            float[] spectrum = new float[sampleSize / 2 + 1];
            float[] lastSpectrum = new float[sampleSize / 2 + 1];

            fft.forward(samples);
            System.arraycopy(spectrum, 0, lastSpectrum, 0, spectrum.length);
            System.arraycopy(fft.getSpectrum(), 0, spectrum, 0, spectrum.length);

            float flux = 0;
            for (int i = 0; i < spectrum.length; i++)
                flux += (spectrum[i] - lastSpectrum[i]);
            spectralFlux.add(flux);
        }
        this.cleanupMP3();

        long endTime = System.currentTimeMillis();
        long delta = endTime - startTime;
        Log.i("AudioAnalyser","Finished analyzing entire audio after: " + Long.toString(delta) + " milliseconds");

        return spectralFlux;
    }

    /**
     * Initiates the mp3 library
     * @return
     */
    public static boolean initAnalyser() {
        return ninitLib();
    }

    /**
     * Cleans up the mp3 library
     */
    public static void cleanupLib() {
        ncleanupLib();
    }

    /**
     * Gets error saying what happend wrong
     * @return
     *      Message indicating the error
     */
    public String getError() {
        return ngetError();
    }

    /**
     * Loads mp3
     * @return
     *      Message indicating result, see jni/mpg123/mpg123.h: mpg123_errors for more info
     */
    public int loadMP3() {
        return ninitMP3(this.filePath);
    }

    /**
     * UnLoads mp3
     */
    public void cleanupMP3() {
        ncleanupMP3();
    }

    /**
     * Sets Equalizer value for given channel
     * @param channel
     *      To set equalizer value
     * @param vol
     *      Value of equalizer
     * @return
     */
    public boolean setEQ(int channel, double vol) {
        return nsetEQ(channel, vol);
    }

    /**
     * Resets equalizer
     */
    public void resetEQ() {
        nresetEQ();
    }

    /**
     * Decodes bufferLen bytes and stores in buffer.
     * Since sizeof(short) = 2 * sizeof(byte),
     * bufferLen has to be doubled.
     *
     * @param bufferLen
     *      Length of bytes to decode
     * @param buffer
     *      Buffer to store decoded data
     * @return
     *      Error message
     */
    public int decodeMP3(int bufferLen, short[] buffer) {
        return ndecodeMP3(bufferLen, buffer);
    }

    /**
     * Seeks to specified frame
     *
     * @param frames
     */
    public static void seekTo(int frames) {
        nseekTo(frames);
    }

    /**
     *
     * @return
     */
    private static native boolean ninitLib();

    /**
     *
     */
    private static native void ncleanupLib();

    /**
     *
     * @return String explaining what went wrong
     */
    private native String ngetError();

    /**
     * Initialize one MP3 file
     * @param filename
     * @return MPG123_OK
     */
    private native int ninitMP3(String filename);

    /**
     * Cleanup all native needed resources for one MP3 file
     */
    private native void ncleanupMP3();

    /**
     *
     * @param channel
     * @param vol
     * @return
     */
    private native boolean nsetEQ(int channel, double vol);

    /**
     *
     */
    private native void nresetEQ();

    /**
     * Read, decode and write PCM data to our java application
     *
     * @param bufferLen
     * @param buffer
     * @return
     */
    private native int ndecodeMP3(int bufferLen, short[] buffer);

    /**
     *
     * @param frames
     */
    private static native void nseekTo(int frames);

    // loads the mp3 decoding library
    static { System.loadLibrary("mp3"); }
}
