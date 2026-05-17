package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.value.TeacherTitle;
import kbtu_oop_project.domain.features.research.ResearchPaper;
import kbtu_oop_project.domain.features.research.ResearchProject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Professor extends Teacher {

    private static final long serialVersionUID = 1L;

    
    private final List<ResearchPaper> papers = new ArrayList<>();
    private final List<ResearchProject> projects = new ArrayList<>();
    private int hIndex = 0; 

    public Professor() {
        super();
        this.setTitle(TeacherTitle.PROFESSOR);
    }

    public Professor(String id, String firstName, String lastName, String email, String password) {
        
        super(id, firstName, lastName, email, password, TeacherTitle.PROFESSOR); 
    }

    @Override
    public void login() {
        System.out.println(String.format("[AUTH] Уважаемый профессор %s %s вошел в систему.", 
                getFirstName(), getLastName()));
    }

    @Override
    public void setTitle(TeacherTitle title) {
        
        super.setTitle(TeacherTitle.PROFESSOR);
    }

    

    @Override
    public int getHIndex() {
        return this.hIndex;
    }

    
    public void setHIndex(int hIndex) {
        this.hIndex = hIndex;
    }

    @Override
    public List<ResearchPaper> getPapers() {
        return this.papers;
    }

    @Override
    public void addPaper(ResearchPaper paper) {
        if (paper != null && !this.papers.contains(paper)) {
            this.papers.add(paper);
        }
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> comparator) {
        if (this.papers.isEmpty()) {
            System.out.println(String.format("У профессора %s пока нет научных публикаций.", getLastName()));
            return;
        }
        
        List<ResearchPaper> sortedCopy = new ArrayList<>(this.papers);
        sortedCopy.sort(comparator);
        
        System.out.println(String.format("=== Научные труды профессора %s %s ===", getFirstName(), getLastName()));
        for (ResearchPaper paper : sortedCopy) {
            System.out.println(paper); 
        }
    }

    @Override
    public List<ResearchProject> getResearchProjects() {
        return this.projects;
    }

    @Override
    public void addResearchProject(ResearchProject project) {
        if (project != null && !this.projects.contains(project)) {
            this.projects.add(project);
        }
    }
}