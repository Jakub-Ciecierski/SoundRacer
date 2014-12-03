package pl.dybisz.testgry;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.dybisz.testgry.shapes.complex.CartesianCoordinates;

import pl.dybisz.testgry.shapes.complex.GameBoard;
import pl.dybisz.testgry.util.CameraType;
import pl.dybisz.testgry.util.camera.DeveloperStaticSphereCamera;
import pl.dybisz.testgry.shapes.complex.SetOfButtons;
import pl.dybisz.testgry.util.camera.PlayerStaticSphereCamera;

import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

/**
 * Created by user on 2014-11-22.
 */
public class GameRenderer implements GLSurfaceView.Renderer {
    public static Context context;
    public static CameraType currentCamera = CameraType.DEVELOPER_CAMERA;
    private float[] mProjectionMatrix = new float[16];
    private float[] mOrthogonalMatrix = new float[16];
    private CartesianCoordinates cartesianCoordinates;
    private SetOfButtons movementButtons;
    private GameBoard gameBoard;


    public GameRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //cube = new Cube();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glDepthMask(true);

        cartesianCoordinates = new CartesianCoordinates(new float[]{0.0f, 0.0f, 0.0f});
        movementButtons = new SetOfButtons(context);
        gameBoard = new GameBoard();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        movementButtons.setDimensions(width, height);
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 100);
        Matrix.orthoM(mOrthogonalMatrix, 0, -ratio, ratio, -1, 1, -1, 1);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        gameBoard.render(getCurrentCameraMatrix());
        cartesianCoordinates.draw(getCurrentCameraMatrix());
        movementButtons.draw(mOrthogonalMatrix);

    }

    private float[] getCurrentCameraMatrix() {
        return (currentCamera == CameraType.DEVELOPER_CAMERA) ?
                DeveloperStaticSphereCamera.getCameraMatrix(mProjectionMatrix) :
                PlayerStaticSphereCamera.getCameraMatrix(mProjectionMatrix);
    }

    public static void swapCameras() {
        currentCamera = (currentCamera == CameraType.DEVELOPER_CAMERA) ?
                CameraType.PLAYER_CAMERA : CameraType.DEVELOPER_CAMERA;
    }
}
