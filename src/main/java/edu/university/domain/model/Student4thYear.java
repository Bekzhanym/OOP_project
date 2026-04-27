package edu.university.domain.model;

public class Student4thYear extends Student {
    private Researcher researchSupervisor;

    public void setSupervisor(Researcher supervisor) {
        this.researchSupervisor = supervisor;
    }

    public Researcher getResearchSupervisor() {
        return researchSupervisor;
    }
}
