package com.projects.battleship;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Board {
    private int boardColumns;
    private int boardRows;
    private int lengthSquare;
    private String boardStyle;
    private GridPane board;
    private HashMap<Integer, String> reservedCells;
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
                StackPane pane = new StackPane();
                Integer idShipPosition = i * 10 + j;
                pane.setId(idShipPosition.toString());
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
        this.reservedCells = new HashMap<>();
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
        board.getChildren().get(idShipPosition).getStyleClass().clear();
        board.getChildren().get(idShipPosition).getStyleClass().add(nameStyle);
    }

    public boolean checkKeyOnMap(int keyToBeChecked, String valueToBeChecked) {

        Iterator<Map.Entry<Integer, String>>
                iterator = reservedCells.entrySet().iterator();

        boolean isKeyPresent = false;

        while (iterator.hasNext()) {

            Map.Entry<Integer, String>
                    entry
                    = iterator.next();

            if (keyToBeChecked == entry.getKey() && (valueToBeChecked == entry.getValue()
             || (valueToBeChecked == "all" && valueToBeChecked != "empty"))) {

                isKeyPresent = true;
            }
        }
        return isKeyPresent;

    }



    public boolean putIntoBoard(Ship ship, int shipNewStartPositionY, int shipNewStartPositionX, String type) {
        char shipOrientation = ship.getShipOrientation();
        int shipSize = ship.getShipSize();
        int idNewShipPosition = shipNewStartPositionX * 10 + shipNewStartPositionY;
        int shipOldStartPositionX = ship.getActualXPosition();
        int shipOldStartPositionY = ship.getActualYPosition();
        int idOldShipPosition = shipOldStartPositionX * 10 + shipOldStartPositionY;

        boolean fullLoop = false;

        int colspan = 1;
        int rowspan = 1;

        char newShipOrientation = shipOrientation;

        if (shipOrientation == 'v') {
            colspan = 1;
            rowspan = shipSize;
            newShipOrientation = 'v';
        } else if (shipOrientation == 'h') {
            colspan = shipSize;
            rowspan = 1;
            newShipOrientation = 'h';
        }

        HashMap<Integer, String> addCellsMap = new HashMap<>();
        HashMap<Integer, String> removeCellsMap = new HashMap<>();

        if(type == "add" || type == "move" || type == "onlyCheck") {
            addCellsMap = changePanesOnBoard(ship, shipNewStartPositionY, shipNewStartPositionX, newShipOrientation, "add");
        } else if (type == "remove") {
            removeCellsMap = changePanesOnBoard(ship, shipOldStartPositionY, shipOldStartPositionX, newShipOrientation, "remove");
        }

        int maxIdCell = getBoardColumns() * getBoardRows() - 1;
        int tmpKey = -1;

        Iterator<Map.Entry<Integer, String>>
                iterator = addCellsMap.entrySet().iterator();
        boolean areInBoardRange = true;
        while (iterator.hasNext()) {
            Map.Entry<Integer, String>
                    entry
                    = iterator.next();
            if (entry.getKey() > maxIdCell) {
                areInBoardRange = false;
            } else if ((entry.getValue() == "with-ship")
                    && (tmpKey > -1)
                    && (entry.getKey() - tmpKey == 1)
                    && (entry.getKey() % 10 == 0)) {
                areInBoardRange = false;
            }
            tmpKey = entry.getKey();
        }

        ImageView imageView;
        if (addCellsMap.size() > 0 && areInBoardRange && type != "onlyCheck") {
            if (ship.getShipImageView() != null) {
                imageView = ship.getShipImageView();
            } else {
                imageView = ship.setShipImageView();
            }
            imageView.getProperties().put("gridpane-column", shipNewStartPositionX);
            imageView.getProperties().put("gridpane-row", shipNewStartPositionY);
            imageView.getProperties().put("gridpane-column-span", colspan);
            imageView.getProperties().put("gridpane-row-span", rowspan);

            board.add(imageView, shipNewStartPositionX, shipNewStartPositionY, colspan, rowspan);

            ship.setActualXPosition(shipNewStartPositionX);
            ship.setActualYPosition(shipNewStartPositionY);

            fleetMap.put(imageView, ship);
            reservedCells.putAll(addCellsMap);

            fullLoop = true;
        } else if (type == "move") {
            putIntoBoard(ship,shipOldStartPositionY,shipOldStartPositionX,"add");
        } else if (type == "remove") {
            reservedCells.keySet().removeAll(removeCellsMap.keySet());
        } else if (type == "onlyCheck") {
            fullLoop = true;
        }

        drawContentBoard();
        return fullLoop;
    }



    public HashMap<Integer, String> changePanesOnBoard(Ship ship, int shipStartPositionY, int shipStartPositionX, char shipOrientation, String type) {
        int shipSize = ship.getShipSize();
        int idShipPosition = shipStartPositionX * 10 + shipStartPositionY;

        boolean fullLoop = false;
        int colspan;
        int rowspan;
        String typeMainCell = "";
        String typeSecondaryCell = "";
        HashMap<Integer, String> changeCells = new HashMap<>();

        if (type == "add") {
            typeMainCell = "with-ship";
            typeSecondaryCell = "reserved";
        } else if (type == "remove") {
            typeMainCell = "empty";
            typeSecondaryCell = "empty";
        }

        outerloop:
        if (shipOrientation == 'v') {
            colspan = 1;
            rowspan = shipSize;

            int secondaryCell3 = (idShipPosition - 1);
            int secondaryCell4 = (idShipPosition + shipSize);

            for(int j = 0; j < shipSize; j++ ) {

                int mainCell = idShipPosition + j;
                int secondaryCell1 = (idShipPosition + j - 10);
                int secondaryCell2 = (idShipPosition + j + 10);

                if (((checkKeyOnMap(mainCell, "all"))
                        || (checkKeyOnMap(secondaryCell1, "with-ship"))
                        || (checkKeyOnMap(secondaryCell2, "with-ship"))
                        || (checkKeyOnMap(secondaryCell3, "with-ship") && secondaryCell3 % 10 != 9)
                        || (checkKeyOnMap(secondaryCell4, "with-ship") && secondaryCell4 % 10 != 0))
                        && type == "add") {
                    changeCells.clear();
                    break outerloop;
                }

                changeCells.put(mainCell, typeMainCell);

                if (secondaryCell1 >= 0) {
                    changeCells.put(secondaryCell1, typeSecondaryCell);
                }
                if(secondaryCell2 < boardColumns*boardRows) {
                    changeCells.put(secondaryCell2, typeSecondaryCell);
                }
            }

            if (secondaryCell3 >= 0 && secondaryCell3 % 10 != 9) {
                changeCells.put(secondaryCell3, typeSecondaryCell);
            }
            if (secondaryCell4 < boardColumns*boardRows && secondaryCell4 % 10 != 0) {
                changeCells.put(secondaryCell4, typeSecondaryCell);
            }

        } else {
            colspan = shipSize;
            rowspan = 1;

            int secondaryCell3 = (idShipPosition - 10);
            int secondaryCell4 = (idShipPosition + (shipSize * 10));

            for(int j = 0; j < shipSize; j++ ) {

                int mainCell = idShipPosition + (j * 10);
                int secondaryCell1 = (idShipPosition + (j * 10) - 1);
                int secondaryCell2 = (idShipPosition + (j * 10) + 1);

                if (((checkKeyOnMap(mainCell, "all"))
                        || (checkKeyOnMap(secondaryCell1, "with-ship") && secondaryCell1 % 10 != 9)
                        || (checkKeyOnMap(secondaryCell2, "with-ship") && secondaryCell2 % 10 != 0)
                        || (checkKeyOnMap(secondaryCell3, "with-ship"))
                        || (checkKeyOnMap(secondaryCell4, "with-ship")))
                        && type == "add") {
                    changeCells.clear();
                    break outerloop;
                }

                changeCells.put(mainCell, typeMainCell);

                if (secondaryCell1 >= 0 && secondaryCell1 % 10 != 9) {
                    changeCells.put(secondaryCell1, typeSecondaryCell);
                }
                if(secondaryCell2 < boardColumns*boardRows && secondaryCell2 % 10 != 0) {
                    changeCells.put(secondaryCell2, typeSecondaryCell);
                }
            }

            if (secondaryCell3 >= 0) {
                changeCells.put(secondaryCell3, typeSecondaryCell);
            }
            if (secondaryCell4 < boardColumns*boardRows) {
                changeCells.put(secondaryCell4, typeSecondaryCell);
            }
        }

        return changeCells;
    }



    public void drawContentBoard() {
        for (int i = 0; i < getBoardRows() * getBoardColumns(); i++){
            putStyle(i,"cell");
            putGameStatus(i, "empty");
        }

        Iterator iterator = reservedCells.entrySet().iterator();
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
        return reservedCells;
    }

    public int getBoardColumns() {
        return boardColumns;
    }

    public int getBoardRows() {
        return boardRows;
    }

    public GridPane getBoard() {
        return board;
    }

    public void putFleetMap(ImageView imageView, Ship ship) {
        this.fleetMap.put(imageView, ship);
    }

    public void removeFleetMap(ImageView imageView) {
        this.fleetMap.remove(imageView);
    }

}
