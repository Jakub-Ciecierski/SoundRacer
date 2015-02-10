package com.example.mini.game.util.camera;

import android.opengl.Matrix;

import com.example.mini.game.util.mathematics.Vector3;


/**
 * Created by user on 2014-12-02.
 */
public abstract class PlayerStaticSphereCamera {
    private static float[] cameraMatrix = new float[16];
    private static Vector3 eyeCoordinates = new Vector3(10f, 4.5f, -1f);
    private static Vector3 lookAtCoordinates = new Vector3(10f, 3f, 8f);
    private static Vector3 upVector = new Vector3(0f, 1f, 0f);

    public static void moveCameraBy(float y) {
        /* basic eye-look triangle */
        float basicZ = Math.abs(eyeCoordinates.getZ()) + Math.abs(lookAtCoordinates.getZ());
        /* new eye-look triangle */
        float newZ = basicZ*y / eyeCoordinates.getY();

        eyeCoordinates.setY(y);
        lookAtCoordinates.setZ(newZ);
    }

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
