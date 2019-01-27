package com.projects.battleship;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Random;

import static javafx.scene.layout.BorderRepeat.STRETCH;

public class Battleship extends Application {

    private double newX, newY, oldX, oldY, offSize;
    private String offX, offY;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox hBox = new HBox(25);
        hBox.setAlignment(Pos.CENTER);

        Board bigBoard = new Board(10,10, 30, "big-board");
        GridPane bigPane = bigBoard.doBoard();
        bigPane.setId("bigPane");

        int fitHeight = bigBoard.getLengthSquare();

        Random generator = new Random();

        char shipOrientation;
        int shipSize = 5;
        int shipMaxPositionX;
        int shipMaxPositionY;

        int shipRandomOrientation = generator.nextInt(2);
        System.out.println(shipRandomOrientation);
        if (shipRandomOrientation == 0) {
            shipOrientation = 'v';
            shipMaxPositionX = 9 - shipSize + 1;
            shipMaxPositionY = 9 + 1;
        } else {
            shipOrientation = 'h';
            shipMaxPositionX = 9 + 1;
            shipMaxPositionY = 9 - shipSize + 1;
        }

        int shipPositionX = generator.nextInt(shipMaxPositionX);
        int shipPositionY = generator.nextInt(shipMaxPositionY);
        int idShipPosition = shipPositionY * 10 + shipPositionX;

        Ship ship = new Ship(shipSize,fitHeight, shipOrientation);
        Image image = ship.getImageClassShip();
        ImageView imageView = new ImageView(image);
        imageView.getStyleClass().add("ship");

        if (ship.getShipOrientation() == 'v') {
            int colspan = 1;
            int rowspan = ship.getShipSize();
            bigPane.add(imageView, shipPositionY, shipPositionX, colspan, rowspan);
            for(int i = 0; i < shipSize; i++ ) {
                bigPane.getChildren().get(idShipPosition + i).getProperties().put("game-status", "reserved");
                System.out.println(bigPane.getChildren().get(idShipPosition + i).getProperties().get("game-status").toString());
            }
        } else {
            int colspan = ship.getShipSize();
            int rowspan = 1;
            bigPane.add(imageView, shipPositionY, shipPositionX, colspan, rowspan);
            for(int i = 0; i < shipSize; i++ ) {
                System.out.println(bigPane.getChildren().get(idShipPosition + i * 10).toString());
            }
        }

        Board smallBoard = new Board(10,10, 20, "smaller-board");
        GridPane smallPane = smallBoard.doBoard();

        Pane leftPlane = new Pane();
        leftPlane.getChildren().addAll(bigPane);
        Pane rightPlane = new Pane();
        rightPlane.getChildren().add(smallPane);
        hBox.getChildren().addAll(leftPlane, rightPlane);

        LinearGradient backgroundGradient = new LinearGradient(0.5,0.5,1,1,true, CycleMethod.NO_CYCLE,new Stop(0,Color.AQUA),new Stop(1,Color.BLUE));
        BackgroundFill backgroundFill = new BackgroundFill(backgroundGradient, CornerRadii.EMPTY, new Insets(0,0,0,0));
        Background background = new Background(backgroundFill);

        Scene scene = new Scene(hBox, 900, 600, Color.BLACK);
        scene.getStylesheets().add("battleship-styles.css");
        hBox.setBackground(background);
        hBox.setMinSize(900,600);

        primaryStage.setTitle("Battleship");
        primaryStage.setScene(scene);
        primaryStage.setHeight(600);
        primaryStage.setWidth(900);
        primaryStage.setResizable(false);
        primaryStage.show();

        double bigPaneHeight = bigPane.getHeight();
        double bigPaneWidth = bigPane.getWidth();
        double imageViewHeight = imageView.getFitHeight();
        double imageViewWidth = imageView.getFitWidth();

        //imageView.getProperties().put("gridpane-column", 4);

        EventHandler<MouseEvent> mousePressed = e -> {
            imageView.setMouseTransparent(true);
            System.out.println("Event on Source: mouse pressed");
            e.setDragDetect(true);
            offX = imageView.getProperties().get("gridpane-column").toString();
            offY = imageView.getProperties().get("gridpane-row").toString();
            offSize = ship.getShipSize();
            if (e.getButton() == MouseButton.SECONDARY) {
                ship.setShipOrientation('h');
                imageView.getProperties().put("gridpane-column-span", 5);
                imageView.getProperties().put("gridpane-row-span", 1);
                imageView.setImage(ship.getImageClassShip());
                System.out.println("Right button clicked");
            }
            System.out.println(offX + " : " + offY + " : " + offSize);
        };
        EventHandler<MouseEvent> mouseReleased = e -> {
            imageView.setMouseTransparent(false);
            System.out.println("Event on Source: mouse released");
        };
        EventHandler<MouseEvent> mouseDragged = e -> {
            System.out.println("Event on Source: mouse dragged");
            e.setDragDetect(false);
            System.out.println("Mouse Drag Released");
            newX = e.getX();
            newY = e.getY();
            imageView.setX(newX);
            imageView.setY(newY);
            oldX = newX;
            oldY = newY;
            System.out.println(newX + " : " + newY);
            bigPane.toFront();
        };
        EventHandler<MouseEvent> dragDetected = e -> {
            imageView.startFullDrag();
            System.out.println("Event on Source: drag detected");
        };
        EventHandler<MouseDragEvent> mouseDragReleased = e -> {
            System.out.println("Mouse Drag Released");
            newX = e.getX();
            newY = e.getY();
            imageView.setX(newX);
            imageView.setY(newY);
            oldX = newX;
            oldY = newY;
            System.out.println(newX + " : " + newY);
            bigPane.toFront();
        };

        imageView.addEventHandler(MouseEvent.MOUSE_PRESSED,mousePressed);
        imageView.addEventHandler(MouseEvent.MOUSE_RELEASED,mouseReleased);
        imageView.addEventHandler(MouseEvent.MOUSE_DRAGGED,mouseDragged);
        imageView.addEventHandler(MouseEvent.DRAG_DETECTED,dragDetected);
        imageView.addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED,mouseDragReleased);
    }
}