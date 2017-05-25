package com.filiereticsa.arc.augmentepf.models;

import java.util.ArrayList;

/**
 * Created by anthony on 07/05/2017.
 */

public class ICalTimeTable {

    private ArrayList<Class> classes;
    private Class nextClass = null;

    public ICalTimeTable(ArrayList<Class> classes) {
        this.classes = classes;
    }

    public ArrayList<Class> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<Class> classes) {
        this.classes = classes;
    }

    public Class getNextClass() {
        if (nextClass != null) {
            return nextClass;
        } else {
            nextClass = null;
            for (int i = 0; i < classes.size(); i++) {
                Class currentClass = classes.get(i);
                if ((nextClass == null
                        && currentClass.getStartDate().getTime() > System.currentTimeMillis())
                        || (nextClass != null
                        && currentClass.getStartDate().getTime() < nextClass.getStartDate().getTime())) {

                    nextClass = currentClass;
                }
            }
        }
        return nextClass;
    }

    public void setNextClass(Class nextClass) {
        this.nextClass = nextClass;
    }
}
