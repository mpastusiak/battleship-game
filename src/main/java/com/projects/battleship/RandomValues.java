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

    public Ship randomShip(Board board, int shipSize){
        char shipRandomOrientation = randomOrientation();

        int shipMaxPositionX;
        int shipMaxPositionY;
        if (shipRandomOrientation == 'v') {
            shipMaxPositionX = board.getBoardColumns();
            shipMaxPositionY = board.getBoardRows() - shipSize;
        } else {
            shipMaxPositionX = board.getBoardColumns() - shipSize;
            shipMaxPositionY = board.getBoardRows();
        }

        Ship ship = new Ship(shipSize, board.getLengthSquare(), shipRandomOrientation);

        int shipPositionX = randomValue(shipMaxPositionX);
        int shipPositionY = randomValue(shipMaxPositionY);

        ship.setActualXPosition(shipPositionX);
        ship.setActualYPosition(shipPositionY);

        return ship;
    }
}
