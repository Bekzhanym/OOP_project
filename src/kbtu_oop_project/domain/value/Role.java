package kbtu_oop_project.domain.value;

public enum Role {
    
    STUDENT("Студент", false, false),
    STUDENT_4TH_YEAR("Студент 4-го курса (Выпускник)", false, false),
    
    TEACHER("Преподаватель", false, true),
    PROFESSOR("Профессор", true, true),
    RESEARCH_STAFF("Научный сотрудник (Researcher Staff)", true, true),
    
    MANAGER("Академический менеджер", false, true),
    ADMIN("Системный администратор", false, true),
    EMPLOYEE("Сотрудник вуза", false, true);

    private final String displayName;
    private final boolean isAlwaysResearcher;
    private final boolean isEmployee; 

    Role(String displayName, boolean isAlwaysResearcher, boolean isEmployee) {
        this.displayName = displayName;
        this.isAlwaysResearcher = isAlwaysResearcher;
        this.isEmployee = isEmployee;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAlwaysResearcher() {
        return isAlwaysResearcher;
    }

    public boolean isEmployee() {
        return isEmployee;
    }

    public boolean isStudentCategory() {
        return this == STUDENT || this == STUDENT_4TH_YEAR;
    }

    public boolean isTeacherCategory() {
        return this == TEACHER || this == PROFESSOR;
    }

    public boolean hasAdministrativePower() {
        return this == ADMIN || this == MANAGER;
    }

    @Override
    public String toString() {
        return displayName;
    }
}