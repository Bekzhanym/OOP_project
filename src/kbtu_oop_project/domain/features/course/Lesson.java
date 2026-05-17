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

    public Lesson() {
        this.day = DayOfWeek.MONDAY;
        this.startTime = LocalTime.of(9, 0);
        this.endTime = LocalTime.of(9, 50);
        this.type = LessonType.LECTURE;
    }

    public Lesson(DayOfWeek day, LocalTime startTime, LocalTime endTime, LessonType type) {
        validateTimes(startTime, endTime);
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }

    public boolean conflictsWith(Lesson other) {
        if (other == null || this.day != other.day) {
            return false;
        }
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }

    public void setTimeSlot(LocalTime startTime, LocalTime endTime) {
        validateTimes(startTime, endTime);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private void validateTimes(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Время начала и окончания не могут быть null.");
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Ошибка интервала: время начала (" + start 
                    + ") должно быть строго раньше времени окончания (" + end + ").");
        }
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
        String typeStr = (type != null) ? type.name() : "LESSON";
        return String.format("[%s | %s] %s - %s", day, typeStr, startTime, endTime);
    }

    
    public DayOfWeek getDay() { return day; }
    public void setDay(DayOfWeek day) { this.day = day; }

    public LocalTime getStartTime() { return startTime; }
    
    @Deprecated
    public void setStartTime(LocalTime startTime) {
        validateTimes(startTime, this.endTime);
        this.startTime = startTime;
    }

    public LocalTime getEndTime() { return endTime; }
    
    @Deprecated
    public void setEndTime(LocalTime endTime) {
        validateTimes(this.startTime, endTime);
        this.endTime = endTime;
    }

    public LessonType getType() { return type; }
    public void setType(LessonType type) { this.type = type; }
}