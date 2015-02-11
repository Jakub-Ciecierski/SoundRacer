package com.example.mini.game.util.loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.example.mini.game.GameActivity;
import com.example.mini.game.GameRenderer;

import java.io.IOException;


/**
 * Created by user on 2014-11-29.
 */
public abstract class TexturesLoader {

    public static int loadTexture(String textureName) {
        final int[] textureObjectIds = new int[1];
        GLES20.glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {

            Log.w("Texture loader", "Could not generate a new OpenGL texture object.");

            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Read in the resource
//        final Bitmap bitmap = BitmapFactory.decodeResource(
//                context.getResources(), resourceId, options);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(GameRenderer.context.getAssets().open("textures/" + textureName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap == null) {

            Log.w("BITMAP NULLLLLLL", "Resource ID " + textureName + " could not be decoded.");


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
            offSet++;
        }

        return texture;
    }

    private static void print(String s) {
        Log.i("",s);
    }
}
