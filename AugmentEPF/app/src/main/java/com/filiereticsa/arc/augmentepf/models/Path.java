package com.filiereticsa.arc.augmentepf.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ARC© Team for AugmentEPF project on 07/05/2017.
 */

public class Path {

    private Position departure;
    private Position arrival;
    private ArrayList<Position> path;
    private boolean mustTakeElevator;
    private Date departureDate;
    private Date arrivalDate;

    public Path(Position departure, Position arrival, ArrayList<Position> path, boolean mustTakeElevator, Date departureDate, Date arrivalDate) {
        this.departure = departure;
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

    public Position getArrival() {
        return arrival;
    }

    public void setArrival(Position arrival) {
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
