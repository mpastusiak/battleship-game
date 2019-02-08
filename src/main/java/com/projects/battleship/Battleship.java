package com.projects.battleship;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.util.*;

import static java.lang.Math.abs;

public class Battleship extends Application {

    private HBox hBox = new HBox(25);
    private GridPane leftPlane = new GridPane();
    private Pane centerPlane = new Pane();
    private Pane rightPlane = new Pane();

    int lengthHumanSquare = 30;
    int lengthComputerSquare = 30;
    int boardSize = 10;

    private Board humanBoard;
    private Pane humanPane;

    private Board computerBoard;
    private Pane computerPane;

    private LinkedList<TextField> textFieldShips = new LinkedList<>();
    private final DataFormat buttonFormat = new DataFormat("MyButton");
    private Node draggingButton;
    private RandomValues generator = new RandomValues();
    private boolean iDoSomething = false;
    private Label textInformation = new Label();
    private String gameStage = "configuration";

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

            if (gameStage == "configuration") {
                setConfigurationStage();
            } else if (gameStage == "game") {
                setGameStage();
            }

            centerPlane.getChildren().addAll(humanPane);
            rightPlane.getChildren().addAll(computerPane);
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


        private void setConfigurationStage() {
            configurationPanel();
            configurationHumanBoard();
            configurationComputerBoard();
        }


        private void setGameStage() {
            gameInformationPanel();
            stopMouseEventFromConfiguration();
        }


        private void configurationPanel() {

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

        }



        private void configurationHumanBoard() {
            humanBoard = new Board(boardSize, boardSize, lengthHumanSquare, "big-board");
            humanPane = humanBoard.doBoard();
            humanPane.setId("humanPane");

            for (int i = 0; i < textFieldShips.size(); i++) {
                for (int j = 0; j < Integer.valueOf(textFieldShips.get(i).getText()); j++) {
                    Ship randomShip = generator.randomShip(humanBoard, i + 1);
                    boolean checkCompletePut = humanBoard.putIntoBoard(randomShip, randomShip.getActualYPosition(), randomShip.getActualXPosition(), "add");
                    if (checkCompletePut != true) {
                        j--;
                    }
                }
            }

            humanBoard.drawContentBoard();

            if (gameStage == "configuration") {

                for (Node child : humanPane.getChildrenUnmodifiable()) {

                    if (child instanceof ImageView) {
                        ImageView imageView = (ImageView) child;
                        dragButton(imageView);
                        clickShip(imageView);
                    }

                    if (child instanceof StackPane) {
                        StackPane pane = (StackPane) child;
                        addDropHandling(pane);
                    }
                }
            }
        }



        private void configurationComputerBoard() {
            computerBoard = new Board(boardSize, boardSize, lengthComputerSquare, "smaller-board");
            if (gameStage != "configuration") {
                rightPlane.getChildren().clear();
                computerPane.getChildren().clear();
            }
            computerPane = computerBoard.doBoard();
        }


        private  void gameInformationPanel() {
            leftPlane.getChildren().clear();
        }

        private boolean checkFreeSpace(int newShipSize) {
            double occupiedSpace = newShipSize;
            for (int i = 0; i < textFieldShips.size(); i++) {
                occupiedSpace += Integer.valueOf(textFieldShips.get(i).getText()) * (i + 1);
            }
            double occupiedSpacePercent = occupiedSpace / (humanBoard.getBoardRows() * humanBoard.getBoardColumns());
            if (occupiedSpacePercent <= 0.35) {
                return true;
            } else {
                return false;
            }
        }

        private void clickRefresh(Button button) {
        button.setOnMousePressed(e -> {
            gameStage = "game";
            setGameStage();
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
                            iterator = humanBoard.getFleetMap().entrySet().iterator();
                    outerloop:
                    while (iterator.hasNext()) {
                        Map.Entry<ImageView, Ship> entry = iterator.next();
                        if (entry.getValue().getShipSize() == shipSize) {
                            boolean checkCompletePut = humanBoard.putIntoBoard(entry.getValue(), 0, 0, "remove");
                            humanBoard.removeFleetMap(entry.getKey());
                            humanBoard.getBoard().getChildren().remove(entry.getKey());
                            iDoSomething = false;
                            break outerloop;
                        }
                    }
                    textInformation.setText("We can start, when you are ready!");
                    textInformation.getStyleClass().clear();
                    textInformation.getStyleClass().add("neutral-information");
                } else if (button.getText() == "+" && checkFreeSpace(shipSize)) {
                    boolean isAdd = false;
                    Ship randomShip = generator.randomShip(humanBoard, shipSize);
                    char firstRandomShipOrientation = randomShip.getShipOrientation();
                    ArrayList<Integer> randomCellsList = generator.randomCellsList(humanBoard);

                    for(int i = 0; i < randomCellsList.size(); i++) {
                        randomShip.setActualXPosition((int) Math.floor(randomCellsList.get(i) / 10));
                        randomShip.setActualYPosition((int) randomCellsList.get(i) % 10);
                        isAdd = humanBoard.putIntoBoard(randomShip, randomShip.getActualYPosition(), randomShip.getActualXPosition(), "add");
                        dragButton(randomShip.getShipImageView());
                        clickShip(randomShip.getShipImageView());
                        if (isAdd) {
                            thisTextField.setText(Integer.toString(getActualValue + 1));
                            humanBoard.putFleetMap(randomShip.getShipImageView(), randomShip);
                            textInformation.setText("We can start, when you are ready!");
                            textInformation.getStyleClass().clear();
                            textInformation.getStyleClass().add("neutral-information");
                            iDoSomething = false;
                            break;
                        } else if ((i == randomCellsList.size() - 1) && (randomShip.getShipOrientation() == firstRandomShipOrientation)) {
                            randomShip.changeShipOrientation();
                            i = 0;
                        }
                    }
                    if (!isAdd) {
                        textInformation.setText("I can't add this ship! Maybe you can change ships positions?");
                        textInformation.getStyleClass().clear();
                        textInformation.getStyleClass().add("error-information");
                        iDoSomething = false;
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


    public void stopMouseEventFromConfiguration() {
        Iterator<Map.Entry<ImageView, Ship>>
                iterator = humanBoard.getFleetMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ImageView, Ship> entry = iterator.next();
            humanBoard.putIntoBoard(entry.getValue(), entry.getValue().getActualYPosition(), entry.getValue().getActualXPosition(), "remove");
            humanBoard.getBoard().getChildren().remove(entry.getKey());
            humanBoard.putIntoBoard(entry.getValue(), entry.getValue().getActualYPosition(), entry.getValue().getActualXPosition(), "add");
            entry.getKey().setOnMousePressed(null);
            entry.getKey().setOnDragDetected(null);
            entry.getKey().setOnDragOver(null);
        }
    }


    private void clickShip(ImageView shipImageView) {
        shipImageView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                HashMap<ImageView, Ship> tmpMap = humanBoard.getFleetMap();
                Ship thisShip = (Ship) tmpMap.get(shipImageView);

                char thisShipOrientation = thisShip.getShipOrientation();
                int shipStartPositionX = thisShip.getActualXPosition();
                int shipStartPositionY = thisShip.getActualYPosition();

                boolean checkCompletePut = humanBoard.putIntoBoard(thisShip, 0, 0, "remove");

                thisShip.changeShipOrientation();

                checkCompletePut = false;
                boolean changeVector = false;

                for (int i = 0; !checkCompletePut; i++) {
                    if ((thisShipOrientation == 'v' && !changeVector) || (thisShipOrientation == 'h' && changeVector == true)) {

                        checkCompletePut = humanBoard.putIntoBoard(thisShip, shipStartPositionY, shipStartPositionX + i, "onlyCheck");

                        if (checkCompletePut) {
                            humanBoard.getBoard().getChildren().remove(shipImageView);
                            checkCompletePut = humanBoard.putIntoBoard(thisShip, shipStartPositionY, shipStartPositionX + i, "add");
                            break;
                        } else if ((i == humanBoard.getBoardRows() - 1) && !changeVector) {
                            changeVector = true;
                            i = 0;
                        } else if ((i == humanBoard.getBoardRows() - 1) && changeVector) {
                            break;
                        }

                    } else if ((thisShipOrientation == 'h' && !changeVector) || (thisShipOrientation == 'v' && changeVector == true)) {

                        checkCompletePut = humanBoard.putIntoBoard(thisShip, shipStartPositionY + i, shipStartPositionX, "onlyCheck");

                        if (checkCompletePut) {
                            humanBoard.getBoard().getChildren().remove(shipImageView);
                            checkCompletePut = humanBoard.putIntoBoard(thisShip, shipStartPositionY + i, shipStartPositionX, "add");
                            break;
                        } else if ((i == humanBoard.getBoardColumns() - 1) && !changeVector) {
                            changeVector = true;
                            i = 0;
                        } else if ((i == humanBoard.getBoardColumns() - 1) && changeVector) {
                            break;
                        }

                    } else {
                        break;
                    }
                }

                if (!checkCompletePut) {
                    ArrayList<Integer> randomCellsList = generator.randomCellsList(humanBoard);
                    outerloop:
                    for (int i = 0; i < randomCellsList.size(); i++) {
                        int newShipStartPositionX = (int) Math.floor(randomCellsList.get(i) / 10);
                        int newShipStartPositionY = (int) randomCellsList.get(i) % 10;
                        checkCompletePut = humanBoard.putIntoBoard(thisShip, newShipStartPositionY, newShipStartPositionX, "onlyCheck");
                        if (checkCompletePut) {
                            humanBoard.getBoard().getChildren().remove(shipImageView);
                            checkCompletePut = humanBoard.putIntoBoard(thisShip, newShipStartPositionY, newShipStartPositionX, "add");
                            dragButton(thisShip.getShipImageView());
                            clickShip(thisShip.getShipImageView());
                            textInformation.setText("We can start, when you are ready!");
                            textInformation.getStyleClass().clear();
                            textInformation.getStyleClass().add("neutral-information");
                            break outerloop;
                        }
                    }
                }

                if (!checkCompletePut) {
                    thisShip.changeShipOrientation();
                    humanBoard.removeFleetMap(shipImageView);
                    humanBoard.getBoard().getChildren().remove(shipImageView);
                    humanBoard.putIntoBoard(thisShip, thisShip.getActualYPosition(), thisShip.getActualXPosition(), "add");
                    textInformation.setText("I can't do this! Maybe you can change ships positions?");
                    textInformation.getStyleClass().clear();
                    textInformation.getStyleClass().add("error-information");
                    iDoSomething = false;
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

                HashMap<ImageView, Ship> tmpMap = humanBoard.getFleetMap();
                Ship thisShip = (Ship) tmpMap.get(draggingButton);
                boolean checkCompletePut = humanBoard.putIntoBoard(thisShip, 0, 0, "remove");

                Integer idPane = Integer.parseInt(pane.getId());

                int shipStartPositionX = (int) Math.floor(idPane / 10);
                int shipStartPositionY = (int) idPane % 10;

                checkCompletePut = humanBoard.putIntoBoard(thisShip, shipStartPositionY, shipStartPositionX, "move");
                humanBoard.drawContentBoard();
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