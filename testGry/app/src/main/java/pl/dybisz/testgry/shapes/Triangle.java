package pl.dybisz.testgry.shapes;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import pl.dybisz.testgry.util.ShadersController;

/**
 * Class consists of full set of methods to draw rectangle using OpenGl ES 2.0 .
 * It encapsulates shaders creation, program linking and drawing.
 * Created by dybisz on 2014-11-23.
 */
public class Triangle {
    /*
        Buffer to pass array of vertices into dalvik machine.
     */
    private FloatBuffer vertexBuffer;
    /*
        Constant describes how many coordinates we need to take from
        verticesCoordinates array to describe one vertex.
     */
    static final int COORDINATES_PER_VERTEX = 3;
    /*
        List of vertices describing triangle.
     */
    static float verticesCoordinates[] = {   // in counterclockwise order:
            0.0f, 0.622008459f, 6f, // top
            -0.5f, -0.311004243f, 6f, // bottom left
            0.5f, -0.311004243f, 6f  // bottom right
    };
    /*
        Color of our Triangle: [0] Red, [1] Green, [2] Blue, [3] Alpha
        saturation.
     */
    float color[] = {0, 1.0f, 0.5f, 1.0f};
    /*
        Vertex shader code.
     */
    private final String vertexShader =
            "uniform mat4 uMVPMatrix;"+
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    /*
        Fragment shader code.
     */
    private final String fragmentShader =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    /*
        Set of handles to OpenGL ES objects
     */
    int programId;
    int attributePositionId;
    int uniformColorId;
    int mvpId;
    //float[] mvpMatrix = new float[16];

    public Triangle() {
        /* Vertices array buffer: create, fill out and set start position to 0 */
        vertexBuffer = ByteBuffer.allocateDirect(verticesCoordinates.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(verticesCoordinates);
        vertexBuffer.position(0);

        /* Compile shaders and program for THIS triangle */
        programId = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, vertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader));

    }
    public Triangle (int program,float[] triangleVertices) {
        programId = program;
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
        GLES20.glUniformMatrix4fv(mvpId, 1, false, mvpMatrix, 0);

        /* Set vColor to our color float table */
        GLES20.glUniform4fv(uniformColorId, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);
    }
}
