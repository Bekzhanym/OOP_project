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
            throw new IllegalArgumentException("Научный руководитель не может быть null.");
        }
        
        
        if (supervisor.getHIndex() < SupervisorQualificationException.minimumRequired()) {
            
            User supervisorUser = (supervisor instanceof User u) ? u : null;
            
            throw SupervisorQualificationException.belowMinimum(supervisorUser, supervisor.getHIndex());
        }
        
        this.researchSupervisor = supervisor;
        System.out.println("✅ Научный руководитель успешно утвержден для выпускника: " + this.getFirstName());
    }

    public Researcher getResearchSupervisor() {
        return researchSupervisor;
    }

    @Override
    public String toString() {
        String supervisorInfo = "Не назначен";
        
        if (researchSupervisor != null) {
            String name = "Научный сотрудник";
            
            if (researchSupervisor instanceof User userSupervisor) {
                name = userSupervisor.getFullName();
            }
            
            supervisorInfo = String.format("%s [h-index: %d]", name, researchSupervisor.getHIndex());
        }

        return String.format(
                "\n🎓 === ВЫПУСКНИК КБТУ (4 КУРС) ===\n" +
                " ФИО: %s\n" +
                " ID: %s | Email: %s\n" +
                " Специальность: %s\n" +
                " Научный руководитель: %s\n" +
                "==================================",
                getFullName(), getStudentId(), getEmail(), getMajor(), supervisorInfo
        );
    }
}