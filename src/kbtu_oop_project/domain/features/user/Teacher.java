package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.research.ResearchPaper;
import kbtu_oop_project.domain.features.research.Researcher;
import kbtu_oop_project.domain.value.TeacherTitle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Teacher extends Employee implements Researcher {

    private static final long serialVersionUID = 1L;

    private String department;
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

    public void attachTeachingAssignment(Course course) {
        internalRegisterCourse(course);
    }

    public void detachTeachingAssignment(Course course) {
        internalUnregisterCourse(course);
    }

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
