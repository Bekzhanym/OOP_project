package edu.university.domain.model;

import edu.university.domain.value.TeacherTitle;

public class Professor extends Teacher {

    private static final long serialVersionUID = 1L;

    public Professor() {
        setTitle(TeacherTitle.PROFESSOR);
    }
}
