package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.research.ResearchPaper;
import kbtu_oop_project.domain.features.research.Researcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ResearchStaff extends Employee implements Researcher {

    private static final long serialVersionUID = 1L;

    private int hIndex;
    private final List<ResearchPaper> papers = new ArrayList<>();

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
        if (paper != null) {
            papers.add(paper);
        }
    }
}
