package com.example.mini.game.util.animation;


import android.util.Log;

import com.example.mini.game.audio.AudioAnalyser;
import com.example.mini.game.audio.Bumper;
import com.example.mini.game.logic.GlobalState;
import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.shapes.complex.Player;
import com.example.mini.game.shapes.complex.Road;
import com.example.mini.game.util.RoadVertex;
import com.example.mini.game.util.camera.PlayerStaticSphereCamera;
import com.example.mini.game.util.enums.TurnStage;
import com.example.mini.game.util.mathematics.Vector3;

import java.util.ArrayList;
import java.util.List;

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
    public static List<RoadVertex> generateNewShit(List<RoadVertex> oldVertices,Vector3 translation) {

        /* Copy everything beside first 2 vertices */
//        float[] newVertices = new float[oldVertices.length];
//        System.arraycopy(oldVertices, 2 * COMPONENTS_PER_VERTEX, newVertices, 0,
//                oldVertices.length - (2 * COMPONENTS_PER_VERTEX));
        oldVertices.subList(0, 2).clear();


        float value = Bumper.getNextBumper();
        oldVertices.add(new RoadVertex(0,value,GameBoard.TIME_UNIT_LENGTH * timeCounter,false));
        oldVertices.add(new RoadVertex(GameBoard.ROAD_WIDTH,value,GameBoard.TIME_UNIT_LENGTH * timeCounter,false));

        Player.setTranslate(Player.getTranslationX(), Road.vertices.get(0).y + 2, Player.getTranslationZ());
        PlayerStaticSphereCamera.moveCameraBy(Player.getTranslationY() + 4);

        //
        timeCounter++;
        translation.setZ(translation.getZ() - GameBoard.TIME_UNIT_LENGTH);

        return oldVertices;

    }
    public List<RoadVertex> generateNextFrame(List<RoadVertex> oldVertices, Vector3 translation) {
        oldVertices.subList(0, 2).clear();

        switch (Road.currentTurnStage) {
            case STRAIGHT:
                oldVertices.add(new RoadVertex(0,Bumper.getNextBumper(),GameBoard.TIME_UNIT_LENGTH * timeCounter,false));
                oldVertices.add(new RoadVertex(GameBoard.ROAD_WIDTH,Bumper.getNextBumper(),GameBoard.TIME_UNIT_LENGTH * timeCounter,false));
                // Update help counter
                timeCounter++;
                rememberZ = GameBoard.TIME_UNIT_LENGTH * timeCounter;
                nextTurnCounter++;
                Road.totalZTranslation += GameBoard.TIME_UNIT_LENGTH;
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
    public ArrayList generateStartShape() {
        //float[] vertices = new float[arrayOfVerticesLength];
        ArrayList vertices = new ArrayList();
        for (int i = 0; i < verticesPerBorder; i++) {
            float value = Bumper.getNextBumper();
            vertices.add(new RoadVertex(0.0f,value,GameBoard.TIME_UNIT_LENGTH * timeCounter,false));
            vertices.add(new RoadVertex(GameBoard.ROAD_WIDTH,value,GameBoard.TIME_UNIT_LENGTH * timeCounter,false));

            /* Update vertex counter <=> position on road */
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
