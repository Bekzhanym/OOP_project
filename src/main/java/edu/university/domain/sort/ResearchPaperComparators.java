package edu.university.domain.sort;

public final class ResearchPaperComparators {
    public static final DateComparator BY_DATE =
            (a, b) -> a.getDate().compareTo(b.getDate());

    public static final CitationsComparator BY_CITATIONS =
            (a, b) -> Integer.compare(a.getCitations(), b.getCitations());

    public static final LengthComparator BY_TITLE_LENGTH =
            (a, b) -> Integer.compare(a.getTitle().length(), b.getTitle().length());

    private ResearchPaperComparators() {
    }
}
