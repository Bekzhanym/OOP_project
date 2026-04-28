package edu.university.domain.sort;

import edu.university.domain.model.ResearchPaper;

import java.util.Comparator;

@FunctionalInterface
public interface PaperComparator extends Comparator<ResearchPaper> {
}
