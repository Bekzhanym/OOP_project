package kbtu_oop_project.domain.value;

public enum Language {
    ENG("English"),
    KAZ("Kazakh"),
    RUS("Russian");

    private final String name;

    Language(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}