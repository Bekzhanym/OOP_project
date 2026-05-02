package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.value.TeacherTitle;

public class Professor extends Teacher {

    private static final long serialVersionUID = 1L;

    public Professor() {
        setTitle(TeacherTitle.PROFESSOR);
    }
}
