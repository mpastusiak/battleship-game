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
import javafx.scene.effect.GaussianBlur;
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

public class BattleshipRunner extends Application {

    private final HBox hBox = new HBox(25);
    private final GridPane leftPlane = new GridPane();
    private GridPane centerPlane = new GridPane();
    private GridPane rightPlane = new GridPane();

    private final int boardSize = 10;

    private Board humanBoard;
    private Pane humanPane;

    private Board computerBoard;
    private Pane computerPane;

    private final LinkedList<TextField> textFieldShips = new LinkedList<>();

    private final Button mainButton = new Button();
    private final Label textInformation = new Label();
    private final ChoiceBox<String> choiceDifficultyLevel = new ChoiceBox<>(FXCollections.observableArrayList(
            "easy", "medium", "hard")
    );

    private final DataFormat dataFormat = new DataFormat("myShip");
    private Node draggingShipImage;

    private String gameStage = "configuration";
    private boolean playerTurn = true;
    private boolean iDoSomething = false;

    private final AI computerPlayer = new AI();
    private final Attack attack = new Attack();
    private final RandomValues generator = new RandomValues();

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        try {

            if (Objects.equals(gameStage, "configuration")) {
                setConfigurationStage();
                choiceDifficultyLevel.getSelectionModel().select(1);
            } else if (Objects.equals(gameStage, "game")) {
                setGameStage();
            }

            mainButton.getStyleClass().add("start-button");

            leftPlane.getStyleClass().add("left-plane");
            centerPlane.getStyleClass().add("center-plane");
            rightPlane.getStyleClass().add("right-plane");
            hBox.getChildren().addAll(leftPlane, centerPlane, rightPlane);

            LinearGradient backgroundGradient = new LinearGradient(0.5, 0.5, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.AQUA), new Stop(1, Color.BLUE));
            BackgroundFill backgroundFill = new BackgroundFill(backgroundGradient, CornerRadii.EMPTY, new Insets(0, 0, 0, 0));
            Background background = new Background(backgroundFill);

            Scene scene = new Scene(hBox, 1000, 600, Color.BLACK);
            scene.getStylesheets().add("battleship-styles.css");
            hBox.setBackground(background);
            hBox.getStyleClass().add("hbox");

            primaryStage.setTitle("BattleshipRunner");
            primaryStage.setScene(scene);
            primaryStage.setHeight(600);
            primaryStage.setWidth(1000);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setConfigurationStage() {
        leftPlane.getChildren().clear();
        leftPlane.getChildren().removeAll();
        configurationPanel();

        if (!Objects.equals(centerPlane, null)) {
            centerPlane.getChildren().clear();
        }
        configurationHumanBoard();

        if (!Objects.equals(rightPlane, null)) {
            rightPlane.getChildren().clear();
        }
        doCleanComputerBoard();
    }


    private void setGameStage() {
        leftPlane.getChildren().clear();
        leftPlane.getChildren().removeAll();
        gameInformationPanel();

        stopConfigurationEventsForObjects();
        doComputerBoardWithShip();
        setAttackEventsForObjects();

        String difficultyLevel = choiceDifficultyLevel.getValue();
        computerPlayer.attackConfiguration(humanBoard, difficultyLevel);
        computerPane.setEffect(null);
        playerTurn = true;
    }


    private void configurationPanel() {

        textFieldShips.clear();

        Label optionsLabel = new Label();
        optionsLabel.getStyleClass().add("options-label");
        optionsLabel.setText("GAME OPTIONS");
        leftPlane.add(optionsLabel, 0, 0, 3, 1);

        for (int i = 1; i <= 5; i++) {
            Label textShip = new Label();
            textShip.setText("Number of " + i + " size ship:");
            leftPlane.add(textShip, 0, (i * 2) - 1, 3, 1);

            Button buttonMinusShip = new Button();
            buttonMinusShip.setText("-");
            clickPlusMinusButton(buttonMinusShip);
            leftPlane.add(buttonMinusShip, 0, (i * 2), 1, 1);

            TextField textFieldShip = new TextField();
            textFieldShip.setText("1");
            textFieldShip.setEditable(false);
            textFieldShips.add(textFieldShip);
            leftPlane.add(textFieldShips.getLast(), 1, (i * 2), 1, 1);

            Button buttonPlusShip = new Button();
            buttonPlusShip.setText("+");
            clickPlusMinusButton(buttonPlusShip);
            leftPlane.add(buttonPlusShip, 2, (i * 2), 1, 1);
        }

        Label emptyLabel1 = new Label();
        emptyLabel1.getStyleClass().clear();
        emptyLabel1.getStyleClass().add("empty-label");
        leftPlane.add(emptyLabel1, 0, 11, 3, 1);

        Label textChoiceDifficultyLevel = new Label();
        textChoiceDifficultyLevel.setText("Choose level: ");
        leftPlane.add(textChoiceDifficultyLevel, 0, 12, 2, 1);
        leftPlane.add(choiceDifficultyLevel, 2, 12, 1, 1);

        Label emptyLabel2 = new Label();
        emptyLabel2.getStyleClass().clear();
        emptyLabel2.getStyleClass().add("empty-label");
        leftPlane.add(emptyLabel2, 0, 13, 3, 1);

        mainButton.setText("Let's play!");
        clickMainButton(mainButton);
        leftPlane.add(mainButton, 0, 14, 3, 1);

        Label emptyLabel3 = new Label();
        emptyLabel3.getStyleClass().clear();
        emptyLabel3.getStyleClass().add("empty-label");
        leftPlane.add(emptyLabel3, 0, 15, 3, 1);

        textInformation.setText("We can start, when you will be ready!");
        textInformation.getStyleClass().add("neutral-information");
        leftPlane.add(textInformation, 0, 16, 3, 1);

    }


    private void gameInformationPanel() {
        setInformationOnLabel(textInformation, "neutral-information", "Your turn!");
        leftPlane.add(textInformation, 0, 0);
        mainButton.setText("I give up!");
        clickMainButton(mainButton);
        leftPlane.add(mainButton, 0, 1);
    }


    private void configurationHumanBoard() {

        int lengthHumanSquare = 30;

        humanBoard = new Board(boardSize, boardSize, lengthHumanSquare, "human-board");
        humanPane = humanBoard.doBoard();
        humanPane.setId("humanPane");

        setDefaultShipsOnHumanBoard();
        humanBoard.drawContentBoard();
        setConfigurationEventsForObjects();
        centerPlane.getChildren().addAll(humanPane);
    }

    private void setConfigurationEventsForObjects() {
        for (Node child : humanPane.getChildrenUnmodifiable()) {

            if (child instanceof ImageView) {
                ImageView imageView = (ImageView) child;
                dragImageView(imageView);
                clickRightButtonOnShip(imageView);
            }

            if (child instanceof StackPane) {
                StackPane pane = (StackPane) child;
                addDropHandling(pane);
            }
        }
    }

    private void setDefaultShipsOnHumanBoard() {
        for (int i = 0; i < textFieldShips.size(); i++) {
            for (int j = 0; j < Integer.valueOf(textFieldShips.get(i).getText()); j++) {
                Ship randomShip = generator.randomShip(humanBoard, i + 1);
                boolean checkCompletePut = humanBoard.setIntoBoard(randomShip, randomShip.getActualYPosition(), randomShip.getActualXPosition(), "add");
                if (!checkCompletePut) {
                    j--;
                }
            }
        }
    }


    private void stopConfigurationEventsForObjects() {
        for (Map.Entry<ImageView, Ship> entry : humanBoard.getShipsWithViewMap().entrySet()) {
            humanBoard.setIntoBoard(entry.getValue(), entry.getValue().getActualYPosition(), entry.getValue().getActualXPosition(), "remove");
            humanBoard.getBoard().getChildren().remove(entry.getKey());
            humanBoard.setIntoBoard(entry.getValue(), entry.getValue().getActualYPosition(), entry.getValue().getActualXPosition(), "add");
            entry.getKey().setOnMousePressed(null);
            entry.getKey().setOnDragDetected(null);
            entry.getKey().setOnDragOver(null);
        }
    }


    private Map<ImageView, Ship> sortFleet(Board board) {
        List<Ship> shipsBySize = new ArrayList<>(board.getShipsWithViewMap().values());

        shipsBySize.sort(Comparator.comparingInt(Ship::getShipSize));

        board.getShipsWithViewMap().clear();
        LinkedHashMap<ImageView, Ship> sortedBoardMap = new LinkedHashMap<>();

        for (int i = shipsBySize.size() - 1; i >= 0; i--) {
            board.getShipsWithViewMap().put(shipsBySize.get(i).getShipImageView(), shipsBySize.get(i));
            sortedBoardMap.put(shipsBySize.get(i).getShipImageView(), shipsBySize.get(i));
        }
        return sortedBoardMap;
    }

    private void doCleanComputerBoard() {
        int lengthComputerSquare = 30;
        computerBoard = new Board(boardSize, boardSize, lengthComputerSquare, "computer-board");
        computerPane = computerBoard.doBoard();
        computerPane.setId("computerPane");
        computerPane.setEffect(new GaussianBlur());

        rightPlane.getChildren().addAll(computerPane);
    }


    private void doComputerBoardWithShip() {
        Map<ImageView, Ship> sortHumanFleet = sortFleet(humanBoard);
        setComputerShips(sortHumanFleet);
    }

    private void setComputerShips(Map<ImageView, Ship> sortHumanFleet) {
        while (sortHumanFleet.size() != computerBoard.getShipsWithViewMap().size()) {
            Iterator<Map.Entry<ImageView, Ship>>
                    iterator;
            iterator = sortHumanFleet.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ImageView, Ship> entry = iterator.next();
                int humanShipSize = entry.getValue().getShipSize();

                ArrayList<Integer> randomCellsList = generator.randomCellsBoardList(computerBoard);

                Ship randomShip = generator.randomShip(computerBoard, humanShipSize);
                char firstRandomShipOrientation = randomShip.getShipOrientation();

                findPlaceForShipOnComputerBoard(randomCellsList, randomShip, firstRandomShipOrientation);
            }
            if (humanBoard.getShipsWithViewMap().size() != computerBoard.getShipsWithViewMap().size()) {
                computerBoard.getShipsWithViewMap().clear();
            }
        }
    }

    private void findPlaceForShipOnComputerBoard(ArrayList<Integer> randomCellsList, Ship randomShip, char firstRandomShipOrientation) {
        boolean isAdd;
        for (int i = 0; i < randomCellsList.size(); i++) {
            randomShip.setActualXPosition((int) Math.floor(randomCellsList.get(i) / 10));
            randomShip.setActualYPosition(randomCellsList.get(i) % 10);
            isAdd = computerBoard.setIntoBoard(randomShip, randomShip.getActualYPosition(), randomShip.getActualXPosition(), "reserved");
            if (isAdd) {
                computerBoard.putShipsWithViewMap(randomShip.getShipImageView(), randomShip);
                break;
            } else if ((i == randomCellsList.size() - 1) && (randomShip.getShipOrientation() == firstRandomShipOrientation)) {
                randomShip.changeShipOrientation();
                i = 0;
            }
        }
    }


    private void setAttackEventsForObjects() {
        for (Node child : computerPane.getChildrenUnmodifiable()) {

            if (child instanceof StackPane) {
                StackPane pane = (StackPane) child;
                playerAttack(pane);
            }
        }
    }

    private boolean checkFreeSpaceOnBoard(int newShipSize) {
        double occupiedSpace = newShipSize;
        for (int i = 0; i < textFieldShips.size(); i++) {
            occupiedSpace += Integer.valueOf(textFieldShips.get(i).getText()) * (i + 1);
        }
        double occupiedSpacePercent = occupiedSpace / (humanBoard.getBoardRows() * humanBoard.getBoardColumns());
        return occupiedSpacePercent <= 0.35;
    }

    private void clickMainButton(Button button) {
        button.setOnMousePressed(e -> {
            if(Objects.equals(gameStage, "configuration")) {
                gameStage = "game";
                setGameStage();

            } else if (Objects.equals(gameStage, "game")) {
                gameStage = "configuration";
                setConfigurationStage();
            }
        });
    }


    private void clickPlusMinusButton(Button button) {
        button.setOnMousePressed(e -> {
            if (!iDoSomething) {
                iDoSomething = true;
                String rowButton = button.getProperties().get("gridpane-row").toString();
                int shipSize = Integer.valueOf(rowButton) / 2;
                TextField thisTextField = textFieldShips.get(shipSize - 1);
                int getActualNumberOfShip = Integer.valueOf(thisTextField.getText());

                if (Objects.equals(button.getText(), "-") && getActualNumberOfShip > 0) {
                    thisTextField.setText(Integer.toString(getActualNumberOfShip - 1));

                    removeShipFromBoard(shipSize);

                } else if (Objects.equals(button.getText(), "+") && checkFreeSpaceOnBoard(shipSize)) {
                    Ship randomShip = generator.randomShip(humanBoard, shipSize);
                    char firstRandomShipOrientation = randomShip.getShipOrientation();
                    ArrayList<Integer> randomCellsList = generator.randomCellsBoardList(humanBoard);

                    boolean isAdd = findPlaceForNewShipOnHumanBoard(thisTextField, getActualNumberOfShip, randomShip, firstRandomShipOrientation, randomCellsList);
                    if (!isAdd) {
                        setInformationOnLabel(textInformation, "error-information", "I can't add this ship! Maybe you can change ships positions?");
                        iDoSomething = false;
                    }
                } else if (Objects.equals(button.getText(), "+") && !checkFreeSpaceOnBoard(shipSize)) {
                    setInformationOnLabel(textInformation, "error-information", "You have enough this ships on your board!");
                    iDoSomething = false;
                } else {
                    setInformationOnLabel(textInformation, "error-information", "You don't have this ship now!");
                    iDoSomething = false;
                }
            } else {
                textInformation.setText("I do something!");
            }
        });
    }

    private void setInformationOnLabel(Label textLabel, String styleClass, String information) {
        textLabel.setText(information);
        textLabel.getStyleClass().clear();
        textLabel.getStyleClass().add(styleClass);
    }

    private boolean findPlaceForNewShipOnHumanBoard(TextField thisTextField, int getActualNumberOfShip, Ship randomShip, char firstRandomShipOrientation, ArrayList<Integer> randomCellsList) {
        boolean isAdd = false;
        for (int i = 0; i < randomCellsList.size(); i++) {
            randomShip.setActualXPosition((int) Math.floor(randomCellsList.get(i) / 10));
            randomShip.setActualYPosition(randomCellsList.get(i) % 10);
            isAdd = humanBoard.setIntoBoard(randomShip, randomShip.getActualYPosition(), randomShip.getActualXPosition(), "add");
            dragImageView(randomShip.getShipImageView());
            clickRightButtonOnShip(randomShip.getShipImageView());
            if (isAdd) {
                thisTextField.setText(Integer.toString(getActualNumberOfShip + 1));
                humanBoard.putShipsWithViewMap(randomShip.getShipImageView(), randomShip);
                setInformationOnLabel(textInformation, "neutral-information", "We can start, when you will be ready!");
                iDoSomething = false;
                break;
            } else if ((i == randomCellsList.size() - 1) && (randomShip.getShipOrientation() == firstRandomShipOrientation)) {
                randomShip.changeShipOrientation();
                i = 0;
            }
        }
        return isAdd;
    }

    private void removeShipFromBoard(int shipSize) {
        for (Map.Entry<ImageView, Ship> entry : humanBoard.getShipsWithViewMap().entrySet()) {
            if (entry.getValue().getShipSize() == shipSize) {
                boolean checkCompletePut = humanBoard.setIntoBoard(entry.getValue(), 0, 0, "remove");
                humanBoard.removeShipsWithViewMap(entry.getKey());
                humanBoard.getBoard().getChildren().remove(entry.getKey());
                iDoSomething = false;
                break;
            }
        }

        setInformationOnLabel(textInformation, "neutral-information", "We can start, when you will be ready!");
    }


    private void clickRightButtonOnShip(ImageView shipImageView) {
        shipImageView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                HashMap<ImageView, Ship> tmpMap = humanBoard.getShipsWithViewMap();
                Ship thisShip = tmpMap.get(shipImageView);

                char shipStartOrientation = thisShip.getShipOrientation();
                int shipStartPositionX = thisShip.getActualXPosition();
                int shipStartPositionY = thisShip.getActualYPosition();

                humanBoard.setIntoBoard(thisShip, 0, 0, "remove");

                thisShip.changeShipOrientation();

                boolean checkCompletePutNewShipOrientation = findPlaceAroundForNewShipOrientation(shipImageView, thisShip, shipStartOrientation, shipStartPositionX, shipStartPositionY);

                if (!checkCompletePutNewShipOrientation) {
                    checkCompletePutNewShipOrientation = findPlaceAnywhereForNewShipOrientation(shipImageView, thisShip);
                }

                if (!checkCompletePutNewShipOrientation) {
                    canNotChangeShipOrientation(shipImageView, thisShip);
                }
            }
        });
    }

    private void canNotChangeShipOrientation(ImageView shipImageView, Ship thisShip) {
        thisShip.changeShipOrientation();
        humanBoard.removeShipsWithViewMap(shipImageView);
        humanBoard.getBoard().getChildren().remove(shipImageView);
        humanBoard.setIntoBoard(thisShip, thisShip.getActualYPosition(), thisShip.getActualXPosition(), "add");
        setInformationOnLabel(textInformation, "error-information", "I can't do this! Maybe you can change ships positions?");
        iDoSomething = false;
    }

    private boolean findPlaceAnywhereForNewShipOrientation(ImageView shipImageView, Ship thisShip) {
        ArrayList<Integer> randomCellsList = generator.randomCellsBoardList(humanBoard);
        boolean checkCompletePutNewShipOrientation = false;

        for (Integer integer : randomCellsList) {
            int newShipStartPositionX = (int) Math.floor(integer / 10);
            int newShipStartPositionY = integer % 10;
            checkCompletePutNewShipOrientation = humanBoard.setIntoBoard(thisShip, newShipStartPositionY, newShipStartPositionX, "onlyCheck");
            if (checkCompletePutNewShipOrientation) {
                checkCompletePutNewShipOrientation = changeShipOrientationComplete(shipImageView, thisShip, newShipStartPositionX, newShipStartPositionY);
                break;
            }
        }
        return checkCompletePutNewShipOrientation;
    }

    private boolean changeShipOrientationComplete(ImageView shipImageView, Ship thisShip, int newShipStartPositionX, int newShipStartPositionY) {
        boolean checkCompletePutNewShipOrientation;
        humanBoard.getBoard().getChildren().remove(shipImageView);
        checkCompletePutNewShipOrientation = humanBoard.setIntoBoard(thisShip, newShipStartPositionY, newShipStartPositionX, "add");
        dragImageView(thisShip.getShipImageView());
        clickRightButtonOnShip(thisShip.getShipImageView());
        setInformationOnLabel(textInformation, "neutral-information", "We can start, when you will be ready!");
        return checkCompletePutNewShipOrientation;
    }

    private boolean findPlaceAroundForNewShipOrientation(ImageView shipImageView, Ship thisShip, char shipStartOrientation, int shipStartPositionX, int shipStartPositionY) {
        boolean checkCompletePutNewShipOrientation;
        boolean changeVector = false;
        checkCompletePutNewShipOrientation = false;
        for (int i = 0; true; i++) {
            if ((shipStartOrientation == 'v' && !changeVector) || (shipStartOrientation == 'h' && changeVector)) {

                checkCompletePutNewShipOrientation = humanBoard.setIntoBoard(thisShip, shipStartPositionY, shipStartPositionX + i, "onlyCheck");

                if (checkCompletePutNewShipOrientation) {
                    humanBoard.getBoard().getChildren().remove(shipImageView);
                    checkCompletePutNewShipOrientation = humanBoard.setIntoBoard(thisShip, shipStartPositionY, shipStartPositionX + i, "add");
                    break;
                } else if ((i == humanBoard.getBoardRows() - 1) && !changeVector) {
                    changeVector = true;
                    i = 0;
                } else if ((i == humanBoard.getBoardRows() - 1) && changeVector) {
                    break;
                }

            } else if (shipStartOrientation == 'h' || shipStartOrientation == 'v') {

                checkCompletePutNewShipOrientation = humanBoard.setIntoBoard(thisShip, shipStartPositionY + i, shipStartPositionX, "onlyCheck");

                if (checkCompletePutNewShipOrientation) {
                    humanBoard.getBoard().getChildren().remove(shipImageView);
                    checkCompletePutNewShipOrientation = humanBoard.setIntoBoard(thisShip, shipStartPositionY + i, shipStartPositionX, "add");
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
        return checkCompletePutNewShipOrientation;
    }

    private void dragImageView(ImageView shipImage) {
        shipImage.setOnDragDetected(e -> {
            Dragboard dragBoard = shipImage.startDragAndDrop(TransferMode.MOVE);
            dragBoard.setDragView(shipImage.snapshot(null, null));
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.put(dataFormat, " ");
            dragBoard.setContent(clipboardContent);
            draggingShipImage = shipImage;
            shipImage.setVisible(false);
        });

    }

    private void addDropHandling(StackPane pane) {
        pane.setOnDragOver(e -> {
            Dragboard dragboard = e.getDragboard();
            if (dragboard.hasContent(dataFormat) && draggingShipImage != null) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
        });

        pane.setOnDragDropped(e -> {
            Dragboard dragboard = e.getDragboard();

            if (dragboard.hasContent(dataFormat)) {
                ((Pane) draggingShipImage.getParent()).getChildren().remove(draggingShipImage);

                HashMap<ImageView, Ship> tmpMap = humanBoard.getShipsWithViewMap();
                Ship thisShip = tmpMap.get(draggingShipImage);
                humanBoard.setIntoBoard(thisShip, 0, 0, "remove");

                Integer idPane = Integer.parseInt(pane.getId());

                int shipStartPositionX = (int) Math.floor(idPane / 10);
                int shipStartPositionY = idPane % 10;

                boolean checkCompletePut = humanBoard.setIntoBoard(thisShip, shipStartPositionY, shipStartPositionX, "move");
                humanBoard.drawContentBoard();
                if (checkCompletePut) {
                    e.setDropCompleted(true);
                    setInformationOnLabel(textInformation, "neutral-information", "I'm waiting for you!");
                } else {
                    e.setDropCompleted(false);
                    setInformationOnLabel(textInformation, "error-information", "Ship must be on board and can't contact with other ship!");
                }
                draggingShipImage.setVisible(true);
                draggingShipImage = null;
            }
        });

    }

    private void playerAttack(StackPane pane) {
        pane.setOnMousePressed(e -> {
            if (playerTurn) {
                Integer idPane = Integer.parseInt(pane.getId());
                boolean isHit = attack.attack(computerBoard, idPane);
                if (isHit) {
                    if (computerBoard.getShipsWithViewMap().size() == 0) {
                        setInformationOnLabel(textInformation, "good-information", "! YOU WIN !");
                        mainButton.setText("Play again");
                        playerTurn = false;
                    }
                } else {
                    playerTurn = false;
                    computerAttack();
                }
            }
        });
    }

    private void computerAttack() {
        setInformationOnLabel(textInformation, "neutral-information", "Computer turn!");
        computerPane.setEffect(new GaussianBlur());
        boolean isHit = computerPlayer.attack(humanBoard);
        if (isHit) {
            if (humanBoard.getShipsWithViewMap().size() == 0) {
                setInformationOnLabel(textInformation, "error-information", "! YOU LOST !");
                mainButton.setText("Try again, please");
            } else {
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(1000),
                        ae -> computerAttack()));
                timeline.play();
            }
        } else {
            playerTurn = true;
            setInformationOnLabel(textInformation, "neutral-information", "Your turn!");
            computerPane.setEffect(null);
        }
    }
}