package com.example.mini.game.audio;

import android.util.Log;

import com.example.mini.game.audio.analysis.FFT;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


/**
 * Onset detection
 * Onset is the first thing to happen when playing a note,
 * followed by an attack until it reaches its maximum amplitude
 *
 * How it is done:
 * 1) Read audio signal - Decoding
 * 2) Transform it to a onset detection function - FFT
 * 3) Pick the peaks in this detection function as onsets/beats - Spectral difference or Spectral flux
 *
 * A single flux is absolute difference between the bin values of the current spectrum and the bin values of the last spectrum.
 * More about beat detection:
 * http://www.badlogicgames.com/wordpress/?p=161
 *
 * Created by Kuba on 26/11/2014.
 */
public class AudioAnalyser {

    // Handle to the mp3 decoder that is responsible for audio analysis
    private static final int ANALYSIS_HANDLE = 1;

    // synchronization tool
    private final Semaphore fluxSemaphore = new Semaphore(1);

    // Tells us if analysis has been finished
    private boolean doneAnalysing = false;

    // the analyzer thread
    public Thread analyzerThread;

    // path to the audio file
    private String filePath;

    // the sample size
    private int sampleSize;
    private FFT fft;
    private List<Float> spectralFlux;

    // 1 frame takes about frameLengthMs in miliseconds
    public static final float frameLengthMs = (1152f/44100f) * 1000f;
    // milliseconds per one byte in Mp3
    public static final float sampleLengthMs = frameLengthMs / 1152f;

    /**
     * TODO check if file exists
     * Creates at AudioAnalyser of input audio file
     * @param filePath
     *      File to be analyzed and played
     * @param sampleSize
     *      sample size to be computed at a time
     * @param sampleRate
     *      speed of the audio playback in Hz.
     *      Standard for mp3 audio is 44100Hz
     */
    public AudioAnalyser(String filePath, int sampleSize, int sampleRate) {
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
    }

    /**
     * Starts analyzing the audio
     */
    public void startAnalyzing() {
        analyzerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                NativeMP3Decoder.loadMP3(filePath, ANALYSIS_HANDLE);
                boolean done = false;

                Log.i("AudioAnalyser", "Starting analyzing entire audio...");
                long startTime = System.currentTimeMillis();

                FFT fft = new FFT(sampleSize, 44100);
                float[] spectrum = new float[sampleSize / 2 + 1];
                float[] lastSpectrum = new float[sampleSize / 2 + 1];

                long totalBytes = 0;

                while (!done) {
                    try {
                        fluxSemaphore.acquire();

                        short[] sample = new short[sampleSize];

                        // decode, sampleSize*2 becouse sizeof(short) = sizeof(byte)*2
                        int ret = NativeMP3Decoder.decodeMP3(sampleSize * 2, sample, ANALYSIS_HANDLE);

                        if (ret == -1 || ret == -12) { // -1 ERR, -12 Track Done
                            done = true;
                            fluxSemaphore.release();
                            continue;
                        }

                        // FFT
                        float[] samples = new float[sampleSize];
                        // convert to float
                        for (int i = 0; i < sample.length; i++) {
                            samples[i] = sample[i];
                        }

                        fft.forward(samples);
                        System.arraycopy(spectrum, 0, lastSpectrum, 0, spectrum.length);
                        System.arraycopy(fft.getSpectrum(), 0, spectrum, 0, spectrum.length);

                        float flux = 0;
                        for (int i = 0; i < spectrum.length; i++)
                            flux += (spectrum[i] - lastSpectrum[i]);
                        spectralFlux.add(flux);

                        fluxSemaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                NativeMP3Decoder.cleanupMP3(ANALYSIS_HANDLE);

                // mark the doneAnalysing flag
                doneAnalysing = true;

                long endTime = System.currentTimeMillis();
                long delta = endTime - startTime;
                Log.i("AudioAnalyser", "Finished analyzing entire audio after: " + Long.toString(delta) + " milliseconds");
                Log.i("AudioAnalyser", "Spectral size: " + Integer.toString(spectralFlux.size()));
                totalBytes = spectralFlux.size() * sampleSize / 2;
                Log.i("AudioAnalyser", "Total bytes read: " + totalBytes);
                Log.i("AudioAnalyser", "Audio Length: " + totalBytes * sampleLengthMs);
            }
        });
        analyzerThread.start();
    }

    /**
     *
     * @return
     *      Flux value at given millisecond
     */
    public int getFluxAtTime(long timeMs) {
        float f = timeMs / AudioAnalyser.sampleLengthMs;
        int sampleNumber = (int)f;
        int position = sampleNumber / sampleSize * 2;
        return position;
    }

    /**
     * Returns flux at given position
     *
     * @param i
     *      position of flux
     * @return Flux at ith position
     * @throws IllegalArgumentException
     */
    public float getFluxAt(int i) throws IllegalArgumentException{
        //float time = i*(bufferSize/2) * AudioAnalyser.sampleLengthMs;
        float flux = 0f;
        try {
            fluxSemaphore.acquire();

            if(this.spectralFlux.size() <= i){
                fluxSemaphore.release();
                throw new IllegalArgumentException("# " + i + " position has not been computed yet");
            }
            flux = this.spectralFlux.get(i);
            fluxSemaphore.release();
        }catch (InterruptedException e){e.printStackTrace();}

        return flux;
    }

    /**
     * Returns time in milliseconds at i-th flux position
     * @param i
     *      i=th flux to check time in milliseconds for
     * @return
     *      time position in milliseconds of i-th flux
     */
    public long getTimeOfFlux(int i) throws IllegalArgumentException{
        try {
            fluxSemaphore.acquire();

            if(this.spectralFlux.size() <= i){
                fluxSemaphore.release();
                throw new IllegalArgumentException("# " + i + " position has not been computed yet");
            }
            fluxSemaphore.release();
        }catch (InterruptedException e){e.printStackTrace();}

        float t  = i * sampleSize/2 * AudioAnalyser.sampleLengthMs;
        long timePosition = (long)t;
        return timePosition;
    }

    /**
     * Returns the amount of bytes that have been analyzed so far
     * @return
     *      Number of analyzed bytes
     */
    public long getNumberOfAnalyzedBytes() {
        long numberOfBytes = 0;
        try {
            fluxSemaphore.acquire();
            numberOfBytes = this.spectralFlux.size() * sampleSize / 2;
            fluxSemaphore.release();
        }catch (InterruptedException e){e.printStackTrace();}
        return numberOfBytes;
    }

    /**
     * Checks if analyzing has been done
     * @return
     *      True if analysing has finished
     */
    public boolean isDoneAnalysing() {
        return doneAnalysing;
    }

    public int getCurrentSpectralFluxSize() {
        int size = 0;
        try {
            fluxSemaphore.acquire();
            size = this.spectralFlux.size();
            fluxSemaphore.release();
        }catch (InterruptedException e){e.printStackTrace();}
        return size;
    }
}
