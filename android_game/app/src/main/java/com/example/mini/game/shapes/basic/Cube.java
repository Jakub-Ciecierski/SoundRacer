package com.example.mini.game.shapes.basic;

/**
 * Created by Łukasz on 2014-11-23.
 */

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.util.loaders.ShadersLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


/**
 * Class consists of full set of methods to draw rectangle using OpenGl ES 2.0 .
 * It encapsulates shaders creation, program linking and drawing.
 * Created by dybisz on 2014-11-23.
 */
public class Cube {
    /*
        Buffer to pass array of vertices into dalvik machine.
     */
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    /*
        Constant describes how many coordinates we need to take from
        verticesCoordinates array to describe one vertex.
     */
    static final int COORDINATES_PER_VERTEX = 3;
    /*
        List of vertices describing triangle.
     */
    float verticesCoordinates[];

    private float width = 2;

    /*
        Color of our Triangle: [0] Red, [1] Green, [2] Blue, [3] Alpha
        saturation.
     */
    private short drawOrder[] = {0, 1, 2, 0, 2, 3, 1, 5, 2, 5, 6, 2, 6, 5, 4, 6, 4, 7, 4, 0, 3, 4, 3, 7, 7, 3, 2, 2, 6, 7, 1, 0, 4, 1, 4, 5};
    float color[] = {0, 1.0f, 0.5f, 1.0f};

    /*
        Set of handles to OpenGL ES objects
     */
    int programId;
    int attributePositionId;
    int uniformColorId;
    int mvpId;
    protected static float[] translate = new float[]{GameBoard.ROAD_WIDTH / 2, 2.0f, 0.0f};
    protected static float[] rotate = new float[]{0.0f, 1.0f, 1.0f, 1.0f};
    //float[] mvpMatrix = new float[16];

    public Cube() {
        this.width = 2;
        generateVerticies();
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                verticesCoordinates.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(verticesCoordinates);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        /* Compile standard shaders and program for THIS triangle */
        programId = ShadersLoader.createProgram(
                ShadersLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShadersLoader.readShaderFromResource("vertex_shader.glsl")),
                ShadersLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersLoader.readShaderFromResource("fragment_shader.glsl")));

    }

    public Cube (float width) {
        this.width = width;
        generateVerticies();
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                verticesCoordinates.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(verticesCoordinates);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        /* Compile standard shaders and program for THIS triangle */
        programId = ShadersLoader.createProgram(
                ShadersLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShadersLoader.readShaderFromResource("vertex_shader.glsl")),
                ShadersLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersLoader.readShaderFromResource("fragment_shader.glsl")));
    }

    public Cube(int program, float[] triangleVertices) {
        programId = program;
    }

    public float getWidth() {
        return this.width;
    }

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
        Matrix.translateM(scratch,0, mvpMatrix, 0, translate[0], translate[1], translate[2]);
        Matrix.rotateM(scratch, 0, rotate[0], rotate[1], rotate[2], rotate[3]);

        GLES20.glUniformMatrix4fv(mvpId, 1, false, scratch, 0);

        /* Set vColor to our color float table */
        GLES20.glUniform4fv(uniformColorId, 1, color, 0);

        // Draw the cube
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);
    }

    private void generateVerticies() {
        verticesCoordinates = new float[COORDINATES_PER_VERTEX*8];
        verticesCoordinates[0] = 0;
        verticesCoordinates[1] = 0;
        verticesCoordinates[2] = 0;

        verticesCoordinates[3] = width;
        verticesCoordinates[4] = 0;
        verticesCoordinates[5] = 0;

        verticesCoordinates[6] = width;
        verticesCoordinates[7] = width;
        verticesCoordinates[8] = 0;

        verticesCoordinates[9] = 0;
        verticesCoordinates[10] = width;
        verticesCoordinates[11] = 0;

        verticesCoordinates[12] = 0;
        verticesCoordinates[13] = 0;
        verticesCoordinates[14] = width;

        verticesCoordinates[15] = width;
        verticesCoordinates[16] = 0;
        verticesCoordinates[17] = width;

        verticesCoordinates[18] = width;
        verticesCoordinates[19] = width;
        verticesCoordinates[20] = width;

        verticesCoordinates[21] = 0;
        verticesCoordinates[22] = width;
        verticesCoordinates[23] = width;
    }
}

