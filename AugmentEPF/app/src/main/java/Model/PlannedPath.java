package Model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by anthony on 07/05/2017.
 */

public class PlannedPath extends Path {


    private AlarmType alarmType;
    private Date whenToAlarmUser;

    public PlannedPath(Position departure, Position arrival, ArrayList<Position> path, boolean mustTakeElevator, Date departureDate, Date arrivalDate, AlarmType alarmType, Date whenToAlarmUser) {
        super(departure, arrival, path, mustTakeElevator, departureDate, arrivalDate);
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
