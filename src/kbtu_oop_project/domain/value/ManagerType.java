package kbtu_oop_project.domain.value;

public enum ManagerType {
    OFFICE_REGISTRATOR("Office of the Registrar"),
    DEPARTMENT("Department Manager");

    private final String description;

    ManagerType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}