package com.example.minibattleship;

import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell extends Rectangle {
//    coordinates
    public int x,y;
//    does the cell contain a ship, and has it been shot yet?
    public boolean containShip = false, wasShot =false;
    public Cell(int x, int y){
//        calling super for Rectangle constructor which lets us set the size of each cell
        super(30,30);
        this.x = x;
        this.y = y;
        this.setFill(Color.AQUAMARINE);
        this.setStroke(Color.BLACK);
    }
//to differentiate our board and the enemy's board
    public Cell(int x, int y, boolean isMyself){
        super(30,30);
        this.x = x;
        this.y = y;
        this.setFill(Color.BLUE);
        this.setStroke(Color.BLACK);
    }

}
