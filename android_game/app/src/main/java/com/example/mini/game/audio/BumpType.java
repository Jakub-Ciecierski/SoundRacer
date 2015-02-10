package com.example.mini.game.audio;

/**
 * This enum class determines which bump type to choose based on spectral flux analysis
 * and their heights on the road it self.
 *
 * Created by Kuba on 07/12/2014.
 */
public enum BumpType {
    BIG_BUMP (4.0f, 10),
    MEDIUM_BUMP (2.0f, 6),
    SMALL_BUMP (1.0f, 3),
    NO_BUMP (0.0f, 0);

    private float height;

    private int scaler;

    BumpType(float height, int scaler) {
        this.height = height;
        this.scaler = scaler;
    }

    public float getHeight() {
        return this.height;
    }

    // TODO, currently unused
    private float getScaler() {
        return this.scaler;
    }

}
