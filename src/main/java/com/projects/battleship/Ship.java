package com.projects.battleship;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Ship {
    private int shipSize;
    private int fitHeight;
    private Image imageClassShip;
    private String urlImageShip;
    private char shipOrientation;
    private int shipWidth;
    private int shipHeight;
    private double actualXPosition;
    private double actualYPosition;

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
    }

    public ImageView getShipImageView() {
        Image image = imageClassShip;
        ImageView imageView = new ImageView(image);
        imageView.getStyleClass().add("ship");
        return imageView;
    }

    public int getShipSize() {
        return shipSize;
    }

    public char getShipOrientation() {
        return shipOrientation;
    }

    public double getActualXPosition() {
        return actualXPosition;
    }

    public double getActualYPosition() {
        return actualYPosition;
    }

    public void setActualXPosition(double positionX) {
        actualXPosition = positionX;
        getShipImageView().getProperties().put("gridpane-column", positionX);
    }

    public void setActualYPosition(double positionY) {
        actualYPosition = positionY;
        getShipImageView().getProperties().put("gridpane-row", positionY);
    }

    public void setShipOrientation(char shipOrientation) {
        this.shipOrientation = shipOrientation;
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
    }
}
