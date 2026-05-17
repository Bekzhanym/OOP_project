package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.course.Lesson;
import kbtu_oop_project.domain.features.course.Mark;
import kbtu_oop_project.domain.features.research.ResearchPaper;
import kbtu_oop_project.domain.features.research.ResearchProject;
import kbtu_oop_project.domain.features.research.Researcher;
import kbtu_oop_project.domain.value.CourseType;
import kbtu_oop_project.domain.value.TeacherTitle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class Teacher extends Employee implements Researcher {
    
    private TeacherTitle teacherTitle;
    private ResearcherProfile researcherProfile;

    private ResearcherProfile getProfile() {
        if (researcherProfile == null && teacherTitle == TeacherTitle.PROFESSOR) {
            researcherProfile = new ResearcherProfile(); 
        }
        return researcherProfile;
    }

    public boolean isResearcher() {
        return getProfile() != null;
    }

    @Override
    public int getHIndex() {
        return isResearcher() ? getProfile().getHIndex() : 0;
    }

    @Override
    public void setHIndex(int hIndex) {
        if (!isResearcher() && teacherTitle != TeacherTitle.PROFESSOR) {
           this.researcherProfile = new ResearcherProfile();
        }
        getProfile().setHIndex(hIndex);
    }

    @Override
    public List<ResearchPaper> getPapers() {
        return isResearcher() ? getProfile().getPapers() : Collections.emptyList();
    }

    @Override
    public void addPaper(ResearchPaper paper) {
        if (!isResearcher()) {
            this.researcherProfile = new ResearcherProfile();
        }
        getProfile().addPaper(paper);
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> comparator) {
        if (!isResearcher()) {
            System.out.println("Данный преподаватель не занимается научной деятельностью.");
            return;
        }
        getProfile().printPapers(comparator);
    }

    @Override
    public List<ResearchProject> getResearchProjects() {
        return isResearcher() ? getProfile().getResearchProjects() : Collections.emptyList();
    }

    @Override
    public void addResearchProject(ResearchProject project) {
        if (!isResearcher()) {
            this.researcherProfile = new ResearcherProfile();
        }
        getProfile().addResearchProject(project);
    }

    public TeacherTitle getTeacherTitle() { return teacherTitle; }
    
    public void setTeacherTitle(TeacherTitle teacherTitle) { 
        this.teacherTitle = teacherTitle; 
        if (teacherTitle == TeacherTitle.PROFESSOR && this.researcherProfile == null) {
            this.researcherProfile = new ResearcherProfile(); // Синхронизируем статус
        }
    }
}