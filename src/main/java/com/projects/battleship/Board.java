package com.projects.battleship;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Board {
    private int boardColumns;
    private int boardRows;
    private int lengthSquare;
    private String boardStyle;
    private GridPane board;
    private HashMap<Integer, String> reservedCalls;
    private HashMap<ImageView, Ship> fleetMap;

    public Board(int boardColumns, int boardRows, int lengthSquare, String boardStyle) {
        this.boardColumns = boardColumns;
        this.boardRows = boardRows;
        this.lengthSquare = lengthSquare;
        this.boardStyle = boardStyle;
    }

    public GridPane doBoard() {
        GridPane board = new GridPane();
        board.getStyleClass().add(boardStyle);

        for(int i = 0; i < boardRows; i++) {
            RowConstraints row = new RowConstraints(lengthSquare);
            board.getRowConstraints().add(row);
        }

        for(int i = 0; i < boardColumns; i++) {
            ColumnConstraints column = new ColumnConstraints(lengthSquare);
            board.getColumnConstraints().add(column);
        }

        board.getStyleClass().add(boardStyle);

        for (int i = 0; i < boardRows; i++) {
            for (int j = 0; j < boardColumns; j++) {
                Pane pane = new Pane();
                pane.setId("c" + j + "r" + i);
                pane.getStyleClass().add("cell");
                pane.getProperties().put("game-status","empty");
                board.add(pane, i, j);
                if (i == 0) {
                    pane.getStyleClass().add("first-column");
                }
                if (j == 0) {
                    pane.getStyleClass().add("first-row");
                }
                if (j == 0 && i == 0) {
                    pane.getStyleClass().add("first-cell");
                }
            }
        }
        this.board = board;
        this.reservedCalls = new HashMap<>();
        this.fleetMap = new HashMap<>();
        return board;
    }

    public int getLengthSquare() {
        return lengthSquare;
    }

    public String getGameStatus(int idShipPosition) {
        return board.getChildren().get(idShipPosition).getProperties().get("game-status").toString();
    }

    public void putGameStatus(int idShipPosition, String gameStatus) {
        board.getChildren().get(idShipPosition).getProperties().put("game-status", gameStatus);
    }

    public void putStyle(int idShipPosition, String nameStyle) {
        board.getChildren().get(idShipPosition).getStyleClass().add(nameStyle);
    }

    public boolean checkKeyOnMap(int keyToBeChecked) {

        Iterator<Map.Entry<Integer, String>>
                iterator = reservedCalls.entrySet().iterator();

        boolean isKeyPresent = false;

        while (iterator.hasNext()) {

            Map.Entry<Integer, String>
                    entry
                    = iterator.next();

            if (keyToBeChecked == entry.getKey()) {

                isKeyPresent = true;
            }
        }
        return isKeyPresent;

    }

    public boolean putIntoBoard(Ship ship, int shipStartPositionY, int shipStartPositionX) {
        char shipOrientation = ship.getShipOrientation();
        int shipSize = ship.getShipSize();
        int idShipPosition = shipStartPositionX * 10 + shipStartPositionY;

        boolean fullLoop = false;
        int colspan;
        int rowspan;
        HashMap<Integer, String> needCells = new HashMap<>();

        if (shipOrientation == 'v') {
            colspan = 1;
            rowspan = shipSize;

            for(int j = 0; j < shipSize; j++ ) {
                needCells.put(idShipPosition + j, "with-ship");

                if (checkKeyOnMap(idShipPosition + j) ||
                        checkKeyOnMap(idShipPosition + j - 10) ||
                        checkKeyOnMap(idShipPosition + j + 10)) {
                    fullLoop = false;
                    break;
                } else {
                    fullLoop = true;
                }

                if ((idShipPosition + j - 10) >= 0) {
                    needCells.put(idShipPosition + j - 10, "reserved");
                }
                if((idShipPosition + j + 10) < boardColumns*boardRows) {
                    needCells.put(idShipPosition + j + 10, "reserved");
                }
            }

            if ((idShipPosition - 1) >= 0 && (idShipPosition - 1) % 10 != 9) {
                needCells.put(idShipPosition - 1, "reserved");
            }
            if ((idShipPosition + shipSize) < boardColumns*boardRows && (idShipPosition + shipSize) % 10 != 0) {
                needCells.put(idShipPosition + shipSize, "reserved");
            }

        } else {
            colspan = shipSize;
            rowspan = 1;
            for(int j = 0; j < shipSize; j++ ) {
                needCells.put(idShipPosition + (j * 10), "with-ship");

                if (checkKeyOnMap(idShipPosition + (j * 10)) ||
                        checkKeyOnMap(idShipPosition + (j * 10) - 1) ||
                        checkKeyOnMap(idShipPosition + (j * 10) + 1)) {
                    fullLoop = false;
                    break;
                } else {
                    fullLoop = true;
                }

                if ((idShipPosition + (j * 10) - 1) >= 0 && (idShipPosition + (j * 10) - 1) % 10 != 9) {
                    needCells.put(idShipPosition + (j * 10) - 1, "reserved");
                }
                if((idShipPosition + (j * 10) + 1) < boardColumns*boardRows && (idShipPosition + (j * 10) + 1) % 10 != 0) {
                    needCells.put(idShipPosition + (j * 10) + 1, "reserved");
                }
            }

            if ((idShipPosition - 10) >= 0) {
                needCells.put(idShipPosition - 10, "reserved");
            }
            if ((idShipPosition + (shipSize * 10)) < boardColumns*boardRows) {
                needCells.put(idShipPosition + (shipSize * 10), "reserved");
            }
        }

        if (fullLoop) {
            ImageView imageView = ship.getShipImageView();
            imageView.setId("ship" + idShipPosition);
            board.add(imageView, shipStartPositionX, shipStartPositionY, colspan, rowspan);
            fleetMap.put(imageView, ship);
            System.out.println(imageView.getProperties());
            reservedCalls.putAll(needCells);
        }

        return fullLoop;
    }

    public void drawContentBoard() {
        Iterator iterator = reservedCalls.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            putStyle((Integer)pair.getKey(), pair.getValue().toString());
            putGameStatus((Integer)pair.getKey(), pair.getValue().toString());
            // iterator.remove();
        }
    }

    public HashMap<ImageView, Ship> getFleetMap() {
        return fleetMap;
    }

    public HashMap<Integer, String> getReservedCalls() {
        return reservedCalls;
    }

}
