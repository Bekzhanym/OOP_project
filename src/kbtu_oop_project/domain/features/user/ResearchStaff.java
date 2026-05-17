package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.research.ResearchPaper;
import kbtu_oop_project.domain.features.research.ResearchProject;
import kbtu_oop_project.domain.features.research.Researcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ResearchStaff extends Employee implements Researcher {

    private static final long serialVersionUID = 1L;

    private int hIndex;
    private final List<ResearchPaper> papers = new ArrayList<>();
    private final List<ResearchProject> researchProjects = new ArrayList<>();

    public ResearchStaff() {
        super();
    }

    public ResearchStaff(String id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email, password);
    }

    @Override
    public int getHIndex() {
        return hIndex;
    }

    @Deprecated
    public void setHIndex(int hIndex) {
        this.hIndex = hIndex;
    }

    @Override
    public List<ResearchPaper> getPapers() {
        return Collections.unmodifiableList(papers);
    }

    @Override
    public void printPapers(Comparator<ResearchPaper> comparator) {
        if (papers.isEmpty()) {
            System.out.println("У данного сотрудника пока нет опубликованных работ.");
            return;
        }
        
        List<ResearchPaper> copy = new ArrayList<>(papers);
        copy.sort(comparator);
        
        System.out.println("\n=== НАУЧНЫЕ ТРУДЫ СОТРУДНИКА ===");
        for (ResearchPaper paper : copy) {
            System.out.println(paper.getDetails());
        }
    }

    @Override
    public void addPaper(ResearchPaper paper) {
        if (paper != null && !papers.contains(paper)) {
            papers.add(paper);
            updateHIndex(); 
        }
    }

    @Override
    public List<ResearchProject> getResearchProjects() {
        return Collections.unmodifiableList(researchProjects);
    }

    @Override
    public void addResearchProject(ResearchProject project) {
        if (project == null) return;

        if (!researchProjects.contains(project)) {
            researchProjects.add(project);
            
            if (!project.getParticipants().contains(this)) {
                project.addParticipant(this); 
            }
        }
    }

    private void updateHIndex() {
        if (papers.isEmpty()) {
            this.hIndex = 0;
            return;
        }

        List<ResearchPaper> sortedPapers = new ArrayList<>(papers);
        
        sortedPapers.sort((p1, p2) -> Integer.compare(p2.getCitations(), p1.getCitations()));

        int h = 0;
        for (int i = 0; i < sortedPapers.size(); i++) {
            
            if (sortedPapers.get(i).getCitations() >= (i + 1)) {
                h = i + 1;
            } else {
                break;
            }
        }
        this.hIndex = h;
    }
}