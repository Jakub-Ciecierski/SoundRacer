package com.example.mini.game.util.screenMovement;

import android.os.Handler;

import com.example.mini.game.shapes.complex.Player;

/**
 * Created by dybisz on 2014-12-08.
 */
public class ShipStabilising implements Runnable {

    public ShipStabilising() {
        /* When someone aks for stabilising we need to say to MovementController that it
         * has been fired up */
        MovementController.stabilising = true;
    }

    @Override
    public void run() {
        /* Depending on current angle increase or decrease value of the Player's angle
         * such that it converges to 0 */
        Player.rotateAroundZ(Player.getCurrentAngle() +
                ((Player.getCurrentAngle() <= 0) ? 2f : -2f));

        /* If we reached 0, stabilizing wont be needed */
        // TODO odroznic skrecanie w lewo od skrecania w prawo i zrobic <= badz >=
        if (Player.getCurrentAngle() == 0.0f)
            MovementController.stabilising = false;

        /* Check if the process is available. Stabilizing can be also break by e.g
         * new movement */
        if (MovementController.stabilising)
            (new Handler()).postDelayed(this, 3);
    }
}
