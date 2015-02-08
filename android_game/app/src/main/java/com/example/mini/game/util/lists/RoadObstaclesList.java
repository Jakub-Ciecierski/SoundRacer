package com.example.mini.game.util.lists;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.mini.game.R;
import com.example.mini.game.models.ObjModel;
import com.example.mini.game.shapes.basic.RoadObstacle;
import com.example.mini.game.shapes.complex.Road;
import com.example.mini.game.util.ShadersController;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * To wrap all {@link com.example.mini.game.shapes.basic.RoadObstacle} elements
 * and provide neat interface for float[] casting.
 * <p></p>
 * Created by user on 2015-02-07.
 */
public class RoadObstaclesList {
    private ObjModel coin = new ObjModel(R.raw.coin, R.drawable.coin_tex);
    private float MOVEMENT_SPEED_DECREASE = 0.5f;

    /**
     * Main list with obstacles to render.
     */
    List<RoadObstacle> obstacles = new ArrayList<RoadObstacle>();

    int textureProgram;
    {
        textureProgram = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.textureVertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.textureFragmentShader));
    }
    FloatBuffer vertexVbo;
    {
        vertexVbo = ByteBuffer.allocateDirect(coin.getVerticesSize() * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(coin.verticesAsFloats());
        vertexVbo.position(0);
    }
    FloatBuffer textureVbo;
    {
        textureVbo = ByteBuffer.allocateDirect(coin.getUvsSize() * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(coin.uvAsFloats());
        textureVbo.position(0);
    }

    public synchronized void draw(float[] mvpMatrix) {
        for(RoadObstacle roadObstacle : obstacles) {
            draw(mvpMatrix, vertexVbo, roadObstacle.getTranslation());
        }
    }
    public synchronized void add(RoadObstacle roadObstacle) {
        obstacles.add(roadObstacle);
    }

    public synchronized void remove(int i) {
        obstacles.remove(i);
    }

    public synchronized void remove(int leftBoundary, int rightBoundary) {
        obstacles.subList(leftBoundary, rightBoundary).clear();
    }

    public synchronized int length() {
        return obstacles.size();
    }

    public synchronized void draw(float[] mvpMatrix, FloatBuffer vertexBuffer, float[] translation) {
        /* Use compiled program to refer shaders attributes/uniforms */
        GLES20.glUseProgram(textureProgram);

        int a_VertexPositionHandle = GLES20.glGetAttribLocation(textureProgram, "a_Position");
        int a_TextureCoordinatesHandle = GLES20.glGetAttribLocation(textureProgram, "a_TextureCoordinates");
        int u_TransformationMatrixHandle = GLES20.glGetUniformLocation(textureProgram, "u_Matrix");
        int u_TextureSamplerHandle = GLES20.glGetUniformLocation(textureProgram, "u_TextureUnit");

        /* Enable handle (I don't get it ) */
        GLES20.glEnableVertexAttribArray(a_VertexPositionHandle);
        GLES20.glEnableVertexAttribArray(a_TextureCoordinatesHandle);

        /* Connect vPosition with our buffer */
        GLES20.glVertexAttribPointer(a_VertexPositionHandle, 3,
                GLES20.GL_FLOAT, false,
        /*stride*/  0, vertexBuffer);
        GLES20.glVertexAttribPointer(a_TextureCoordinatesHandle, 2,
                GLES20.GL_FLOAT, false,/*stride*/  0, textureVbo);


        /* Active texture unit = 0 */
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        /* Bind our loaded texture to this unit */
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, coin.getTextureId());

        /* Set unit sampler in shader to use this unit */
        GLES20.glUniform1i(u_TextureSamplerHandle, 0);


        // Pass the projection and view transformation to the shader
        float[] scratch = new float[16];
        Matrix.translateM(scratch, 0, mvpMatrix, 0,
                Road.translation.getX() + translation[0],
                Road.translation.getY() + translation[1],
                Road.translation.getZ() + translation[2]);
        GLES20.glUniformMatrix4fv(u_TransformationMatrixHandle, 1, false, scratch, 0);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,coin.getVerticesSize()/3);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(a_VertexPositionHandle);
        GLES20.glDisableVertexAttribArray(a_TextureCoordinatesHandle);
    }

}
