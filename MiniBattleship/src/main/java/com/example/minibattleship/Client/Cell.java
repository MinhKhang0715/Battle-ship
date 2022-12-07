package com.example.minibattleship.Client;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell extends Rectangle {
    //    coordinates
    public int x, y;
    //    does the cell contain a ship, and has it been shot yet?
    private boolean containShip = false, wasShot = false;

    public Cell(int x, int y) {
//        calling super for Rectangle constructor which lets us set the size of each cell
        super(30, 30);
        this.x = x;
        this.y = y;
        this.setFill(Color.WHITESMOKE);
        this.setStroke(Color.BLACK);
    }

    public void setContainShip(boolean containShip) {
        this.containShip = containShip;
        if (this.containShip)
            this.setFill(Color.YELLOW);
    }

    public void setWasShot(boolean wasShot) {
        this.wasShot = wasShot;
        if (this.wasShot && this.containShip)
            this.setFill(Color.CRIMSON);
        if (this.wasShot && !this.containShip)
            this.setFill(Color.GREY);
    }

    public boolean isContainShip() {
        return containShip;
    }

    public boolean getWasShot() {
        return wasShot;
    }


}
