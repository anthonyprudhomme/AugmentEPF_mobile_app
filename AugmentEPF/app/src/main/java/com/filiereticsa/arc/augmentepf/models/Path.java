package com.filiereticsa.arc.augmentepf.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ARC© Team for AugmentEPF project on 07/05/2017.
 */

public class Path {

    private Position departure;
    private Place closestDeparturePlace;
    private Place arrival;
    private ArrayList<Position> path;
    private boolean mustTakeElevator;
    private Date departureDate;
    private Date arrivalDate;

    public static ArrayList<Path> testPath;

    static{
        testPath = new ArrayList<>();
        for (int i=0; i < 10; i++){

            Position pos = new Position(0,0,0);
            Position departureTest = new Position(0,0,0);
            Place closestDeparturePlaceTest = new Place("Closes" + i, pos);
            Place arrivalTest = new Place("ArrName" +i, pos);
            ArrayList<Position> pathTest = new ArrayList<Position>();
            Date DepDateTest = new Date(System.currentTimeMillis());
            Date ArrDateTest = new Date(System.currentTimeMillis());

            Path path = new Path(
                    departureTest,
                    closestDeparturePlaceTest,
                    arrivalTest,
                    pathTest,
                    false,
                    DepDateTest,
                    ArrDateTest
            );

            testPath.add(path);
        }
    }

    public Path(Position departure, Place closestDeparturePlace, Place arrival,
                ArrayList<Position> path, boolean mustTakeElevator, Date departureDate,
                Date arrivalDate) {
        this.departure = departure;
        this.closestDeparturePlace = closestDeparturePlace;
        this.arrival = arrival;
        this.path = path;
        this.mustTakeElevator = mustTakeElevator;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
    }


    public Position getDeparture() {
        return departure;
    }

    public void setDeparture(Position departure) {
        this.departure = departure;
    }

    public Place getArrival() {
        return arrival;
    }

    public void setArrival(Place arrival) {
        this.arrival = arrival;
    }

    public ArrayList<Position> getPath() {
        return path;
    }

    public void setPath(ArrayList<Position> path) {
        this.path = path;
    }

    public boolean isMustTakeElevator() {
        return mustTakeElevator;
    }

    public void setMustTakeElevator(boolean mustTakeElevator) {
        this.mustTakeElevator = mustTakeElevator;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }
}
