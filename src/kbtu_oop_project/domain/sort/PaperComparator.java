package kbtu_oop_project.domain.sort;

import kbtu_oop_project.domain.features.research.ResearchPaper;

import java.util.Comparator;

@FunctionalInterface
public interface PaperComparator extends Comparator<ResearchPaper> {
}
