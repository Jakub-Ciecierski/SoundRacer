package com.example.mini.game.util.animation;


import com.example.mini.game.audio.BumperAnalyser;
import com.example.mini.game.audio.analysis.Bump;
import com.example.mini.game.shapes.basic.RoadObstacle;
import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.shapes.complex.Player;
import com.example.mini.game.shapes.complex.Road;
import com.example.mini.game.shapes.basic.RoadVertex;
import com.example.mini.game.util.camera.PlayerStaticSphereCamera;
import com.example.mini.game.util.lists.RoadVerticesList;
import com.example.mini.game.util.mathematics.Vector3;

import java.util.Random;

import static java.lang.Math.abs;

/**
 * Created by dybisz on 2014-11-30.
 */
public class VBORoadAnimation {
    private final static int TEXTURE_COMPONENTS_PER_VERTEX = 2;
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

    public static RoadVerticesList generateNewShit(RoadVerticesList oldVertices) {
        Bump bump = BumperAnalyser.getNextBumperObj();

        oldVertices.remove(0, 2);
        oldVertices.add(new RoadVertex(0, bump.getValue(), 0, false));
        oldVertices.add(new RoadVertex(GameBoard.ROAD_WIDTH, bump.getValue(), 0, false));
        oldVertices.keepInPosition();

        //Player.setTranslate(Player.getTranslationX(), Road.vertices.get(0).y + 2, Player.getTranslationZ());
        //PlayerStaticSphereCamera.moveCameraBy(Player.getTranslationY()+3);
        return oldVertices;

    }



}
