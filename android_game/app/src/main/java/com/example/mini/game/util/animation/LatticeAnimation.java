package com.example.mini.game.util.animation;

import android.util.Log;

import com.example.mini.game.shapes.basic.Line;
import com.example.mini.game.shapes.complex.GameBoard;

/**
 * Simple class to loop lattice.
 * <p></p>
 * Created by dybisz on 2014-11-30.
 */
public class LatticeAnimation {
    private float vanishBorder;
    private float spawnBorder;

    public LatticeAnimation(float vanishBorder, float spawnBorder) {
        this.vanishBorder = vanishBorder;
        this.spawnBorder = spawnBorder;
    }

    public void generateNextFrame(Line line) {
        if (line.getTranslationsZ() <= vanishBorder) {
            line.setTranslationsZ(spawnBorder);
            Log.i("BANG!", "GENERATE!" + GameBoard.animationCounter + " " + GameBoard.tempCounter);
        } else
            line.setTranslationsZ(line.getTranslationsZ() - GameBoard.TIME_UNIT_LENGTH);
    }
}
