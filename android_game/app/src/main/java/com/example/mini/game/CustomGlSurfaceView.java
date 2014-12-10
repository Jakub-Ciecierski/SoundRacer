package com.example.mini.game;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.example.mini.game.util.screenMovement.MovementController;

/**
 * Created by dybisz on 2014-11-23.
 */
public class CustomGlSurfaceView extends GLSurfaceView {
    MovementController movementController;
    public static Context context;
    public static float screenHeight;
    public static float screenWidth;
    private GameRenderer gameRenderer;


    /**
     * @param context
     */
    public CustomGlSurfaceView(Context context) {
        super(context);
        this.context = context;
        gameRenderer = new GameRenderer(context);
        movementController = new MovementController();
        setEGLContextClientVersion(2);
        setRenderer(gameRenderer);


    }

    /**
     * @param e
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return movementController.handleMovement(e);
    }

    public void onClickAudio() {
        gameRenderer.startAudio();
    }

    public void onClickAnal() {
        gameRenderer.startAnalyzing();
    }
}
