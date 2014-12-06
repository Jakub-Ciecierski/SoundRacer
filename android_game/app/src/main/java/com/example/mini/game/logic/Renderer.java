package com.example.mini.game.logic;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.mini.game.logic.collision.Board;
import com.example.mini.game.shapes.basic.Triangle;
import com.example.mini.game.shapes.complex.CartesianCoordinates;
import com.example.mini.game.shapes.complex.SetOfButtons;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

/**
 * Created by Kuba on 05/12/2014.
 */
public class Renderer implements GLSurfaceView.Renderer {
    public Context context;

    private Board board;
    private Triangle triangle;
    private CartesianCoordinates cartesianCoordinates;
    private SetOfButtons movementButtons;

    private final float[] mvpMatrix = new float[16];
    private final float[] projMatrix = new float[16];
    private final float[] vMatrix = new float[16];
    private final float[] rotationMatrix = new float[16];
    private final float[] translationMatrix = new float[16];
    private float[] orthogonalMatrix = new float[16];

    float currentX = 0f;
    float currentY = 0f;

    float color = 0.0f;

    public Renderer(Context context) {
        this.context = context;

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        //GLES20.glDepthMask(true);

        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        board = new Board();
        triangle = new Triangle();
        cartesianCoordinates = new CartesianCoordinates(new float[] {0.0f,0.0f,0.0f});
        movementButtons = new SetOfButtons(context);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        float ratio = (float) width / height;
        Matrix.frustumM(projMatrix, 0, -ratio, ratio, -1, 1, 3, 100);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        // Set the camera position (View matrix)

        Matrix.setLookAtM(vMatrix, 0,
                0f, 0f, -5.0f, // eye coords
                0f, 0f, 0f, // look at coords
                0f, 1f, 1.0f); // up vector

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, vMatrix, 0);

        Matrix.setIdentityM(translationMatrix,0);
        Matrix.translateM(translationMatrix, 0, -2.0f, -2.0f, 0.0f);
        cartesianCoordinates.draw(mvpMatrix);

        //Matrix.setRotateM(rotationMatrix, 0, 0, 0, 0, 0);
        Matrix.setIdentityM(translationMatrix,0);
        Matrix.translateM(translationMatrix, 0, currentX, currentY, 0.0f);

        //currentX += 0.1f;
        //currentY += 0.1f;

        // Combine the rotation matrix with the projection and camera view
        //Matrix.multiplyMM(mvpMatrix, 0, rotationMatrix, 0, mvpMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, translationMatrix, 0, mvpMatrix, 0);

        board.draw(mvpMatrix);

    }
}
