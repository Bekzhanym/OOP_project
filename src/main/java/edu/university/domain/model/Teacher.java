package edu.university.domain.model;

import edu.university.domain.value.TeacherTitle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Teacher extends Employee implements Researcher {
    private String department;
    private final List<Course> taughtCourses = new ArrayList<>();
    private TeacherTitle title;
    private int hIndex;
    private final List<ResearchPaper> papers = new ArrayList<>();

    public void putMark() {
    }

    public void manageCourse() {
    }

    public void writeRecommendation(Student student) {
    }

    public void exportGradeReport(Course course) {
    }

    @Override
    public int getHIndex() {
        return hIndex;
    }

    @Override
    public void setHIndex(int hIndex) {
        this.hIndex = hIndex;
    }

    @Override
    public List<ResearchPaper> getPapers() {
        return papers;
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> comparator) {
        List<ResearchPaper> copy = new ArrayList<>(papers);
        copy.sort(comparator);
        for (ResearchPaper paper : copy) {
            System.out.println(paper.getDetails());
        }
    }

    @Override
    public void addPaper(ResearchPaper paper) {
        papers.add(paper);
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Course> getTaughtCourses() {
        return taughtCourses;
    }

    public TeacherTitle getTitle() {
        return title;
    }

    public void setTitle(TeacherTitle title) {
        this.title = title;
    }
}
