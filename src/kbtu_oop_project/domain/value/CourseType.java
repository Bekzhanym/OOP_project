package kbtu_oop_project.domain.value;

public enum CourseType {
    MAJOR(true, "Core Major Discipline"),
    
    ELECTIVE(false, "Elective Discipline"),
    
    MINOR(false, "Minor Program Course"),
    
    REQUIRED_GENERAL(true, "Required General Education");

    private final boolean isRequired;
    private final String description;

    CourseType(boolean isRequired, String description) {
        this.isRequired = isRequired;
        this.description = description;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public String getDescription() {
        return description;
    }
}