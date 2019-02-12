package com.projects.battleship;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

class AI {
    private final Attack attack = new Attack();
    private final RandomValues randomValues = new RandomValues();
    private ArrayList<Integer> attackCellsList;

    public void attackConfiguration(Board board, String difficultyLevel) {
        this.attackCellsList = randomValues.randomCellsBoardList(board);

        switch (difficultyLevel) {
            case "easy":
                break;

            case "medium":
                checkAllCellsProperties(board, "medium");
                break;

            case "hard":
                checkAllCellsProperties(board, "hard");
                break;
        }
    }

    private int movesForCellsWithShipHard(Board board, int i, int idPane) {
        Ship thisShip = board.getPanesWithShipsMap().get(idPane);

        LinkedList<Integer> thisShipPanePositionsList = thisShip.getShipPositionsList();
        List<Integer> thisShipPanePositionsArray = new ArrayList<>(thisShipPanePositionsList);

        attackCellsList.removeAll(thisShipPanePositionsArray);
        attackCellsList.addAll(i, thisShipPanePositionsArray);
        i += thisShip.getShipSize();
        return i;
    }

    private void checkAllCellsProperties(Board board, String difficultyLevel) {
        for (int i = 0; i < attackCellsList.size(); i++) {
            int idPane = attackCellsList.get(i);

            if (Objects.equals(board.getGameStatus(idPane), "with-ship")) {
                if (Objects.equals(difficultyLevel, "medium")) {
                    i = movesForCellsWithShipMedium(board, i, idPane);
                } else if (Objects.equals(difficultyLevel, "hard")) {
                    i = movesForCellsWithShipHard(board, i, idPane);
                }
            }
        }
    }

    private int movesForCellsWithShipMedium(Board board, int i, int idPane) {
        Ship thisShip = board.getPanesWithShipsMap().get(idPane);
        LinkedList<Integer> tmpAttackList = new LinkedList<>();
        int counterThisHitAttacks = 1;
        int addPosition = 1;
        char thisAttacksOrientation = randomValues.randomOrientation();

        while (counterThisHitAttacks < thisShip.getShipSize()) {

            if (thisAttacksOrientation == 'v') {

                int nextCellAttackPosition = idPane + addPosition;

                if (((nextCellAttackPosition % board.getBoardRows() != 0) &&
                        (nextCellAttackPosition < (board.getBoardRows() * board.getBoardColumns())) &&
                        (addPosition > 0)) ||
                        ((nextCellAttackPosition % board.getBoardRows() != 9) &&
                                (nextCellAttackPosition >= 0) &&
                                (addPosition < 0))) {

                    tmpAttackList.add(nextCellAttackPosition);

                    if (Objects.equals(board.getGameStatus(nextCellAttackPosition), "with-ship")) {
                        counterThisHitAttacks++;
                        if (addPosition > 0) {
                            addPosition++;
                        } else {
                            addPosition--;
                        }
                    } else if (addPosition > 0) {
                        addPosition = (-1);
                    } else {
                        thisAttacksOrientation = 'h';
                        addPosition = 1;
                    }
                } else if (addPosition > 0) {
                    addPosition = (-1);
                } else {
                    thisAttacksOrientation = 'h';
                    addPosition = 1;
                }
            } else {

                int nextCellAttackPosition = idPane + (addPosition * 10);

                if ((nextCellAttackPosition < (board.getBoardColumns() * board.getBoardRows())) &&
                        (nextCellAttackPosition >= 0)) {

                    tmpAttackList.add(nextCellAttackPosition);

                    if (Objects.equals(board.getGameStatus(nextCellAttackPosition), "with-ship")) {
                        counterThisHitAttacks++;
                        if (addPosition > 0) {
                            addPosition++;
                        } else {
                            addPosition--;
                        }
                    } else if (addPosition > 0) {
                        addPosition = (-1);
                    } else {
                        thisAttacksOrientation = 'v';
                        addPosition = 1;
                    }
                } else if (addPosition > 0) {
                    addPosition = (-1);
                } else {
                    thisAttacksOrientation = 'v';
                    addPosition = 1;
                }
            }
        }

        List<Integer> thisAttackList = new ArrayList<>(tmpAttackList);
        attackCellsList.removeAll(thisAttackList);
        attackCellsList.addAll(i, thisAttackList);
        i += thisAttackList.size();
        return i;
    }

    public boolean attack(Board board) {
        boolean isHit = attack.attack(board, attackCellsList.get(0));
        attackCellsList.remove(0);
        return isHit;
    }

}
