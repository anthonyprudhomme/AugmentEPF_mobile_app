package com.filiereticsa.arc.augmentepf.models;

import java.util.ArrayList;

/**
 * Created by ARC© Team for AugmentEPF project on 07/05/2017.
 */

public enum AlarmType {

    NONE,
    NOTIFICATION,
    VIBRATION,
    ALARM,
    EMAIL;

    public static AlarmType getAlarmAtIndex(int index){
        if (alarmTypes == null){
            fillAlarmTypes();
        }
        return alarmTypes.get(index);
    }

    private static void fillAlarmTypes() {
        alarmTypes = new ArrayList<>();
        alarmTypes.add(NONE);
        alarmTypes.add(NOTIFICATION);
        alarmTypes.add(VIBRATION);
        alarmTypes.add(ALARM);
        alarmTypes.add(EMAIL);
    }

    public static ArrayList<AlarmType> alarmTypes;


}
