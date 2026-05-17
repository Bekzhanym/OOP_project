package kbtu_oop_project.domain.features.research;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ResearcherProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private int hIndex;
    private final List<ResearchPaper> papers = new ArrayList<>();
    private final List<ResearchProject> researchProjects = new ArrayList<>();

    public int getHIndex() { return hIndex; }
    public void setHIndex(int hIndex) { this.hIndex = hIndex; }

    public List<ResearchPaper> getPapers() {
        return Collections.unmodifiableList(papers);
    }

    public void addPaper(ResearchPaper paper) {
        if (paper != null && !papers.contains(paper)) {
            papers.add(paper);
        }
    }

    public List<ResearchProject> getResearchProjects() {
        return Collections.unmodifiableList(researchProjects);
    }

    public void addResearchProject(ResearchProject project) {
        if (project != null && !researchProjects.contains(project)) {
            researchProjects.add(project);
        }
    }

    public void printPapers(Comparator<ResearchPaper> comparator) {
        if (papers.isEmpty()) {
            System.out.println("Нет опубликованных научных трудов.");
            return;
        }
        List<ResearchPaper> copy = new ArrayList<>(papers);
        copy.sort(comparator);
        for (ResearchPaper paper : copy) {
            System.out.println(paper.getDetails()); // Метод из ТЗ (IEEE fields)
        }
    }
}