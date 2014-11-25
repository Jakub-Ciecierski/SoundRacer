package pl.dybisz.testgry.shapes;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.FloatMath;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Created by user on 2014-11-25.
 */
public class Cone {
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
    private float[] verticesCoordinates;
    /*
        Color of our Triangle: [0] Red, [1] Green, [2] Blue, [3] Alpha
        saturation.
     */
    float color[];
    private static final int BASE_TRIANGLES_NUMBER = 16;
    private float height;
    int programId;
    int attributePositionId;
    int uniformColorId;
    int mvpId;
    private float radius = 0.3f;
    private float[] translation = new float[3];
    private float[] rotation = new float[4];

    /**
     * By default cone is centered at the (0,0,0).
     *
     * @param radius
     * @param height
     * @param color
     * @param program
     */
    public Cone(float radius, float height, float[] color, int program) {
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

    private float[] generateVerticesCoordinates() {
        //float angle = 360 / BASE_TRIANGLES_NUMBER;
        float[] scratch = new float[(BASE_TRIANGLES_NUMBER + 2) * COORDINATES_PER_VERTEX];
        int offset = 0;
        scratch[offset++] = 0;
        scratch[offset++] = 0;
        scratch[offset++] = 0;
        for (int i = 0; i <= BASE_TRIANGLES_NUMBER; i++) {
            float angleInRadians =
                    ((float) i / (float) BASE_TRIANGLES_NUMBER)
                            * ((float) Math.PI * 2f);
            scratch[offset++] = radius * FloatMath.cos(angleInRadians);
            scratch[offset++] = 0;
            scratch[offset++] = radius * FloatMath.sin(angleInRadians);
        }
        /* New array with different base point */
        float[] scratch2 = Arrays.copyOf(scratch, scratch.length);
        scratch2[1] = height;
        /* Combining two arrays together */
        float[] returnArray = new float[scratch.length + scratch2.length];
        System.arraycopy(scratch, 0, returnArray, 0, scratch.length);
        System.arraycopy(scratch2, 0, returnArray, scratch.length, scratch2.length);

        return returnArray;
    }

    public void translate(float[] translation, float[] rotation) {
        this.translation = translation;
        this.rotation = rotation;
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

        // Apply additional transformation
        float[] scratch = new float[16];
        scratch = Arrays.copyOf(mvpMatrix, mvpMatrix.length);
        Matrix.translateM(scratch, 0, translation[0], translation[1], translation[2]);
        Matrix.rotateM(scratch, 0, rotation[0], rotation[1], rotation[2], rotation[3]);
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mvpId, 1, false, scratch, 0);

        /* Set vColor to our color float table */
        GLES20.glUniform4fv(uniformColorId, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 1 + BASE_TRIANGLES_NUMBER + 1);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, (1 + BASE_TRIANGLES_NUMBER + 1), 1 + BASE_TRIANGLES_NUMBER + 1);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);
    }
}
