package pl.dybisz.testgry;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.dybisz.testgry.shapes.Button;
import pl.dybisz.testgry.shapes.CartesianCoordinates;
import pl.dybisz.testgry.shapes.Cone;
import pl.dybisz.testgry.shapes.HeightMap;

import pl.dybisz.testgry.shapes.Cube;
import pl.dybisz.testgry.shapes.RoadPrototype;
import pl.dybisz.testgry.shapes.Web;
import pl.dybisz.testgry.util.StaticSphereCamera;
import pl.dybisz.testgry.util.screenMovement.SetOfButtons;

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
    private float[] mOrthogonalMatrix = new float[16];
    private Cube cube;
    private CartesianCoordinates cartesianCoordinates;
    private SetOfButtons movementButtons;
    private RoadPrototype road;

    public GameRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //cube = new Cube();
        cartesianCoordinates = new CartesianCoordinates(new float[] {0.0f,0.0f,0.0f});
        movementButtons = new SetOfButtons(context);
        road = new RoadPrototype();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        movementButtons.setDimensions(width,height);
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 100);
        Matrix.orthoM(mOrthogonalMatrix,0, -ratio, ratio, -1,1,-1,1);
    }
    private int count = 0;
    @Override
    public void onDrawFrame(GL10 gl10) {
        count++;
        glClear(GL_COLOR_BUFFER_BIT);
        //cube.draw(StaticSphereCamera.getCameraMatrix(mProjectionMatrix));
        cartesianCoordinates.draw(StaticSphereCamera.getCameraMatrix(mProjectionMatrix));
        if(count == 3) {
            road.switchFrame();
            count =0;
        }
        road.draw(StaticSphereCamera.getCameraMatrix(mProjectionMatrix));
        movementButtons.draw(mOrthogonalMatrix);

    }
}