package kbtu_oop_project.domain.features.course;

import kbtu_oop_project.domain.value.RoomType;
import kbtu_oop_project.domain.value.LessonType; 

import java.io.Serializable;
import java.util.Objects;

public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    private String roomNumber;
    private int capacity;
    private RoomType type;

    public Room() {}

    public Room(String roomNumber, int capacity, RoomType type) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.type = type;
    }

   public boolean isSuitableFor(LessonType lessonType) {
        if (this.type == null || lessonType == null) return false;
        
        switch (this.type) {
            case LECTURE_HALL:
                return lessonType == LessonType.LECTURE;
            case LABORATORY:
            case PRACTICE_ROOM:
                return lessonType == LessonType.PRACTICE;
            default:
                return true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(roomNumber, room.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber);
    }

    @Override
    public String toString() {
        return String.format("Кабинет %s (%s, вместимость: %d мест)", roomNumber, type, capacity);
    }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
    }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }
}