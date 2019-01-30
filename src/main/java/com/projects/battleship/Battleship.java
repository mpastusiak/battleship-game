package com.projects.battleship;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
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

    private double newX, newY, oldX, oldY, offX, offY;
    // private String offX, offY;
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        HBox hBox = new HBox(25);
        hBox.setAlignment(Pos.CENTER);

        int lengthSquare = 30;
        int boardSize = 10;

        Board bigBoard = new Board(boardSize,boardSize, lengthSquare, "big-board");
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

            Ship ship = new Ship(i,lengthSquare,shipRandomOrientation);

            int shipPositionX = generator.randomValue(shipMaxPositionX);
            int shipPositionY = generator.randomValue(shipMaxPositionY);

            boolean checkCompletePut = bigBoard.putIntoBoard(ship,shipPositionY,shipPositionX);
            if (checkCompletePut != true) {
                i++;
            }
        }

        bigBoard.drawContentBoard();

        for( Node child: bigPane.getChildrenUnmodifiable()) {
            if( child instanceof ImageView) {
                ImageView imageView = (ImageView) child;
                oldX = imageView.getX();
                oldY = imageView.getY();

                HashMap<ImageView, Ship> tmpMap = bigBoard.getFleetMap();
                Ship thisShip = (Ship) tmpMap.get(imageView);

                imageView.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent event) {
                        imageView.setMouseTransparent(true);
                        System.out.println("Event on Source: mouse pressed");
                        event.setDragDetect(true);
                        // oldX = imageView.getX() - event.getX();
                        // oldY = imageView.getY() - event.getY();
                        offX = imageView.getX() - event.getX();
                        offY = imageView.getY() - event.getY();
                        /*
                        if (event.getButton() == MouseButton.SECONDARY) {
                            ship.setShipOrientation('h');
                            bigPane.getProperties().put("gridpane-column-span", 5);
                            bigPane.getProperties().put("gridpane-row-span", 1);
                            bigPane.setImage(ship.getImageClassShip());
                            System.out.println("Right button clicked");
                        }
                        */
                        System.out.println(oldX + " : " + oldY + " : ");
                    }
                });

                imageView.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {

                            @Override
                            public void handle(MouseEvent event) {
                                imageView.setMouseTransparent(false);
                                System.out.println("Event on Source: mouse released");
                                newX = oldX;
                                newY = oldY;
                                int actualPostionX = thisShip.getActualXPosition();
                                int actualPostionY = thisShip.getActualYPosition();
                                int positionX = (int) (actualPostionX + (oldX / 30));
                                int positionY = (int) (actualPostionY + (oldY / 30));
                                boolean checkCompletePut = bigBoard.putIntoBoard(thisShip, positionY, positionX);
                                System.out.println(positionX + " : " + positionY + " : " + checkCompletePut);
                            }
                });

                imageView.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

                            @Override
                            public void handle(MouseEvent event) {
                                System.out.println("Event on Source: mouse dragged");
                                event.setDragDetect(false);
                                newX = event.getX() + offX - (0.5 * lengthSquare);
                                newY = event.getY() + offY - (0.5 * lengthSquare);
                                double x = Math.min(oldX, newX);
                                double y = Math.min(oldY, newY);
                                double roundNewX = Math.round(x/lengthSquare) * lengthSquare;
                                double roundNewY = Math.round(y/lengthSquare) * lengthSquare;
                                imageView.setX(roundNewX);
                                imageView.setY(roundNewY);
                                bigPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                                        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                                oldX = newX;
                                oldY = newY;
                                System.out.println(roundNewX + " : " + roundNewY);
                                bigPane.toFront();
                            }
                });

                imageView.addEventHandler(MouseEvent.DRAG_DETECTED, new EventHandler<MouseEvent>() {

                            @Override
                            public void handle(MouseEvent event) {
                                imageView.startFullDrag();
                                System.out.println("Event on Source: drag detected");
                            }
                });

      /*         imageView.addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, new EventHandler<MouseDragEvent>() {
                    @Override
                    public void handle(MouseDragEvent event) {
                        System.out.println("Mouse Drag Released");
                        newX = oldX + event.getX();
                        newY = oldY + event.getY();
                        imageView.setX(newX);
                        imageView.setY(newY);
                        oldX = newX;
                        oldY = newY;
                        System.out.println(newX + " : " + newY);
                        bigPane.toFront();
                    }
                }); */
            }
        }

        HashMap<Integer, String> tmpMap = bigBoard.getReservedCalls();
        Iterator iterator = tmpMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            Integer tmpKey = (Integer) pair.getKey();
            String tmpValue = (String) pair.getValue();
            // iterator.remove();

            if (tmpValue == "with-ship") {
                bigPane.getChildren().get(tmpKey).addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent event) {
                        System.out.println("Click " + tmpKey);
                        event.consume();
                    }
                });
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

        /*
        //imageView.getProperties().put("gridpane-column", 4);

        EventHandler<MouseEvent> mousePressed = e -> {
            ship.getShipImageView().setMouseTransparent(true);
            System.out.println("Event on Source: mouse pressed");
            e.setDragDetect(true);
            offX = ship.getShipImageView().getProperties().get("gridpane-column").toString();
            offY = ship.getShipImageView().getProperties().get("gridpane-row").toString();
            /*
            offSize = ship.getShipSize();
            if (e.getButton() == MouseButton.SECONDARY) {
                ship.setShipOrientation('h');
                bigPane.getProperties().put("gridpane-column-span", 5);
                bigPane.getProperties().put("gridpane-row-span", 1);
                bigPane.setImage(ship.getImageClassShip());
                System.out.println("Right button clicked");
            }
            */ /*
            System.out.println(offX + " : " + offY + " : " + offSize);
        };
        EventHandler<MouseEvent> mouseReleased = e -> {
            ship.getShipImageView().setMouseTransparent(false);
            System.out.println("Event on Source: mouse released");
        };
        EventHandler<MouseEvent> mouseDragged = e -> {
            System.out.println("Event on Source: mouse dragged");
            e.setDragDetect(false);
            System.out.println("Mouse Drag Released");
            newX = e.getX();
            newY = e.getY();
            ship.getShipImageView().setX(newX);
            ship.getShipImageView().setY(newY);
            oldX = newX;
            oldY = newY;
            System.out.println(newX + " : " + newY);
            bigPane.toFront();
        };
        EventHandler<MouseEvent> dragDetected = e -> {
            ship.getShipImageView().startFullDrag();
            System.out.println("Event on Source: drag detected");
        };
        EventHandler<MouseDragEvent> mouseDragReleased = e -> {
            System.out.println("Mouse Drag Released");
            newX = e.getX();
            newY = e.getY();
            ship.getShipImageView().setX(newX);
            ship.getShipImageView().setY(newY);
            oldX = newX;
            oldY = newY;
            System.out.println(newX + " : " + newY);
            bigPane.toFront();
        };

            ship.getShipImageView().addEventHandler(MouseEvent.MOUSE_PRESSED,mousePressed);
            ship.getShipImageView().addEventHandler(MouseEvent.MOUSE_RELEASED,mouseReleased);
            ship.getShipImageView().addEventHandler(MouseEvent.MOUSE_DRAGGED,mouseDragged);
            ship.getShipImageView().addEventHandler(MouseEvent.DRAG_DETECTED,dragDetected);
            ship.getShipImageView().addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED,mouseDragReleased);

            */
    }

}