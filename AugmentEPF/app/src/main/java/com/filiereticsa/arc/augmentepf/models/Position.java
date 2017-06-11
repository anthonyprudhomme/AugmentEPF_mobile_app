package com.filiereticsa.arc.augmentepf.models;

/**
 * Created by ARC© Team for AugmentEPF project on 07/05/2017.
 */

public class Position {

    private int positionX;
    private int positionY;
    private int floor;

    public Position(int positionX, int positionY, int floor) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.floor = floor;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}
