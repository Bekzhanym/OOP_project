package kbtu_oop_project.domain.features.research;

import java.util.Comparator;
import java.util.List;

public interface Researcher {

    int getHIndex();

    List<ResearchPaper> getPapers();

    void addPaper(ResearchPaper paper);

    void printPapers(Comparator<ResearchPaper> comparator);

    List<ResearchProject> getResearchProjects();

    void addResearchProject(ResearchProject project);
}
