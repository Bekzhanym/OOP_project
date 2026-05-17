package kbtu_oop_project.domain.features.course;

import java.io.Serializable;

public class Mark implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private double firstAttestation;  
    private double secondAttestation; 
    private double finalExam;         
    
    public double getTotalScore() {
        return firstAttestation + secondAttestation + finalExam;
    }

    public String getLetterGrade() {
        double total = getTotalScore();
        if (total >= 94.5) return "A";
        if (total >= 89.5) return "A-";
        if (total >= 84.5) return "B+";
        if (total >= 79.5) return "B";
        if (total >= 74.5) return "B-";
        if (total >= 69.5) return "C+";
        if (total >= 64.5) return "C";
        if (total >= 59.5) return "C-";
        if (total >= 54.5) return "D+";
        if (total >= 49.5) return "D";
        return "F";
    }

    public double calculateGPA() {
        String grade = getLetterGrade();
        switch (grade) {
            case "A":  return 4.00;
            case "A-": return 3.67;
            case "B+": return 3.33;
            case "B":  return 3.00;
            case "B-": return 2.67;
            case "C+": return 2.33;
            case "C":  return 2.00;
            case "C-": return 1.67;
            case "D+": return 1.33;
            case "D":  return 1.00;
            default:   return 0.00; 
        }
    }

    public double calculateFinalScore() {
        return getTotalScore();
    }

    public boolean isPassed() {
        return getTotalScore() >= 49.5 && finalExam >= 19.5;
    }

    @Override
    public String toString() {
        return String.format("1st Att: %.1f | 2nd Att: %.1f | Final: %.1f | Total: %.1f (%s, GPA: %.2f)",
                firstAttestation, secondAttestation, finalExam, getTotalScore(), getLetterGrade(), calculateGPA());
    }

    public double getFirstAttestation() { return firstAttestation; }
    public void setFirstAttestation(double firstAttestation) {
        if (firstAttestation < 0 || firstAttestation > 30) throw new IllegalArgumentException("1st attestation must be between 0 and 30");
        this.firstAttestation = firstAttestation;
    }

    public double getSecondAttestation() { return secondAttestation; }
    public void setSecondAttestation(double secondAttestation) {
        if (secondAttestation < 0 || secondAttestation > 30) throw new IllegalArgumentException("2nd attestation must be between 0 and 30");
        this.secondAttestation = secondAttestation;
    }

    public double getFinalExam() { return finalExam; }
    public void setFinalExam(double finalExam) {
        if (finalExam < 0 || finalExam > 40) throw new IllegalArgumentException("Final exam must be between 0 and 40");
        this.finalExam = finalExam;
    }
}