package pl.dybisz.testgry.shapes;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import pl.dybisz.testgry.util.ShadersController;

/**
 * Created by user on 2014-11-24.
 */
public class RoadPrototype {
    private static final int POSITION_DATA_SIZE_IN_ELEMENTS = 3;
    private final int VERTICES_PER_WIDTH = 2;
    private final int VERTICES_PER_LENGTH = 7;
    private final int FLOATS_PER_VERTEX = POSITION_DATA_SIZE_IN_ELEMENTS;
    private float[] listOfVertices /*= new float[VERTICES_PER_LENGTH * VERTICES_PER_WIDTH * FLOATS_PER_VERTEX]*/;
    private short[] listOfIndices;
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private int programId;
    private int attributePositionId;
    private int uniformColorId;
    private int mvpId;
    float color[] = {1.0f, 0.0f, 0.5f, 1.0f};

    public RoadPrototype() {
        generateVertices();
        generateIndices();
        createBuffers();
        loadStandardProgram();
    }

    private void loadStandardProgram() {
        programId = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.vertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.fragmentShader));
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
        GLES20.glVertexAttribPointer(attributePositionId, 3,
                GLES20.GL_FLOAT, false,
        /*stride*/  0, vertexBuffer);

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mvpId, 1, false, mvpMatrix, 0);

        /* Set vColor to our color float table */
        GLES20.glUniform4fv(uniformColorId, 1, color, 0);

        // Draw the cube
//        GLES20.glDrawElements(
//                GLES20.GL_TRIANGLES, listOfIndices.length,
//                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, listOfIndices.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);
    }

    private void createBuffers() {
        /* Vertex buffer */
        vertexBuffer = ByteBuffer.allocateDirect(listOfVertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(listOfVertices);
        vertexBuffer.position(0);
        /* Index buffer */
        drawListBuffer = ByteBuffer.allocateDirect(listOfIndices.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer().put(listOfIndices);
        drawListBuffer.position(0);
    }

    private void generateIndices() {
//        listOfIndices = new short[]{
//                0, 2, 1, 3, 3, 2, 2, 4, 3,
//                5, 5, 4, 4, 6, 5, 7, 7, 6,
//                6, 8, 7, 9, 9, 8, 8, 10,
//                9, 11, 11, 10, 10, 12, 11, 13
//        };
        listOfIndices = new short[] {
          0,2,1
        };
    }

    private void generateVertices() {
//        listOfVertices = new float[]{
//                -1f, 8f, 14f, // 1
//                1f, 8f, 14f,  // 2
//                -1f, 7f, 13.5f,  // 3
//                1f, 7f, 13.5f,  // 4
//                -1f, 6f, 13f, // 5
//                1f, 6f, 13f,  // 6
//                -1f, 5f, 12.5f, // 7
//                1f, 5f, 12.5f,//8
//                -1f, 4f, 12f,//9
//                1f, 4f, 12f // 10
//                - 1f, 3f, 11.5f, //11
//                1f, 3f, 11.5f, //12
//                -1f, 2f, 11f, //13
//                1f, 2f, 11f //14
//
//
//        };

        listOfVertices = new float[]{
                -1f, 2.0f, 20.0f, //1

                1f, 2.0f, 20.0f,
                -1f, 0.0f, 20.0f,
//                -1f, 2f, 18f,
//                1f, 1f, 16f,
//
//
//                -1f, 1f,16f
        };
    }
}
