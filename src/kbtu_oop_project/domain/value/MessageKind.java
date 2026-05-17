package kbtu_oop_project.domain.value;

public enum MessageKind {
    
    MESSAGE("General Message", "Общее сообщение", UrgencyLevel.LOW),
    
    COMPLAINT("Official Complaint", "Официальная жалоба", UrgencyLevel.HIGH),
    
    REQUEST("Academic Request", "Академический запрос", UrgencyLevel.MEDIUM);

    private final String englishTitle;
    private final String russianTitle;
    private final UrgencyLevel defaultUrgency;

    MessageKind(String englishTitle, String russianTitle, UrgencyLevel defaultUrgency) {
        this.englishTitle = englishTitle;
        this.russianTitle = russianTitle;
        this.defaultUrgency = defaultUrgency;
    }

    public String getEnglishTitle() {
        return englishTitle;
    }

    public String getRussianTitle() {
        return russianTitle;
    }

    public UrgencyLevel getDefaultUrgency() {
        return defaultUrgency;
    }

    public enum UrgencyLevel {
        LOW, MEDIUM, HIGH
    }

    public boolean requiresOfficialResponse() {
        return this == COMPLAINT || this == REQUEST;
    }
}