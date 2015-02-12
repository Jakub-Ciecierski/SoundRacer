package com.example.mini.game.util.mathematics;

import java.util.Random;

/**
 * Created by user on 2015-02-11.
 */
public abstract class RandomInteger {
    private static Random random = new Random();

    public static int generate(int minimum, int maximum) {
        int randomNum = random.nextInt((maximum - minimum) + 1) + minimum;
        return randomNum;
    }
}
