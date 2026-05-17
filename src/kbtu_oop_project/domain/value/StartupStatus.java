package kbtu_oop_project.domain.value;

public enum StartupStatus {
    
    UNDER_REVIEW("Under Review", "На рассмотрении"),
    
    APPROVED("Approved / In Incubator", "Одобрен / В инкубаторе"),
    
    REJECTED("Rejected", "Отклонен"),
    
    FUNDED("Funded / Graduated", "Профинансирован / Выпущен");

    private final String descriptionEng;
    private final String descriptionRus;

    StartupStatus(String descriptionEng, String descriptionRus) {
        this.descriptionEng = descriptionEng;
        this.descriptionRus = descriptionRus;
    }

    public String getDescriptionEng() {
        return descriptionEng;
    }

    public String getDescriptionRus() {
        return descriptionRus;
    }

    public boolean isActiveResident() {
        return this == APPROVED || this == FUNDED;
    }

    public boolean canTransitionTo(StartupStatus nextStatus) {
        if (nextStatus == null) return false;

        return switch (this) {
            case UNDER_REVIEW -> nextStatus == APPROVED || nextStatus == REJECTED;
            case APPROVED -> nextStatus == FUNDED || nextStatus == REJECTED;
            case REJECTED -> nextStatus == UNDER_REVIEW; 
            case FUNDED -> false;
        };
    }

    @Override
    public String toString() {
        return descriptionEng;
    }
}