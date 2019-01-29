package com.projects.battleship;

import java.util.Random;

public class RandomValues {
    Random generator = new Random();

    public int randomValue(int maxValue) {
        return generator.nextInt(maxValue);
    }

    public char randomOrientation() {
        if (randomValue(2) == 0) {
            return 'v';
        } else {
            return 'h';
        }
    }
}
