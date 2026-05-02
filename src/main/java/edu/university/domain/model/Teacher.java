package edu.university.domain.model;

import edu.university.domain.value.TeacherTitle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Teacher extends Employee implements Researcher {

    private static final long serialVersionUID = 1L;

    private String department;
    /** Restored after deserialization via {@link Course} instructor lists + DB rebuild. */
    private transient List<Course> taughtCourses;
    private TeacherTitle title;
    private int hIndex;
    private final List<ResearchPaper> papers = new ArrayList<>();

    private List<Course> taughtCoursesBacking() {
        if (taughtCourses == null) {
            taughtCourses = new ArrayList<>();
        }
        return taughtCourses;
    }

    /** Restores reverse link after Course-centric deserialization (called from persistence layer). */
    public void attachTeachingAssignment(Course course) {
        internalRegisterCourse(course);
    }

    /** Called when a {@link Course} assigns this teacher as instructor. */
    void internalRegisterCourse(Course course) {
        if (course != null && !taughtCoursesBacking().contains(course)) {
            taughtCoursesBacking().add(course);
        }
    }

    void internalUnregisterCourse(Course course) {
        taughtCoursesBacking().remove(course);
    }

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
        return Collections.unmodifiableList(taughtCoursesBacking());
    }

    public TeacherTitle getTitle() {
        return title;
    }

    public void setTitle(TeacherTitle title) {
        this.title = title;
    }
}
