package com.example.mini.game.shapes.complex;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.mini.game.GameRenderer;
import com.example.mini.game.util.loaders.ShadersLoader;
import com.example.mini.game.util.mathematics.RandomInteger;
import com.example.mini.game.util.mathematics.Vec3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2015-02-11.
 */
public class SpaceDust {
    private static final int WIDTH = 100;
    private static final int HEIGHT = 50;
    private static final int DEPTH = 250;
    private static final int DEFAULT_NUMBER_OF_PARTICLES = 200;
    private static final int NUMBER_OF_COORDINATE_PER_PARTICLE = 3;
    private static final int DISAPPEAR_BORDER = 0;
    public float[] translation = new float[]{0.0f, 0.0f, 0.0f};
    private List<Vec3> particles = new ArrayList<Vec3>();

    private int fogProgram = ShadersLoader.createProgram(
            ShadersLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShadersLoader.readShaderFromResource("space_dust_vertex_shader.glsl")),
            ShadersLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersLoader.readShaderFromResource("space_dust_fragment_shader.glsl")));

    FloatBuffer vertexBuffer;

    public SpaceDust() {
        generateRandomParticles();
        loadBuffers();
    }

    private void loadBuffers() {
        vertexBuffer = ByteBuffer.allocateDirect(asFloatArray().length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(asFloatArray());
        vertexBuffer.position(0);
    }

    private void generateRandomParticles() {
        for (int i = 0; i < DEFAULT_NUMBER_OF_PARTICLES; i++) {
            particles.add(new Vec3(RandomInteger.generate(-WIDTH, WIDTH),
                    RandomInteger.generate(-HEIGHT, HEIGHT),
                    RandomInteger.generate(0, DEPTH)));
        }
    }

    private float[] asFloatArray() {
        float[] floatArray = new float[particles.size() * NUMBER_OF_COORDINATE_PER_PARTICLE];
        int d = 0;
        for (Vec3 particle : particles) {
            floatArray[d++] = particle.x;
            floatArray[d++] = particle.y;
            floatArray[d++] = particle.z;
        }
        return floatArray;
    }

    public void switchFrame() {
        for (Vec3 v : particles) {
            if (v.z < -1) {
                v.x = RandomInteger.generate(-WIDTH, WIDTH);
                v.y = RandomInteger.generate(-HEIGHT, HEIGHT);
                v.z = RandomInteger.generate(150, 250);
            } else {
                v.z -= GameBoard.TIME_UNIT_LENGTH;
            }
        }
        loadBuffers();
    }

    public void fogDraw(float[] mvpMatrix) {
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
        GLES20.glVertexAttribPointer(attributePositionId, NUMBER_OF_COORDINATE_PER_PARTICLE,
                GLES20.GL_FLOAT, false,
        /*stride*/  0, vertexBuffer);

        // Pass the projection and view transformation to the shader
        float[] scratch = new float[16];
        Matrix.translateM(scratch, 0, mvpMatrix, 0, translation[0],
                translation[1], translation[2]);
        GLES20.glUniformMatrix4fv(mvpId, 1, false, scratch, 0);

        /* Set vColor to our color float table */
        GLES20.glUniform4fv(uniformColorId, 1, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);


        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, particles.size());

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);
    }
}
