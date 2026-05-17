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
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Collection<Mark> allMarks() {
        return Collections.unmodifiableCollection(courseMarks.values());
    }

    public boolean hasMarks() {
        return !courseMarks.isEmpty();
    }

    public Map<String, Mark> getGradesBySemester(String semesterFilter) {
        if (semesterFilter == null || semesterFilter.isBlank()) {
            return Collections.emptyMap();
        }

        return courseMarks.entrySet().stream()
                .filter(e -> e.getKey().getCourseCode() != null)
                .filter(e -> matchSemesterOrCode(e.getKey(), semesterFilter))
                .collect(Collectors.toMap(
                        e -> e.getKey().getCourseCode(),
                        Map.Entry::getValue,
                        (existingMark, newMark) -> existingMark.getTotalScore() >= newMark.getTotalScore() ? existingMark : newMark
                ));
    }

    private boolean matchSemesterOrCode(Course course, String semesterFilter) {
        if (course.getCourseName() == null) return false;
        return true; 
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
            
            int credits = course.getCredits();
            if (credits > 0) {
                totalWeightedPoints += mark.calculateGPA() * credits;
                totalCredits += credits;
            }
        }

        if (totalCredits == 0) return 0.0;
        
        double rawGpa = totalWeightedPoints / totalCredits;
        
        return Math.round(rawGpa * 100.0) / 100.0;
    }

    public double getTotalGPA() {
        return calculateTotalGPA();
    }

    @Override
    public String toString() {
        if (courseMarks.isEmpty()) {
            return "=================== OFFICIAL TRANSCRIPT ===================\n" +
                   " Траскрипт пуст (нет завершенных дисциплин).\n" +
                   "===========================================================";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n=================== OFFICIAL TRANSCRIPT ===================\n");
        
        courseMarks.forEach((course, mark) -> {
            sb.append(String.format(" %-9s | %-25.25s | %2d ECTS | %-2s (Points: %3d, GPA: %.2f)\n",
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getCredits(),
                    mark.getLetterGrade(),
                    mark.calculateFinalScore(),
                    mark.calculateGPA()));
        });
        
        sb.append("-----------------------------------------------------------\n");
        sb.append(String.format(" Итоговый кумулятивный GPA (Cumulative GPA): %.2f\n", getTotalGPA()));
        sb.append("===========================================================");
        return sb.toString();
    }
}