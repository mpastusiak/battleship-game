package com.projects.battleship;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class Battleship extends Application {

    private HBox hBox = new HBox(25);
    private GridPane leftPlane = new GridPane();
    private GridPane centerPlane = new GridPane();
    private GridPane rightPlane = new GridPane();

    int lengthHumanSquare = 30;
    int lengthComputerSquare = 30;
    int boardSize = 10;

    private ChoiceBox choiceDifficultyLevel = new ChoiceBox(FXCollections.observableArrayList(
            "easy", "medium", "hard")
    );

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
    private boolean playerTurn = true;
    private AI computerPlayer = new AI();
    private Attack attack = new Attack();

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

            if (gameStage == "configuration") {
                setConfigurationStage();
                choiceDifficultyLevel.getSelectionModel().select(1);
            } else if (gameStage == "game") {
                setGameStage();
            }
            leftPlane.getStyleClass().add("left-plane");
            centerPlane.getChildren().addAll(humanPane);
            centerPlane.getStyleClass().add("center-plane");
            rightPlane.getChildren().addAll(computerPane);
            rightPlane.getStyleClass().add("right-plane");
            hBox.getChildren().addAll(leftPlane, centerPlane, rightPlane);

            LinearGradient backgroundGradient = new LinearGradient(0.5, 0.5, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.AQUA), new Stop(1, Color.BLUE));
            BackgroundFill backgroundFill = new BackgroundFill(backgroundGradient, CornerRadii.EMPTY, new Insets(0, 0, 0, 0));
            Background background = new Background(backgroundFill);

            Scene scene = new Scene(hBox, 1000, 600, Color.BLACK);
            scene.getStylesheets().add("battleship-styles.css");
            hBox.setBackground(background);
            hBox.getStyleClass().add("hbox");

            primaryStage.setTitle("Battleship");
            primaryStage.setScene(scene);
            primaryStage.setHeight(600);
            primaryStage.setWidth(1000);
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
            doComputerShip();
            String difficultyLevel = (String) choiceDifficultyLevel.getValue();
            computerPlayer.attackConfiguration(humanBoard, difficultyLevel);
            activeAttackComputerBoard();
        }


        private void setEndGameStage() {

        }


        private void configurationPanel() {

            Label optionsLabel = new Label();
            optionsLabel.getStyleClass().add("options-label");
            optionsLabel.setText("GAME OPTIONS");
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

            Label emptyLabel1 = new Label();
            emptyLabel1.getStyleClass().clear();
            emptyLabel1.getStyleClass().add("empty-label");
            leftPlane.add(emptyLabel1, 0, 11,3,1);

            Label textChoiceDifficultyLevel = new Label();
            textChoiceDifficultyLevel.setText("Choose level: ");
            leftPlane.add(textChoiceDifficultyLevel, 0, 12, 2, 1);
            leftPlane.add(choiceDifficultyLevel, 2, 12, 1, 1);

            Label emptyLabel2 = new Label();
            emptyLabel2.getStyleClass().clear();
            emptyLabel2.getStyleClass().add("empty-label");
            leftPlane.add(emptyLabel2, 0, 13,3,1);

            Button startButton = new Button();
            startButton.setText("Let's play!");
            startButton.getStyleClass().add("start-button");
            clickRefresh(startButton);
            leftPlane.add(startButton, 0, 14, 3, 1);

            Label emptyLabel3 = new Label();
            emptyLabel3.getStyleClass().clear();
            emptyLabel3.getStyleClass().add("empty-label");
            leftPlane.add(emptyLabel3, 0, 15,3,1);

            textInformation.setText("We can start, when you are ready!");
            textInformation.getStyleClass().add("neutral-information");
            leftPlane.add(textInformation, 0, 16,3,1);

        }



    private  void gameInformationPanel() {
        leftPlane.getChildren().clear();
        textInformation.setText("Your turn!");
        textInformation.getStyleClass().clear();
        textInformation.getStyleClass().add("neutral-information");
        leftPlane.add(textInformation, 0, 0);
    }


        private void configurationHumanBoard() {

            humanBoard = new Board(boardSize, boardSize, lengthHumanSquare, "human-board");
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


    public void stopMouseEventFromConfiguration() {
        Iterator<Map.Entry<ImageView, Ship>>
                iterator = humanBoard.getShipsWithViewMap().entrySet().iterator();
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


    public Map<ImageView, Ship> sortFleet(Board board) {
        List<Ship> shipsBySize = new ArrayList<Ship>(board.getShipsWithViewMap().values());

        Collections.sort(shipsBySize, new Comparator<Ship>() {

            public int compare(Ship o1, Ship o2) {
                return o1.getShipSize() - o2.getShipSize();
            }
        });

        board.getShipsWithViewMap().clear();
        LinkedHashMap<ImageView, Ship> sortedBoardMap = new LinkedHashMap<>();

        for (int i = shipsBySize.size()-1; i >= 0; i--) {
            board.getShipsWithViewMap().put(shipsBySize.get(i).getShipImageView(), shipsBySize.get(i));
            sortedBoardMap.put(shipsBySize.get(i).getShipImageView(), shipsBySize.get(i));
        }
        return sortedBoardMap;
    }

        private void configurationComputerBoard () {
            computerBoard = new Board(boardSize, boardSize, lengthComputerSquare, "computer-board");
            if (gameStage != "configuration") {
                rightPlane.getChildren().clear();
                computerPane.getChildren().clear();
            }
            computerPane = computerBoard.doBoard();
        }


    public void doComputerShip() {
        Map sortHumanFleet = sortFleet(humanBoard);
        while (sortHumanFleet.size() != computerBoard.getShipsWithViewMap().size()) {
            Iterator<Map.Entry<ImageView, Ship>>
                    iterator = sortHumanFleet.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ImageView, Ship> entry = iterator.next();
                int humanShipSize = entry.getValue().getShipSize();

                ArrayList<Integer> randomCellsList = generator.randomCellsList(computerBoard);
                boolean isAdd;

                Ship randomShip = generator.randomShip(computerBoard, humanShipSize);
                char firstRandomShipOrientation = randomShip.getShipOrientation();

                outerloop:
                for (int i = 0; i < randomCellsList.size(); i++) {
                    randomShip.setActualXPosition((int) Math.floor(randomCellsList.get(i) / 10));
                    randomShip.setActualYPosition((int) randomCellsList.get(i) % 10);
                    isAdd = computerBoard.putIntoBoard(randomShip, randomShip.getActualYPosition(), randomShip.getActualXPosition(), "reserved");
                    if (isAdd) {
                        computerBoard.putShipsWithViewMap(randomShip.getShipImageView(), randomShip);
                        break outerloop;
                    } else if ((i == randomCellsList.size() - 1) && (randomShip.getShipOrientation() == firstRandomShipOrientation)) {
                        randomShip.changeShipOrientation();
                        i = 0;
                    }
                }
            }
            if (humanBoard.getShipsWithViewMap().size() != computerBoard.getShipsWithViewMap().size()) {
                computerBoard.getShipsWithViewMap().clear();
                // remove reserved
            }
        }
    }


    private void activeAttackComputerBoard() {
        for (Node child : computerPane.getChildrenUnmodifiable()) {

            if (child instanceof StackPane) {
                StackPane pane = (StackPane) child;
                playerAttack(pane);
            }
        }
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
                            iterator = humanBoard.getShipsWithViewMap().entrySet().iterator();
                    outerloop:
                    while (iterator.hasNext()) {
                        Map.Entry<ImageView, Ship> entry = iterator.next();
                        if (entry.getValue().getShipSize() == shipSize) {
                            boolean checkCompletePut = humanBoard.putIntoBoard(entry.getValue(), 0, 0, "remove");
                            humanBoard.removeShipsWithViewMap(entry.getKey());
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
                            humanBoard.putShipsWithViewMap(randomShip.getShipImageView(), randomShip);
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


    private void clickShip(ImageView shipImageView) {
        shipImageView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                HashMap<ImageView, Ship> tmpMap = humanBoard.getShipsWithViewMap();
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
                    humanBoard.removeShipsWithViewMap(shipImageView);
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

                HashMap<ImageView, Ship> tmpMap = humanBoard.getShipsWithViewMap();
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

    private void playerAttack(StackPane pane) {
        pane.setOnMousePressed(e -> {
            if (playerTurn) {
                Integer idPane = Integer.parseInt(pane.getId());
                boolean isHit = attack.attack(computerBoard,idPane);
                if(isHit){
                    if (computerBoard.getShipsWithViewMap().size() == 0) {
                        textInformation.getStyleClass().clear();
                        textInformation.getStyleClass().add("good-information");
                        textInformation.setText("! YOU WIN !");
                    }
                } else {
                    playerTurn = false;
                    computerAttack();
                }
            }
        });
    }

    private void computerAttack() {
        textInformation.setText("Computer's turn!");
        boolean isHit = computerPlayer.easyAttack(humanBoard);
        if(isHit){
            if (humanBoard.getShipsWithViewMap().size() == 0) {
                textInformation.getStyleClass().clear();
                textInformation.getStyleClass().add("error-information");
                textInformation.setText("! COMPUTER WIN :( !");
            } else {
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(1000),
                        ae -> computerAttack()));
                timeline.play();
            }
        } else {
            playerTurn = true;
            textInformation.setText("Your turn!");
        }
    }
}