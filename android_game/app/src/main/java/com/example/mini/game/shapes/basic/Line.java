package com.example.mini.game.shapes.basic;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.mini.game.util.loaders.ShadersLoader;
import com.example.mini.game.util.mathematics.Vector3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Class represents line object in the 3D space.
 * <p></p>
 * Created by dybisz on 2014-11-23.
 */
public class Line {
    /**
     * Constant describes how many coordinates we need to take from
     * verticesCoordinates array to describe one vertex.
     */
    static final int COORDINATES_PER_VERTEX = 3;
    /**
     * Buffer to pass array of vertices into native heap.
     */
    private FloatBuffer vertexBuffer;
    /**
     * List of vertices describing triangle.
     */
    float verticesCoordinates[];
    /**
     * Color of the line in the for: {r,g,b,a}.
     */
    float[] color;
    /**
     * Id of compiled openGL program, which will be used to rendering.
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
     * Additional translation.
     */
    Vector3 translations = new Vector3(0f, 0f, 0f);
    /**
     * To... my God...
     */
    private float originalPositionZ;
    /**
     * Constructor automatically compile openGL program using standard shaders code:
     * {@link pl.dybisz.testgry.util.ShadersController#vertexShader vertexShader} and
     * {@link pl.dybisz.testgry.util.ShadersController#fragmentShader fragmentShader}.
     *
     * @param color    Color of the line in the form: {r,g,b,a}.
     * @param vertices Start and end points in the form: {xStart, yStart, zStart, xEnd, yEnd, zEnd}.
     */
    public Line(float[] color, float[] vertices) {
        /* Fill out fields */
        this.color = color;
        this.verticesCoordinates = vertices;

        /* Vertices array buffer: create, fill out and set start position to 0 */
        vertexBuffer = ByteBuffer.allocateDirect(verticesCoordinates.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(verticesCoordinates);
        vertexBuffer.position(0);

        /* Compile standard shaders and program for THIS triangle */
        programId = ShadersLoader.createProgram(
                ShadersLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShadersLoader.readShaderFromResource("vertex_shader.glsl")),
                ShadersLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersLoader.readShaderFromResource("fragment_shader.glsl")));
    }

    /**
     * Constructor accepts (beside standard arguments) a program id, which saves time
     * when more lines are going to be drawn using the same shaders.ł
     *
     * @param program  Id of openGL program.
     * @param vertices Start and end points in the form: {xStart, yStart, zStart, xEnd, yEnd, zEnd}.
     * @param color    Color of the line in the form: {r,g,b,a}.
     */
    public Line(float[] color, float[] vertices, int program) {
        this.verticesCoordinates = vertices;
        this.color = color;
        programId = program;

         /* Vertices array buffer: create, fill out and set start position to 0 */
        vertexBuffer = ByteBuffer.allocateDirect(verticesCoordinates.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(verticesCoordinates);
        vertexBuffer.position(0);
    }

    /**
     * When we are about to draw vast amount of lines, it is good(in terms of memory saving)
     * to pass the same FloatBuffer to each arrow and then just translate them according
     * to our wish.
     *
     * @param color        Color of the line.
     * @param vertexBuffer Buffer with vertices for shader.
     * @param program      Id of out shader program.
     */
    public Line(float[] color, FloatBuffer vertexBuffer, Vector3 translations, int program) {
        this.programId = program;
        this.color = color;
        this.vertexBuffer = vertexBuffer;
        this.translations = translations;
        this.originalPositionZ = translations.getZ();
    }

    /**
     * Main draw methods of the line. It loads {@link #programId programId} and by
     * acquiring all needed attributes/uniforms from shaders it assigns them values
     * and call appropriate oGL draw method.
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

        // Pass the projection and view transformation to the shader
        float[] scratch = new float[16];
        Matrix.translateM(scratch, 0, mvpMatrix, 0, translations.getX(), translations.getY(), translations.getZ());
        GLES20.glUniformMatrix4fv(mvpId, 1, false, scratch, 0);

        /* Set vColor to our color float table */
        GLES20.glUniform4fv(uniformColorId, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);
    }

    public void setTranslationsZ(float newZ) {
       translations.setZ(newZ);
    }
    public float getTranslationsZ() {
        return translations.getZ();
    }
    public float getOriginalPositionZ() {
        return originalPositionZ;
    }
}
