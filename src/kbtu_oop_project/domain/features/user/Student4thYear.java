package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.exception.SupervisorQualificationException;
import kbtu_oop_project.domain.features.research.Researcher;

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
