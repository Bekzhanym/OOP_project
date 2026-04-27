package edu.university.domain.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Transcript {
    private final Map<String, Mark> marksByCourseCode = new HashMap<>();

    public void addMark(String courseCode, Mark mark) {
        marksByCourseCode.put(courseCode, mark);
    }

    public Map<String, Mark> getGradesBySemester(String semesterKey) {
        return Collections.unmodifiableMap(marksByCourseCode);
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
