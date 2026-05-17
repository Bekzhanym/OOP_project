package kbtu_oop_project.domain.features.course;

import kbtu_oop_project.domain.value.LessonType;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class Lesson implements Serializable {

    private static final long serialVersionUID = 1L;
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private LessonType type;

    public Lesson() {}

    public Lesson(DayOfWeek day, LocalTime startTime, LocalTime endTime, LessonType type) {
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }

    public boolean conflictsWith(Lesson other) {
        if (other == null || this.day != other.day) {
            return false;
        }
        return !this.startTime.isAfter(other.endTime) && !other.startTime.isAfter(this.endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return day == lesson.day && 
               Objects.equals(startTime, lesson.startTime) && 
               Objects.equals(endTime, lesson.endTime) && 
               type == lesson.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, startTime, endTime, type);
    }

    @Override
    public String toString() {
        String typeStr = (type != null) ? type.toString() : "LESSON";
        return String.format("[%s] %s: %s - %s", day, typeStr, startTime, endTime);
    }

    public DayOfWeek getDay() { return day; }
    public void setDay(DayOfWeek day) { this.day = day; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public LessonType getType() { return type; }
    public void setType(LessonType type) { this.type = type; }
}