package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.course.Transcript;
import kbtu_oop_project.domain.features.research.ResearchPaper;
import kbtu_oop_project.domain.features.research.Researcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Student extends User implements Researcher {

    private static final long serialVersionUID = 1L;
    private static final int MAX_CREDITS_PER_TERM = 21;
    private static final int MAX_FAILED_ATTEMPTS = 3;

    private double gpa;
    private int yearOfStudy;
    private final List<Course> enrolledCourses = new ArrayList<>();
    private int totalCredits;
    private int failedAttempts;
    private final Transcript transcript = new Transcript();
    private int hIndex;
    private final List<ResearchPaper> papers = new ArrayList<>();

    @Override
    public void login() {
    }

    @Override
    public void logout() {
    }

    @Override
    public void changePassword(String newPassword) {
        setPassword(newPassword);
    }

    public void registerForCourse(Course course) {
        if (totalCredits + course.getCredits() > MAX_CREDITS_PER_TERM) {
            throw new IllegalStateException("Pre: totalCredits + c.credits <= " + MAX_CREDITS_PER_TERM);
        }
        enrolledCourses.add(course);
        totalCredits += course.getCredits();
    }

    public void viewTranscript() {
    }

    public void submitStartup(String title) {
    }

    public void requestRecommendation(Teacher teacher) {
        teacher.writeRecommendation(this);
    }

    public void addFailedAttempt() {
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            throw new IllegalStateException("max failed attempts");
        }
        failedAttempts++;
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

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public Transcript getTranscript() {
        return transcript;
    }
}
