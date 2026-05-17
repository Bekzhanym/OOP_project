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

    public Room() {
        this.roomNumber = "000";
        this.capacity = 1;
        this.type = RoomType.PRACTICE_ROOM;
    }

    public Room(String roomNumber, int capacity, RoomType type) {
        setRoomNumber(roomNumber);
        if (capacity <= 0) {
            throw new IllegalArgumentException("Вместимость аудитории должна быть строго больше нуля.");
        }
        this.capacity = capacity;
        this.type = type != null ? type : RoomType.PRACTICE_ROOM;
    }

    public boolean isSuitableFor(LessonType lessonType) {
        if (this.type == null || lessonType == null) {
            return false;
        }
        
        return switch (this.type) {
            case LECTURE_HALL -> lessonType == LessonType.LECTURE || lessonType == LessonType.PRACTICE;
            case LABORATORY -> lessonType == LessonType.LABORATORY;
            case PRACTICE_ROOM -> lessonType == LessonType.PRACTICE;
        };
    }

    public boolean canAccommodate(int studentCount) {
        return studentCount <= this.capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return roomNumber.equalsIgnoreCase(room.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber.toLowerCase());
    }

    @Override
    public String toString() {
        String typeStr = (type != null) ? type.name() : "ROOM";
        return String.format("Кабинет %s [%s | Мест: %d]", roomNumber, typeStr, capacity);
    }

    public String getRoomNumber() { return roomNumber; }
    
    public void setRoomNumber(String roomNumber) { 
        if (roomNumber == null || roomNumber.isBlank()) {
            throw new IllegalArgumentException("Номер кабинета не может быть пустым.");
        }
        this.roomNumber = roomNumber.trim(); 
    }

    public int getCapacity() { return capacity; }
    
    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Вместимость должна быть положительным числом.");
        }
        this.capacity = capacity;
    }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type != null ? type : RoomType.PRACTICE_ROOM; }
}