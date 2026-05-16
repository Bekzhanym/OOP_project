package kbtu_oop_project.domain.features.course;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Transcript implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Map<String, Mark> marksByCourseCode = new HashMap<>();

    public void addMark(String courseCode, Mark mark) {
        marksByCourseCode.put(courseCode, mark);
    }

    public Map<String, Mark> getGradesBySemester(String semesterKey) {
        return Collections.unmodifiableMap(marksByCourseCode);
    }

    public Collection<Mark> allMarks() {
        return Collections.unmodifiableCollection(marksByCourseCode.values());
    }

    public boolean hasMarks() {
        return !marksByCourseCode.isEmpty();
    }

    /** Оценка по коду курса (как при {@link #addMark}). */
    public Mark getMarkForCourse(String courseCode) {
        if (courseCode == null || courseCode.isBlank()) {
            return null;
        }
        Mark exact = marksByCourseCode.get(courseCode.trim());
        if (exact != null) {
            return exact;
        }
        return marksByCourseCode.entrySet().stream()
                .filter(e -> e.getKey() != null && e.getKey().trim().equalsIgnoreCase(courseCode.trim()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private double calculateTotalGPA() {
        if (marksByCourseCode.isEmpty()) {
            return 0;
        }
        return marksByCourseCode.values().stream().mapToDouble(Mark::calculateGPA).average().orElse(0);
    }

    public double getTotalGPA() {
        return calculateTotalGPA();
    }
}
