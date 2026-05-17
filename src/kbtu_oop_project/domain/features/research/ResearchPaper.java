package kbtu_oop_project.domain.features.research;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class ResearchPaper implements Serializable, Comparable<ResearchPaper> {

    private static final long serialVersionUID = 1L;

    private final String title;
    private final List<String> authors;
    private final String journal;
    private final String publisher;
    private final String doi;
    private final String keywords;
    private int citations; 
    private final int pages;
    private final LocalDate date;

    public ResearchPaper(String title, List<String> authors, String journal, String publisher,
                         String doi, String keywords, int citations, int pages, LocalDate date) {
        this.title = title != null ? title.trim() : "Untitled";
        this.authors = authors != null ? new ArrayList<>(authors) : new ArrayList<>();
        this.journal = journal != null ? journal.trim() : "—";
        this.publisher = publisher != null ? publisher.trim() : "—";
        this.doi = doi != null ? doi.trim() : "";
        this.keywords = keywords != null ? keywords.trim() : "—";
        this.citations = Math.max(0, citations);
        this.pages = Math.max(1, pages);
        this.date = date != null ? date : LocalDate.MIN;
    }

    public ResearchPaper(String title, int citations, int pages, LocalDate date) {
        this(title, List.of(), null, null, null, null, citations, pages, date);
    }

    @Override
    public int compareTo(ResearchPaper other) {
        if (other == null) return 1;
        
        int byDate = Comparator.nullsFirst(LocalDate::compareTo).compare(other.date, this.date);
        if (byDate != 0) return byDate;
        
        return Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER).compare(this.title, other.title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResearchPaper that = (ResearchPaper) o;
        
        if (!this.doi.isEmpty() && !that.doi.isEmpty()) {
            return this.doi.equalsIgnoreCase(that.doi);
        }
        return Objects.equals(this.title.toLowerCase(), that.title.toLowerCase()) && 
               Objects.equals(this.date, that.date);
    }

    @Override
    public int hashCode() {
        if (!doi.isEmpty()) {
            return Objects.hash(doi.toLowerCase());
        }
        return Objects.hash(title.toLowerCase(), date);
    }

    @Override
    public String toString() {
        return getDetails();
    }

    public String getDetails() {
        String authorStr = authors.isEmpty() ? "n/a" : String.join(", ", authors);
        return String.format(
                "\"%s\" | Авторы: [%s] | Journal: %s | Publisher: %s | DOI: %s | Импакт: %.2f | Цитирования: %d | Стр: %d | Дата: %s",
                title, authorStr, journal, publisher, doi.isEmpty() ? "—" : doi,
                calculateImpact(), citations, pages, date);
    }

    public String toPlainCitation() {
        String authorStr = authors.isEmpty() ? "Unknown Author" : String.join(", ", authors);
        int year = date.getYear();
        return String.format("%s. (%d). \"%s\". %s. Поцитировано: %d раз.", 
                authorStr, year, title, journal, citations);
    }

    public double calculateImpact() {
        return (double) citations / pages;
    }

    public void incrementCitations(int count) {
        if (count > 0) {
            this.citations += count;
        }
    }

    public String getTitle() { return title; }
    public List<String> getAuthors() { return Collections.unmodifiableList(authors); }
    public String getJournal() { return journal; }
    public String getPublisher() { return publisher; }
    public String getDoi() { return doi; }
    public String getKeywords() { return keywords; }
    public int getCitations() { return citations; }
    public int getPages() { return pages; }
    public LocalDate getDate() { return date; }
}