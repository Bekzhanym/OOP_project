package kbtu_oop_project.domain.value;

public enum LessonType {
    LECTURE("Lecture"),
    PRACTICE("Practice"),
    LABORATORY("Laboratory"); 

    private final String description;

    LessonType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}