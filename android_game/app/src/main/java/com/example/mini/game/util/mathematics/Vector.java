package com.example.mini.game.util.mathematics;

import android.util.Log;

import static android.util.FloatMath.sqrt;

/**
 * Created by user on 2014-11-29.
 */
public abstract class Vector {
    protected float x;
    protected float y;
    protected float z;

    public static void print(float[] vector, String tag) {
        Log.i(tag, "x: " + vector[0] + " y:" + vector[1] + " z:" + vector[2]);
    }

    public static void scalarMultiplication(float[] vec1, float scalar) {
        for (int i = 0; i < vec1.length; i++) {
            vec1[i] *= scalar;
        }
    }

    public static float[] crossProduct(float[] vec1, float[] vec2) {
        return new float[]{
                vec1[1] * vec2[2] - vec1[2] * vec2[1],
                vec1[2] * vec2[0] - vec1[0] * vec2[2],
                vec1[0] * vec2[1] - vec1[1] * vec2[0]
        };
    }

    public static float[] normalize(float[] vec) {
        float magnitude = sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
        return new float[]{
                vec[0] / magnitude,
                vec[1] / magnitude,
                vec[2] / magnitude,
        };
    }
}
