package com.example.mini.game.util.lists;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.mini.game.GameRenderer;
import com.example.mini.game.shapes.basic.RoadObstacle;
import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.shapes.complex.Road;
import com.example.mini.game.util.ShadersController;
import com.example.mini.game.util.mathematics.Vector3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * To wrap all {@link com.example.mini.game.shapes.basic.RoadObstacle} elements
 * and provide neat interface for float[] casting.
 * <p></p>
 * Created by user on 2015-02-07.
 */
public class RoadObstaclesList {
    private final static int COORDINATES_PER_VERTEX = 3;

    /**
     * Main list with obstacles to render.
     */
    List<RoadObstacle> obstacles = new ArrayList<RoadObstacle>();
    /**
     * Each obstacle has the same draw order.
     */
    public static short[] drawOrder = new short[]{
            0, 3, 1, 0, 2, 3,
            5, 1, 3, 3, 4, 5,
            6, 5, 4, 4, 7, 6,

    };

    /**
     * This buffer will be used statically to each obstacle we want to render.
     */
    public static ShortBuffer drawOrderBuffer;

    {
        drawOrderBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer().put(drawOrder);
        drawOrderBuffer.position(0);
    }

    /**
     * Index of out shader program. Created statically because it will be the
     * for all obstacles.
     */
    int fogProgram;

    {
        fogProgram = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.fogVertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.fogFragmentShader));
    }
    Vector3 translation = new Vector3(0.0f, 0.0f, 0.0f);
    /**
     * Method produces array of floats, which are ready to
     * be added to the vertices buffer end used for rendering
     *
     * @return
     */
    public synchronized float[] asFloatArray() {
        float[] array = new float[obstacles.size() *
                RoadObstacle.COORDINATES_PER_VERTEX *
                RoadObstacle.NUMBER_OF_VERTICES];
        int d = 0;

        for (RoadObstacle rO : obstacles) {
            for (int i = 0; i < rO.getVertices().length; i++) {
                array[d++] = rO.getVertices()[i];
            }
        }

        return array;
    }
    public synchronized void draw(float[] mvpMatrix) {
        for(RoadObstacle roadObstacle : obstacles) {
            fogDraw(mvpMatrix, roadObstacle.getVerticesVbo());
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

    public synchronized void fogDraw(float[] mvpMatrix, FloatBuffer vertexBuffer) {
         /* Use compiled program to refer shaders attributes/uniforms */
        GLES20.glUseProgram(fogProgram);



        /* Get handle to vPosition */
        int attributePositionId = GLES20.glGetAttribLocation(fogProgram, "vPosition");
        /* Get handle to vColor */
        int uniformColorId = GLES20.glGetUniformLocation(fogProgram, "u_Color");
        // get handle to shape's transformation matrix
        int mvpId = GLES20.glGetUniformLocation(fogProgram, "uMVPMatrix");
        int u_fogColor_HANDLE = GLES20.glGetUniformLocation(fogProgram, "u_fogColor");
        int u_fogMaxDist_HANDLE = GLES20.glGetUniformLocation(fogProgram, "u_fogMaxDist");
        int u_fogMinDist_HANDLE = GLES20.glGetUniformLocation(fogProgram, "u_fogMinDist");
        int u_eyePos_HANDLE = GLES20.glGetUniformLocation(fogProgram, "u_eyePos");

        // ASSIGN VALUES
        GLES20.glUniform4fv(u_fogColor_HANDLE, 1, new float[]{0.7f, 0.2f, 1f, 1.0f}, 0);
        GLES20.glUniform4fv(u_eyePos_HANDLE, 1, GameRenderer.getEyePosition(), 0);
        GLES20.glUniform1f(u_fogMaxDist_HANDLE,
                GameBoard.ROAD_VERTICES_PER_BORDER);
        GLES20.glUniform1f(u_fogMinDist_HANDLE, 50.0f);




        /* Enable handle (I don't get it ) */
        GLES20.glEnableVertexAttribArray(attributePositionId);

        /* Connect vPosition with our buffer */
        GLES20.glVertexAttribPointer(attributePositionId, COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT, false,
        /*stride*/  0, vertexBuffer);

        // Pass the projection and view transformation to the shader
        float[] scratch = new float[16];
        Matrix.translateM(scratch, 0, mvpMatrix, 0, Road.translation.getX(),
                Road.translation.getY(), Road.translation.getZ());
        GLES20.glUniformMatrix4fv(mvpId, 1, false, scratch, 0);

        /* Set vColor to our color float table */
        GLES20.glUniform4fv(uniformColorId, 1, new float[]{1.0f,0.0f,1.0f,1.0f}, 0);

        // Draw the triangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);
    }
}
