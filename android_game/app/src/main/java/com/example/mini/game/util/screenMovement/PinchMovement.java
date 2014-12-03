package com.example.mini.game.util.screenMovement;

import android.view.ScaleGestureDetector;

import com.example.mini.game.GameRenderer;
import com.example.mini.game.util.camera.DeveloperStaticSphereCamera;


/**
 * Created by dybisz on 2014-11-25.
 */
public class PinchMovement extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    private float PINCH_SCALE_FACTOR = 5;
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        switch(GameRenderer.currentCamera) {
            case DEVELOPER_CAMERA:
                float scale = detector.getScaleFactor();
                DeveloperStaticSphereCamera.setRadiusOfView(DeveloperStaticSphereCamera.getRadiusOfView() +
                        scale * ((scale >= 1) ? 1 : -1) / PINCH_SCALE_FACTOR);
                break;
            case PLAYER_CAMERA:
                break;
        }

        return true;
    }


}
