package com.example.mini.game.util.screenMovement;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Created by user on 2014-11-25.
 */
public class MovementController {
    private ScaleGestureDetector pinchDetector;
    private GestureDetector basicMovement;
    private Context context;

    public MovementController(Context context) {
        this.context = context;
        pinchDetector  = new ScaleGestureDetector(context, new PinchMovement());
        basicMovement = new GestureDetector(context, new BasicGestures());
    }

    public void handleMovement(MotionEvent e) {
        basicMovement.onTouchEvent(e);
        pinchDetector.onTouchEvent(e);
    }
}


