package pl.dybisz.testgry.shapes;

import android.opengl.GLES20;

import pl.dybisz.testgry.util.ShadersController;

/**
 * Created by user on 2014-11-23.
 */
public class CartesianCoordinates {
    private int programId;
    private float[] origin;
    private final float DEFAULT_AXES_LENGTH = 10;
    private Arrow xAxis;
    private Arrow yAxis;
    private Arrow zAxis;

    public enum axes {X_AXIS, Y_AXIS, Z_AXIS}


    public CartesianCoordinates(float[] origin) {
        this.origin = origin;
        /* Compile program for all elements to save memory */
        programId = ShadersController.createProgram(
                ShadersController.loadShader(GLES20.GL_VERTEX_SHADER, ShadersController.vertexShader),
                ShadersController.loadShader(GLES20.GL_FRAGMENT_SHADER, ShadersController.fragmentShader));

        /* Pass information about position, color and program to manage rendering for each arrow */
        xAxis = new Arrow(
                origin,
                new float[]{DEFAULT_AXES_LENGTH, 0, 0},
                new float[]{1.0f, 0.0f, 0.0f, 1.0f},
                axes.X_AXIS,
                programId);
        yAxis = new Arrow(
                origin,
                new float[]{0, DEFAULT_AXES_LENGTH, 0},
                new float[]{0.0f, 1.0f, 0.0f, 1.0f},
                axes.Y_AXIS,
                programId);
        zAxis = new Arrow(
                origin,
                new float[]{0, 0, DEFAULT_AXES_LENGTH},
                new float[]{0.0f, 0.0f, 1.0f, 1.0f},
                axes.Z_AXIS,
                programId);
    }

    public void draw(float[] mMVPMatrix) {
        xAxis.draw(mMVPMatrix);
        yAxis.draw(mMVPMatrix);
        zAxis.draw(mMVPMatrix);
    }
}
