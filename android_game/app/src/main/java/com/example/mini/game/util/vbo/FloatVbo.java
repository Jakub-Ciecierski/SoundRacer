package com.example.mini.game.util.vbo;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by dybisz on 2015-02-16.
 */
public class FloatVbo {
    private static final int BYTES_PER_FLOAT = 4;
    private FloatBuffer floatBuffer;

    public FloatVbo(float[] newData) {
        floatBuffer = ByteBuffer
                .allocateDirect(newData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(newData);
        floatBuffer.position(0);
    }
    public void setVertexAttributePointer(int dataOffset, int attributeLocation,
                                       int componentCount, int stride) {
        floatBuffer.position(dataOffset);
        GLES20.glVertexAttribPointer(attributeLocation, componentCount, GLES20.GL_FLOAT,
                false, stride, floatBuffer);
        GLES20.glEnableVertexAttribArray(attributeLocation);
        floatBuffer.position(0);
    }
}
