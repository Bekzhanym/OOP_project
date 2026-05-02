package edu.university.domain.model;

import edu.university.domain.exception.SupervisorQualificationException;

public class Student4thYear extends Student {

    private static final long serialVersionUID = 1L;

    private Researcher researchSupervisor;

    public void setSupervisor(Researcher supervisor) {
        if (supervisor == null) {
            throw new IllegalArgumentException("Supervisor cannot be null");
        }
        if (supervisor.getHIndex() < SupervisorQualificationException.minimumRequired()) {
            throw SupervisorQualificationException.belowMinimum(supervisor.getHIndex());
        }
        this.researchSupervisor = supervisor;
    }

    public Researcher getResearchSupervisor() {
        return researchSupervisor;
    }
}
