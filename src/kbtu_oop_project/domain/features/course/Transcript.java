package kbtu_oop_project.domain.features.course;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Transcript implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final Map<Course, Mark> courseMarks = new HashMap<>();

    public void addMark(Course course, Mark mark) {
        if (course != null && mark != null) {
            courseMarks.put(course, mark);
        }
    }

    public Map<Course, Mark> getGradesByYear(int yearOfStudy) {
        return courseMarks.entrySet().stream()
                .filter(entry -> entry.getKey().getIntendedYearOfStudy() == yearOfStudy)
                .collect(Collectors.unmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Collection<Mark> allMarks() {
        return Collections.unmodifiableCollection(courseMarks.values());
    }

    public boolean hasMarks() {
        return !courseMarks.isEmpty();
    }

    public Mark getMarkForCourse(String courseCode) {
        if (courseCode == null || courseCode.isBlank()) {
            return null;
        }
        
        return courseMarks.entrySet().stream()
                .filter(e -> e.getKey().getCourseCode() != null 
                        && e.getKey().getCourseCode().trim().equalsIgnoreCase(courseCode.trim()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private double calculateTotalGPA() {
        if (courseMarks.isEmpty()) {
            return 0.0;
        }

        double totalWeightedPoints = 0.0;
        int totalCredits = 0;

        for (Map.Entry<Course, Mark> entry : courseMarks.entrySet()) {
            Course course = entry.getKey();
            Mark mark = entry.getValue();
            
            // Умножаем GPA за курс на его кредиты (например, 4.0 * 5 кредитов = 20)
            totalWeightedPoints += mark.calculateGPA() * course.getCredits();
            totalCredits += course.getCredits();
        }

        if (totalCredits == 0) return 0.0;
        
        return totalWeightedPoints / totalCredits;
    }

    public double getTotalGPA() {
        return calculateTotalGPA();
    }

    @Override
    public String toString() {
        if (courseMarks.isEmpty()) {
            return "Транскрипт пуст.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("============= OFFICIAL TRANSCRIPT =============\n");
        courseMarks.forEach((course, mark) -> {
            sb.append(String.format("%-10s | %-25s | Кредиты: %d | Оценка: %s (GPA: %.2f)\n",
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getCredits(),
                    mark.getLetterGrade(),
                    mark.calculateGPA()));
        });
        sb.append("-----------------------------------------------\n");
        sb.append(String.format("Итоговый кумулятивный GPA: %.2f\n", getTotalGPA()));
        sb.append("===============================================");
        return sb.toString();
    }
}