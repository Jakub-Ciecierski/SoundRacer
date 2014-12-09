package com.example.mini.game.util.enums;

/**
 * Turning(left or right) has 3 stages. In addition {@link com.example.mini.game.shapes.complex.Road}
 * can also go straight(which we also represent using this enum).
 * {@link com.example.mini.game.util.animation.VBORoadAnimation} is the class which uses this enum
 * the most.
 * <p></p>
 * Created by dybisz on 2014-12-04.
 */
public enum TurnStage {
    STRAIGHT, TURN_RIGHT_START, TURN_RIGHT_STABLE, TURN_RIGHT_END, TURN_LEFT_START, TURN_LEFT_END,
    TURN_LEFT_STABLE
}
