package kbtu_oop_project.domain.value;

public enum ManagerType {
    
    OFFICE_REGISTRATOR("Office of the Registrar", "Офис Регистратора", 10),
    
    DEPARTMENT("Department Manager", "Менеджер Департамента", 5);

    private final String descriptionEng;
    private final String descriptionRus;
    private final int accessLevel; 

    ManagerType(String descriptionEng, String descriptionRus, int accessLevel) {
        this.descriptionEng = descriptionEng;
        this.descriptionRus = descriptionRus;
        this.accessLevel = accessLevel;
    }

    public String getDescriptionEng() {
        return descriptionEng;
    }

    public String getDescriptionRus() {
        return descriptionRus;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public boolean hasHigherOrEqualAuthority(ManagerType other) {
        if (other == null) return true;
        return this.accessLevel >= other.getAccessLevel();
    }

    @Override
    public String toString() {
        return descriptionEng;
    }
}