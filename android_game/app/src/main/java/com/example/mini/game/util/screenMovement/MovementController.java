package com.example.mini.game.util.screenMovement;

import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.example.mini.game.CustomGlSurfaceView;
import com.example.mini.game.util.enums.MoveType;

/**
 * Created by user on 2014-11-25.
 */
public class MovementController {
    private static final int INVALID_POINTER = -1;
    private ScaleGestureDetector pinchDetector;
    private GestureDetector basicMovement;
    public static boolean isPlayerOnMove = false;
    private MoveType firstFinger;
    private MoveType secondFinger;
    private ShipMovement shipMovement;
    private int activePointer = INVALID_POINTER;
    private int secondPointer = INVALID_POINTER;
    public static boolean stabilising = false;

    public MovementController() {
        pinchDetector = new ScaleGestureDetector(CustomGlSurfaceView.context, new PinchMovement());
        basicMovement = new GestureDetector(CustomGlSurfaceView.context, new BasicGestures());
    }

    public boolean handleMovement(MotionEvent e) {
        basicMovement.onTouchEvent(e);
        pinchDetector.onTouchEvent(e);
        /* If player action is different than the above two ... */
        int numberOfTouches = e.getPointerCount();
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                /* Save first finger's event id */
                activePointer = e.getPointerId(0);
                MovementController.isPlayerOnMove = true;

                /* Check which part of the screen was touched and decide
                   about movement direction */
                if (e.getX() > CustomGlSurfaceView.screenWidth / 2) {
                    firstFinger = MoveType.MOVE_RIGHT;
                } else {
                    firstFinger = MoveType.MOVE_LEFT;
                }
                secondFinger = firstFinger;
                /* Fire up the background runnable */
                shipMovement = new ShipMovement(firstFinger);
                (new Handler()).post(shipMovement);
                Log.i("!!!!!!", "pierwszy palec, ustawiam jako aktywny i odpalam runnable; koordynaty: " +e.getX() +" y;"+e.getY());
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                int pointeIndex = e.getActionIndex();
                int pointeId = e.getPointerId(pointeIndex);
                if(pointeId !=activePointer) {

                      /* Check which part of the screen was touched */
                    secondFinger = (secondFinger == MoveType.MOVE_LEFT)? MoveType.MOVE_RIGHT : MoveType.MOVE_LEFT;
                    Log.i("!!!!!!", "dodatkowy palec ktory nie jest aktywny,ma koordynaty:" + e.getX() + "y: " + e.getY() );
                   secondPointer = pointeId;
                }


                return true;
            case MotionEvent.ACTION_UP:
                if(activePointer != INVALID_POINTER) {
                    Log.i("!!!!!!", "jeden palec na ekranie, wlasnie odjales od ekranu aktywny palec");
                    MovementController.isPlayerOnMove = false;
                    activePointer = INVALID_POINTER;
                }
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                int pointerIndex = e.getActionIndex();
                int pointerId = e.getPointerId(pointerIndex);
                if(pointerId == activePointer) {
                    Log.i("!!!!!!", "na ekranie sa dwa palce, odjales jeden ktory jest active, ustawiam" +
                            "           jedyny palec jako active:");
                    shipMovement.setMoveType(secondFinger);

                    activePointer = secondPointer;
                    secondPointer = INVALID_POINTER;
                }

                return true;
            default:
                return true;
        }

    }
}


