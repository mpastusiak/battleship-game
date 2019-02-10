package com.projects.battleship;

import java.util.LinkedList;
import java.util.ListIterator;

public class Attack {
    public boolean attack(Board board, Integer idPane) {
        boolean isHit = false;
        String paneGameStatus = board.getGameStatus(idPane);
        switch (paneGameStatus) {
            case "with-ship":
                board.putGameStatus(idPane, "hit");
                board.putStyle(idPane, "hit");
                Ship thisShip = board.getPanesWithShipsMap().get(idPane);

                LinkedList<Integer> thisShipPanePositionsList = thisShip.getShipPositionsList();
                ListIterator listIterator = thisShipPanePositionsList.listIterator(0);
                boolean isNotHitHere = false;
                while (listIterator.hasNext()) {
                    if (board.getGameStatus((Integer) listIterator.next()) == "with-ship") {
                        isNotHitHere = true;
                    }
                }

                if (!isNotHitHere) {
                    board.putIntoBoard(thisShip, 0, 0, "remove");
                    board.getShipsWithViewMap().remove(thisShip);
                    board.putIntoBoard(thisShip, thisShip.getActualYPosition(), thisShip.getActualXPosition(), "hit-sink");
                } else {
                    board.getReservedCallsMaps().put(idPane, "hit");
                }

                isHit = true;

                break;
            case "empty":
            case "reserved":
                board.putGameStatus(idPane, "missed");
                board.putStyle(idPane, "missed");
                board.getReservedCallsMaps().put(idPane,"missed");
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
}
