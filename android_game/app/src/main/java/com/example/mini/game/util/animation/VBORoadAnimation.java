package com.example.mini.game.util.animation;


import android.util.Log;

import com.example.mini.game.audio.BumperAnalyser;
import com.example.mini.game.audio.analysis.Bump;
import com.example.mini.game.shapes.basic.RoadObstacle;
import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.shapes.complex.Player;
import com.example.mini.game.shapes.complex.Road;
import com.example.mini.game.shapes.basic.RoadVertex;
import com.example.mini.game.util.camera.PlayerStaticSphereCamera;
import com.example.mini.game.util.enums.TurnStage;
import com.example.mini.game.util.lists.RoadVerticesList;
import com.example.mini.game.util.mathematics.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

/**
 * Created by dybisz on 2014-11-30.
 */
public class VBORoadAnimation {
    private static int OBSTACLE_GENERATION_TIMER = 0;
    private final static int TEXTURE_COMPONENTS_PER_VERTEX = 2;
    private static float timeCounter = 0.0f;
    static Random rand = new Random();
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

    public static RoadVerticesList generateNewShit(RoadVerticesList oldVertices, Vector3 translation) {
        oldVertices.remove(0, 2);
        Bump bump = BumperAnalyser.getNextBumperObj();
        oldVertices.add(new RoadVertex(0, bump.getValue(), GameBoard.TIME_UNIT_LENGTH * timeCounter, (timeCounter%25 == 0)));
        oldVertices.add(new RoadVertex(GameBoard.ROAD_WIDTH, bump.getValue(), GameBoard.TIME_UNIT_LENGTH * timeCounter, (timeCounter%25 == 0)));
        if(timeCounter%25 == 0) {
            Road.obstacles.add(new RoadObstacle(rand.nextInt(6)*GameBoard.ROAD_WIDTH/5, bump.getValue(), GameBoard.TIME_UNIT_LENGTH * timeCounter));
        }

        Player.setTranslate(Player.getTranslationX(), Road.vertices.get(0).y + 2, Player.getTranslationZ());
        PlayerStaticSphereCamera.moveCameraBy(Player.getTranslationY()+3);

        //
        timeCounter++;
        translation.setZ(translation.getZ() - GameBoard.TIME_UNIT_LENGTH);

        return oldVertices;

    }

   /**
     * At the beginning we just create a road shape for further animation.
     *
     * @return Array of vertices of newly created road.
     */
    public void generateStartShape() {
        for (int i = 0; i < GameBoard.ROAD_VERTICES_PER_BORDER; i++) {
            Bump bump = BumperAnalyser.getNextBumperObj();
            /* 2 new vertices*/
            Road.vertices.add(new RoadVertex(0.0f, bump.getValue(), GameBoard.TIME_UNIT_LENGTH * timeCounter, (timeCounter%25 == 0)));
            Road.vertices.add(new RoadVertex(GameBoard.ROAD_WIDTH, bump.getValue(), GameBoard.TIME_UNIT_LENGTH * timeCounter, (timeCounter%25 == 0)));
            /* New bump if needed */
            if (timeCounter% 25 == 0) {
                Road.obstacles.add(new RoadObstacle(
                        rand.nextInt(6)*GameBoard.ROAD_WIDTH/5, bump.getValue(), GameBoard.TIME_UNIT_LENGTH * timeCounter));
            }
            /* Update vertex counter <=> position on road */
            timeCounter++;
        }
    }

}
