package com.projects.battleship;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AI {
    private Attack attack = new Attack();
    private RandomValues randomValues = new RandomValues();
    private ArrayList<Integer> attackCellsList;

    public void attackConfiguration(Board board, String difficultyLevel) {
        switch(difficultyLevel) {
            case "easy":
                this.attackCellsList = randomValues.randomCellsList(board);
                break;

            case "medium":
                this.attackCellsList = randomValues.randomCellsList(board);
                for (int i = 0; i < attackCellsList.size(); i++) {
                    int idPane = (Integer) attackCellsList.get(i);

                    if (board.getGameStatus(idPane) == "with-ship") {
                        Ship thisShip = board.getPanesWithShipsMap().get(idPane);
                        LinkedList<Integer> tmpAttackList = new LinkedList<>();
                        int counterThisHitAttacks = 1;
                        int addPosition = 1;
                        char thisAttacksOrientation = randomValues.randomOrientation();

                        while (counterThisHitAttacks < thisShip.getShipSize()) {

                            if (thisAttacksOrientation == 'v') {

                                if ((((idPane + addPosition) % board.getBoardRows() != 0) &&
                                        ((idPane + addPosition) < (board.getBoardRows() * board.getBoardColumns())) &&
                                        (addPosition > 0)) ||
                                        (((idPane + addPosition) % board.getBoardRows() != 9) &&
                                            ((idPane + addPosition) >= 0) &&
                                            (addPosition < 0))) {

                                    tmpAttackList.add(idPane + addPosition);

                                    if (board.getGameStatus(idPane + addPosition) == "with-ship") {
                                        counterThisHitAttacks++;
                                        if (addPosition > 0) {
                                            addPosition++;
                                        } else {
                                            addPosition--;
                                        }
                                    } else if (addPosition > 0){
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
                                if (((idPane + (addPosition * 10)) < (board.getBoardColumns() * board.getBoardRows())) &&
                                        ((idPane + (addPosition * 10)) >= 0)) {

                                    tmpAttackList.add(idPane + (addPosition * 10));

                                    if (board.getGameStatus(idPane + (addPosition * 10)) == "with-ship") {
                                        counterThisHitAttacks++;
                                        if (addPosition > 0) {
                                            addPosition++;
                                        } else {
                                            addPosition--;
                                        }
                                    } else if (addPosition > 0){
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

                        List<Integer> thisAttackList = new ArrayList<Integer>(tmpAttackList);
                        attackCellsList.removeAll(thisAttackList);
                        attackCellsList.addAll(i, thisAttackList);
                        i += thisAttackList.size();
                    }
                }
                break;

            case "hard":
                this.attackCellsList = randomValues.randomCellsList(board);
                for (int i = 0; i < attackCellsList.size(); i++) {
                    int idPane = (Integer) attackCellsList.get(i);
                    if (board.getGameStatus(idPane) == "with-ship") {
                        Ship thisShip = board.getPanesWithShipsMap().get(idPane);
                        LinkedList<Integer> thisShipPanePositionsList = thisShip.getShipPositionsList();
                        List<Integer> thisShipPanePositionsArray = new ArrayList<Integer>(thisShipPanePositionsList);
                        attackCellsList.removeAll(thisShipPanePositionsArray);
                        attackCellsList.addAll(i, thisShipPanePositionsArray);
                        i += thisShip.getShipSize();
                    }
                }
                break;
        }
    }

    public boolean easyAttack(Board board) {
        boolean isHit = attack.attack(board, attackCellsList.get(0));
        attackCellsList.remove(0);
        return isHit;
    }

    public boolean hardAttack(Board board) {
        boolean isHit = attack.attack(board, attackCellsList.get(0));
        attackCellsList.remove(0);
        return isHit;
    }
}
