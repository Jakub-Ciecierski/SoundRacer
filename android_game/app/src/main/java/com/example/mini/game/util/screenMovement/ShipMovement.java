package com.example.mini.game.util.screenMovement;

import android.os.Handler;
import android.util.Log;

import com.example.mini.game.shapes.complex.Player;
import com.example.mini.game.util.enums.MoveType;

/**
 * Created by user on 2014-12-08.
 */
public class ShipMovement implements Runnable {
    private MoveType moveType;
    private Player player;
    public static float movementSensitivity=0.1f;
    public ShipMovement(MoveType moveType) {
        this.moveType = moveType;
        //this.player = player;
        MovementController.stabilising = false;
    }

    @Override
    public void run() {
        switch (moveType) {
            case MOVE_LEFT:
                Player.setTranslate(
                        Player.getTranslationX() + movementSensitivity,
                        Player.getTranslationY(),
                        Player.getTranslationZ());
                Player.rotateAroundZ(Player.getCurrentAngle() - 1);
                Log.i("ShipMovement", Float.toString(movementSensitivity));
                break;
            case MOVE_RIGHT:
                Player.setTranslate(
                        Player.getTranslationX() - movementSensitivity,
                        Player.getTranslationY(),
                        Player.getTranslationZ());
                Player.rotateAroundZ(Player.getCurrentAngle()+1);
                Log.i("ShipMovement", Float.toString(movementSensitivity));
                break;
        }
        if (MovementController.isPlayerOnMove)
            (new Handler()).postDelayed(this, 10);
        else
            (new Handler()).postDelayed(new ShipStabilising(), 10);
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }
}
