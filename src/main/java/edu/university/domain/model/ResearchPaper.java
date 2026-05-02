package edu.university.domain.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Fields aligned with typical bibliographic metadata (title, authors, venue, identifiers).
 */
public class ResearchPaper implements Serializable, Comparable<ResearchPaper> {

    private static final long serialVersionUID = 1L;

    private String title;
    private List<String> authors;
    private String journal;
    private String publisher;
    private String doi;
    private String keywords;
    private int citations;
    private int pages;
    private LocalDate date;

    public ResearchPaper(String title, List<String> authors, String journal, String publisher,
                         String doi, String keywords, int citations, int pages, LocalDate date) {
        this.title = Objects.requireNonNullElse(title, "");
        this.authors = authors != null ? new ArrayList<>(authors) : new ArrayList<>();
        this.journal = journal;
        this.publisher = publisher;
        this.doi = doi;
        this.keywords = keywords;
        this.citations = citations;
        this.pages = pages;
        this.date = date != null ? date : LocalDate.MIN;
    }

    /** Compact ctor for legacy call sites / tests. */
    public ResearchPaper(String title, int citations, int pages, LocalDate date) {
        this(title, List.of(), null, null, null, null, citations, pages,
                date != null ? date : LocalDate.MIN);
    }

    @Override
    public int compareTo(ResearchPaper other) {
        if (other == null) {
            return 1;
        }
        int byDate = Comparator.nullsFirst(LocalDate::compareTo).compare(date, other.date);
        if (byDate != 0) {
            return byDate;
        }
        return Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER).compare(title, other.title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResearchPaper that)) {
            return false;
        }
        if (doi != null && !doi.isBlank() && that.doi != null && !that.doi.isBlank()) {
            return doi.equalsIgnoreCase(that.doi);
        }
        return Objects.equals(title, that.title) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        if (doi != null && !doi.isBlank()) {
            return Objects.hash(doi.toLowerCase());
        }
        return Objects.hash(title, date);
    }

    @Override
    public String toString() {
        return getDetails();
    }

    public String getDetails() {
        String authorStr = authors.isEmpty() ? "n/a" : String.join(", ", authors);
        return String.format(
                "%s | authors=[%s] | %s | journal=%s | publisher=%s | doi=%s | kw=%s | cit=%d | pages=%d | date=%s",
                title,
                authorStr,
                calculateImpact(),
                journal != null ? journal : "—",
                publisher != null ? publisher : "—",
                doi != null ? doi : "—",
                keywords != null ? keywords : "—",
                citations,
                pages,
                date);
    }

    public double calculateImpact() {
        return citations == 0 ? 0 : (double) citations / Math.max(pages, 1);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return Collections.unmodifiableList(authors);
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors != null ? new ArrayList<>(authors) : new ArrayList<>();
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getCitations() {
        return citations;
    }

    public void setCitations(int citations) {
        this.citations = citations;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
