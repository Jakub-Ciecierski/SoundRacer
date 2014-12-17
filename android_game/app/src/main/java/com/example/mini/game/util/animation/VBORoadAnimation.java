package com.example.mini.game.util.animation;


import android.util.Log;

import com.example.mini.game.audio.AudioAnalyser;
import com.example.mini.game.audio.Bumper;
import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.shapes.complex.Player;
import com.example.mini.game.shapes.complex.Road;
import com.example.mini.game.util.camera.PlayerStaticSphereCamera;
import com.example.mini.game.util.enums.TurnStage;
import com.example.mini.game.util.mathematics.Vector3;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;
import static android.util.FloatMath.sqrt;
import static java.lang.Math.abs;

/**
 * Created by dybisz on 2014-11-30.
 */
public class VBORoadAnimation {
    private final static int COMPONENTS_PER_VERTEX = 3;
    private final static int TEXTURE_COMPONENTS_PER_VERTEX = 2;
    private final int arrayOfVerticesLength;
    private final float roadLength;
    private static float timeCounter = 0.0f;
    private int verticesPerBorder;
    private float timeUnitLength;
    private TurnStage lastTurn = TurnStage.TURN_RIGHT_START;
    /**
     * *** RIGHT TURN AHEAD **********
     */
    private float[] turnRightVertices;
    private int turningOffset = 0;
    public float rememberZ = 0.0f;
    public float alpha;
    private int deepThroatCounter = 12;
    private float nextTurnCounter = 0.0f;
    private float innerRadius;
    private float outerRadius;
    private float rightStablePhaseModifier = 0.0f;
    private float[] turnLeftVertices;
    private float tempCounter = 0.0f;


    /**
     * ******************************
     */

    public VBORoadAnimation(int verticesPerBorder, float timeUnitLength, float roadWidth) {
        this.verticesPerBorder = verticesPerBorder;
        this.timeUnitLength = timeUnitLength;
        this.roadLength = verticesPerBorder * timeUnitLength;
        this.arrayOfVerticesLength = verticesPerBorder * 2 * COMPONENTS_PER_VERTEX;

        this.outerRadius = (2 * roadLength) / GameBoard.PI;
        this.innerRadius = outerRadius - GameBoard.ROAD_WIDTH;

        generateTurningRightVertices(90);
        generateTurningLeftVertices(90);

    }

    /**
     * Fills {@link #turnRightVertices} with appropriate values i.e.
     * coordinates of vertices simulating turning right.
     *
     * @param angleOfTurn How curvy you want the turn to be?
     */
    private void generateTurningRightVertices(float angleOfTurn) {
        turnRightVertices = new float[verticesPerBorder * 3 * 2];
        float startAngle = 0;
        float oneStep = (angleOfTurn / ((float) verticesPerBorder));
        for (int i = 0; i < turnRightVertices.length; ) {
            /* Inner vertex */
            turnRightVertices[i++] =
                    innerRadius * cos(startAngle * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT)
                            - innerRadius;
            turnRightVertices[i++] = 0.0f;
            turnRightVertices[i++] = innerRadius * sin(startAngle * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT);
            /* Outer vertex */
            turnRightVertices[i++] = outerRadius * cos(startAngle * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT) -
                    innerRadius;
            turnRightVertices[i++] = 0.0f;
            turnRightVertices[i++] = outerRadius * sin(startAngle * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT);

            startAngle += oneStep;
        }
    }

    /**
     * Fills {@link #turnRightVertices} with appropriate values i.e.
     * coordinates of vertices simulating turning left.
     *
     * @param angleOfTurn How curvy you want the turn to be?
     */
    private void generateTurningLeftVertices(float angleOfTurn) {
        turnLeftVertices = new float[verticesPerBorder * 3 * 2];
        float innerOffSet = 180;
        float oneStep = (angleOfTurn / ((float) verticesPerBorder));
        for (int i = 0; i < turnRightVertices.length; ) {
            /* Inner vertex */
            turnLeftVertices[i++] =
                    outerRadius * cos(innerOffSet * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT)
                            + outerRadius;
            turnLeftVertices[i++] = 0.0f;
            turnLeftVertices[i++] = outerRadius * sin(innerOffSet * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT);
            /* Outer vertex */
            turnLeftVertices[i++] = innerRadius * cos(innerOffSet * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT) +
                    outerRadius;
            turnLeftVertices[i++] = 0.0f;
            turnLeftVertices[i++] = innerRadius * sin(innerOffSet * GameBoard.DEGREES_TO_RADIAN_COEFFICIENT);

            innerOffSet -= oneStep;
        }
    }

    /**
     * Updates given set of texture coordinates by 'rolling it'.
     * 'Rolling' means moving all texture coordinates down by one vertex and
     * adding previously lost first 2 vertices at the end. More or less.
     *
     * @param oldTexture Set of texture coordinates to be 'rolled'.
     * @return 'Rolled' texture coordinates.
     */
    public static float[] generateNextTexture(float[] oldTexture) {
        float[] newTextures = new float[oldTexture.length];
        float[] oldVertices = new float[2 * TEXTURE_COMPONENTS_PER_VERTEX];
        System.arraycopy(oldTexture, 0, oldVertices, 0, oldVertices.length);

        System.arraycopy(oldTexture, 2 * TEXTURE_COMPONENTS_PER_VERTEX, newTextures, 0,
                oldTexture.length - (2 * TEXTURE_COMPONENTS_PER_VERTEX));

        // Mobius by się nie powstydził :-D
        newTextures[newTextures.length - 4] = oldVertices[2];
        newTextures[newTextures.length - 3] = oldVertices[3];
        newTextures[newTextures.length - 2] = oldVertices[0];
        newTextures[newTextures.length - 1] = oldVertices[1];

        return newTextures;
    }
    public static float[] generateNewShit(float[] oldVertices,Vector3 translation) {

        /* Copy everything beside first 2 vertices */
        float[] newVertices = new float[oldVertices.length];
        System.arraycopy(oldVertices, 2 * COMPONENTS_PER_VERTEX, newVertices, 0,
                oldVertices.length - (2 * COMPONENTS_PER_VERTEX));

        float value = Bumper.getNextBumper();

        //Log.i("GENERATE_NEW_SHIT", timeCounter + ": " + value);
        /* Fill out 2 last vertices */
        newVertices[oldVertices.length - 6] = 0;
        newVertices[oldVertices.length - 5] = value;
        newVertices[oldVertices.length - 4] = GameBoard.TIME_UNIT_LENGTH * timeCounter;
        // Right border x,y,z
        newVertices[oldVertices.length - 3] = GameBoard.ROAD_WIDTH;
        newVertices[oldVertices.length - 2] = value;
        newVertices[oldVertices.length - 1] = GameBoard.TIME_UNIT_LENGTH * timeCounter;

        Player.setTranslate(Player.getTranslationX(), Road.vertices[1] + 2, Player.getTranslationZ());
        PlayerStaticSphereCamera.moveCameraBy(Player.getTranslationY() + 4);

        //
        timeCounter++;
        translation.setZ(translation.getZ() - GameBoard.TIME_UNIT_LENGTH);

        return newVertices;

    }
    public float[] generateNextFrame(float[] oldVertices, Vector3 translation) {
        /* Copy everything beside first 2 vertices */
        float[] newVertices = new float[arrayOfVerticesLength];
        System.arraycopy(oldVertices, 2 * COMPONENTS_PER_VERTEX, newVertices, 0,
                arrayOfVerticesLength - (2 * COMPONENTS_PER_VERTEX));



        switch (Road.currentTurnStage) {
            case STRAIGHT:
                // Left border x, y ,z
                newVertices[arrayOfVerticesLength - 6] = 0;
                newVertices[arrayOfVerticesLength - 5] = Bumper.getNextBumper();
                newVertices[arrayOfVerticesLength - 4] = GameBoard.TIME_UNIT_LENGTH * timeCounter;
                // Right border x,y,z
                newVertices[arrayOfVerticesLength - 3] = GameBoard.ROAD_WIDTH;
                newVertices[arrayOfVerticesLength - 2] = Bumper.getNextBumper();
                newVertices[arrayOfVerticesLength - 1] = GameBoard.TIME_UNIT_LENGTH * timeCounter;

                // Update help counter
                timeCounter++;
                rememberZ = GameBoard.TIME_UNIT_LENGTH * timeCounter;
                nextTurnCounter++;
                Road.totalZTranslation += GameBoard.TIME_UNIT_LENGTH;
//                if (nextTurnCounter == GameBoard.TURNING_RIGHT_ANIMATION_FREQUENCY) {
//                    TurnStage newTurnStage = (lastTurn == TurnStage.TURN_RIGHT_START) ?
//                            TurnStage.TURN_LEFT_START : TurnStage.TURN_RIGHT_START;
//                    Road.currentTurnStage = newTurnStage;
//                    lastTurn = newTurnStage;
//                }

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

                // Update help counter
                timeCounter++;

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

                timeCounter++;

                /* If we filled out array with turnRightVertices values,
                * we swap to STABLE state */
                if (turningOffset >= turnLeftVertices.length) {
                    Road.currentTurnStage = TurnStage.TURN_LEFT_STABLE;
                    turningOffset = 0;
                }
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
                float inner_x = oldVertices[oldVertices.length - deepThroatCounter];
                float inner_z = oldVertices[oldVertices.length - deepThroatCounter + 2];
                float outer_x = oldVertices[oldVertices.length - deepThroatCounter + 3];
                float outer_z = oldVertices[oldVertices.length - deepThroatCounter + 5];

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

                /* Normalization to timeUnitLength */
                float length = sqrt(perpendicular[0] * perpendicular[0] +
                        perpendicular[1] * perpendicular[1]);
                perpendicular[0] = (perpendicular[0] / length) * timeUnitLength;
                perpendicular[1] = (perpendicular[1] / length) * timeUnitLength;

                /* Now, when we have an appropriate vector, we need to
                 * apply it to all new vertices */
                int additionalCounter = deepThroatCounter - 6;
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
                deepThroatCounter += 6;
                /* When deepThroatCounter exceeds array length we reset it and
                 * change the phase*/
                if (deepThroatCounter > verticesPerBorder * 2 * 3) {
                    Road.currentTurnStage = TurnStage.STRAIGHT;
                    deepThroatCounter = 12;
                }
                return oldVertices;
            case TURN_LEFT_END:
                /* Get last 2 points from shrinking curve */
                float inner_xl = oldVertices[oldVertices.length - deepThroatCounter];
                float inner_zl = oldVertices[oldVertices.length - deepThroatCounter + 2];
                float outer_xl = oldVertices[oldVertices.length - deepThroatCounter + 3];
                float outer_zl = oldVertices[oldVertices.length - deepThroatCounter + 5];

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
                int additionalCounterl = deepThroatCounter - 6;
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
                deepThroatCounter += 6;
                /* When deepThroatCounter exceeds array length we reset it and
                 * change the phase*/
                if (deepThroatCounter > verticesPerBorder * 2 * 3) {
                    Road.currentTurnStage = TurnStage.STRAIGHT;
                    deepThroatCounter = 12;
                }
                return oldVertices;
        }
        /* For animation purpose only */
        nextTurnCounter++;
        return oldVertices;
    }

    /**
     * At the beginning we just create a road shape for further animation.
     *
     * @return Array of vertices of newly created road.
     */
    public float[] generateStartShape() {
        float[] vertices = new float[arrayOfVerticesLength];
        for (int i = 0; i < arrayOfVerticesLength; ) {
            float value = Bumper.getNextBumper();
            //Log.i("START_SHAPE","VALUE: " + value);
            /* Inner vertex */
            vertices[i++] = 0;
            vertices[i++] = value;
            vertices[i++] = GameBoard.TIME_UNIT_LENGTH * timeCounter;
            /* Outer vertex */
            vertices[i++] = GameBoard.ROAD_WIDTH;
            vertices[i++] = value;
            vertices[i++] = GameBoard.TIME_UNIT_LENGTH * timeCounter;
            /* Update vertex */
            timeCounter++;
        }
        return vertices;
    }

    /**
     * Method 'animates' the road by translating it on Z axis.
     * It also increase {@link com.example.mini.game.shapes.complex.Road#totalZTranslation}
     * value.
     *
     * @param translation Since this this class does not have access to
     *                    {@link com.example.mini.game.shapes.complex.Road} fields,
     *                    translation vector need to be shared.
     */
    public void translateByTimeUnit(Vector3 translation) {
        if (Road.currentTurnStage != TurnStage.TURN_LEFT_STABLE
                && Road.currentTurnStage != TurnStage.TURN_RIGHT_END
                && Road.currentTurnStage != TurnStage.TURN_LEFT_END
                && Road.currentTurnStage != TurnStage.TURN_RIGHT_STABLE) {
            translation.setZ(translation.getZ() - GameBoard.TIME_UNIT_LENGTH);
            //Road.totalZTranslation += timeUnitLength;
        }
    }


}
