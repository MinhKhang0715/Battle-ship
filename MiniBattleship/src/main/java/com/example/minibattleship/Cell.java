package com.example.minibattleship;

import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell extends Rectangle {
    public int x,y;
    public boolean containShip = false, wasShot =false;
    public Cell(int x, int y){
        super(30,30);
        this.x = x;
        this.y = y;
        this.setFill(Color.AQUAMARINE);
        this.setStroke(Color.BLACK);
    }

    public Cell(int x, int y, boolean isMyself){
        super(30,30);
        this.x = x;
        this.y = y;
        this.setFill(Color.BLUE);
        this.setStroke(Color.BLACK);
    }

}
