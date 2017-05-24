package com.filiereticsa.arc.augmentepf.models;

/**
 * Created by Cécile on 22/05/2017.
 */

public class Place {
    private String name;
    private Position position;

    public Place(String name, Position position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}