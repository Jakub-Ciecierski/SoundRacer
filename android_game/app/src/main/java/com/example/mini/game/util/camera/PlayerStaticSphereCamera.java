package com.example.mini.game.util.camera;

import android.opengl.Matrix;

import com.example.mini.game.util.mathematics.Vector3;


/**
 * Created by user on 2014-12-02.
 */
public abstract class PlayerStaticSphereCamera {
    private static float[] cameraMatrix = new float[16];
    private static Vector3 eyeCoordinates = new Vector3(10f, 6f, 2f);
    private static Vector3 lookAtCoordinates = new Vector3(10f, 4f, 6f);
    private static Vector3 upVector = new Vector3(0f, 1f, 0f);

    public static float[] getCameraMatrix(float[] projectionMatrix) {
        float[] scratch = new float[16];
        updateMatrixInformation();
        Matrix.multiplyMM(scratch, 0, projectionMatrix, 0, cameraMatrix, 0);
        return scratch;
    }

    private static void updateMatrixInformation() {
        Matrix.setLookAtM(cameraMatrix,
                0,
                eyeCoordinates.getX(),
                eyeCoordinates.getY(),
                eyeCoordinates.getZ(),
                lookAtCoordinates.getX(),
                lookAtCoordinates.getY(),
                lookAtCoordinates.getZ(),
                upVector.getX(),
                upVector.getY(),
                upVector.getZ())
        ;
    }

    public static Vector3 getEyeVector() {
        return eyeCoordinates;
    }
}
