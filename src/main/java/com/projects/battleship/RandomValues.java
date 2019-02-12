package com.projects.battleship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class RandomValues {
    private final Random generator = new Random();

    private int randomValue(int maxValue) {
        return generator.nextInt(maxValue);
    }

    public char randomOrientation() {
        if (randomValue(2) == 0) {
            return 'v';
        } else {
            return 'h';
        }
    }

    public ArrayList<Integer> randomCellsBoardList(Board board) {
        int boardColumns = board.getBoardColumns();
        int boardRows = board.getBoardRows();
        int maxValue = boardColumns * boardRows;

        ArrayList<Integer> randomList = new ArrayList<>(maxValue);
        for (int i = 0; i < maxValue; i++) {
            randomList.add(i);
        }

        Collections.shuffle(randomList);
        return randomList;

    }


    public Ship randomShip(Board board, int shipSize) {
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
