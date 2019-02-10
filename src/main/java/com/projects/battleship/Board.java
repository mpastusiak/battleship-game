package com.projects.battleship;

import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class Board {
    private int boardColumns;
    private int boardRows;
    private int lengthSquare;
    private String boardStyle;
    private GridPane board;
    private HashMap<Integer, String> reservedCellsMap;
    private HashMap<ImageView, Ship> shipsWithViewMap;
    private HashMap<Integer, Ship> panesWithShipsMap;

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
            }
        }
        this.board = board;
        this.reservedCellsMap = new HashMap<>();
        this.shipsWithViewMap = new HashMap<>();
        this.panesWithShipsMap = new HashMap<>();
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
                iterator = reservedCellsMap.entrySet().iterator();

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

        if(type == "add" || type == "move" || type == "onlyCheck" || type == "reserved") {
            addCellsMap = changePanesOnBoard(ship, shipNewStartPositionY, shipNewStartPositionX, newShipOrientation, "add");
        } else if (type == "remove") {
            removeCellsMap = changePanesOnBoard(ship, shipOldStartPositionY, shipOldStartPositionX, newShipOrientation, "remove");
        } else if (type == "hit-sink") {
            addCellsMap = changePanesOnBoard(ship, shipNewStartPositionY, shipNewStartPositionX, newShipOrientation, "hit-sink");
        }

        int maxIdCell = getBoardColumns() * getBoardRows() - 1;
        int firstKey = idNewShipPosition;

        Iterator<Map.Entry<Integer, String>>
                iterator = addCellsMap.entrySet().iterator();
        boolean areInBoardRange = true;
        while (iterator.hasNext()) {
            Map.Entry<Integer, String>
                    entry
                    = iterator.next();
            if (entry.getKey() > maxIdCell) {
                areInBoardRange = false;
            } else if ((entry.getValue() == "with-ship") &&
                    (getIdPaneToXYPosition(entry.getKey()).getFirst() != getIdPaneToXYPosition(firstKey).getFirst()) &&
                            (getIdPaneToXYPosition(entry.getKey()).getLast() != getIdPaneToXYPosition(firstKey).getLast())) {
                areInBoardRange = false;
            }
        }

        if (shipNewStartPositionX >= getBoardColumns() || shipNewStartPositionY >= getBoardRows()) {
            areInBoardRange = false;
        }

        ImageView imageView;
        if (addCellsMap.size() > 0 && areInBoardRange && type != "onlyCheck") {
            if (ship.getShipImageView() != null) {
                imageView = ship.getShipImageView();
            } else {
                imageView = ship.setShipImageView();
            }

            if (type != "reserved" && !board.getChildren().contains(imageView)) {
                imageView.getProperties().put("gridpane-column", shipNewStartPositionX);
                imageView.getProperties().put("gridpane-row", shipNewStartPositionY);
                imageView.getProperties().put("gridpane-column-span", colspan);
                imageView.getProperties().put("gridpane-row-span", rowspan);

                board.add(imageView, shipNewStartPositionX, shipNewStartPositionY, colspan, rowspan);
                if (type != "hit-sink") {
                    shipsWithViewMap.put(imageView, ship);
                }
            }

            if (type == "hit-sink") {
                shipsWithViewMap.remove(imageView);
            }

            ship.setActualXPosition(shipNewStartPositionX);
            ship.setActualYPosition(shipNewStartPositionY);

            reservedCellsMap.putAll(addCellsMap);

            LinkedList<Integer> shipPanePositions = new LinkedList<>();
            iterator = addCellsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, String> entry = iterator.next();
                if (entry.getValue() == "with-ship") {
                    shipPanePositions.addLast(entry.getKey());
                    panesWithShipsMap.put(entry.getKey(), ship);
                }
            }
            ship.getShipPositionsList().clear();
            ship.setPaneShipPositionsList(shipPanePositions);

            fullLoop = true;

        } else if (type == "move") {
            putIntoBoard(ship,shipOldStartPositionY,shipOldStartPositionX,"add");

        } else if (type == "remove") {
            ship.getShipPositionsList().clear();
            reservedCellsMap.keySet().removeAll(removeCellsMap.keySet());
            panesWithShipsMap.keySet().removeAll(removeCellsMap.keySet());
        } else if (addCellsMap.size() > 0 && areInBoardRange && type == "onlyCheck") {
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
        } else if (type == "hit-sink") {
            typeMainCell = "hit-sink";
            typeSecondaryCell = "missed";
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

        Iterator iterator = reservedCellsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            putStyle((Integer)pair.getKey(), pair.getValue().toString());
            putGameStatus((Integer)pair.getKey(), pair.getValue().toString());
        }
    }

    public HashMap<ImageView, Ship> getShipsWithViewMap() {
        return shipsWithViewMap;
    }

    public HashMap<Integer, String> getReservedCallsMaps() {
        return reservedCellsMap;
    }

    public HashMap<Integer, Ship> getPanesWithShipsMap() { return panesWithShipsMap; }

    public int getBoardColumns() {
        return boardColumns;
    }

    public int getBoardRows() {
        return boardRows;
    }

    public GridPane getBoard() {
        return board;
    }

    public void putShipsWithViewMap(ImageView imageView, Ship ship) {
        this.shipsWithViewMap.put(imageView, ship);
    }

    public void removeShipsWithViewMap(ImageView imageView) {
        this.shipsWithViewMap.remove(imageView);
    }

    public LinkedList<Integer> getIdPaneToXYPosition(int idPane) {
        int xPosition = (int) Math.floor(idPane / 10);
        int yPosition = (int) idPane % 10;
        LinkedList<Integer> paneXYPositionList = new LinkedList<>();
        paneXYPositionList.add(xPosition);
        paneXYPositionList.addLast(yPosition);
        return paneXYPositionList;
    }

}
