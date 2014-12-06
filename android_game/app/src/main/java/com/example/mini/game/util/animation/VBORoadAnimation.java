package com.example.mini.game.util.animation;


import android.util.Log;

import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.shapes.complex.Road;
import com.example.mini.game.util.enums.TurnStage;
import com.example.mini.game.util.mathematics.Vector3;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;
import static java.lang.Math.abs;

/**
 * Created by user on 2014-11-30.
 */
public class VBORoadAnimation {
    private final static int COMPONENTS_PER_VERTEX = 3;
    private final static int TEXTURE_COMPONENTS_PER_VERTEX = 2;
    private final static float SIN_ARGUMENT_MULTIPLIER = 0.5f;
    private final static float SIN_VALUE_MULTIPLIER = 1.0f;
    private final static float LEVITATION_HEIGHT = 1.3f;
    private final int arrayOfVerticesLength;
    private final float roadLength;
    private float currentTime = 0.0f;
    private float helpCounter = 0.0f;
    private int verticesPerBorder;
    private float timeUnitLength;
    private float roadWidth;
    private TurnStage lastTurn = TurnStage.TURN_LEFT_START;
    /**
     * *** RIGHT TURN AHEAD **********
     */
    private float[] turnRightVertices;
    private int turningOffset = 0;
    public float rememberZ = 0.0f;
    public float alpha;
    private int deepCounter = 12;
    private float nextTurnCounter = 0.0f;
    private float innerRadius;
    private float outerRadius;
    private float rightStablePhaseModifier = 0.0f;

    /**
     * ******************************
     */

    /**
     * *** LEFT TURN AHEAD **********
     */
    private float[] turnLeftVertices;

    /**
     * ******************************
     */

    public VBORoadAnimation(int verticesPerBorder, float timeUnitLength, float roadWidth) {
        this.verticesPerBorder = verticesPerBorder;
        this.timeUnitLength = timeUnitLength;
        this.roadLength = verticesPerBorder * timeUnitLength;
        this.roadWidth = roadWidth;
        this.arrayOfVerticesLength = verticesPerBorder * 2 * COMPONENTS_PER_VERTEX;

        this.outerRadius = (2 * roadLength) / GameBoard.PI;
        this.innerRadius = outerRadius - GameBoard.ROAD_WIDTH;

        generateTurningRightVertices();
        generateTurningLeftVertices();

    }

    private void generateTurningRightVertices() {
        turnRightVertices = new float[verticesPerBorder * 3 * 2];
        float innerOffSet = 0;
        float oneStep = (90 / ((float) verticesPerBorder * 0.8f));
        float radius = 50;
        float[] center = new float[]{-30, 0, -50};

        for (int i = 0; i < turnRightVertices.length; ) {
            // Inner
            turnRightVertices[i++] =
                    innerRadius * cos(innerOffSet * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT)
                            - innerRadius;
            turnRightVertices[i++] = 0.0f;
            turnRightVertices[i++] = innerRadius * sin(innerOffSet * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT);
            // outer
            turnRightVertices[i++] = outerRadius * cos(innerOffSet * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT) -
                    innerRadius;
            turnRightVertices[i++] = 0.0f;
            turnRightVertices[i++] = outerRadius * sin(innerOffSet * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT);

            innerOffSet += oneStep;
        }
    }

    private void generateTurningLeftVertices() {
        turnLeftVertices = new float[verticesPerBorder * 3 * 2];
        float innerOffSet = 180;
        float oneStep = (90 / ((float) verticesPerBorder * 0.8f));
        float radius = 50;
        float[] center = new float[]{-30, 0, -50};

        for (int i = 0; i < turnRightVertices.length; ) {
            // Inner
            turnLeftVertices[i++] =
                    outerRadius * cos(innerOffSet * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT)
                            + outerRadius;
            turnLeftVertices[i++] = 0.0f;
            turnLeftVertices[i++] = outerRadius * sin(innerOffSet * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT);
            // Outer
            turnLeftVertices[i++] = innerRadius * cos(innerOffSet * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT) +
                    outerRadius;
            turnLeftVertices[i++] = 0.0f;
            turnLeftVertices[i++] = innerRadius * sin(innerOffSet * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT);

            innerOffSet -= oneStep;
        }
    }

    public float[] generateNextTexture(float[] oldTexture) {
        float[] newTextures = new float[oldTexture.length];
        float[] oldVertices = new float[2 * TEXTURE_COMPONENTS_PER_VERTEX];
        System.arraycopy(oldTexture, 0, oldVertices, 0, oldVertices.length);

        System.arraycopy(oldTexture, 2 * TEXTURE_COMPONENTS_PER_VERTEX, newTextures, 0,
                oldTexture.length - (2 * TEXTURE_COMPONENTS_PER_VERTEX));

        // Mobius by się nie powstydził : D
        newTextures[newTextures.length - 4] = oldVertices[2];
        newTextures[newTextures.length - 3] = oldVertices[3];
        newTextures[newTextures.length - 2] = oldVertices[0];
        newTextures[newTextures.length - 1] = oldVertices[1];

        return newTextures;
    }


    public float[] generateNextFrame(float[] oldVertices) {
            /* Copy everything beside first 2 vertices */
        float[] newVertices = new float[arrayOfVerticesLength];
        System.arraycopy(oldVertices, 2 * COMPONENTS_PER_VERTEX, newVertices, 0,
                arrayOfVerticesLength - (2 * COMPONENTS_PER_VERTEX));

        switch (Road.currentTurnStage) {
            case STRAIGHT:
                // Left border x, y ,z
                newVertices[arrayOfVerticesLength - 6] = 0;
                newVertices[arrayOfVerticesLength - 5] = 0;/*SIN_VALUE_MULTIPLIER * sin(SIN_ARGUMENT_MULTIPLIER * currentTime)
                        + LEVITATION_HEIGHT;*/
                newVertices[arrayOfVerticesLength - 4] = timeUnitLength * helpCounter;
                // Right border x,y,z
                newVertices[arrayOfVerticesLength - 3] = roadWidth;
                newVertices[arrayOfVerticesLength - 2] = 0;/*SIN_VALUE_MULTIPLIER * sin(SIN_ARGUMENT_MULTIPLIER * currentTime)
                        + LEVITATION_HEIGHT;*/
                newVertices[arrayOfVerticesLength - 1] = timeUnitLength * helpCounter;

                // Update current time
                currentTime += 0.5;
                // Update help counter
                helpCounter++;
                rememberZ = timeUnitLength * helpCounter;
                //Road.rememberMyPlx = timeUnitLength * helpCounter;
                if (nextTurnCounter == GameBoard.TURNING_RIGHT_ANIMATION_FREQUENCY) {
                    TurnStage newTurnStage = (lastTurn == TurnStage.TURN_RIGHT_START) ?
                            TurnStage.TURN_LEFT_START : TurnStage.TURN_RIGHT_START;
                    Road.currentTurnStage = newTurnStage;
                    lastTurn = newTurnStage;
                }

                return newVertices;
            case TURN_RIGHT_START:
                // Left border x, y ,z
                newVertices[arrayOfVerticesLength - 6] = turnRightVertices[turningOffset++];
                newVertices[arrayOfVerticesLength - 5] = turnRightVertices[turningOffset++];
                newVertices[arrayOfVerticesLength - 4] = rememberZ +
                        turnRightVertices[turningOffset++];
                // Right border x,y,z
                newVertices[arrayOfVerticesLength - 3] = turnRightVertices[turningOffset++];
                newVertices[arrayOfVerticesLength - 2] = turnRightVertices[turningOffset++];
                newVertices[arrayOfVerticesLength - 1] = rememberZ +
                        turnRightVertices[turningOffset++];

                // Update current time
                currentTime += 0.5;
                // Update help counter
                helpCounter++;

                //Road.rememberMyPlx = timeUnitLength * helpCounter;
                /* If we filled out array with turnRightVertices values,
                * we swap to STABLE state */
                if (turningOffset >= turnRightVertices.length) {
                    Road.currentTurnStage = TurnStage.TURN_RIGHT_STABLE;
                    turningOffset = 0;
                }

                return newVertices;
            case TURN_LEFT_START:
                // Left border x, y ,z
                newVertices[arrayOfVerticesLength - 6] = turnLeftVertices[turningOffset++];
                newVertices[arrayOfVerticesLength - 5] = turnLeftVertices[turningOffset++];
                newVertices[arrayOfVerticesLength - 4] = rememberZ +
                        turnLeftVertices[turningOffset++];
                // Right border x,y,z
                newVertices[arrayOfVerticesLength - 3] = turnLeftVertices[turningOffset++];
                newVertices[arrayOfVerticesLength - 2] = turnLeftVertices[turningOffset++];
                newVertices[arrayOfVerticesLength - 1] = rememberZ +
                        turnLeftVertices[turningOffset++];

                // Update current time
                currentTime += 0.5;
                // Update help counter
                helpCounter++;


                /* If we filled out array with turnRightVertices values,
                * we swap to STABLE state */
                if (turningOffset >= turnLeftVertices.length) {
                    Road.currentTurnStage = TurnStage.TURN_LEFT_STABLE;
                    turningOffset = 0;
                }
                //Road.rememberMyPlx = timeUnitLength * helpCounter;
                return newVertices;
            case TURN_RIGHT_STABLE:
                nextTurnCounter = 0.0f;
               Road.currentTurnStage = TurnStage.TURN_RIGHT_END;
                return oldVertices;
            case TURN_LEFT_STABLE:
                nextTurnCounter = 0.0f;
                Road.currentTurnStage = TurnStage.TURN_LEFT_END;
                return oldVertices;
            case TURN_RIGHT_END:
                /* Get last 2 points from shrinking curve */
                float inner_x = oldVertices[oldVertices.length - deepCounter];
                float inner_y = oldVertices[oldVertices.length - deepCounter + 1];
                float inner_z = oldVertices[oldVertices.length - deepCounter + 2];

                float outer_x = oldVertices[oldVertices.length - deepCounter + 3];
                float outer_y = oldVertices[oldVertices.length - deepCounter + 4];
                float outer_z = oldVertices[oldVertices.length - deepCounter + 5];

                /* Calculate vector between them(2d plane will be enough) */
                float[] innerOuterVec = new float[]
                        {
                                inner_x - outer_x,
                                inner_z - outer_z
                        };
                /* At this point we are able to calculate 2 vectors perpendicular
                 * to innerOuterVec and we need to choose one of them. */
                float[] perpendicular = new float[]
                        {
                                innerOuterVec[1],
                                innerOuterVec[0]
                        };
                //if (perpendicular[0] < 0) perpendicular[0] *= -1;
                if (perpendicular[1] < 0) perpendicular[1] *= -1;

                /* Normalization to timeUnitLength*/
                float length = sqrt(perpendicular[0] * perpendicular[0] +
                        perpendicular[1] * perpendicular[1]);
                perpendicular[0] = (perpendicular[0] / length) * timeUnitLength;
                perpendicular[1] = (perpendicular[1] / length) * timeUnitLength;

                /* Now, when we have an appropriate vector, we need to
                 * apply it to all new vertices */
                int additionalCounter = deepCounter - 6;
                for (int i = additionalCounter; i > 0; i -= 6) {
                    // Inner vertex
                    oldVertices[oldVertices.length - i] = oldVertices[oldVertices.length - (i + 6)] + perpendicular[0];
                    oldVertices[oldVertices.length - (i - 1)] = oldVertices[oldVertices.length - (i + 5)];
                    oldVertices[oldVertices.length - (i - 2)] = oldVertices[oldVertices.length - (i + 4)] + perpendicular[1];
                    // Outer vertex
                    oldVertices[oldVertices.length - (i - 3)] = oldVertices[oldVertices.length - (i + 3)] + perpendicular[0];
                    oldVertices[oldVertices.length - (i - 4)] = oldVertices[oldVertices.length - (i + 2)];
                    oldVertices[oldVertices.length - (i - 5)] = oldVertices[oldVertices.length - (i + 1)] + perpendicular[1];
                }

                /* At the end we increase counter for the next iteration */
                deepCounter += 6;
                /* When deepCounter exceeds array length we reset it and
                 * change the phase*/
                if (deepCounter > verticesPerBorder * 2 * 3) {
                    Road.currentTurnStage = TurnStage.STRAIGHT;
                    deepCounter = 12;
                }
                return oldVertices;
            case TURN_LEFT_END:
                /* Get last 2 points from shrinking curve */
                float inner_xl = oldVertices[oldVertices.length - deepCounter];
                float inner_yl = oldVertices[oldVertices.length - deepCounter + 1];
                float inner_zl = oldVertices[oldVertices.length - deepCounter + 2];

                float outer_xl = oldVertices[oldVertices.length - deepCounter + 3];
                float outer_yl = oldVertices[oldVertices.length - deepCounter + 4];
                float outer_zl = oldVertices[oldVertices.length - deepCounter + 5];

                /* Calculate vector between them(2d plane will be enough) */
                float[] innerOuterVecl = new float[]
                        {
                                inner_xl - outer_xl,
                                inner_zl - outer_zl
                        };
                /* At this point we are able to calculate 2 vectors perpendicular
                 * to innerOuterVec and we need to choose one of them. */
                float[] perpendicularl = new float[]
                        {
                                innerOuterVecl[1],
                                innerOuterVecl[0]
                        };
                if (perpendicularl[0] < 0) perpendicularl[0] *= -1;
                if (perpendicularl[1] < 0) perpendicularl[1] *= -1;

                /* Normalization to timeUnitLength*/
                float lengthl = sqrt(perpendicularl[0] * perpendicularl[0] +
                        perpendicularl[1] * perpendicularl[1]);
                perpendicularl[0] = (perpendicularl[0] / lengthl) * timeUnitLength;
                perpendicularl[1] = (perpendicularl[1] / lengthl) * timeUnitLength;

                /* Now, when we have an appropriate vector, we need to
                 * apply it to all new vertices */
                int additionalCounterl = deepCounter - 6;
                for (int i = additionalCounterl; i > 0; i -= 6) {
                    // Inner vertex
                    oldVertices[oldVertices.length - i] = oldVertices[oldVertices.length - (i + 6)] + perpendicularl[0];
                    oldVertices[oldVertices.length - (i - 1)] = oldVertices[oldVertices.length - (i + 5)];
                    oldVertices[oldVertices.length - (i - 2)] = oldVertices[oldVertices.length - (i + 4)] + perpendicularl[1];
                    // Outer vertex
                    oldVertices[oldVertices.length - (i - 3)] = oldVertices[oldVertices.length - (i + 3)] + perpendicularl[0];
                    oldVertices[oldVertices.length - (i - 4)] = oldVertices[oldVertices.length - (i + 2)];
                    oldVertices[oldVertices.length - (i - 5)] = oldVertices[oldVertices.length - (i + 1)] + perpendicularl[1];
                }

                /* At the end we increase counter for the next iteration */
                deepCounter += 6;
                /* When deepCounter exceeds array length we reset it and
                 * change the phase*/
                if (deepCounter > verticesPerBorder * 2 * 3) {
                    Road.currentTurnStage = TurnStage.STRAIGHT;
                    deepCounter = 12;
                }
                return oldVertices;
        }
        return oldVertices;
    }

    private void simulateVerticesSwap(float[] oldVertices) {
        /* After end of this loop vertices will keep their Z coordinates,
         * but X's and Y's will be swap in a following manner:
         *(i) inner vertex n will have x and y coordinates of inner vertex n-1
         * (ii) outer vertex n will have x and y coordinates of outer vertex n-1
         * First two vertices in array will keep old values since we will change
         * them anyway.*/
        for (int i = oldVertices.length; i > 6; i = i- 6) {
//            // Inner vertex
            oldVertices[i-6] = oldVertices[i-12];//x
            oldVertices[i-5] = oldVertices[i -11];//y
            // Outer vertex
            oldVertices[i-3] = oldVertices[i-9];//x
            oldVertices[i-2] = oldVertices[i-8];//y
//            Log.i("i:","" + i);
        }
//        for(int i = 0 ; i < 12; i++) {
//            Log.i("CHECK", "["+i+"]: " + oldVertices[i]);
//        }

    }

    public float[] generateStartShape() {
        float oneStep = (90 / (float) verticesPerBorder *
                GameBoard.DEGREES_TO_RADIAN_COEFFICIENT);
        float offSet = 0.0f;
        float[] vertices = new float[arrayOfVerticesLength];

        for (int i = 0; i < arrayOfVerticesLength; ) {
            // Left border x, y ,z
            vertices[i++] = 0;
            vertices[i++] = 0;/*SIN_VALUE_MULTIPLIER * sin(SIN_ARGUMENT_MULTIPLIER * currentTime)
                    + LEVITATION_HEIGHT;*/
            vertices[i++] = timeUnitLength * helpCounter;
            // Right border x,y,z
            vertices[i++] = roadWidth;
            vertices[i++] = 0;/*SIN_VALUE_MULTIPLIER * sin(SIN_ARGUMENT_MULTIPLIER * currentTime)
                    + LEVITATION_HEIGHT;*/
            vertices[i++] = timeUnitLength * helpCounter;
            // Update current time
            currentTime += 0.5;
            // Update help counter
            helpCounter++;
            offSet += oneStep;

        }

        return vertices;
    }

    public void generateNextFrame(Vector3 translation, float[] rotate) {
        if (/*Road.currentPhase != Phase.TURN_RIGHT_STABLE &&*/ Road.currentTurnStage != TurnStage.TURN_LEFT_STABLE
                && Road.currentTurnStage != TurnStage.TURN_RIGHT_END
                && Road.currentTurnStage != TurnStage.TURN_LEFT_END) {
            translation.setZ(translation.getZ() - timeUnitLength);
            Road.rememberMyPlx += timeUnitLength;
        }

//        if (Road.currentPhase == Phase.TURN_RIGHT_STABLE)
//            translation.setX(translation.getX() + rightStablePhaseModifier);
        nextTurnCounter++;
        Log.i("", "plx: " + Road.rememberMyPlx);
    }


}
