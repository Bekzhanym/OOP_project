package kbtu_oop_project.domain.value;

public enum TeacherTitle {
    
    TUTOR("Tutor / Assistant", "Тьютор / Ассистент"),
    
    LECTOR("Lecturer", "Лектор"),
    
    SENIOR_LECTOR("Senior Lecturer", "Старший преподаватель"),
    
    PROFESSOR("Professor", "Профессор");

    private final String displayEn;
    private final String displayRu;

    TeacherTitle(String displayEn, String displayRu) {
        this.displayEn = displayEn;
        this.displayRu = displayRu;
    }

    public String getDisplayEn() {
        return displayEn;
    }

    public String getDisplayRu() {
        return displayRu;
    }

    public boolean isAlwaysResearcher() {
        return this == PROFESSOR;
    }

    public boolean canConduct(LessonType lessonType) {
        if (lessonType == null) return false;
        
        return switch (this) {
            case TUTOR -> lessonType == LessonType.PRACTICE || lessonType == LessonType.LABORATORY;
            case LECTOR, SENIOR_LECTOR, PROFESSOR -> true; 
        };
    }

    @Override
    public String toString() {
        return displayEn;
    }
}