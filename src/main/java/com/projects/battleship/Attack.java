package com.projects.battleship;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;

class Attack {
    public boolean attack(Board board, Integer idPane) {
        boolean isHit = false;
        String paneGameStatus = board.getGameStatusOfCell(idPane);
        switch (paneGameStatus) {
            case "with-ship":
                isHit = true;
                attackWithHit(board, idPane);
                break;

            case "empty":
            case "reserved":
                board.setGameStatusOfCell(idPane, "missed");
                board.setStyleOfCell(idPane, "missed");
                board.getReservedCallsMaps().put(idPane, "missed");
                isHit = false;
                break;

            case "hit":
            case "hit-sink":
            case "missed":
                isHit = true;

            default: {
                break;
            }
        }
        return isHit;
    }

    private void attackWithHit(Board board, Integer idPane) {
        board.setGameStatusOfCell(idPane, "hit");
        board.setStyleOfCell(idPane, "hit");
        Ship thisShip = board.getPanesWithShipsMap().get(idPane);

        boolean isHitYet = findCellsThatCanHit(board, thisShip);

        if (!isHitYet) {
            board.setIntoBoard(thisShip, 0, 0, "remove");
            board.getShipsWithViewMap().remove(thisShip);
            board.setIntoBoard(thisShip, thisShip.getActualYPosition(), thisShip.getActualXPosition(), "hit-sink");
        } else {
            board.getReservedCallsMaps().put(idPane, "hit");
        }
    }

    private boolean findCellsThatCanHit(Board board, Ship thisShip) {
        boolean isHitYet = false;

        LinkedList<Integer> thisShipPanePositionsList = thisShip.getShipPositionsList();
        ListIterator listIterator = thisShipPanePositionsList.listIterator(0);
        while (listIterator.hasNext()) {
            if (Objects.equals(board.getGameStatusOfCell((Integer) listIterator.next()), "with-ship")) {
                isHitYet = true;
            }
        }
        return isHitYet;
    }
}