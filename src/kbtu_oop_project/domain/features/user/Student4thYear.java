package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.exception.SupervisorQualificationException;
import kbtu_oop_project.domain.features.research.Researcher;

public class Student4thYear extends Student {

    private static final long serialVersionUID = 1L;

    private Researcher researchSupervisor;

    public Student4thYear() {
        super();
        this.setYearOfStudy(4);
    }

    public Student4thYear(String id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email, password, 4);
    }

    public void setSupervisor(Researcher supervisor) {
        if (supervisor == null) {
            throw new IllegalArgumentException("Supervisor cannot be null");
        }
        if (supervisor.getHIndex() < SupervisorQualificationException.minimumRequired()) {
            String msg = "Supervisor h-index must be >= " + SupervisorQualificationException.minimumRequired()
                    + " (given: " + supervisor.getHIndex() + ")";
            throw new SupervisorQualificationException(msg);
        }
        this.researchSupervisor = supervisor;
    }

    public Researcher getResearchSupervisor() {
        return researchSupervisor;
    }

    @Override
    public String toString() {
        String supervisorInfo = "Не назначен";
        if (researchSupervisor != null) {
            if (researchSupervisor instanceof User) {
                supervisorInfo = ((User) researchSupervisor).getFullName() + " (h-index: " + researchSupervisor.getHIndex() + ")";
            } else {
                supervisorInfo = "Researcher (h-index: " + researchSupervisor.getHIndex() + ")";
            }
        }

        return String.format(
                "=== ВЫПУСКНИК (4 КУРС) ===\n" +
                "ФИО: %s\n" +
                "ID: %s | Email: %s\n" +
                "Научный руководитель: %s\n" +
                "=========================",
                getFullName(), getStudentId(), getEmail(), supervisorInfo
        );
    }
}