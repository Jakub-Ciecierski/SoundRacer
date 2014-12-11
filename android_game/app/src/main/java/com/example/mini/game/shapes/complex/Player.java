package com.example.mini.game.shapes.complex;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.mini.game.R;
import com.example.mini.game.models.ObjModel;
import com.example.mini.game.shapes.basic.Cube;
import com.example.mini.game.util.ShadersController;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by dybisz on 2014-12-08.
 */
public class Player {
    /**
     * All information needed to draw model of Player are encapsulated in
     * ths field.
     */
    ObjModel shipModel = new ObjModel(R.raw.player_ship);
    /**
     * Buffer between Dalvik's heap and the native one for vertices coordinates.
     */
    private FloatBuffer verticesVbo;
    /**
     * Buffer between Dalvik's heap and the native one for drawing order.
     */
    private ShortBuffer facesVbo;
    /**
     * Vector represents translation of the final drawing position of Player.
     * It is in the form: x, y, z.
     */
    private static float[] translate = new float[]{GameBoard.ROAD_WIDTH / 2, 2.0f, 0.0f};
    /**
     * Vector represents rotation of the final drawing of Player.
     * It is in the form: angle, x, y, z.
     */
    private static float[] rotate = new float[]{0.0f, 1.0f, 1.0f, 1.0f};
    /**
     * Id of an OpenGL program used to draw Player model.
     */
    private final int program;


    public Player() {
        /* Compile OpenGL program */
        program = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.vertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.fragmentShader));
        loadBuffers();
    }

    private void loadBuffers() {
        verticesVbo = ByteBuffer.allocateDirect(shipModel.getVerticesSize() * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(shipModel.getVertices());
        verticesVbo.position(0);

        facesVbo = ByteBuffer.allocateDirect(shipModel.getFacesSize() * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer().put(shipModel.getFaces());
        facesVbo.position(0);
    }

    public void draw(float[] mvpMatrix) {
        /* Use compiled program to refer shaders attributes/uniforms */
        GLES20.glUseProgram(program);

        /* Get handle to vPosition */
        int attributePositionId = GLES20.glGetAttribLocation(program, "vPosition");
        /* Get handle to vColor */
        int uniformColorId = GLES20.glGetUniformLocation(program, "vColor");
        // get handle to shape's transformation matrix
        int mvpId = GLES20.glGetUniformLocation(program, "uMVPMatrix");

        /* Enable handle (I don't get it ) */
        GLES20.glEnableVertexAttribArray(attributePositionId);

        /* Connect vPosition with our buffer */
        GLES20.glVertexAttribPointer(attributePositionId, 3,
                GLES20.GL_FLOAT, false,
        /*stride*/  0, verticesVbo);

        // Pass the projection and view transformation to the shader
        float[] scratch = new float[16];
        Matrix.translateM(scratch, 0, mvpMatrix, 0, translate[0], translate[1], translate[2]);
        Matrix.rotateM(scratch, 0, rotate[0], rotate[1], rotate[2], rotate[3]);

        GLES20.glUniformMatrix4fv(mvpId, 1, false, scratch, 0);

        /* Set vColor to our color float table */
        GLES20.glUniform4fv(uniformColorId, 1, new float[]{0, 1.0f, 0.5f, 1.0f}, 0);

        // Draw the cube
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, shipModel.getFacesSize(),
                GLES20.GL_UNSIGNED_SHORT, facesVbo);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);
    }

    public void switchFrame() {

    }

    public static void setTranslate(float x, float y, float z) {
        if (x <= GameBoard.ROAD_WIDTH - GameBoard.PLAYER_WIDTH && x >= 0 + GameBoard.PLAYER_WIDTH)
            translate[0] = x;
        translate[1] = y;
        translate[2] = z;
    }

    public static void rotateAroundZ(float a) {
        if (a <= 35 && a >= -35)
            rotate = new float[]{a, 0.0f, 0.0f, 1.0f};
    }

    public static float getCurrentAngle() {
        return rotate[0];
    }

    public static float getTranslationX() {
        return translate[0];
    }

    public static float getTranslationY() {
        return translate[1];
    }

    public static float getTranslationZ() {
        return translate[2];
    }
}
