package kbtu_oop_project.domain.value;

public enum LessonType {
    
    LECTURE("Lecture", "Лекция", 1, 150),
    
    PRACTICE("Practice", "Практика", 1, 30),
    
    LABORATORY("Laboratory", "Лабораторное занятие", 2, 15);

    private final String descriptionEng;
    private final String descriptionRus;
    private final int defaultDurationHours; 
    private final int maxRecommendedCapacity;

    LessonType(String descriptionEng, String descriptionRus, int defaultDurationHours, int maxRecommendedCapacity) {
        this.descriptionEng = descriptionEng;
        this.descriptionRus = descriptionRus;
        this.defaultDurationHours = defaultDurationHours;
        this.maxRecommendedCapacity = maxRecommendedCapacity;
    }

    public String getDescriptionEng() {
        return descriptionEng;
    }

    public String getDescriptionRus() {
        return descriptionRus;
    }

    public int getDefaultDurationHours() {
        return defaultDurationHours;
    }

    public int getMaxRecommendedCapacity() {
        return maxRecommendedCapacity;
    }

    public boolean isGroupSizeValid(int currentStudentCount) {
        return currentStudentCount <= maxRecommendedCapacity;
    }

    @Override
    public String toString() {
        return descriptionEng;
    }
}