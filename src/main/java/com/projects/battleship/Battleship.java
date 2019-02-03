package com.projects.battleship;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.Math.abs;

public class Battleship extends Application {

    private Board bigBoard;
    private final DataFormat buttonFormat = new DataFormat("MyButton");
    private Node draggingButton;


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

            HBox hBox = new HBox(25);
            hBox.setAlignment(Pos.CENTER);

            int lengthSquare = 30;
            int boardSize = 10;

            bigBoard = new Board(boardSize, boardSize, lengthSquare, "big-board");
            GridPane bigPane = bigBoard.doBoard();
            bigPane.setId("bigPane");

            int ships = 5;
            RandomValues generator = new RandomValues();
            int shipMaxPositionX;
            int shipMaxPositionY;
            Map<Integer, Ship> playerFleet = new HashMap<>();

            for (int i = ships; i > 0; i--) {
                char shipRandomOrientation = generator.randomOrientation();

                if (shipRandomOrientation == 'v') {
                    shipMaxPositionX = boardSize;
                    shipMaxPositionY = boardSize - i;
                } else {
                    shipMaxPositionX = boardSize - i;
                    shipMaxPositionY = boardSize;
                }

                Ship ship = new Ship(i, lengthSquare, shipRandomOrientation);

                int shipPositionX = generator.randomValue(shipMaxPositionX);
                int shipPositionY = generator.randomValue(shipMaxPositionY);

                boolean checkCompletePut = bigBoard.putIntoBoard(ship, shipPositionY, shipPositionX, "add");
                if (checkCompletePut != true) {
                    i++;
                }
            }

            bigBoard.drawContentBoard();

            for( Node child: bigPane.getChildrenUnmodifiable()) {

                if( child instanceof ImageView) {
                    ImageView imageView = (ImageView) child;
                    dragButton(imageView);
                    clickShip(imageView);
                }

                if( child instanceof StackPane) {
                    StackPane pane = (StackPane) child;
                    addDropHandling(pane);
                }
            }

            Board smallBoard = new Board(10, 10, 20, "smaller-board");
            GridPane smallPane = smallBoard.doBoard();

            Pane leftPlane = new Pane();
            leftPlane.getChildren().addAll(bigPane);
            Pane rightPlane = new Pane();
            rightPlane.getChildren().add(smallPane);
            hBox.getChildren().addAll(leftPlane, rightPlane);

            LinearGradient backgroundGradient = new LinearGradient(0.5, 0.5, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.AQUA), new Stop(1, Color.BLUE));
            BackgroundFill backgroundFill = new BackgroundFill(backgroundGradient, CornerRadii.EMPTY, new Insets(0, 0, 0, 0));
            Background background = new Background(backgroundFill);

            Scene scene = new Scene(hBox, 900, 600, Color.BLACK);
            scene.getStylesheets().add("battleship-styles.css");
            hBox.setBackground(background);
            hBox.setMinSize(900, 600);

            primaryStage.setTitle("Battleship");
            primaryStage.setScene(scene);
            primaryStage.setHeight(600);
            primaryStage.setWidth(900);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        } }

    private void clickShip(ImageView shipImageView) {
        shipImageView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                HashMap<ImageView, Ship> tmpMap = bigBoard.getFleetMap();
                Ship thisShip = (Ship) tmpMap.get(shipImageView);

                char thisShipOrientation = thisShip.getShipOrientation();
                int shipStartPositionX = thisShip.getActualXPosition();
                int shipStartPositionY = thisShip.getActualYPosition();

                boolean checkCompletePut = bigBoard.putIntoBoard(thisShip, 0, 0, "remove");
                bigBoard.getBoard().getChildren().remove(shipImageView);

                if (thisShipOrientation == 'v') {
                    thisShip.setShipOrientation('h');
                } else {
                    thisShip.setShipOrientation('v');
                }

                checkCompletePut = false;
                boolean changeVector = false;
                for (int i = 0; !checkCompletePut; i++) {
                    System.out.println(i);
                   if (thisShipOrientation == 'v' || (thisShipOrientation == 'h' && changeVector == true)) {
                       checkCompletePut = bigBoard.putIntoBoard(thisShip, shipStartPositionY, shipStartPositionX + i, "add");
                       if ((i == bigBoard.getBoardRows() - 1) && !changeVector) {
                           changeVector = true;
                           i = 0;
                       } else {
                           break;
                       }
                   } else if (thisShipOrientation == 'h' || (thisShipOrientation == 'v' && changeVector == true)) {
                       checkCompletePut = bigBoard.putIntoBoard(thisShip, shipStartPositionY + i, shipStartPositionX, "add");
                       if ((i == bigBoard.getBoardColumns() - 1) && !changeVector) {
                           changeVector = true;
                           i = 0;
                       } else {
                           break;
                       }
                   }
                }

                while (!checkCompletePut) {
                    RandomValues generator = new RandomValues();
                    int newShipStartPositionX = generator.randomValue(bigBoard.getBoardColumns());
                    int newShipStartPositionY = generator.randomValue(bigBoard.getBoardRows());
                    checkCompletePut = bigBoard.putIntoBoard(thisShip, newShipStartPositionY, newShipStartPositionX, "add");
                    System.out.println(checkCompletePut + " | " + newShipStartPositionX + " | " + newShipStartPositionY);
                }
            }
        });
    }


        private void dragButton(ImageView b) {
        b.setOnDragDetected(e -> {
            Dragboard db = b.startDragAndDrop(TransferMode.MOVE);
            db.setDragView(b.snapshot(null, null));
            ClipboardContent cc = new ClipboardContent();
            cc.put(buttonFormat, " ");
            db.setContent(cc);
            draggingButton = b;

            HashMap<ImageView, Ship> tmpMap = bigBoard.getFleetMap();
            Ship thisShip = (Ship) tmpMap.get(draggingButton);

            boolean checkCompletePut = bigBoard.putIntoBoard(thisShip, 0, 0, "remove");
        });
    }

    private void addDropHandling(StackPane pane) {
        pane.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(buttonFormat) && draggingButton != null) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });

        pane.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();

            if (db.hasContent(buttonFormat)) {
                ((Pane)draggingButton.getParent()).getChildren().remove(draggingButton);

                Integer idPane = Integer.parseInt(pane.getId());

                int shipStartPositionX = (int) Math.floor(idPane / 10);
                int shipStartPositionY = (int) idPane % 10;

                HashMap<ImageView, Ship> tmpMap = bigBoard.getFleetMap();
                Ship thisShip = (Ship) tmpMap.get(draggingButton);

                boolean checkCompletePut = bigBoard.putIntoBoard(thisShip, shipStartPositionY, shipStartPositionX, "move");
                System.out.println(bigBoard.getReservedCalls().toString());
                bigBoard.drawContentBoard();
                if (checkCompletePut) {
                    e.setDropCompleted(true);
                } else {
                    e.setDropCompleted(false);
                }
                draggingButton = null;
            }
        });

    }
}