package Model;

import java.util.Date;

/**
 * Created by anthony on 07/05/2017.
 */

public class Class {

    private String name;
    private Date startDate;
    private Date endDate;
    private ClassRoom classRoom;

    public Class(String name, Date startDate, Date endDate, ClassRoom classRoom) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.classRoom = classRoom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ClassRoom getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(ClassRoom classRoom) {
        this.classRoom = classRoom;
    }
}
