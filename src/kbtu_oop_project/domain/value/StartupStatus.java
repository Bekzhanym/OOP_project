package kbtu_oop_project.domain.value;

public enum StartupStatus {
    UNDER_REVIEW("На рассмотрении"),
    
    APPROVED("Одобрен / В инкубаторе"),
    
    REJECTED("Отклонен"),
    
    FUNDED("Профинансирован / Выпущен");

    private final String description;

    StartupStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}