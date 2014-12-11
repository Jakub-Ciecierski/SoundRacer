package com.example.mini.game;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;


import com.example.mini.game.audio.AudioAnalyser;
import com.example.mini.game.audio.AudioPlayer;
import com.example.mini.game.audio.NativeMP3Decoder;
import com.example.mini.game.shapes.complex.CartesianCoordinates;
import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.shapes.complex.SetOfButtons;
import com.example.mini.game.util.enums.CameraType;
import com.example.mini.game.util.camera.DeveloperStaticSphereCamera;
import com.example.mini.game.util.camera.PlayerStaticSphereCamera;
import com.example.mini.game.util.mathematics.Vector3;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

/**
 * Created by user on 2014-11-22.
 */
public class GameRenderer implements GLSurfaceView.Renderer {
    public static Context context;
    public static CameraType currentCamera = CameraType.PLAYER_CAMERA;

    public static float FLUX_LENGTH_MS = 0;

    private float[] mProjectionMatrix = new float[16];
    private float[] mOrthogonalMatrix = new float[16];
    private CartesianCoordinates cartesianCoordinates;
    private SetOfButtons movementButtons;
    private GameBoard gameBoard;

    // path to file
    //final String FILE = "/sdcard/external_sd/Music/Billy_Talent/Billy Talent - Diamond on a Landmine with Lyrics.mp3";
    //final String FILE = "/sdcard/external_sd/Music/samples/tests/limit.mp3";
    //final String FILE = "/sdcard/external_sd/Music/Billy_Talent/judith.mp3";
    //final String FILE = "/sdcard/external_sd/Music/Billy_Talent/explosivo.mp3";
    //final String FILE = "/sdcard/external_sd/Music/samples/jazz.mp3";
    //final String FILE = "/sdcard/music/judith.mp3";
    //final String FILE = "/sdcard/music/explosivo.mp3";
    //final String FILE = "/sdcard/music/kat - 04 - stworzylem piekna rzecz.mp3";
    final String FILE = "/sdcard/music/intoTheVoid.mp3";
    AudioAnalyser audioAnalyser;
    AudioPlayer audioPlayer;
    final int bufferSize = 1024;


    public GameRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //cube = new Cube();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glDepthMask(true);

        // axis
        cartesianCoordinates = new CartesianCoordinates(new float[]{0.0f, 0.0f, 0.0f});
        movementButtons = new SetOfButtons(context);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // AUDIO
        NativeMP3Decoder.initLib();
        audioAnalyser = new AudioAnalyser(FILE, bufferSize, 44100, 400);
        FLUX_LENGTH_MS = audioAnalyser.FLUX_LENGTH_MS;

        audioPlayer = new AudioPlayer(FILE, bufferSize, 44100);
        audioAnalyser.startAnalyzing();

        while(!AudioAnalyser.isReadyToGo){}

        Log.i("GAME_RENDERER","Anal is ready for action");

        gameBoard = new GameBoard();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        movementButtons.setDimensions(width, height);
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 400);
        Matrix.orthoM(mOrthogonalMatrix, 0, -ratio, ratio, -1, 1, -1, 1);

        CustomGlSurfaceView.screenWidth = width;
        CustomGlSurfaceView.screenHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        gameBoard.render(getCurrentCameraMatrix());
        if (currentCamera == CameraType.DEVELOPER_CAMERA)
            cartesianCoordinates.draw(getCurrentCameraMatrix());
        movementButtons.draw(mOrthogonalMatrix);


        // AUDIO

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

    public static float[] getEyePosition() {
        Vector3 temp = PlayerStaticSphereCamera.getEyeVector();
        return new float[]{temp.getX(), temp.getY(), temp.getZ(), 1.0f};
    }


    public void startAnalyzing() {
        audioAnalyser.startAnalyzing();
    }

    public void startAudio() {
        Log.i("", "start audio clicked");
        audioPlayer.startDecoding();
        audioPlayer.playAudio();
    }
}
