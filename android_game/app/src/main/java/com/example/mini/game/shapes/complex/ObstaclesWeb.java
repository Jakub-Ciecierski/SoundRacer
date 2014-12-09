package com.example.mini.game.shapes.complex;

import android.opengl.GLES20;

import com.example.mini.game.shapes.basic.Line;
import com.example.mini.game.shapes.basic.Obstacle;
import com.example.mini.game.util.ShadersController;
import com.example.mini.game.util.animation.LatticeAnimation;
import com.example.mini.game.util.mathematics.Vector3;
import com.example.mini.game.util.obstacles.AvailableObstacleBuffer;
import com.example.mini.game.util.obstacles.RenderedObstacleBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by user on 2014-11-30.
 */
public class ObstaclesWeb {
    private final static float TIME_UNIT_LENGTH = 0.5f;
    private final static int NUMBER_OF_OBSTACLES = 16;
    private final static int OBSTACLE_GENERATION_TIME = 30;

    private int program;
    private LatticeAnimation latticeAnimation;

    /**
     * * Width Lines Data ***
     */
    private int numberOfLinesWidth;
    private Line[] linesWidth;
    private FloatBuffer linesWidthVertexBuffer; //TODO consider one float buffer with a stride.
    private float[] linesWidthVerticesCoordinates = new float[]{
            0f, 0f, 0f,
            1f, 0f, 0f
    };
    private float[] linesWidthColor = new float[]{0.5f, 0.5f, 0.5f, 1.0f,};
    /*************************/

    /**
     * * Length Lines Data ***
     */
    private int numberOfLinesLength;
    private Line[] linesLength;
    private FloatBuffer linesLengthVertexBuffer; //TODO consider one float buffer with a stride.
    private float[] linesLengthVerticesCoordinates = new float[]{
            0f, 0f, 0f,
            0f, 0f, 1f
    };
    private float[] linesLengthColor = new float[]{0.5f, 0.5f, 0.5f, 1.0f,};
    /**
     * *********************
     */

    private Obstacle[] obstacles = new Obstacle[NUMBER_OF_OBSTACLES];
    private AvailableObstacleBuffer availableObstacles = new AvailableObstacleBuffer();
    private RenderedObstacleBuffer renderedObstacles;
    private int obstacleGenerationCounter = 0;

    public ObstaclesWeb(float width, float length, float gapLength, Vector3 bottomRightCorner) {
        numberOfLinesLength = (int) (Math.abs(bottomRightCorner.getX() - width) / gapLength);
        /* We need one less to keep providing illusion of movement */
        numberOfLinesWidth = (int) (Math.abs(bottomRightCorner.getZ() - length) / gapLength) - 1;

        linesWidth = new Line[numberOfLinesWidth];
        linesLength = new Line[numberOfLinesLength];

        program = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.vertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.fragmentShader));


        linesWidthVerticesCoordinates[3] = width;
        linesLengthVerticesCoordinates[5] = length;
        loadBuffers();

        for (int i = 1; i <= numberOfLinesWidth; i++) {
            linesWidth[i - 1] = new Line(linesWidthColor, linesWidthVertexBuffer,
                    new Vector3(0f, 0f, i * gapLength), program);
        }
        for (int j = 1; j <= numberOfLinesLength; j++) {
            linesLength[j - 1] = new Line(linesLengthColor, linesLengthVertexBuffer,
                    new Vector3(j * gapLength, 0f, 0f), program);
        }

//        latticeAnimation = new LatticeAnimation(bottomRightCorner.getZ(),
//                bottomRightCorner.getZ() + length, TIME_UNIT_LENGTH);

        for (int k = 0; k < obstacles.length; k++) {
            obstacles[k] = new Obstacle(gapLength,
                    4.0f, gapLength, new float[]{0.1f, 0.2f, 0.3f, 0.8f});
        }
        availableObstacles.addToBuffer(obstacles);
//        renderedObstacles = new RenderedObstacleBuffer(numberOfLinesLength, gapLength);
    }

    public void draw(float[] mvpMatrix) {
        for (int i = 0; i < numberOfLinesWidth; i++) {
            linesWidth[i].draw(mvpMatrix);
        }
        for (int j = 0; j < numberOfLinesLength; j++) {
            linesLength[j].draw(mvpMatrix);
        }
        for (int k = 0; k < obstacles.length; k++) {
            obstacles[k].draw(mvpMatrix);
        }
    }

    public void updateFrame() {
        float vanishBorder = 0.0f;
        float spawnBorder = 37.5f;

        for (int i = 0; i < numberOfLinesWidth; i++) {
            if (linesWidth[i].getTranslationsZ() <= vanishBorder)
                linesWidth[i].setTranslationsZ(spawnBorder);
            else
                linesWidth[i].setTranslationsZ(linesWidth[i].getTranslationsZ() - 0.5f);
        }
        availableObstacles.addToBuffer(renderedObstacles.getFreeObstacles(0.0f));

        obstacleGenerationCounter++;
        if (obstacleGenerationCounter == OBSTACLE_GENERATION_TIME) {
            // If there are 2 available obstacles take them for rendering
            renderedObstacles.addToBuffer(availableObstacles.getObstacles(2));
            // Reset counter
            obstacleGenerationCounter = 0;
        }

    }

    private void loadBuffers() {
        linesWidthVertexBuffer = ByteBuffer.allocateDirect(linesWidthVerticesCoordinates.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(linesWidthVerticesCoordinates);
        linesWidthVertexBuffer.position(0);

        linesLengthVertexBuffer = ByteBuffer.allocateDirect(linesLengthVerticesCoordinates.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(linesLengthVerticesCoordinates);
        linesLengthVertexBuffer.position(0);
    }

    // animate this web taking parameters from road animation
    // add obstacle creation, random generation + check storage
    // move obstacles
    // move to storage and take out when needed
}
