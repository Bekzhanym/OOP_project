package edu.university.domain.model;

import java.time.LocalDate;

public class ResearchPaper {
    private String title;
    private int citations;
    private int pages;
    private LocalDate date;

    public ResearchPaper(String title, int citations, int pages, LocalDate date) {
        this.title = title;
        this.citations = citations;
        this.pages = pages;
        this.date = date;
    }

    public String getDetails() {
        return title + " (" + date + "), citations=" + citations + ", pages=" + pages;
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
