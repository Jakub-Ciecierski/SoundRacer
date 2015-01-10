package com.example.mini.game.audio;

/**
 * Created by kuba on 1/3/15.
 */

public class FrequencySpectrum {

    static final float LOWER_SUB_BASS = 0.0f;
    static final float UPPER_SUB_BASS = 60.0f;

    static final float LOWER_LOW_BASS = 61.0f;
    static final float UPPER_LOW_BASS = 120.0f;

    static final float LOWER_HIGH_BASS = 121.0f;
    static final float UPPER_HIGH_BASS = 215.0f;

    static final float LOWER_MID_RANGE = 216.0f;
    static final float UPPER_MID_RANGE = 2024.0f;

    static final float LOWER_HIGH_MID = 2025.0f;
    static final float UPPER_HIGH_MID = 6029.0f;

    static final float LOWER_HIGH_FREQ = 6030.0f;
    static final float UPPER_HIGH_FREQ = 22050.0f;

    static public SpectrumBand computeSpectrum (float spectrum[], float sampleRate, float sampleSize) {

        final float SCALER = 1000.0f;

        float subBassValue = 0.0f;
        float lowBassValue = 0.0f;
        float highBassValue = 0.0f;
        float midRangeValue = 0.0f;
        float highMidValue = 0.0f;
        float highFreqValue = 0.0f;

        int subBassCounter = 0;
        int lowBassCounter = 0;
        int highBassCounter = 0;
        int midRangeCounter = 0;
        int highMidCounter = 0;
        int highFreqCounter = 0;

        for(int i = 0; i < spectrum.length;i++) {
            float freq = i * sampleRate / sampleSize;
            float value = spectrum[i] / SCALER;
            if(freq >= LOWER_SUB_BASS && freq <= UPPER_SUB_BASS) {
                subBassCounter++;
                subBassValue += value;
            }
            if(freq >= LOWER_LOW_BASS && freq <= UPPER_LOW_BASS) {
                lowBassCounter++;
                lowBassValue += value;
            }
            if(freq >= LOWER_HIGH_BASS && freq <= UPPER_HIGH_BASS) {
                highBassCounter++;
                highBassValue += value;
            }
            if(freq >= LOWER_MID_RANGE && freq <= UPPER_MID_RANGE) {
                midRangeCounter++;
                midRangeValue += value;
            }
            if(freq >= LOWER_HIGH_MID && freq <= UPPER_HIGH_MID) {
                highMidCounter++;
                highMidValue += value;
            }
            if(freq >= LOWER_HIGH_FREQ && freq <= UPPER_HIGH_FREQ) {
                highFreqCounter++;
                highFreqValue += value;
            }

            //Log.i("FFT","Band[" + i + "] = " + freq + " Hz, Amplitude: " + value);
        }
        subBassValue /= subBassCounter;
        lowBassValue /= lowBassCounter;
        highBassValue /= highBassCounter;
        midRangeValue /= midRangeCounter;
        highMidValue /= highMidCounter;
        highFreqValue /= highFreqCounter;

        float maxFreq = subBassValue;
        SpectrumBand spectrumBand = SpectrumBand.SUB_BASS;

        if (maxFreq < lowBassValue) {
            maxFreq = lowBassValue;
            spectrumBand = SpectrumBand.LOW_BASS;
        }
        if (maxFreq < highBassValue) {
            maxFreq = highBassValue;
            spectrumBand = SpectrumBand.HIGH_BASS;
        }
        if (maxFreq < midRangeValue) {
            maxFreq = midRangeValue;
            spectrumBand = SpectrumBand.MID_RANGE;
        }
        if (maxFreq < highMidValue) {
            maxFreq = highMidValue;
            spectrumBand = SpectrumBand.HIGH_MID;
        }
        if (maxFreq < highFreqValue) {
            maxFreq = highFreqValue;
            spectrumBand = SpectrumBand.HIGH_FREQ;
        }
        return spectrumBand;
    }

    public enum SpectrumBand {
        NO_SPECTRUM(-1,-1, "NO_SPECTRUM"),
        SUB_BASS(LOWER_SUB_BASS, UPPER_SUB_BASS, "SUB_BASS"),
        LOW_BASS(LOWER_LOW_BASS, UPPER_LOW_BASS, "LOW_BASS"),
        HIGH_BASS(LOWER_HIGH_BASS, UPPER_HIGH_BASS, "HIGH_BASS"),
        MID_RANGE(LOWER_MID_RANGE, UPPER_MID_RANGE, "MID_RANGE"),
        HIGH_MID(LOWER_HIGH_MID , UPPER_HIGH_MID, "HIGH_MID"),
        HIGH_FREQ(LOWER_HIGH_FREQ, UPPER_HIGH_FREQ, "HIGH_FREQ");

        private float lower;
        private float upper;

        private String name;

        SpectrumBand(float lower, float upper, String name) {
            this.lower = lower;
            this.upper = upper;

            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        static SpectrumBand getSpectrum(float freq) {
            if (freq >= LOWER_SUB_BASS && freq <= UPPER_SUB_BASS)
                return SUB_BASS;
            else if (freq >= LOWER_LOW_BASS && freq <= UPPER_LOW_BASS)
                return LOW_BASS;
            else if (freq >= LOWER_HIGH_BASS && freq <= UPPER_HIGH_BASS)
                return HIGH_BASS;
            else if (freq >= LOWER_MID_RANGE && freq <= UPPER_MID_RANGE)
                return MID_RANGE;
            else if (freq >= LOWER_HIGH_MID && freq <= UPPER_HIGH_MID)
                return HIGH_MID;
            else if (freq >= LOWER_HIGH_FREQ && freq <= UPPER_HIGH_FREQ)
                return HIGH_FREQ;
            else
                return NO_SPECTRUM;

        }

    }
}
