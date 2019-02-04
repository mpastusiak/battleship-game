package com.projects.battleship;

import javafx.scene.Group;
import javafx.scene.text.*;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static java.lang.Math.abs;

public class Battleship extends Application {

    private Board bigBoard;
    private LinkedList<TextField> textFieldShips = new LinkedList<>();
    private final DataFormat buttonFormat = new DataFormat("MyButton");
    private Node draggingButton;
    private RandomValues generator = new RandomValues();
    private boolean iDoSomething = false;
    private Label textInformation = new Label();

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

            HBox hBox = new HBox(25);
            hBox.setAlignment(Pos.CENTER);

            GridPane leftPlane = new GridPane();

            Label optionsLabel = new Label();
            optionsLabel.setText("Options");
            leftPlane.add(optionsLabel,0,0,3,1);

            for (int i = 1; i <= 5; i++) {
                Label textShip = new Label();
                textShip.setText("Number of " + i + " size ship:");
                leftPlane.add(textShip, 0, (i*2)-1, 3, 1);

                Button buttonMinusShip = new Button();
                buttonMinusShip.setText("-");
                clickButton(buttonMinusShip);
                leftPlane.add(buttonMinusShip, 0,(i*2),1,1);

                TextField textFieldShip = new TextField();
                textFieldShip.setText("1");
                textFieldShip.setEditable(false);
                textFieldShips.add(textFieldShip);
                leftPlane.add(textFieldShips.getLast(), 1,(i*2),1,1);

                Button buttonPlusShip = new Button();
                buttonPlusShip.setText("+");
                clickButton(buttonPlusShip);
                leftPlane.add(buttonPlusShip, 2,(i*2),1,1);
            }

            Button buttonRefresh = new Button();
            buttonRefresh.setText("Confirm");
            clickRefresh(buttonRefresh);
            leftPlane.add(buttonRefresh, 0, 11, 3, 1);

            textInformation.setText("We can start, when you are ready!");
            textInformation.getStyleClass().add("neutral-information");
            leftPlane.add(textInformation, 0, 12,3,1);

            int lengthSquare = 30;
            int boardSize = 10;

            bigBoard = new Board(boardSize, boardSize, lengthSquare, "big-board");
            GridPane bigPane = bigBoard.doBoard();
            bigPane.setId("bigPane");

            refreshBoard();

            /*
            RandomValues generator = new RandomValues();
            Map<Integer, Ship> playerFleet = new HashMap<>();

            for (int i = 0; i < textFieldShips.size(); i++) {
                for (int j = 0; j < Integer.valueOf(textFieldShips.get(i).getText()); j++) {

                    Ship randomShip = generator.randomShip(bigBoard, i + 1);

                    boolean checkCompletePut = bigBoard.putIntoBoard(randomShip, randomShip.getActualYPosition(), randomShip.getActualXPosition(), "add");
                    if (checkCompletePut != true) {
                        j--;
                    }
                }
            }
            */

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

            Pane centerPlane = new Pane();
            centerPlane.getChildren().addAll(bigPane);
            Pane rightPlane = new Pane();
            rightPlane.getChildren().addAll(smallPane);
            hBox.getChildren().addAll(leftPlane, centerPlane, rightPlane);

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

        private void refreshBoard() {
            for (int i = 0; i < textFieldShips.size(); i++) {
                for (int j = 0; j < Integer.valueOf(textFieldShips.get(i).getText()); j++) {

                    Ship randomShip = generator.randomShip(bigBoard, i + 1);

                    boolean checkCompletePut = bigBoard.putIntoBoard(randomShip, randomShip.getActualYPosition(), randomShip.getActualXPosition(), "add");
                    if (checkCompletePut != true) {
                        j--;
                    }
                }
            }
        }

        private boolean checkFreeSpace(int newShipSize) {
            double occupiedSpace = newShipSize;
            for (int i = 0; i < textFieldShips.size(); i++) {
                occupiedSpace += Integer.valueOf(textFieldShips.get(i).getText()) * (i + 1);
            }
            double occupiedSpacePercent = occupiedSpace / (bigBoard.getBoardRows() * bigBoard.getBoardColumns());
            if (occupiedSpacePercent <= 0.30) {
                return true;
            } else {
                return false;
            }
        }

        private void clickRefresh(Button button) {
        button.setOnMousePressed(e -> {
            bigBoard.getBoard().getChildren().clear();
            refreshBoard();
            bigBoard.drawContentBoard();
        });
        }


    private void disablePane(GridPane pane, boolean setDisable){
        for( Node child: pane.getChildrenUnmodifiable()) {
            child.setDisable(setDisable);
        }
    }


        private void clickButton(Button button) {
        button.setOnMousePressed(e -> {
            if (!iDoSomething) {
                iDoSomething = true;
                String rowButton = button.getProperties().get("gridpane-row").toString();
                int shipSize = Integer.valueOf(rowButton) / 2;
                TextField thisTextField = textFieldShips.get(shipSize - 1);
                int getActualValue = Integer.valueOf(thisTextField.getText());

                if (button.getText() == "-" && getActualValue > 0) {
                    thisTextField.setText(Integer.toString(getActualValue - 1));

                    Iterator<Map.Entry<ImageView, Ship>>
                            iterator = bigBoard.getFleetMap().entrySet().iterator();
                    outerloop:
                    while (iterator.hasNext()) {
                        Map.Entry<ImageView, Ship> entry = iterator.next();
                        if (entry.getValue().getShipSize() == shipSize) {
                            boolean checkCompletePut = bigBoard.putIntoBoard(entry.getValue(), 0, 0, "remove");
                            bigBoard.removeFleetMap(entry.getKey());
                            bigBoard.getBoard().getChildren().remove(entry.getKey());
                            iDoSomething = false;
                            break outerloop;
                        }
                    }
                    textInformation.setText("We can start, when you are ready!");
                    textInformation.getStyleClass().clear();
                    textInformation.getStyleClass().add("neutral-information");
                } else if (button.getText() == "+" && checkFreeSpace(shipSize)) {
                    boolean isAdd = false;
                    long startTime = System.nanoTime();
                    while (!isAdd) {
                        Ship randomShip = generator.randomShip(bigBoard, shipSize);
                        isAdd = bigBoard.putIntoBoard(randomShip, randomShip.getActualYPosition(), randomShip.getActualXPosition(), "add");
                        dragButton(randomShip.getShipImageView());
                        clickShip(randomShip.getShipImageView());
                        if (isAdd) {
                            thisTextField.setText(Integer.toString(getActualValue + 1));
                            bigBoard.putFleetMap(randomShip.getShipImageView(), randomShip);
                            textInformation.setText("We can start, when you are ready!");
                            textInformation.getStyleClass().clear();
                            textInformation.getStyleClass().add("neutral-information");
                            iDoSomething = false;
                        }
                        long endTime = System.nanoTime();
                        if (endTime - startTime > Math.pow(10, 9) * 2) {
                            textInformation.setText("I can't add this ship! Maybe you can change ships positions?");
                            textInformation.getStyleClass().clear();
                            textInformation.getStyleClass().add("error-information");
                            iDoSomething = false;
                            break;
                        }
                    }
                } else if (button.getText() == "+" && !checkFreeSpace(shipSize)) {
                    textInformation.setText("You have enough ships on this board!");
                    textInformation.getStyleClass().clear();
                    textInformation.getStyleClass().add("error-information");
                    iDoSomething = false;
                } else {
                    textInformation.setText("You don't have this ship yet!");
                    textInformation.getStyleClass().clear();
                    textInformation.getStyleClass().add("error-information");
                    iDoSomething = false;
                }
            } else {
                textInformation.setText("I do something!");
            }
        });
    }

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

                HashMap<ImageView, Ship> tmpMap = bigBoard.getFleetMap();
                Ship thisShip = (Ship) tmpMap.get(draggingButton);
                boolean checkCompletePut = bigBoard.putIntoBoard(thisShip, 0, 0, "remove");

                Integer idPane = Integer.parseInt(pane.getId());

                int shipStartPositionX = (int) Math.floor(idPane / 10);
                int shipStartPositionY = (int) idPane % 10;

                checkCompletePut = bigBoard.putIntoBoard(thisShip, shipStartPositionY, shipStartPositionX, "move");
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