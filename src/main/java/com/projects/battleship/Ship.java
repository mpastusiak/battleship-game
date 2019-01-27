package com.projects.battleship;

import javafx.scene.image.Image;

import java.net.URL;

public class Ship {
    private int shipSize;
    private int fitHeight;
    private Image imageClassShip;
    private String urlImageShip;
    private char shipOrientation;
    private int shipWidth;
    private int shipHeight;

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

    public Image getImageClassShip() {
        return imageClassShip;
    }

    public int getShipSize() {
        return shipSize;
    }

    public char getShipOrientation() {
        return shipOrientation;
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
