package com.filiereticsa.arc.augmentepf.models;

import java.util.Date;

/**
 * Created by ARC© Team for AugmentEPF project on 07/05/2017.
 */

public class PlannedPath extends Path {

    private AlarmType alarmType;
    private Date whenToAlarmUser;

    public PlannedPath(Position departure, Place closestDeparturePlace, Place arrival,
                       boolean mustTakeElevator, Date departureDate, Date arrivalDate,
                       AlarmType alarmType, Date whenToAlarmUser) {
        super(departure, closestDeparturePlace, arrival, mustTakeElevator, departureDate, arrivalDate);
        this.alarmType = alarmType;
        this.whenToAlarmUser = whenToAlarmUser;
    }

    public AlarmType getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(AlarmType alarmType) {
        this.alarmType = alarmType;
    }

    public Date getWhenToAlarmUser() {
        return whenToAlarmUser;
    }

    public void setWhenToAlarmUser(Date whenToAlarmUser) {
        this.whenToAlarmUser = whenToAlarmUser;
    }
}
