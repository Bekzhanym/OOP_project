package edu.university.domain.model;

import edu.university.domain.value.RoomType;

import java.io.Serializable;

public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    private String roomNumber;
    private int capacity;
    private RoomType type;

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }
}
