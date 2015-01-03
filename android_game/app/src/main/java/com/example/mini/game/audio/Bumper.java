package com.example.mini.game.audio;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class computes bumps to be implemented on the road.
 * Using computations on small samples of spectral flux of entire audio,
 * we determine the points of interests and their magnitudes.
 *
 * Created by Kuba on 07/12/2014.
 */
public class Bumper {
    /**
     *  Each position in the list, represents a single Flux - peaks in audio playback.
     *  We determine the height of the bump by calculating the points of interests in spectral flux
     */
    private List<Float> bumps;

    private static List<Float> s_bumps = new ArrayList<Float>();
    private static int s_currentReadIndex = 0;

    /**
     * Pointer to current position of reading index for bumps
     */
    private int currentReadIndex;

    /**
     * Pointer to current position of writing index for bumps
     */
    private int currentWriteIndex;

    /**
     * Default constructor
     */
    protected Bumper() {
        this.bumps = new ArrayList<Float>();

        this.currentReadIndex = 0;
        this.currentWriteIndex = -1;
    }

    /**
     * Gets next bumps value and moves the pointer to next position
     * @return
     *      Next bump value or -1 if Read index points at non existing element
     */
    public float getNextBump() {
        synchronized (this) {
            if(currentReadIndex >= bumps.size())
                return -1;
            float value = this.bumps.get(currentReadIndex);
            // increment the pointer
            currentReadIndex++;
            return value;
        }
    }

    public static float getNextBumper() {
        if(s_currentReadIndex >= s_bumps.size())
            return -1;
        float value = s_bumps.get(s_currentReadIndex);
        // increment the pointer
        s_currentReadIndex++;
        //Log.i("Bumper","Bumper[" + s_currentReadIndex + "]: " + value);
        return value;
    }

    private void setStaticBumper() {

    }

    /**
     * Get current write index
     * This means that the next value read from
     * getNextBump() will be at this index
     *
     * @return
     *      Current write index
     */
    public int getCurrentWriteIndex() {
        synchronized (this) {
            return this.currentWriteIndex;
        }
    }

    /**
     * Get current read index
     * This means that next flux will be computed and
     * added as some BUMP_HEIGHT at currentReadIndex
     * @return
     *      Current read index
     */
    public int getCurrentReadIndex() {
        synchronized (this) {
            return this.currentReadIndex;
        }
    }

    /**
     * Get size of bump list
     * @return
     *      Bump size
     */
    public int getCurrentBumpSize() {
        synchronized (this) {
            return this.bumps.size();
        }
    }

    /**
     * Gets next i bumps and moves the pointer accordingly
     * @param i
     *      Number of bumps to return
     * @return
     *      Next i bumps
     */
    public float[] getNextBumps(int i) {
        synchronized (this) {
            float values[] = new float[i];
            for(int j = 0; j < i; j++) {
                float value = this.bumps.get(currentReadIndex);
                // increment the pointer
                currentReadIndex++;
                values[j] = value;
            }
            return values;
        }
    }

    /**
     * Computes bumps for next flux sample
     * @param fluxSample
     *      Flux sample to be computed
     */
    protected void computeBumps(Flux[] fluxSample, float average, float max, float min) {
        synchronized (this) {
            int length = fluxSample.length;

           /*Log.i("Bump", "Computing bumps, sample size: " + length);
            Log.i("Bump", "Average: " + average);
            Log.i("Bump", "min: " + min);
            Log.i("Bump", "max: " + max);*/

            // find points of interests
            for(int i = 0;i < length; i++) {
                Flux flux = fluxSample[i];

                float bumpHeight = flux.getValue();

                if(bumpHeight >= max * 0.75f && flux.isValidBump()) {
                    interpolate(BumpType.BIG_BUMP);
                }
                else if(bumpHeight >= max * 0.60f && flux.isValidBump()) {
                    interpolate(BumpType.MEDIUM_BUMP);
                }
                else if(bumpHeight >= max * 0.35f && flux.isValidBump()) {
                    interpolate(BumpType.SMALL_BUMP);
                }
                // if flux is not big enough, set no bumps
                // make sure to increment pointer to write index
                else {
                    // if fist, simple add it
                    if(currentWriteIndex == -1) {
                        this.bumps.add(BumpType.NO_BUMP.getHeight());
                        s_bumps.add(BumpType.NO_BUMP.getHeight());
                    }
                    else {
                        // if we are sliding down or up a bump, do not reset the road height
                        if(currentWriteIndex <= bumps.size() - 1) {
                            this.bumps.add(BumpType.NO_BUMP.getHeight());
                            s_bumps.add(BumpType.NO_BUMP.getHeight());
                        }
                    }
                    currentWriteIndex++;
                }
            }
        }
    }

    /**
     * Interpolates the road with peak height at given bumType.Height
     * @param bumType
     *      bumType to interpolate road around
     */
    private void interpolate(BumpType bumType) {
        // TODO determine length of bumps, Take 1 Flux length in time
        final float INTERPOLATION_LENGTH = 25;
        final int LENGTH_TO_PEAK = (int)INTERPOLATION_LENGTH / 2;

        // Skip first iteration which would make divider equal to 0
        // Skip last iteration where divider is equal to 1, due to error rounding
        //
        // Doing this we make sure that the heights at start and end
        // of the bump have equal rounding up to 6th digit after 0
        for(int i = 1; i < INTERPOLATION_LENGTH; i++) {
            float divider = i / INTERPOLATION_LENGTH;
            float value = (float) (bumType.getHeight() * Math.sin(Math.PI * divider));

            // set the values from left to peak to right
            int bumpPosition = currentWriteIndex + (i - LENGTH_TO_PEAK);

            //Log.i("Bump_Interpolation", "Index: " + currentWriteIndex + " bumps size: " + this.bumps.size());
            //Log.i("Bump_Interpolation", "Bump: [" + bumpPosition + "] " + " value: " + value);

            // if the position is before currentWriteIndex,
            // then change the value of already computed bump
            if(bumpPosition <= currentWriteIndex) {
                try {
                    // continuity of bumps
                    // Don't replace the values that are smaller than previous
                    float prevValue = this.bumps.get(bumpPosition);
                    if(prevValue < value) {
                        this.bumps.set(bumpPosition, value);
                        s_bumps.set(bumpPosition, value);
                    }
                } catch(IndexOutOfBoundsException e) {
                    //Log.e("Bump_Interpolation", "Trying to set up a bump at negative index");
                    //e.printStackTrace();
                }
            }
            // else add these values
            else {
                if(bumpPosition < bumps.size()) {
                    this.bumps.set(bumpPosition, value);
                    s_bumps.set(bumpPosition, value);
                }
                else {
                    this.bumps.add(value);
                    s_bumps.add(value);
                }
            }
        }

        // go to next Flux
        currentWriteIndex++;
    }
}
