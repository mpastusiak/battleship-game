package com.projects.battleship;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.LinkedList;

public class Ship {
    private final int shipSize;
    private final int fitHeight;
    private Image imageClassShip;
    private String urlImageShip;
    private char shipOrientation;
    private int shipWidth;
    private int shipHeight;
    private int actualXPosition;
    private int actualYPosition;
    private LinkedList<Integer> shipPanePositionsList;
    private ImageView shipImageView;

    public Ship(int shipSize, int fitHeight, char shipOrientation) {
        this.shipSize = shipSize;
        this.fitHeight = fitHeight;
        if (shipOrientation == 'v') {
            this.urlImageShip = "ship" + shipSize + "v.png";
            this.shipWidth = fitHeight;
            this.shipHeight = fitHeight * shipSize;
        } else {
            this.urlImageShip = "ship" + shipSize + ".png";
            this.shipWidth = fitHeight * shipSize;
            this.shipHeight = fitHeight;
        }
        this.imageClassShip = new Image(urlImageShip, shipWidth, shipHeight, false, true);
        this.shipOrientation = shipOrientation;
        this.shipPanePositionsList = new LinkedList<>();
        setShipImageView();
    }

    public ImageView setShipImageView() {
        Image image = imageClassShip;
        ImageView imageView = new ImageView(image);
        imageView.getStyleClass().add("ship");
        this.shipImageView = imageView;
        return imageView;
    }

    public ImageView getShipImageView() {
        return shipImageView;
    }

    public int getShipSize() {
        return shipSize;
    }

    public char getShipOrientation() {
        return shipOrientation;
    }

    public int getActualXPosition() {
        return actualXPosition;
    }

    public int getActualYPosition() {
        return actualYPosition;
    }

    public LinkedList<Integer> getShipPositionsList() {
        return shipPanePositionsList;
    }

    public void setPaneShipPositionsList(LinkedList<Integer> shipPanePositionsList) {
        this.shipPanePositionsList = shipPanePositionsList;
    }

    public void setActualXPosition(int positionX) {
        this.actualXPosition = positionX;
        getShipImageView().getProperties().put("gridpane-column", positionX);
    }

    public void setActualYPosition(int positionY) {
        this.actualYPosition = positionY;
        getShipImageView().getProperties().put("gridpane-row", positionY);
    }

    public void changeShipOrientation() {
        char oldShipOrientation = getShipOrientation();
        if (oldShipOrientation == 'h') {
            this.urlImageShip = "ship" + shipSize + "v.png";
            this.shipWidth = fitHeight;
            this.shipHeight = fitHeight * shipSize;
            this.shipOrientation = 'v';
        } else {
            this.urlImageShip = "ship" + shipSize + ".png";
            this.shipWidth = fitHeight * shipSize;
            this.shipHeight = fitHeight;
            this.shipOrientation = 'h';
        }
        this.imageClassShip = new Image(urlImageShip, shipWidth, shipHeight, false, true);
        getShipImageView().setImage(this.imageClassShip);
    }
}
