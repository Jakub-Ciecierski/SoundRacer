package pl.dybisz.testgry.shapes;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import pl.dybisz.testgry.util.ShadersController;
import pl.dybisz.testgry.util.VBORoadAnimation;
import pl.dybisz.testgry.util.mathematics.Vector3;

import static android.util.FloatMath.sin;

/**
 * Animation "na dwa baty".
 * Created by user on 2014-11-24.
 */
public class RoadPrototype {
    private static final int VERTICES_PER_BORDER = 80;
    private static final float ROAD_WIDTH = 20.0f;
    private static final float TIME_UNIT_LENGTH = 0.5f;
    private float[] vertices;
    FloatBuffer vbo1;
    FloatBuffer vbo2;
    private Buffer bufferToDraw = Buffer.VBO1;

    private enum Buffer {VBO1, VBO2}

    int programId;
    float[] color = new float[]{0.5f, 0.5f, 0.5f, 0.7f};
    private int animationOffset = 0;
    private VBORoadAnimation vboRoadAnimation =
            new VBORoadAnimation(VERTICES_PER_BORDER, TIME_UNIT_LENGTH, ROAD_WIDTH);
    private Vector3 translation = new Vector3(0f,0f,0f);

    public RoadPrototype() {
        /* Compile standard shaders and program for THIS triangle */
        programId = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.vertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.fragmentShader));
        vertices = vboRoadAnimation.generateStartShape();
        loadBuffers(bufferToDraw);
    }

    public void draw(float[] mvpMatrix) {
        /* Use compiled program to refer shaders attributes/uniforms */
        GLES20.glUseProgram(programId);

        /* Get handle to vPosition */
        int attributePositionId = GLES20.glGetAttribLocation(programId, "vPosition");
        /* Get handle to vColor */
        int uniformColorId = GLES20.glGetUniformLocation(programId, "vColor");
        // get handle to shape's transformation matrix
        int mvpId = GLES20.glGetUniformLocation(programId, "uMVPMatrix");

        /* Enable handle (I don't get it ) */
        GLES20.glEnableVertexAttribArray(attributePositionId);

        /* Connect vPosition with our buffer */
        switch (bufferToDraw) {
            case VBO1:
                GLES20.glVertexAttribPointer(attributePositionId, 3,
                        GLES20.GL_FLOAT, false,
                /*stride*/  0, vbo1);
                break;
            case VBO2:
                GLES20.glVertexAttribPointer(attributePositionId, 3,
                        GLES20.GL_FLOAT, false,
                /*stride*/  0, vbo2);
                break;
        }


        // Pass the projection and view transformation to the shader
        float[] scratch = new float[16];
        Matrix.translateM(scratch,0,mvpMatrix,0,translation.getX(), translation.getY(), translation.getZ());
        GLES20.glUniformMatrix4fv(mvpId, 1, false, scratch, 0);

        /* Set vColor to our color float table */
        GLES20.glUniform4fv(uniformColorId, 1, color, 0);

         /* Draw */
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, animationOffset, (vertices.length / 3) - (2 - animationOffset));
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
        // Draw the triangle
        // GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);

    }

    public void switchFrame() {
        vertices = vboRoadAnimation.generateVerticesAfterOneFrame(vertices);
        loadBuffers(bufferToDraw);
        translation.setZ(translation.getZ() - TIME_UNIT_LENGTH);
    }

    /**
     * Depending on start value of {@link #bufferToDraw bufferToDraw} we update one.
     */
    private void loadBuffers(Buffer buffer) {
        switch (buffer) {
            case VBO1:
                vbo1 = ByteBuffer.allocateDirect(vertices.length * 4)
                        .order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertices);
                vbo1.position(0);
                break;
            case VBO2:
                vbo2 = ByteBuffer.allocateDirect(vertices.length * 4)
                        .order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertices);
                vbo2.position(0);
                break;
        }
    }
}
