package pl.dybisz.testgry.shapes.complex;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import pl.dybisz.testgry.util.ShadersController;
import pl.dybisz.testgry.util.animation.VBORoadAnimation;
import pl.dybisz.testgry.util.mathematics.Vector3;

/**
 * Animation "na dwa baty".
 * Created by user on 2014-11-24.
 */
public class Road {
    private float[] color;
    private float[] vertices;
    private FloatBuffer vbo;
    private VBORoadAnimation vboRoadAnimation;
    private Vector3 translation = new Vector3(0f, 0f, 0f);
    private int program;

    public Road(int verticesPerBorder, float timeUnitLength, float roadWidth, float[] color) {
        this.color = color;
        this.vboRoadAnimation =
                new VBORoadAnimation(verticesPerBorder, timeUnitLength, roadWidth);
        this.program = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.vertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.fragmentShader));
        this.vertices = vboRoadAnimation.generateStartShape();
        loadBuffers();
    }

    public void draw(float[] mvpMatrix) {
        /* Use compiled program to refer shaders attributes/uniforms */
        GLES20.glUseProgram(program);

        /* Get handle to vPosition */
        int attributePositionId = GLES20.glGetAttribLocation(program, "vPosition");
        /* Get handle to vColor */
        int uniformColorId = GLES20.glGetUniformLocation(program, "vColor");
        // get handle to shape's transformation matrix
        int mvpId = GLES20.glGetUniformLocation(program, "uMVPMatrix");

        /* Enable handle (I don't get it ) */
        GLES20.glEnableVertexAttribArray(attributePositionId);

        GLES20.glVertexAttribPointer(attributePositionId, 3,
                GLES20.GL_FLOAT, false,
                /*stride*/  0, vbo);

        // Pass the projection and view transformation to the shader
        float[] scratch = new float[16];
        Matrix.translateM(scratch, 0, mvpMatrix, 0, translation.getX(), translation.getY(), translation.getZ());
        GLES20.glUniformMatrix4fv(mvpId, 1, false, scratch, 0);

        /* Set vColor to our color float table */
        GLES20.glUniform4fv(uniformColorId, 1, color, 0);

         /* Draw */
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

        /* Safe bullshit */
        GLES20.glDisableVertexAttribArray(attributePositionId);

    }

    public void switchFrame() {
        /* Main road update */
        vertices = vboRoadAnimation.generateNextFrame(vertices);
        loadBuffers();
        /* Adapt translation vector to the next frame of the animation */
        vboRoadAnimation.generateNextFrame(translation);
    }

    /**
     *
     */
    private void loadBuffers() {
        vbo = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertices);
        vbo.position(0);
    }

}
