package pl.dybisz.testgry;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.dybisz.testgry.shapes.CartesianCoordinates;
import pl.dybisz.testgry.shapes.Cone;
import pl.dybisz.testgry.shapes.HeightMap;

import pl.dybisz.testgry.shapes.Cube;
import pl.dybisz.testgry.shapes.RoadPrototype;
import pl.dybisz.testgry.shapes.Web;
import pl.dybisz.testgry.util.StaticSphereCamera;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;

import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

/**
 * Created by user on 2014-11-22.
 */
public class GameRenderer implements GLSurfaceView.Renderer {
    private final Context context;
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private float[] cartesianMatrix = new float[16];
    private Cube cube;
    private Web web;
    private Cone cone;
    private CartesianCoordinates cartesianCoordinates;
    private RoadPrototype roadPrototype;


    private float[] mRotationMatrix = new float[16];
    private HeightMap heightMap;

    public GameRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        heightMap = new HeightMap();
        cube = new Cube();

        //web = new Web();
        cartesianCoordinates = new CartesianCoordinates(new float[] {0.0f,0.0f,0.0f});
        roadPrototype = new RoadPrototype();

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 100);
        //Matrix.orthoM(cartesianMatrix,0, -ratio, ratio, -1,1,-1,1);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
//        long time = SystemClock.uptimeMillis();
//        float z = 0.00045f * ((int) time);
//        Matrix.setLookAtM(mViewMatrix, 0,
//                20 * cos(z), 5, 20 * sin(z), // eye
//                0, 0, 0,                          // lookAt
//                0f, 1.0f, 0.0f);                //up vector
//
//        // Calculate the projection and view transformation
//        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);


        glClear(GL_COLOR_BUFFER_BIT);
        cube.draw(StaticSphereCamera.getCameraMatrix(mProjectionMatrix));
        //web.draw(mMVPMatrix);
        cartesianCoordinates.draw(StaticSphereCamera.getCameraMatrix(mProjectionMatrix));
        roadPrototype.draw(StaticSphereCamera.getCameraMatrix(mProjectionMatrix));

    }
}
