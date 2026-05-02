package edu.university.domain.sort;

public final class ResearchPaperComparators {
    public static final DateComparator BY_DATE =
            (a, b) -> a.getDate().compareTo(b.getDate());

    public static final CitationsComparator BY_CITATIONS =
            (a, b) -> Integer.compare(a.getCitations(), b.getCitations());

    /** Assignment: sort by article length using page count. */
    public static final LengthComparator BY_PAGES =
            (a, b) -> Integer.compare(a.getPages(), b.getPages());

    public static final LengthComparator BY_TITLE_LENGTH =
            (a, b) -> Integer.compare(a.getTitle().length(), b.getTitle().length());

    private ResearchPaperComparators() {
    }
}
