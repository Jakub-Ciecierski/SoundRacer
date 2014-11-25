package pl.dybisz.testgry.shapes;

import android.opengl.GLES20;

import pl.dybisz.testgry.util.ShadersController;

/**
 * Class represents full cartesian axes: x, y and z as arrows
 * in different colors.
 * <p></p>
 * Created by dybisz on 2014-11-23.
 */
public class CartesianCoordinates {
    /**
     * Constant describes default length of all 3 axes.
     */
    private final float DEFAULT_AXES_LENGTH = 10;
    /**
     * Id of compiled openGL program used for rendering.
     */
    private int programId;
    /**
     * Origin point for the cartesian coordinates system
     * in {x,y,z} form.
     */
    private float[] origin;
    /**
     * Arrow representation of the x axis.
     */
    private Arrow xAxis;
    /**
     * Arrow representation of the y axis.
     */
    private Arrow yAxis;
    /**
     * Arrow representation of the z axis.
     */
    private Arrow zAxis;

    /**
     * Enum helps distinguish, which arrow represents which axis.
     */
    public enum axes {
        X_AXIS, Y_AXIS, Z_AXIS
    }

    /**
     * Main constructor for cartesian coordinates. It compiles openGL program using:
     * {@link pl.dybisz.testgry.util.ShadersController#vertexShader vertexShader},
     * {@link pl.dybisz.testgry.util.ShadersController#fragmentShader fragmentShader}
     * and calls constructors for 3 arrows.
     *
     * @param origin Cartesian coordinates origin in the form: {x,y,z}.
     */
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

    /**
     * Main draw method for cartesian coordinates. It just calls
     * draw method of each arrow.
     *
     * @param mMVPMatrix Matrix used to project Line on the 3D scene.
     *                   Most commonly camera matrix.
     */
    public void draw(float[] mMVPMatrix) {
        xAxis.draw(mMVPMatrix);
        yAxis.draw(mMVPMatrix);
        zAxis.draw(mMVPMatrix);
    }
}
