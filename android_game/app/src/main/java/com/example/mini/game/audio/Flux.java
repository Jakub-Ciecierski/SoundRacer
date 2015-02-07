package com.example.mini.game.audio;

/**
 * This class encapsulates the Flux object, keeping its value and the dominating spectrum band
 * in which the peak happened to occur
 *
 * Created by kuba on 1/3/15.
 */
public class Flux {

    private static final float OBSTACLE_TOLERANCE = 0.75f;

    private float value;
    private FrequencySpectrum.SpectrumBand spectrumBand;

    public Flux() {

    }

    public Flux(float value, FrequencySpectrum.SpectrumBand spectrumBand) {
        this.value = value;
        this.spectrumBand = spectrumBand;
    }

    public float getValue() {
        return this.value;
    }

    public FrequencySpectrum.SpectrumBand getSpectrumBand() {
        return this.spectrumBand;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setSpectrumBand(FrequencySpectrum.SpectrumBand spectrumBand) {
        this.spectrumBand = spectrumBand;
    }

    /**
     * Method used to tell the game world if this flux is to be used for
     * Bumping.
     *
     * This method is to be changed for different musical sensation
     * @return
     *  True if flux is to be calculated for bumps
     */
    public boolean isValidBump() {
        if(spectrumBand == FrequencySpectrum.SpectrumBand.SUB_BASS
                || spectrumBand == FrequencySpectrum.SpectrumBand.LOW_BASS) {
            return true;
        }
        return false;
    }

    /**
     * Method used to tell the game world if this flux is to be used for
     * obstacle spawning.
     *
     * This method is to be changed for different musical sensation
     * @return
     *  True if flux is to be calculated for obstacle spawning
     */
    public boolean isValidObstacle(float max) {
        if(spectrumBand != FrequencySpectrum.SpectrumBand.SUB_BASS
                && spectrumBand != FrequencySpectrum.SpectrumBand.LOW_BASS
                && value > max * OBSTACLE_TOLERANCE) {
            return true;
        }
        return false;
    }
}