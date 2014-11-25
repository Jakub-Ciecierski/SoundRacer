package pl.dybisz.testgry.shapes;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import pl.dybisz.testgry.util.ShadersController;

/**
 * Created by dybisz on 2014-11-23.
 */
public class Line {
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
    float verticesCoordinates[];/* = {   // in counterclockwise order:
            0.0f, 0.0f, 0.5f, // top
            1.0f, 0.0f, 0.5f, // bottom left

    };*/
    /*
       Color of our Triangle: [0] Red, [1] Green, [2] Blue, [3] Alpha
       saturation.
    */
    //float color[] = {1.0f, 0.0f, 0.0f, 1.0f};
    float[] color;

    /*
        Set of handles to OpenGL ES objects
     */
    int programId;
    int attributePositionId;
    int uniformColorId;
    int mvpId;
    //float[] mvpMatrix = new float[16];

    public Line(float[] color, float[] vertices) {
        this.color = color;
        this.verticesCoordinates = vertices;
        /* Vertices array buffer: create, fill out and set start position to 0 */
        vertexBuffer = ByteBuffer.allocateDirect(verticesCoordinates.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(verticesCoordinates);
        vertexBuffer.position(0);

        /* Compile standard shaders and program for THIS triangle */
        programId = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.vertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.fragmentShader));


    }

    /**
     * Constructor accepts (beside standart arguments) a program id, which saves time
     * when more lines are going to be drawn using the same shaders.ł
     *
     * @param program
     * @param triangleVertices
     * @param color
     */
    public Line( float[] color, float[] triangleVertices, int program) {
        this.verticesCoordinates = triangleVertices;
        this.color = color;
        programId = program;

         /* Vertices array buffer: create, fill out and set start position to 0 */
        vertexBuffer = ByteBuffer.allocateDirect(verticesCoordinates.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(verticesCoordinates);
        vertexBuffer.position(0);
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
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);
    }
}
