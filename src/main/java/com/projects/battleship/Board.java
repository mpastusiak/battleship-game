package com.projects.battleship;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

public class Board {
    private int boardColumns;
    private int boardRows;
    private int lengthSquare;
    private String boardStyle;
    private int boardPositionX;
    private int boardPositionY;

    public Board(int boardColumns, int boardRows, int lengthSquare, String boardStyle) {
        this.boardColumns = boardColumns;
        this.boardRows = boardRows;
        this.lengthSquare = lengthSquare;
        this.boardStyle = boardStyle;
    }

    public GridPane doBoard() {
        GridPane board = new GridPane();
        board.getStyleClass().add(boardStyle);

        for(int i = 0; i < boardColumns; i++) {
            ColumnConstraints column = new ColumnConstraints(lengthSquare);
            board.getColumnConstraints().add(column);
        }

        for(int i = 0; i < boardRows; i++) {
            RowConstraints row = new RowConstraints(lengthSquare);
            board.getRowConstraints().add(row);
        }

        board.getStyleClass().add(boardStyle);

        for (int i = 0; i < boardColumns; i++) {
            for (int j = 0; j < boardRows; j++) {
                Pane pane = new Pane();
                pane.setId("c" + i + "r" + j);
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
        return board;
    }

    public void doShip() {

    }

    public int getLengthSquare() {
        return lengthSquare;
    }


}
