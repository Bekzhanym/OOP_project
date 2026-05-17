package kbtu_oop_project.domain.value;

import java.util.Locale;

public enum Language {
    
    ENG("en", "English", "Английский", "Ағылшын"),
    KAZ("kk", "Kazakh", "Казахский", "Қазақ"),
    RUS("ru", "Russian", "Русский", "Орыс");

    private final String isoCode;
    private final String nameEng;
    private final String nameRus;
    private final String nameKaz;

    Language(String isoCode, String nameEng, String nameRus, String nameKaz) {
        this.isoCode = isoCode;
        this.nameEng = nameEng;
        this.nameRus = nameRus;
        this.nameKaz = nameKaz;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getNameEng() {
        return nameEng;
    }

    public String getNameRus() {
        return nameRus;
    }

    public String getNameKaz() {
        return nameKaz;
    }

    public Locale toLocale() {
        return new Locale(this.isoCode);
    }

    public String getNameByContext(Language contextLanguage) {
        if (contextLanguage == null) return nameEng;
        return switch (contextLanguage) {
            case ENG -> nameEng;
            case RUS -> nameRus;
            case KAZ -> nameKaz;
        };
    }

    @Override
    public String toString() {
        return nameEng; 
    }
}