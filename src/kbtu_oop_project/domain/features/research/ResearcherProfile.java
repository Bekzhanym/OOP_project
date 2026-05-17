package kbtu_oop_project.domain.features.research;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ResearcherProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<ResearchPaper> papers = new ArrayList<>();
    private final List<ResearchProject> researchProjects = new ArrayList<>();

   public int getHIndex() {
        if (papers.isEmpty()) return 0;

        List<Integer> citations = papers.stream()
                .map(ResearchPaper::getCitations)
                .sorted(Comparator.reverseOrder())
                .toList();

        int hIndex = 0;
        for (int i = 0; i < citations.size(); i++) {
            if (citations.get(i) >= i + 1) {
                hIndex = i + 1;
            } else {
                break;
            }
        }
        return hIndex;
    }

    
    public List<ResearchPaper> getPapers() {
        return Collections.unmodifiableList(papers);
    }

    public void addPaper(ResearchPaper paper) {
        if (paper == null) throw new IllegalArgumentException("Статья не может быть null");
        if (!papers.contains(paper)) {
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
            System.out.println(paper.getDetails()); 
        }
    }

    public void printPapers() {
        if (papers.isEmpty()) {
            System.out.println("Нет опубликованных научных трудов.");
            return;
        }
        List<ResearchPaper> copy = new ArrayList<>(papers);
        Collections.sort(copy); 
        for (ResearchPaper paper : copy) {
            System.out.println(paper.getDetails());
        }
    }
}