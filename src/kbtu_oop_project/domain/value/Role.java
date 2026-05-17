package kbtu_oop_project.domain.value;

public enum Role {
    STUDENT("Студент", false),
    
    STUDENT_4TH_YEAR("Студент 4-го курса (Выпускник)", false),
    
    TEACHER("Преподаватель", false),
    
    PROFESSOR("Профессор", true),
    
    RESEARCH_STAFF("Научный сотрудник (Researcher Staff)", true),
    
    MANAGER("Академический менеджер", false),
    
    ADMIN("Системный администратор", false),
    
    EMPLOYEE("Сотрудник вуза", false);

    private final String displayName;
    private final boolean isAlwaysResearcher;

    Role(String displayName, boolean isAlwaysResearcher) {
        this.displayName = displayName;
        this.isAlwaysResearcher = isAlwaysResearcher;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAlwaysResearcher() {
        return isAlwaysResearcher;
    }
}