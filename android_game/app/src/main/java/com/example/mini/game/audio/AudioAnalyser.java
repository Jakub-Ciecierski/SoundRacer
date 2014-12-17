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
 * When the current spectrum has more overall energy than the previous spectrum the spectral flux function will rise.
 * If the current spectrum has less energy than the previous spectrum the spectral flux function will fal.
 * http://www.badlogicgames.com/wordpress/?p=161
 *
 * TODO fix the constants of rate and formatting:
 * http://stackoverflow.com/questions/15225894/windows-mp3-decode-library-c-c
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
    public static List<String> freqSpectrumValues = new ArrayList<String>();
    public static List<Float> s_spectralFlux = new ArrayList<Float>();

    private final int FLUX_SCALER = 100000;

    // 1 frame takes about FRAME_LENGTH_MS in milliseconds
    public static final float FRAME_LENGTH_MS = (1152f/44100f) * 1000f;

    // milliseconds per one byte in layer 3 = mp3
    public static final float SAMPLE_LENGTH_MS = FRAME_LENGTH_MS / 1152f;

    // lengths of one flux in milliseconds
    public float FLUX_LENGTH_MS;

    private Bumper bumper;
    private int bumperSampleSize;
    private int currentBumperIndex;

    private static int currentFluxIndex = 0;

    public static boolean isReadyToGo = false;
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
     * @param bumperSampleSize
     *      The maximum size of sample for bumper computations
     *      Also this means that first bumper computations will start
     *      when spectral flux size reaches that size, or analyzing has finished
     *      before reaching that size
     */
    public AudioAnalyser(String filePath, int sampleSize, int sampleRate, int bumperSampleSize) {
        File file = new File(filePath);
        if(!file.exists()) {
            Log.w("AudioAnalyser","File: " + filePath + " does not exist");
        }

        // path to audio file
        this.filePath = filePath;

        // size of sample
        this.sampleSize = sampleSize;

        // spectral flux
        this.spectralFlux = new ArrayList<Float>( );

        this.FLUX_LENGTH_MS = this.sampleSize / 2 * SAMPLE_LENGTH_MS;

        // create instance of fourier transform
        this.fft = new FFT(sampleSize, sampleRate);

        this.bumper = new Bumper();
        this.bumperSampleSize = bumperSampleSize;
        this.currentBumperIndex = 0;

        Log.i("AudioAnalyser","Creating instance of AudioAnalyser");
        Log.i("AudioAnalyser","Sample rate: " + sampleRate);
        Log.i("AudioAnalyser","Sample size: " + sampleSize);
        Log.i("AudioAnalyser","Sample length in milliseconds: " + SAMPLE_LENGTH_MS);
        Log.i("AudioAnalyser","Flux length in milliseconds: " + FLUX_LENGTH_MS);
    }

    public static float getNextFlux() {
        if(currentFluxIndex >= s_spectralFlux.size())
            return -1;
        float value = s_spectralFlux.get(currentFluxIndex);
        // increment the pointer
        currentFluxIndex++;
        //Log.i("Bumper","Bumper[" + s_currentReadIndex + "]: " + value);
        return value;
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

                // freq spectrum test
                float subBassValueTotal = 0.0f;
                float bassValueTotal = 0.0f;
                float bassValueTotal2 = 0.0f;
                float midRangeValueTotal = 0.0f;
                float highMidValueTotal = 0.0f;
                float highFreqValueTotal = 0.0f;

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
                        for (int i = 0; i < spectrum.length; i++) {
                            // Rectifying.
                            // We are not interested in falling spectral flux but only in rising spectral flux.
                            float value = (spectrum[i] - lastSpectrum[i]);
                            flux += value < 0 ? 0 : value;
                        }
                        // TODO fix flux scaler
                        flux /= FLUX_SCALER;
                        //Log.i("AudioAnalyser","Flux: " + flux);
                        spectralFlux.add(flux);
                        s_spectralFlux.add(flux);

                        // frequency spectrum test
                        //Log.i("AudioAnalyzer","time: " + spectralFlux.size() * FLUX_LENGTH_MS);
                        final float lowerSubBass = 0.0f;
                        final float upperSubBass = 60.0f;

                        final float lowerBass = 61.0f;
                        final float upperBass = 120.0f;

                        final float lowerBass2 = 121.0f;
                        final float upperBass2 = 215.0f;

                        final float lowerMidRange = 216.0f;
                        final float upperMidRange = 2024.0f;

                        final float lowerHighMid = 2025.0f;
                        final float upperHighMid = 6029.0f;

                        final float lowerHighFreq = 6030.0f;
                        final float upperHighFreq = 22050.0f;

                        float subBassValue = 0.0f;
                        float bassValue = 0.0f;
                        float bassValue2 = 0.0f;
                        float midRangeValue = 0.0f;
                        float highMidValue = 0.0f;
                        float highFreqValue = 0.0f;

                        int subBassCounter = 0;
                        int bassCounter = 0;
                        int bassCounter2 = 0;
                        int midRangeCounter = 0;
                        int highMidCounter = 0;
                        int highFreqCounter = 0;

                        for(int i = 0; i < spectrum.length;i++) {
                            float freq = i * 44100 / 1024;
                            float value = spectrum[i] / 1000.0f;
                            if(freq >= lowerSubBass && freq <= upperSubBass) {
                                subBassCounter++;
                                subBassValue += value;
                            }
                            if(freq >= lowerBass && freq <= upperBass) {
                                bassCounter++;
                                bassValue += value;
                            }
                            if(freq >= lowerBass2 && freq <= upperBass2) {
                                bassCounter2++;
                                bassValue2 += value;
                            }
                            if(freq >= lowerMidRange && freq <= upperMidRange) {
                                midRangeCounter++;
                                midRangeValue += value;
                            }
                            if(freq >= lowerHighMid && freq <= upperHighMid) {
                                highMidCounter++;
                                highMidValue += value;
                            }
                            if(freq >= lowerHighFreq && freq <= upperHighFreq) {
                                highFreqCounter++;
                                highFreqValue += value;
                            }

                            //Log.i("FFT","Band[" + i + "] = " + freq + " Hz, Amplitude: " + value);
                        }
                        subBassValue /= subBassCounter;
                        bassValue /= bassCounter;
                        bassValue2 /= bassCounter2;
                        midRangeValue /= midRangeCounter;
                        highMidValue /= highMidCounter;
                        highFreqValue /= highFreqCounter;

                        subBassValueTotal += subBassValue;
                        bassValueTotal += bassValue;
                        bassValueTotal2 += bassValue2;
                        midRangeValueTotal += midRangeValue;
                        highMidValueTotal += highMidValue;
                        highFreqValueTotal += highFreqValue;

                        float maxFreq = subBassValue;
                        String maxStr = "SubBass";
                        if (maxFreq < bassValue) {
                            maxFreq = bassValue;
                            maxStr = "Bass";
                        }
                        if (maxFreq < bassValue2) {
                            maxFreq = bassValue2;
                            maxStr = "Bass2";
                        }
                        if (maxFreq < midRangeValue) {
                            maxFreq = midRangeValue;
                            maxStr = "MidRange";
                        }
                        if (maxFreq < highMidValue) {
                            maxFreq = highMidValue;
                            maxStr = "HighMid";
                        }
                        if (maxFreq < highFreqValue) {
                            maxFreq = highFreqValue;
                            maxStr = "HighFreq";
                        }
                        freqSpectrumValues.add(maxStr);

//                        Log.i("FFT","[" + bassCounter + "] Bass " + lowerBass + " - " + upperBass + " Hz = " + bassValue);
//                        Log.i("FFT","[" + bassCounter2 + "] Bass2 " + lowerBass2 + " - " + upperBass2 + " Hz = " + bassValue2);
//                        Log.i("FFT","[" + midRangeCounter + "] MidRange " + lowerMidRange + " - " + upperMidRange + " Hz = " + midRangeValue);
//                        Log.i("FFT","[" + highMidCounter + "] HighMid " + lowerHighMid + " - " + upperHighMid + " Hz = " + highMidValue);
//                        Log.i("FFT","[" + highFreqCounter + "] HighFreq " + lowerHighFreq + " - " + upperHighFreq + " Hz = " + highFreqValue);

                        // compute bumpers
                        if(spectralFlux.size() == bumperSampleSize + currentBumperIndex) {
                            float average = 0;
                            float min = spectralFlux.get(currentBumperIndex);
                            float max = spectralFlux.get(currentBumperIndex);

                            float[] fluxSample = new float[bumperSampleSize];
                            for(int i = 0; i < bumperSampleSize; i++) {
                                float value = spectralFlux.get(currentBumperIndex);
                                average += value;
                                if(min > value) {
                                    min = value;
                                }
                                if(max < value) {
                                    max = value;
                                }

                                fluxSample[i] = value;
                                currentBumperIndex++;
                            }
                            average /= bumperSampleSize;

                            bumper.computeBumps(fluxSample, average, max, min);
                            isReadyToGo = true;
                            //Log.i("AudioAnalyser", "Computed: " + bumperSampleSize + " bumper samples");
                            //Log.i("AudioAnalyser", "Current BumperIndex: " + currentBumperIndex);
                        }

                        fluxSemaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // compute the leftovers of flux
                int fluxLeftOver = spectralFlux.size() - currentBumperIndex;
                if(fluxLeftOver > 0) {
                    float average = 0;
                    float min = spectralFlux.get(currentBumperIndex);
                    float max = spectralFlux.get(currentBumperIndex);

                    float[] fluxSample = new float[fluxLeftOver];
                    for(int i = 0; i < fluxLeftOver; i++) {
                        float value = spectralFlux.get(currentBumperIndex);
                        average += value;
                        if(min > value) {
                            min = value;
                        }
                        if(max < value) {
                            max = value;
                        }
                        fluxSample[i] = value;
                        currentBumperIndex++;
                    }
                    average /= fluxLeftOver;
                    bumper.computeBumps(fluxSample, average, max, min);
                    isReadyToGo = true;
                    //Log.i("AudioAnalyser", "Computed leftover: " + fluxLeftOver + " bumper samples");
                    //Log.i("AudioAnalyser", "Current BumperIndex: " + currentBumperIndex);
                }

                NativeMP3Decoder.cleanupMP3(ANALYSIS_HANDLE);

                // mark the doneAnalysing flag
                doneAnalysing = true;

                long endTime = System.currentTimeMillis();
                long delta = endTime - startTime;
                Log.i("AudioAnalyser", "Finished analyzing entire audio after: " + Long.toString(delta) + " milliseconds");
                Log.i("AudioAnalyser", "Spectral size: " + Integer.toString(spectralFlux.size()));
                Log.i("AudioAnalyser", "Bumper size: " + bumper.getCurrentBumpSize());
                totalBytes = spectralFlux.size() * sampleSize / 2;
                Log.i("AudioAnalyser", "Total bytes read: " + totalBytes);
                Log.i("AudioAnalyser", "Audio Length in milliseconds: " + totalBytes * SAMPLE_LENGTH_MS);

                // freq spectrum test
                Log.i("AudioAnalyser", "Bass value: " + bassValueTotal);
                Log.i("AudioAnalyser", "Bass2 value: " + bassValueTotal2);
                Log.i("AudioAnalyser", "MidRange value: " + midRangeValueTotal);
                Log.i("AudioAnalyser", "HighMid value: " + highMidValueTotal);
                Log.i("AudioAnalyser", "HighFreq value: " + highFreqValueTotal);
            }
        });
        analyzerThread.start();
    }

    /**
     *
     * @return
     *      Flux value at given millisecond
     */
    private int getFluxAtTime(long timeMs) {
        float f = timeMs / AudioAnalyser.SAMPLE_LENGTH_MS;
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
        //float time = i*(bufferSize/2) * AudioAnalyser.SAMPLE_LENGTH_MS;
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

        float t  = i * sampleSize/2 * AudioAnalyser.SAMPLE_LENGTH_MS;
        long timePosition = (long)t;
        return timePosition;
    }

    /**
     * Returns the amount of bytes that have been analyzed so far
     * When analysing is done, the number of bytes is equal to decoded
     * file length
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

    /**
     * Gets current spectral flux size
     * @return
     *      Spectral flux size
     */
    public int getCurrentSpectralFluxSize() {
        int size = 0;
        try {
            fluxSemaphore.acquire();
            size = this.spectralFlux.size();
            fluxSemaphore.release();
        }catch (InterruptedException e){e.printStackTrace();}
        return size;
    }

    public Bumper getBumper() {
        return this.bumper;
    }
}
