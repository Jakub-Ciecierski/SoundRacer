package com.example.mini.game.util.camera;

import android.opengl.Matrix;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;

/**
 * Created by dybisz on 2014-11-24.
 */
public abstract class EulerAnglesCamera {
    private static float phi;
    private static float theta;
    private static float[] cameraMatrix = new float[16];

    public static void rotate(float differenceOnX, float differenceOnY) {
        phi += differenceOnX;
        //if((differenceOnY + theta)<90)
            theta += differenceOnY;
    }

    public static float[] calculateMatrix(float[] projectionMatrix) {
        float[] scratch = new float[16];
        Matrix.setIdentityM(cameraMatrix,0);
        Matrix.rotateM(cameraMatrix,0,phi,0,1,0);

        Matrix.rotateM(cameraMatrix,0,theta,1,0,0);
        Matrix.translateM(cameraMatrix,0,0,0,-10);

        Matrix.multiplyMM(scratch,0,projectionMatrix,0,cameraMatrix,0);
        return scratch;

    }
}
