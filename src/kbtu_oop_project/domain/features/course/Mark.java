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

    public int calculateFinalScore() {
        return (int) Math.round(getTotalScore());
    }

    public String getLetterGrade() {
        double termScore = firstAttestation + secondAttestation;
        
        if (termScore < 30.0) {
            return "F";
        }
        
        if (finalExam < 20.0) {
            return "FX";
        }

        int total = calculateFinalScore();
        if (total < 50) {
            return "FX";
        }

        if (total >= 95) return "A";
        if (total >= 90) return "A-";
        if (total >= 85) return "B+";
        if (total >= 80) return "B";
        if (total >= 75) return "B-";
        if (total >= 70) return "C+";
        if (total >= 65) return "C";
        if (total >= 60) return "C-";
        if (total >= 55) return "D+";
        if (total >= 50) return "D";
        
        return "F";
    }

    public double calculateGPA() {
        return switch (getLetterGrade()) {
            case "A"  -> 4.00;
            case "A-" -> 3.67;
            case "B+" -> 3.33;
            case "B"  -> 3.00;
            case "B-" -> 2.67;
            case "C+" -> 2.33;
            case "C"  -> 2.00;
            case "C-" -> 1.67;
            case "D+" -> 1.33;
            case "D"  -> 1.00;
            default   -> 0.00; 
        };
    }

    public boolean isPassed() {
        String grade = getLetterGrade();
        return !grade.equals("F") && !grade.equals("FX");
    }

    public boolean isAllowedToExam() {
        return (firstAttestation + secondAttestation) >= 30.0;
    }

    @Override
    public String toString() {
        return String.format("1st Att: %.1f/30 | 2nd Att: %.1f/30 | Exam: %.1f/40 | Total: %d (%s, GPA: %.2f) [%s]",
                firstAttestation, secondAttestation, finalExam, calculateFinalScore(), 
                getLetterGrade(), calculateGPA(), getLetterGrade());
    }

    public double getFirstAttestation() { return firstAttestation; }
    public void setFirstAttestation(double firstAttestation) {
        if (firstAttestation < 0.0 || firstAttestation > 30.0) {
            throw new IllegalArgumentException("Балл за первую аттестацию должен быть в диапазоне от 0 до 30");
        }
        this.firstAttestation = firstAttestation;
    }

    public double getSecondAttestation() { return secondAttestation; }
    public void setSecondAttestation(double secondAttestation) {
        if (secondAttestation < 0.0 || secondAttestation > 30.0) {
            throw new IllegalArgumentException("Балл за вторую аттестацию должен быть в диапазоне от 0 до 30");
        }
        this.secondAttestation = secondAttestation;
    }

    public double getFinalExam() { return finalExam; }
    public void setFinalExam(double finalExam) {
        if (!isAllowedToExam() && finalExam > 0.0) {
            throw new IllegalStateException("Невозможно выставить балл за экзамен: у студента нет академического допуска!");
        }
        if (finalExam < 0.0 || finalExam > 40.0) {
            throw new IllegalArgumentException("Балл за экзамен должен быть в диапазоне от 0 до 40");
        }
        this.finalExam = finalExam;
    }
}