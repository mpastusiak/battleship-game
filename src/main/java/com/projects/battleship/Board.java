package com.projects.battleship;

import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.*;

class Board {
    private final int boardColumns;
    private final int boardRows;
    private final int lengthSquare;
    private final String boardStyle;
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

        setBoardRows(board);
        setBoardColumns(board);
        setBoardCells(board);

        this.board = board;
        this.reservedCellsMap = new HashMap<>();
        this.shipsWithViewMap = new HashMap<>();
        this.panesWithShipsMap = new HashMap<>();
        return board;
    }

    private void setBoardRows(GridPane board) {
        for (int i = 0; i < boardRows; i++) {
            RowConstraints row = new RowConstraints(lengthSquare);
            board.getRowConstraints().add(row);
        }
    }

    private void setBoardColumns(GridPane board) {
        for (int i = 0; i < boardColumns; i++) {
            ColumnConstraints column = new ColumnConstraints(lengthSquare);
            board.getColumnConstraints().add(column);
        }
    }

    private void setBoardCells(GridPane board) {
        for (int i = 0; i < boardRows; i++)
            for (int j = 0; j < boardColumns; j++) {
                StackPane pane = new StackPane();
                int idShipPosition = i * 10 + j;
                pane.setId(Integer.toString(idShipPosition));
                pane.getStyleClass().add("cell");
                pane.getProperties().put("game-status", "empty");
                board.add(pane, i, j);
            }
    }

    public int getLengthSquare() {
        return lengthSquare;
    }

    public String getGameStatusOfCell(int idShipPosition) {
        return board.getChildren().get(idShipPosition).getProperties().get("game-status").toString();
    }

    public void setGameStatusOfCell(int idShipPosition, String gameStatus) {
        board.getChildren().get(idShipPosition).getProperties().put("game-status", gameStatus);
    }

    public void setStyleOfCell(int idShipPosition, String nameStyle) {
        board.getChildren().get(idShipPosition).getStyleClass().clear();
        board.getChildren().get(idShipPosition).getStyleClass().add(nameStyle);
    }

    private boolean checkReservedCellOnMap(int keyToBeChecked, String valueToBeChecked) {
        Iterator<Map.Entry<Integer, String>> iterator = reservedCellsMap.entrySet().iterator();
        boolean isReservedCell = false;

        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();

            if (keyToBeChecked == entry.getKey() && (Objects.equals(valueToBeChecked, entry.getValue()) || Objects.equals(valueToBeChecked, "all"))) {
                isReservedCell = true;
            }
        }
        return isReservedCell;
    }

    public boolean setIntoBoard(Ship ship, int shipNewStartPositionY, int shipNewStartPositionX, String type) {
        char shipOrientation = ship.getShipOrientation();
        int shipSize = ship.getShipSize();
        int idNewShipPosition = shipNewStartPositionX * 10 + shipNewStartPositionY;
        int shipOldStartPositionX = ship.getActualXPosition();
        int shipOldStartPositionY = ship.getActualYPosition();

        int colspan = 1;
        int rowspan = 1;
        char newShipOrientation = shipOrientation;

        if (shipOrientation == 'v') {
            rowspan = shipSize;
            newShipOrientation = 'v';
        } else if (shipOrientation == 'h') {
            colspan = shipSize;
            newShipOrientation = 'h';
        }

        HashMap<Integer, String> addCellsMap = new HashMap<>();
        HashMap<Integer, String> removeCellsMap = new HashMap<>();

        if (Objects.equals(type, "add") || Objects.equals(type, "move") || Objects.equals(type, "onlyCheck") || Objects.equals(type, "reserved")) {
            addCellsMap = changePanesOnBoard(ship, shipNewStartPositionY, shipNewStartPositionX, newShipOrientation, "add");

        } else if (Objects.equals(type, "remove")) {
            removeCellsMap = changePanesOnBoard(ship, shipOldStartPositionY, shipOldStartPositionX, newShipOrientation, "remove");

        } else if (Objects.equals(type, "hit-sink")) {
            addCellsMap = changePanesOnBoard(ship, shipNewStartPositionY, shipNewStartPositionX, newShipOrientation, "hit-sink");
        }

        boolean areInBoardRange = checkCellsAreOnBoard(shipNewStartPositionY, shipNewStartPositionX, idNewShipPosition, addCellsMap);

        ImageView imageView;
        boolean fullLoop = false;
        if (addCellsMap.size() > 0 && areInBoardRange && !Objects.equals(type, "onlyCheck")) {
            if (ship.getShipImageView() != null) {
                imageView = ship.getShipImageView();
            } else {
                imageView = ship.setShipImageView();
            }

            if (!Objects.equals(type, "reserved") && !board.getChildren().contains(imageView)) {
                putImageViewOnBoard(shipNewStartPositionY, shipNewStartPositionX, colspan, rowspan, imageView);

                if (!Objects.equals(type, "hit-sink")) {
                    shipsWithViewMap.put(imageView, ship);
                }
            }

            if (Objects.equals(type, "hit-sink")) {
                shipsWithViewMap.remove(imageView);
            }

            ship.setActualXPosition(shipNewStartPositionX);
            ship.setActualYPosition(shipNewStartPositionY);

            reservedCellsMap.putAll(addCellsMap);
            connectShipWithCells(ship, addCellsMap);
            fullLoop = true;

        } else if (Objects.equals(type, "move")) {
            setIntoBoard(ship, shipOldStartPositionY, shipOldStartPositionX, "add");

        } else if (Objects.equals(type, "remove")) {
            ship.getShipPositionsList().clear();
            reservedCellsMap.keySet().removeAll(removeCellsMap.keySet());
            panesWithShipsMap.keySet().removeAll(removeCellsMap.keySet());

        } else if (addCellsMap.size() > 0 && areInBoardRange && Objects.equals(type, "onlyCheck")) {
            fullLoop = true;
        }

        drawContentBoard();
        return fullLoop;
    }

    private void connectShipWithCells(Ship ship, HashMap<Integer, String> addCellsMap) {
        LinkedList<Integer> shipPanePositions = new LinkedList<>();
        Iterator<Map.Entry<Integer, String>> iterator;
        iterator = addCellsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();
            if (Objects.equals(entry.getValue(), "with-ship")) {
                shipPanePositions.addLast(entry.getKey());
                panesWithShipsMap.put(entry.getKey(), ship);
            }
        }
        ship.getShipPositionsList().clear();
        ship.setPaneShipPositionsList(shipPanePositions);
    }

    private void putImageViewOnBoard(int shipNewStartPositionY, int shipNewStartPositionX, int colspan, int rowspan, ImageView imageView) {
        imageView.getProperties().put("gridpane-column", shipNewStartPositionX);
        imageView.getProperties().put("gridpane-row", shipNewStartPositionY);
        imageView.getProperties().put("gridpane-column-span", colspan);
        imageView.getProperties().put("gridpane-row-span", rowspan);

        board.add(imageView, shipNewStartPositionX, shipNewStartPositionY, colspan, rowspan);
    }

    private boolean checkCellsAreOnBoard(int shipNewStartPositionY, int shipNewStartPositionX, int idNewShipPosition, HashMap<Integer, String> addCellsMap) {
        int maxIdCell = getBoardColumns() * getBoardRows() - 1;

        Iterator<Map.Entry<Integer, String>> iterator = addCellsMap.entrySet().iterator();
        boolean areInBoardRange = true;
        while (iterator.hasNext()) {
            Map.Entry<Integer, String>
                    entry
                    = iterator.next();
            if (entry.getKey() > maxIdCell) {
                areInBoardRange = false;
            } else if ((Objects.equals(entry.getValue(), "with-ship")) &&
                    (!Objects.equals(getIdPaneToXYPosition(entry.getKey()).getFirst(), getIdPaneToXYPosition(idNewShipPosition).getFirst())) &&
                    (!Objects.equals(getIdPaneToXYPosition(entry.getKey()).getLast(), getIdPaneToXYPosition(idNewShipPosition).getLast()))) {
                areInBoardRange = false;
            }
        }

        if (shipNewStartPositionX >= getBoardColumns() || shipNewStartPositionY >= getBoardRows()) {
            areInBoardRange = false;
        }
        return areInBoardRange;
    }


    private HashMap<Integer, String> changePanesOnBoard(Ship ship, int shipStartPositionY, int shipStartPositionX, char shipOrientation, String type) {
        int shipSize = ship.getShipSize();
        int idShipPosition = shipStartPositionX * 10 + shipStartPositionY;

        String typeMainCell = "";
        String typeSecondaryCell = "";
        HashMap<Integer, String> changeCells = new HashMap<>();

        if (Objects.equals(type, "add")) {
            typeMainCell = "with-ship";
            typeSecondaryCell = "reserved";
        } else if (Objects.equals(type, "remove")) {
            typeMainCell = "empty";
            typeSecondaryCell = "empty";
        } else if (Objects.equals(type, "hit-sink")) {
            typeMainCell = "hit-sink";
            typeSecondaryCell = "missed";
        }

        if (shipOrientation == 'v') {
            changeCellsOnBoardForVerticalShipPosition(type, shipSize, idShipPosition, typeMainCell, typeSecondaryCell, changeCells);

        } else {
            changeCellsOnBoardForHorizontalShipPosition(type, shipSize, idShipPosition, typeMainCell, typeSecondaryCell, changeCells);
        }
        return changeCells;
    }

    private void changeCellsOnBoardForHorizontalShipPosition(String type, int shipSize, int idShipPosition, String typeMainCell, String typeSecondaryCell, HashMap<Integer, String> changeCells) {
        int secondaryCell3 = (idShipPosition - 10);
        int secondaryCell4 = (idShipPosition + (shipSize * 10));

        for (int j = 0; j < shipSize; j++) {

            int mainCell = idShipPosition + (j * 10);
            int secondaryCell1 = (idShipPosition + (j * 10) - 1);
            int secondaryCell2 = (idShipPosition + (j * 10) + 1);

            if (((checkReservedCellOnMap(mainCell, "all"))
                    || (checkReservedCellOnMap(secondaryCell1, "with-ship") && secondaryCell1 % 10 != 9)
                    || (checkReservedCellOnMap(secondaryCell2, "with-ship") && secondaryCell2 % 10 != 0)
                    || (checkReservedCellOnMap(secondaryCell3, "with-ship"))
                    || (checkReservedCellOnMap(secondaryCell4, "with-ship")))
                    && Objects.equals(type, "add")) {
                changeCells.clear();
                return;
            }

            changeCells.put(mainCell, typeMainCell);

            if (secondaryCell1 >= 0 && secondaryCell1 % 10 != 9) {
                changeCells.put(secondaryCell1, typeSecondaryCell);
            }
            if (secondaryCell2 < boardColumns * boardRows && secondaryCell2 % 10 != 0) {
                changeCells.put(secondaryCell2, typeSecondaryCell);
            }
        }

        if (secondaryCell3 >= 0) {
            changeCells.put(secondaryCell3, typeSecondaryCell);
        }
        if (secondaryCell4 < boardColumns * boardRows) {
            changeCells.put(secondaryCell4, typeSecondaryCell);
        }
    }

    private void changeCellsOnBoardForVerticalShipPosition(String type, int shipSize, int idShipPosition, String typeMainCell, String typeSecondaryCell, HashMap<Integer, String> changeCells) {
        int secondaryCell3 = (idShipPosition - 1);
        int secondaryCell4 = (idShipPosition + shipSize);

        for (int j = 0; j < shipSize; j++) {

            int mainCell = idShipPosition + j;
            int secondaryCell1 = (idShipPosition + j - 10);
            int secondaryCell2 = (idShipPosition + j + 10);

            if (((checkReservedCellOnMap(mainCell, "all"))
                    || (checkReservedCellOnMap(secondaryCell1, "with-ship"))
                    || (checkReservedCellOnMap(secondaryCell2, "with-ship"))
                    || (checkReservedCellOnMap(secondaryCell3, "with-ship") && secondaryCell3 % 10 != 9)
                    || (checkReservedCellOnMap(secondaryCell4, "with-ship") && secondaryCell4 % 10 != 0))
                    && Objects.equals(type, "add")) {
                changeCells.clear();
                return;
            }

            changeCells.put(mainCell, typeMainCell);

            if (secondaryCell1 >= 0) {
                changeCells.put(secondaryCell1, typeSecondaryCell);
            }
            if (secondaryCell2 < boardColumns * boardRows) {
                changeCells.put(secondaryCell2, typeSecondaryCell);
            }
        }

        if (secondaryCell3 >= 0 && secondaryCell3 % 10 != 9) {
            changeCells.put(secondaryCell3, typeSecondaryCell);
        }
        if (secondaryCell4 < boardColumns * boardRows && secondaryCell4 % 10 != 0) {
            changeCells.put(secondaryCell4, typeSecondaryCell);
        }
    }


    public void drawContentBoard() {
        for (int i = 0; i < getBoardRows() * getBoardColumns(); i++) {
            setStyleOfCell(i, "cell");
            setGameStatusOfCell(i, "empty");
        }

        for (Map.Entry<Integer, String> integerStringEntry : reservedCellsMap.entrySet()) {
            setStyleOfCell((Integer) ((Map.Entry) integerStringEntry).getKey(), ((Map.Entry) integerStringEntry).getValue().toString());
            setGameStatusOfCell((Integer) ((Map.Entry) integerStringEntry).getKey(), ((Map.Entry) integerStringEntry).getValue().toString());
        }
    }

    public HashMap<ImageView, Ship> getShipsWithViewMap() {
        return shipsWithViewMap;
    }

    public HashMap<Integer, String> getReservedCallsMaps() {
        return reservedCellsMap;
    }

    public HashMap<Integer, Ship> getPanesWithShipsMap() {
        return panesWithShipsMap;
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

    public void putShipsWithViewMap(ImageView imageView, Ship ship) {
        this.shipsWithViewMap.put(imageView, ship);
    }

    public void removeShipsWithViewMap(ImageView imageView) {
        this.shipsWithViewMap.remove(imageView);
    }

    private LinkedList<Integer> getIdPaneToXYPosition(int idPane) {
        int xPosition = (int) Math.floor(idPane / 10);
        int yPosition = idPane % 10;
        LinkedList<Integer> paneXYPositionList = new LinkedList<>();
        paneXYPositionList.add(xPosition);
        paneXYPositionList.addLast(yPosition);
        return paneXYPositionList;
    }
}