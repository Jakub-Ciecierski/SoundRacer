package pl.dybisz.testgry.util;

import android.opengl.Matrix;
import android.util.Log;
import android.util.FloatMath.*;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;

/**
 * Class will represent camera object as a combined projection matrix
 * and view matrix.
 * Created by dybisz on 2014-11-24.
 */
public abstract class CustomCamera {
    private static float[] cameraMatrix = new float[16];
    private static float[] eyeCoordinates = {0f, 0f, 0f};
    private static float[] lookAtCoordinates = {0f, 0f, 0f};
    private static float[] upVectorCoordinates = {0f, 1f, 0f};
    private static float xAngle = 0;
    private static float yAngle = 0;
    /**
     * This array represents shifts on X, Y and Z planes (of the eye).
     * If one wants just to move the eye by some vector,
     * eyeCoordinates should remain 0.
     */
    private static float[] eyeShifts = {0f, 5f, -10f};
    private static float radiusOfView = 10;

    public static void rotate(float dx, float dy) {
        /* Update information vectors */
        eyeCoordinates[0] = sin(xAngle + dx) * sin(yAngle + dy);
        eyeCoordinates[1] = cos(yAngle + dy);
        eyeCoordinates[2] = sin(yAngle + dy) * cos(xAngle + dx);
        eyeShifts[2] = 0f;
        xAngle += dx;
        yAngle += dy;
    }

    public static float[] getCameraMatrix(float[] projectionMatrix) {
        float[] scratch = new float[16];
        updateMatrixInformation();
        Matrix.multiplyMM(scratch, 0, projectionMatrix, 0, cameraMatrix, 0);
        return scratch;
    }

    /**
     * Every time renderer demands a matrix (each frame of onDraw()),
     * cameraMatrix will be updated to current settings.
     */
    private static void updateMatrixInformation() {
        Matrix.setLookAtM(cameraMatrix, 0,
                radiusOfView * eyeCoordinates[0] + eyeShifts[0],
                radiusOfView * eyeCoordinates[1] + eyeShifts[1],
                radiusOfView * eyeCoordinates[2] + eyeShifts[2],
                lookAtCoordinates[0], lookAtCoordinates[1], lookAtCoordinates[2],
                upVectorCoordinates[0], upVectorCoordinates[1], upVectorCoordinates[2]);
    }


}
