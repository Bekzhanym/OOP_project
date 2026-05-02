package kbtu_oop_project.domain.features.course;

import java.io.Serializable;

public class Mark implements Serializable {

    private static final long serialVersionUID = 1L;
    private double firstAttestation;
    private double secondAttestation;
    private double finalExam;
    private double totalScore;

    public double calculateGPA() {
        return calculateFinalScore() / 100.0 * 4.0;
    }

    public double calculateFinalScore() {
        totalScore = firstAttestation + secondAttestation + finalExam;
        return totalScore;
    }

    public boolean isPassed() {
        return calculateFinalScore() >= 50;
    }

    public double getFirstAttestation() {
        return firstAttestation;
    }

    public void setFirstAttestation(double firstAttestation) {
        this.firstAttestation = firstAttestation;
    }

    public double getSecondAttestation() {
        return secondAttestation;
    }

    public void setSecondAttestation(double secondAttestation) {
        this.secondAttestation = secondAttestation;
    }

    public double getFinalExam() {
        return finalExam;
    }

    public void setFinalExam(double finalExam) {
        this.finalExam = finalExam;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }
}
