package com.example.mini.game;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;


import com.example.mini.game.audio.AudioAnalyser;
import com.example.mini.game.audio.AudioPlayer;
import com.example.mini.game.audio.NativeMP3Decoder;
import com.example.mini.game.gameMenu.MenuActivity;
import com.example.mini.game.launcher.LauncherActivity;
import com.example.mini.game.logic.GlobalState;
import com.example.mini.game.shapes.complex.CartesianCoordinates;
import com.example.mini.game.shapes.complex.GameBoard;
import com.example.mini.game.shapes.complex.SetOfButtons;
import com.example.mini.game.util.enums.CameraType;
import com.example.mini.game.util.camera.DeveloperStaticSphereCamera;
import com.example.mini.game.util.camera.PlayerStaticSphereCamera;
import com.example.mini.game.util.mathematics.Vector3;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;

/**
 * Created by user on 2014-11-22.
 */
public class GameRenderer implements GLSurfaceView.Renderer {

    protected boolean gameRunning = true;
    private boolean timerEnd = false;
    private boolean isFirstTime = true;
    private boolean isFirstTimeSongNameDisplay=true;
    private long beginningTime=0;
    private long currentTime;
    private long beginningTimeSongName=0;
    private long currentTimeSongName;
    private float[] mProjectionMatrix = new float[16];
    private float[] mOrthogonalMatrix = new float[16];
    private CartesianCoordinates cartesianCoordinates;
    private SetOfButtons movementButtons;
    static private GameBoard gameBoard;

    public static Context context;
    public static CameraType currentCamera = CameraType.PLAYER_CAMERA;
    public GameActivity gameActivity;
    public GameRenderer(Context context) {
        this.context = context;
        //this.gameActivity = gameActivity;
        gameRunning = true;
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

        // TODO put this in loading screen
        GlobalState.loadGraphics();

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        /*
            turning off loading animation in GameActivity
         */
        GlobalState.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GlobalState.gameActivity.turnOffImageView();
            }
        });


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
        /*
        Song start counter change
        */
        /*
            delta = currenttime - lasttime
            delta > 1000
            if counter == 3
        * */

        if(GlobalState.displaySongName){
            if(isFirstTimeSongNameDisplay){
                currentTimeSongName=System.currentTimeMillis();
                isFirstTimeSongNameDisplay=false;
            }
            if((System.currentTimeMillis()-currentTimeSongName)>3000){
                GlobalState.gameActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GlobalState.gameActivity.changeSongNameText("");
                    }
                });

                isFirstTimeSongNameDisplay=true;
                GlobalState.displaySongName=false;
            }
        }
         if(isFirstTime){
            beginningTime = System.currentTimeMillis();
            //startAudio();
            isFirstTime = false;
        }
        if(!timerEnd){
            currentTime=System.currentTimeMillis();
            long dif = currentTime-beginningTime;
            if(dif>1000 && dif<1200){
                GlobalState.gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GlobalState.gameActivity.changeSongCounterText(2);
                       }
                   });
            }
            if(dif>2000 && dif<2200){
                GlobalState.gameActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GlobalState.gameActivity.changeSongCounterText(1);
                    }
                });
            }
            if(dif>3000 && dif<3200){
                GlobalState.gameActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GlobalState.gameActivity.changeSongCounterText(0);
                    }
                });
                timerEnd=true;
                startAudio();
            }
        }

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glDepthMask(true);

        gameBoard.render(getCurrentCameraMatrix());
        if (currentCamera == CameraType.DEVELOPER_CAMERA)
            cartesianCoordinates.draw(getCurrentCameraMatrix());
        movementButtons.draw(mOrthogonalMatrix);

        if(gameRunning) {
            if(GlobalState.isAnalyserDone()) {
                Log.i("GameLoop","Creating new Analyser");
                GlobalState.createNextAudioAnalyser();
                Log.i("GameLoop","After Creating new Analyser");
            }

            if(GlobalState.isPlayerDone()) {
                if( !GlobalState.createNextAudioPlayer() ) {
                    Log.i("GameLoop","Returning to Menu");
                    gameRunning = false;
                    returnToMenu();
                    Log.i("GameLoop","After Returning to Menu");
                }
                else {
                    Log.i("GameLoop","Starting new audio");
                    GlobalState.startAudio();
                    Log.i("GameLoop","After Starting new audio");
                }
            }
        }
    }

    static public void initGameBoard() {
        gameBoard = new GameBoard();
    }

    private void returnToMenu() {
        Intent intent = new Intent(context, LauncherActivity.class);
        context.startActivity(intent);
        GlobalState.gameActivity.finish();

//        Intent mStartActivity = new Intent(context, MenuActivity.class);
//        int mPendingIntentId = 123456;
//        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//        GlobalState.shutDownSystem();
//        System.exit(0);
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

    public void startAudio() {
        Log.i("GameRenderer", "start audio clicked");

        GlobalState.startAudio();
        gameRunning = true;
    }
    public void stopAudio(){
        GlobalState.pauseAudio();
        gameRunning = false;
    }
}