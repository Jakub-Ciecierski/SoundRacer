package com.example.mini.game.audio;

import android.util.Log;

import com.example.mini.game.audio.analysis.Bump;

import java.util.ArrayList;
import java.util.List;

/**
 * This class computes bumps to be implemented on the road.
 * Using computations on small samples of spectral flux of entire audio,
 * we determine the points of interests and their magnitudes.
 *
 * Created by Kuba on 07/12/2014.
 */
public class BumperAnalyser {
    /**
     *  Each position in the list, represents a single Flux - peaks in audio playback.
     *  We determine the height of the bump by calculating the points of interests in spectral flux
     */
    private static List<Float> bumps = new ArrayList<Float>();
    private static int currentReadIndex = 0;

    private static List<Bump> bumpsObj = new ArrayList<Bump>();
    private static int currentReadIndexObj = 0;

    /**
     * Pointer to current position of writing index for bumps
     */
    private static int currentWriteIndex = -1;


    /**
     * Gets next bumps value and moves the pointer to next position
     * @return
     *      Next bump value or -1 if Read index points at non existing element
     */
    public static float getNextBumper() {
        if(currentReadIndex >= bumps.size())
            return -1;
        float value = bumps.get(currentReadIndex);
        // increment the pointer
        currentReadIndex++;
        //Log.i("Bumper", "Bumper[" + currentReadIndex + "]: " + value);
        return value;
    }

    public static Bump getNextBumperObj() {
        if(currentReadIndexObj >= bumpsObj.size())
            return new Bump(BumpType.NO_BUMP.getHeight());
        Bump bump= bumpsObj.get(currentReadIndexObj);
        // increment the pointer
        currentReadIndexObj++;
        Log.i("Bumper", "Bumper[" + currentReadIndexObj + "]: " + bump.getValue());
        if(bump.isValidObstacle()) {
            Log.i("Bumper", "Bumper[" + currentReadIndexObj + "]: is an obstacle");
        }
        return bump;
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
     * Computes bumps for next flux sample
     * @param fluxSample
     *      Flux sample to be computed
     */
    protected static void computeBumps(Flux[] fluxSample, float average, float max, float min) {
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
            else if(bumpHeight >= max * 0.65f && flux.isValidBump()) {
                interpolate(BumpType.MEDIUM_BUMP);
            }
            else if(bumpHeight >= max * 0.50f && flux.isValidBump()) {
                interpolate(BumpType.SMALL_BUMP);
            }
            // if flux is not big enough, set no bumps
            // make sure to increment pointer to write index
            else {

                // if fist, simple add it
                if(currentWriteIndex == -1) {
                    bumpsObj.add(new Bump(BumpType.NO_BUMP.getHeight()));
                    bumps.add(BumpType.NO_BUMP.getHeight());
                }
                else {
                    // if we are sliding down or up a bump, do not reset the road height
                    if(currentWriteIndex <= bumpsObj.size() - 1) {
                        bumpsObj.add(new Bump(BumpType.NO_BUMP.getHeight(),flux.isValidObstacle(max)));
                        bumps.add(BumpType.NO_BUMP.getHeight());
                    }
                }
                currentWriteIndex++;
            }
        }
    }

    /**
     * Interpolates the road with peak height at given bumType.Height
     * @param bumType
     *      bumType to interpolate road around
     */
    private static void interpolate(BumpType bumType) {
        // TODO determine length of bumps, Take 1 Flux length in time
        final float INTERPOLATION_LENGTH = 50;
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
                    Bump prevBump = bumpsObj.get(bumpPosition);
                    float prevValue = bumps.get(bumpPosition);

                    if(prevBump.getValue() < value) {
                        bumpsObj.set(bumpPosition,new Bump(value, prevBump.isValidObstacle()));
                        bumps.set(bumpPosition, value);
                    }
                } catch(IndexOutOfBoundsException e) {
                    //Log.e("Bump_Interpolation", "Trying to set up a bump at negative index");
                    //e.printStackTrace();
                }
            }
            // else add these values
            else {
                if(bumpPosition < bumpsObj.size()) {
                    Bump bump = bumpsObj.get(bumpPosition);

                    if(bump.getValue() < value) {
                        bump.setValue(value);
                        bumpsObj.set(bumpPosition, bump);
                        bumps.set(bumpPosition, value);
                    }
                }
                else {
                    bumpsObj.add(new Bump(value));
                    bumps.add(value);
                }
            }
        }

        // go to next Flux
        currentWriteIndex++;
    }

/*    public static void reset() {
        bumps = new ArrayList<Float>();
        currentReadIndex = 0;
        currentWriteIndex = -1;
    }*/
}