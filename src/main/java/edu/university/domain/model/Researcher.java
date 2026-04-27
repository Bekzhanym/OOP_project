package edu.university.domain.model;

import java.util.Comparator;
import java.util.List;

public interface Researcher {
    int getHIndex();

    void setHIndex(int hIndex);

    List<ResearchPaper> getPapers();

    void printPapers(Comparator<ResearchPaper> comparator);

    void addPaper(ResearchPaper paper);
}
