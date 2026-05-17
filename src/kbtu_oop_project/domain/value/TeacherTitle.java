package kbtu_oop_project.domain.value;

public enum TeacherTitle {
    TUTOR("Тьютор / Ассистент", false),
    
    LECTOR("Лектор", false),
    
    SENIOR_LECTOR("Старший преподаватель", false),
    
    PROFESSOR("Профессор", true);

    private final String displayRu;
    private final boolean isAlwaysResearcher;

    TeacherTitle(String displayRu, boolean isAlwaysResearcher) {
        this.displayRu = displayRu;
        this.isAlwaysResearcher = isAlwaysResearcher;
    }

    public String getDisplayRu() {
        return displayRu;
    }

    public boolean isAlwaysResearcher() {
        return isAlwaysResearcher;
    }
}