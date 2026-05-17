package kbtu_oop_project.domain.value;

public enum RoomType {
    
    LECTURE_HALL(120, "Lecture Hall", "Лекционная аудитория"),
    
    LABORATORY(30, "Computer Lab", "Компьютерный класс / Лаборатория"),
    
    PRACTICE_ROOM(40, "Practice / Seminar Room", "Кабинет практических занятий");

    private final int defaultCapacity;
    private final String displayNameEng;
    private final String displayNameRus;

    RoomType(int defaultCapacity, String displayNameEng, String displayNameRus) {
        this.defaultCapacity = defaultCapacity;
        this.displayNameEng = displayNameEng;
        this.displayNameRus = displayNameRus;
    }

    public int getDefaultCapacity() {
        return defaultCapacity;
    }

    public String getDisplayNameEng() {
        return displayNameEng;
    }

    public String getDisplayNameRus() {
        return displayNameRus;
    }

    public boolean canAccommodate(int studentCount) {
        return studentCount <= this.defaultCapacity;
    }

    public boolean isCompatibleWith(LessonType lessonType) {
        if (lessonType == null) return false;
        
        return switch (this) {
            case LECTURE_HALL -> lessonType == LessonType.LECTURE;
            case LABORATORY -> lessonType == LessonType.LABORATORY;
            case PRACTICE_ROOM -> lessonType == LessonType.PRACTICE || lessonType == LessonType.LECTURE;
        };
    }

    @Override
    public String toString() {
        return displayNameEng;
    }
}