package com.example.mini.game.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;


/**
 * Created by user on 2014-11-29.
 */
public class TexturesLoader {

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        GLES20.glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {

            Log.w("Texture loader", "Could not generate a new OpenGL texture object.");

            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options);

        if (bitmap == null) {

            Log.w("BITMAP NULLLLLLL", "Resource ID " + resourceId + " could not be decoded.");


            GLES20.glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }
        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0]);

        // Set filtering: a default must be set, or the texture will be
        // black.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // BEZ TEGO GÓWNA SKURWYSYN NIE DZIAŁA.
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        bitmap.recycle();

        // Unbind from the texture.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }

    public static float[] generateUvForTriangleStrip(int verticesPerBorder) {
        float[] texture = new float[verticesPerBorder * 2 * 2];
        float oneStep = 1 / (float)verticesPerBorder;
        float offSet = 0;
        for (int i = 0; i < texture.length; ) {
            // Vertex 1
            texture[i++] = offSet * oneStep;
            texture[i++] = 1.0f;
            texture[i++] = offSet * oneStep;
            texture[i++] = 0.0f;
            print("offset: " + offSet + " vertex_botom: " + texture[i-4] +" " + texture[i-3]
                    + "vertex_up:" + texture[i-2] + " " + texture[i-1] + "oneStep: " + oneStep );
            offSet++;
        }

        return texture;
    }

    private static void print(String s) {
        Log.i("",s);
    }
}
