package com.example.mini.game.util.mathematics;

import android.util.Log;

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
}
