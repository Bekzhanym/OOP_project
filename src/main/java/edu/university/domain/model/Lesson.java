package edu.university.domain.model;

import edu.university.domain.value.LessonType;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class Lesson {
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
