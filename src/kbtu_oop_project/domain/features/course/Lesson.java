package kbtu_oop_project.domain.features.course;

import kbtu_oop_project.domain.value.LessonType;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class Lesson implements Serializable {

    private static final long serialVersionUID = 1L;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private LessonType type;

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LessonType getType() {
        return type;
    }

    public void setType(LessonType type) {
        this.type = type;
    }
}
