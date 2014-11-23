package pl.dybisz.testgry.shapes;

/**
 * Created by Łukasz on 2014-11-23.
 */import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import pl.dybisz.testgry.util.ShadersController;

/**
 * Class consists of full set of methods to draw rectangle using OpenGl ES 2.0 .
 * It encapsulates shaders creation, program linking and drawing.
 * Created by dybisz on 2014-11-23.
 */
public class Cube{
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
    static float verticesCoordinates[] = {   // in counterclockwise order:
            -1,-1,6,
            1,-1,6,
            1,1,6,
            -1,1,6,
            -1,-1,4,
            1,-1,4,
            1,1,4,
            -1,1,4
    };
    /*
        Color of our Triangle: [0] Red, [1] Green, [2] Blue, [3] Alpha
        saturation.
     */
    private short drawOrder[] ={0,1,2,0,2,3,1,5,2,5,6,2,6,5,4,6,4,7,4,0,3,4,3,7,7,3,2,2,6,7,1,0,4,1,4,5};
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

    public Cube() {
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
        /* Compile shaders and program for THIS triangle */
        programId = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, vertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader));

    }
    public Cube (int program,float[] triangleVertices) {
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

        // Draw the cube
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);
    }
}

