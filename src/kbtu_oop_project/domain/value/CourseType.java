package kbtu_oop_project.domain.value;

import java.util.ArrayList;
import java.util.List;

public enum CourseType {
    
    MAJOR(true, "Core Major Discipline", "Обязательный профильный предмет"),
    
    ELECTIVE(false, "Elective Discipline", "Элективная дисциплина"),
    
    MINOR(false, "Minor Program Course", "Курс программы Minor"),
    
    REQUIRED_GENERAL(true, "Required General Education", "Обязательный общеобразовательный предмет");

    private final boolean isRequired;
    private final String descriptionEng;
    private final String descriptionRus;

    CourseType(boolean isRequired, String descriptionEng, String descriptionRus) {
        this.isRequired = isRequired;
        this.descriptionEng = descriptionEng;
        this.descriptionRus = descriptionRus;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public String getDescriptionEng() {
        return descriptionEng;
    }

    public String getDescriptionRus() {
        return descriptionRus;
    }

    public static List<CourseType> getRequiredTypes() {
        List<CourseType> required = new ArrayList<>();
        for (CourseType type : CourseType.values()) {
            if (type.isRequired()) {
                required.add(type);
            }
        }
        return required;
    }

    public static List<CourseType> getElectiveTypes() {
        List<CourseType> electives = new ArrayList<>();
        for (CourseType type : CourseType.values()) {
            if (!type.isRequired()) {
                electives.add(type);
            }
        }
        return electives;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name(), descriptionEng);
    }
}