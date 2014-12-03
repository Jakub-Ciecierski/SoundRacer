package com.example.mini.game.shapes.basic;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.FloatMath;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Class represents cone object in the 3D scene.
 * <p></p>
 * Created by dybisz on 2014-11-25.
 */
public class Cone {
    /**
     * Constant describes how many coordinates we need to take from
     * verticesCoordinates array to describe one vertex.
     */
    static final int COORDINATES_PER_VERTEX = 3;
    /**
     * Default number of triangle describing base of the cone.
     * More triangle, more circle-like base.
     */
    private static final int BASE_TRIANGLES_NUMBER = 16;
    /**
     * Buffer to pass array of vertices into native heap.
     */
    private FloatBuffer vertexBuffer;
    /**
     * List of vertices describing cone
     */
    private float[] verticesCoordinates;
    /**
     * Color of the cone in the for: {r,g,b,a}.
     */
    float color[];
    /**
     * Height of the cone.
     */
    private float height;
    /**
     * Id of compiled openGL program used for rendering.
     */
    int programId;
    /**
     * Id of shader vPosition attribute.
     */
    int attributePositionId;
    /**
     * Id of shader vColor uniform.
     */
    int uniformColorId;
    /**
     * Id of shader uMVPMatrix matrix.
     */
    int mvpId;
    /**
     * Radius of the cone.
     */
    private float radius = 0.3f;
    /**
     * Translation applied to the cone before rendering.
     * Form: {xTranslate, yTranslate, zTranslate}.
     */
    private float[] translation = new float[3];
    /**
     * Rotation applied to the cone before rendering.
     * Form: {angle, xAxis, yAxis, zAxis}.
     */
    private float[] rotation = new float[4];

    /**
     * By default cone is centered at the (0,0,0).
     *
     * @param radius  Desired radius of the cone.
     * @param height  Desired height of the cone.
     * @param color   Desired color of the cone.
     * @param program Compiled openGL program id.
     */
    public Cone(float radius, float height, float[] color, int program) {
        /* Fill out fields */
        this.radius = radius;
        this.height = height;
        this.color = color;
        this.programId = program;

        /* Generate vertices */
        this.verticesCoordinates = generateVerticesCoordinates();

        /* Vertices array buffer: create, fill out and set start position to 0 */
        vertexBuffer = ByteBuffer.allocateDirect(verticesCoordinates.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(verticesCoordinates);
        vertexBuffer.position(0);

    }

    /**
     * Method generates array of vertices needed for rendering the cone using
     * double call of drawing method of openGL with GL_TRIANGLE_FAN mode.
     *
     * @return Completed array of vertices.
     */
    private float[] generateVerticesCoordinates() {

        float[] scratch = new float[(BASE_TRIANGLES_NUMBER + 2) * COORDINATES_PER_VERTEX];
        int offset = 0;
        /* First middle point of GL_TRIANGLE_FAN */
        scratch[offset++] = 0;
        scratch[offset++] = 0;
        scratch[offset++] = 0;

        /* Set of points surrounding middle point positioned on the circles with
        center at the middle point and radius of the cone. In addition last point is
        duplicated with the first in this series (demand of GL_TRIANGLE_STRIPS)*/
        for (int i = 0; i <= BASE_TRIANGLES_NUMBER; i++) {
            float angleInRadians =
                    ((float) i / (float) BASE_TRIANGLES_NUMBER)
                            * ((float) Math.PI * 2f);
            scratch[offset++] = radius * FloatMath.cos(angleInRadians);
            scratch[offset++] = 0;
            scratch[offset++] = radius * FloatMath.sin(angleInRadians);
        }

        /* New array with different middle point */
        float[] scratch2 = Arrays.copyOf(scratch, scratch.length);
        scratch2[1] = height;

        /* Combining two arrays together */
        float[] returnArray = new float[scratch.length + scratch2.length];
        System.arraycopy(scratch, 0, returnArray, 0, scratch.length);
        System.arraycopy(scratch2, 0, returnArray, scratch.length, scratch2.length);

        return returnArray;
    }

    /**
     * Methods sets up the transformations applied before rendering to
     * the cone.
     *
     * @param translation See {@link #translation}.
     * @param rotation    See {@link #rotation}.
     */
    public void translate(float[] translation, float[] rotation) {
        this.translation = translation;
        this.rotation = rotation;
    }

    /**
     * Main draw methods of the cone. It loads {@link #programId programId} and by
     * acquiring all needed attributes/uniforms from shaders it assigns them values,
     * performs transformations and calls appropriate an oGL draw method.
     *
     * @param mvpMatrix Matrix used to project Line on the 3D scene.
     *                  Most commonly camera matrix.
     */
    public void draw(float[] mvpMatrix) {
        /* Use compiled program to refer shaders attributes/uniforms */
        GLES20.glUseProgram(programId);

        /* Get handle to vPosition */
        attributePositionId = GLES20.glGetAttribLocation(programId, "vPosition");
        /* Get handle to vColor */
        uniformColorId = GLES20.glGetUniformLocation(programId, "vColor");
        // get handle to shape's transformation matrix
        mvpId = GLES20.glGetUniformLocation(programId, "uMVPMatrix");

        /* Enable handle (I don't get it ) */
        GLES20.glEnableVertexAttribArray(attributePositionId);

        /* Connect vPosition with our buffer */
        GLES20.glVertexAttribPointer(attributePositionId, COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT, false,
        /*stride*/  0, vertexBuffer);

        /*Apply additional transformations */
        float[] scratch = new float[16];
        scratch = Arrays.copyOf(mvpMatrix, mvpMatrix.length);
        Matrix.translateM(scratch, 0, translation[0], translation[1], translation[2]);
        Matrix.rotateM(scratch, 0, rotation[0], rotation[1], rotation[2], rotation[3]);
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mvpId, 1, false, scratch, 0);

        /* Set vColor to our color float table */
        GLES20.glUniform4fv(uniformColorId, 1, color, 0);

        // Draw the triangles
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 1 + BASE_TRIANGLES_NUMBER + 1);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, (1 + BASE_TRIANGLES_NUMBER + 1), 1 + BASE_TRIANGLES_NUMBER + 1);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);
    }
}
