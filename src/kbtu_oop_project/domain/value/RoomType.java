package kbtu_oop_project.domain.value;

public enum RoomType {
    LECTURE_HALL(120, "Lecture Hall"),
    
    LABORATORY(30, "Computer Lab"),
    
    PRACTICE_ROOM(40, "Practice / Seminar Room");

    private final int defaultCapacity;
    private final String displayName;

    RoomType(int defaultCapacity, String displayName) {
        this.defaultCapacity = defaultCapacity;
        this.displayName = displayName;
    }

    public int getDefaultCapacity() {
        return defaultCapacity;
    }

    public String getDisplayName() {
        return displayName;
    }
}