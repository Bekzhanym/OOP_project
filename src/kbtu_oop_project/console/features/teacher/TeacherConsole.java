package kbtu_oop_project.console.features.teacher;

import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.course.Lesson;
import kbtu_oop_project.domain.features.course.Mark;
import kbtu_oop_project.domain.features.misc.EmployeeMessage;
import kbtu_oop_project.domain.features.research.ResearchPaper;
import kbtu_oop_project.domain.features.research.ResearchProject;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.sort.PaperComparator;
import kbtu_oop_project.domain.value.CourseType;
import kbtu_oop_project.domain.value.LessonType;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public final class TeacherConsole {

    private TeacherConsole() {
    }

    public static boolean teacherMenu(Teacher teacher, UniversityDatabase db, Scanner in) {
        System.out.println("  1 — Мой профиль и курсы");
        System.out.println("  2 — Добавить статью");
        System.out.println("  3 — Все исследователи и их статьи (сортировка)");
        System.out.println("  4 — Лучший по сумме цитирований");
        System.out.println("  5 — Лучший по цитированиям за год");
        System.out.println("  6 — Поставить оценку студенту");
        System.out.println("  7 — Студенты на моих курсах");
        System.out.println("  8 — Внутренняя почта (входящие)");
        System.out.println("  9 — Отправить сообщение / жалобу сотруднику");
        System.out.println(" 10 — Исследовательские проекты");
        System.out.println(" 11 — Управление своим курсом (UML manageCourse)");
        System.out.println(" 12 — Отчёт по оценкам на курсе (exportGradeReport)");
        System.out.println("  0 — Выйти из аккаунта");
        System.out.print("Выбор: ");
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1":
                ConsoleUi.header("Профиль преподавателя");
                System.out.println("H-index: " + teacher.getHIndex());
                System.out.println("Кафедра: " + teacher.getDepartment());
                System.out.println("Ведёт курсов: " + teacher.getTaughtCourses().size());
                for (Course c : teacher.getTaughtCourses()) {
                    System.out.println("   • " + c.getCourseCode() + " — " + c.getCourseName());
                }
                break;
            case "2":
                System.out.print("Название статьи: ");
                String paperTitle = ConsoleUi.trim(in.nextLine());
                if (paperTitle.isEmpty()) {
                    ConsoleUi.printlnErr("Пустое название — отмена.");
                    break;
                }
                System.out.print("Авторы (через запятую): ");
                List<String> authors = ConsoleUi.splitAuthors(in.nextLine());
                System.out.print("Журнал: ");
                String journal = ConsoleUi.trim(in.nextLine());
                System.out.print("Издатель (опц.): ");
                String publisher = ConsoleUi.trim(in.nextLine());
                System.out.print("DOI (опц.): ");
                String doi = ConsoleUi.trim(in.nextLine());
                System.out.print("Ключевые слова (опц.): ");
                String keywords = ConsoleUi.trim(in.nextLine());
                int citations = ConsoleUi.promptInt(in, "Цитирования", 0, 1_000_000);
                int pages = ConsoleUi.promptInt(in, "Страниц", 1, 10_000);
                LocalDate pubDate = ConsoleUi.promptDate(in, "Дата yyyy-MM-dd (Enter = сегодня)");
                teacher.addPaper(new ResearchPaper(paperTitle, authors, journal, publisher, doi, keywords,
                        citations, pages, pubDate));
                ConsoleUi.printlnOk("Статья добавлена.");
                break;
            case "3":
                PaperComparator cmp = ConsoleUi.choosePaperComparator(in);
                ConsoleUi.header("Статьи всех исследователей");
                db.printAllResearchersPapersSorted(cmp);
                break;
            case "4":
                db.findTopResearcherByTotalCitations().ifPresentOrElse(
                        r -> System.out.println("Топ по цитированию: "
                                + r.getClass().getSimpleName()
                                + ", сумма="
                                + r.getPapers().stream().mapToInt(ResearchPaper::getCitations).sum()),
                        () -> ConsoleUi.printlnErr("Нет исследователей."));
                break;
            case "5":
                int year = ConsoleUi.promptInt(in, "Год (YYYY)", 1900, 2100);
                db.findTopResearcherByCitationsInYear(year).ifPresentOrElse(
                        r -> System.out.println("Топ за " + year + ": "
                                + r.getClass().getSimpleName()
                                + ", сумма="
                                + r.getPapers().stream()
                                .filter(p -> p.getDate() != null && p.getDate().getYear() == year)
                                .mapToInt(ResearchPaper::getCitations).sum()),
                        () -> ConsoleUi.printlnErr("Нет данных за этот год."));
                break;
            case "6":
                putMarksFlow(teacher, db, in);
                break;
            case "7":
                ConsoleUi.header("Студенты на ваших курсах");
                for (Course c : teacher.getTaughtCourses()) {
                    System.out.println(c.getCourseCode() + " — " + c.getCourseName());
                    if (c.getEnrolledStudents().isEmpty()) {
                        System.out.println("   (нет записавшихся)");
                        continue;
                    }
                    for (Student st : c.getEnrolledStudents()) {
                        System.out.println("   • " + st.getStudentId() + " — "
                                + st.getFirstName() + " " + st.getLastName());
                    }
                }
                break;
            case "8":
                printTeacherInbox(db, teacher.getEmail());
                break;
            case "9":
                sendTeacherEmployeeMail(teacher, db, in);
                break;
            case "10":
                teacherResearchFlow(teacher, db, in);
                break;
            case "11":
                manageCourseFlow(teacher, db, in);
                break;
            case "12":
                exportGradeReportFlow(teacher, db, in);
                break;
            case "0":
                return true;
            default:
                ConsoleUi.printlnErr("Неизвестная команда.");
        }
        return false;
    }

    private static void printTeacherInbox(UniversityDatabase db, String email) {
        ConsoleUi.header("Входящие");
        List<EmployeeMessage> inbox = db.messagesForRecipientEmailIgnoreCase(email);
        if (inbox.isEmpty()) {
            System.out.println("(нет сообщений)");
            return;
        }
        int i = 1;
        for (EmployeeMessage m : inbox) {
            System.out.println(i++ + ") [" + m.getKind() + "] от " + m.getFromEmail());
            System.out.println("   deanFlag=" + m.isRequiresDeanSignature());
            System.out.println("   " + m.getBody());
            System.out.println();
        }
    }

    private static void sendTeacherEmployeeMail(Teacher teacher, UniversityDatabase db, Scanner in) {
        System.out.print("Email получателя (Employee): ");
        String to = ConsoleUi.trim(in.nextLine());
        if (to.isEmpty()) {
            ConsoleUi.printlnErr("Отмена.");
            return;
        }
        System.out.println("Тип: 1 — сообщение   2 — жалоба");
        System.out.print("Выбор: ");
        String kindChoice = ConsoleUi.trim(in.nextLine());
        String kind = switch (kindChoice) {
            case "2" -> EmployeeMessage.KIND_COMPLAINT;
            default -> EmployeeMessage.KIND_MESSAGE;
        };
        System.out.print("Текст: ");
        String body = ConsoleUi.trim(in.nextLine());
        System.out.print("Требуется подпись декана? y/n: ");
        boolean dean = "y".equalsIgnoreCase(ConsoleUi.trim(in.nextLine()));
        try {
            db.postEmployeeMessage(teacher, to, kind, body, dean);
            ConsoleUi.printlnOk("Отправлено.");
        } catch (IllegalArgumentException ex) {
            ConsoleUi.printlnErr(ex.getMessage());
        }
    }

    private static void teacherResearchFlow(Teacher teacher, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Исследовательские проекты");
        System.out.println("  1 — Создать проект и добавить в каталог");
        System.out.println("  2 — Мои проекты");
        System.out.println("  3 — Каталог университета");
        System.out.println("  0 — Назад");
        System.out.print("Выбор: ");
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1":
                System.out.print("Тема: ");
                String topic = ConsoleUi.trim(in.nextLine());
                if (topic.isEmpty()) {
                    ConsoleUi.printlnErr("Пустая тема — отмена.");
                    break;
                }
                ResearchProject p = new ResearchProject();
                p.setTopic(topic);
                teacher.addResearchProject(p);
                db.registerGlobalResearchProject(p);
                ConsoleUi.printlnOk("Создано и зарегистрировано в каталоге.");
                break;
            case "2":
                ConsoleUi.header("Мои проекты");
                if (teacher.getResearchProjects().isEmpty()) {
                    System.out.println("(нет)");
                    break;
                }
                for (ResearchProject rp : teacher.getResearchProjects()) {
                    String t = rp.getTopic() != null ? rp.getTopic() : "(без темы)";
                    System.out.println(" • " + t + " | участников: " + rp.getParticipants().size());
                }
                break;
            case "3":
                ConsoleUi.header("Каталог");
                if (db.getResearchProjectsUnmodifiable().isEmpty()) {
                    System.out.println("(пусто)");
                    break;
                }
                int n = 1;
                for (ResearchProject rp : db.getResearchProjectsUnmodifiable()) {
                    String t = rp.getTopic() != null ? rp.getTopic() : "(без темы)";
                    System.out.println((n++) + ") " + t);
                }
                break;
            case "0":
                break;
            default:
                ConsoleUi.printlnErr("Неизвестная команда.");
        }
    }

    private static Course pickTaughtCourse(Teacher teacher, Scanner in) {
        List<Course> list = teacher.getTaughtCourses();
        if (list.isEmpty()) {
            ConsoleUi.printlnErr("У вас нет назначенных курсов.");
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            Course c = list.get(i);
            System.out.println((i + 1) + ") " + c.getCourseCode() + " — " + c.getCourseName());
        }
        int idx = ConsoleUi.promptInt(in, "Номер курса", 1, list.size()) - 1;
        return list.get(idx);
    }

    private static void manageCourseFlow(Teacher teacher, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Управление курсом");
        Course c = pickTaughtCourse(teacher, in);
        if (c == null) {
            return;
        }
        System.out.println("Текущее: " + c);
        System.out.print("Новое название (Enter — без изменений): ");
        String nn = ConsoleUi.trim(in.nextLine());
        String newName = nn.isEmpty() ? null : nn;

        System.out.print("Новые кредиты (Enter — без изменений): ");
        String crLine = ConsoleUi.trim(in.nextLine());
        Integer credits = null;
        if (!crLine.isEmpty()) {
            try {
                credits = Integer.parseInt(crLine);
            } catch (NumberFormatException ex) {
                ConsoleUi.printlnErr("Неверное число.");
                return;
            }
        }

        CourseType ctNew = null;
        System.out.println("Тип курса (CourseType): 1 MAJOR   2 MINOR   3 ELECTIVE   Enter — без изменений");
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1" -> ctNew = CourseType.MAJOR;
            case "2" -> ctNew = CourseType.MINOR;
            case "3" -> ctNew = CourseType.ELECTIVE;
            default -> {
            }
        }

        Lesson lessonPatch = null;
        System.out.println("Обновить занятие (Lesson): 1 Lecture   2 Practice   Enter — не менять");
        String ltPick = ConsoleUi.trim(in.nextLine());
        if ("1".equals(ltPick) || "2".equals(ltPick)) {
            lessonPatch = new Lesson();
            lessonPatch.setType("1".equals(ltPick) ? LessonType.Lecture : LessonType.Practice);
            int dow = ConsoleUi.promptInt(in, "День недели (1=Пн … 7=Вс)", 1, 7);
            lessonPatch.setDay(DayOfWeek.of(dow));
            int sh = ConsoleUi.promptInt(in, "Час начала", 8, 21);
            int eh = ConsoleUi.promptInt(in, "Час окончания", sh + 1, 23);
            lessonPatch.setStartTime(LocalTime.of(sh, 0));
            lessonPatch.setEndTime(LocalTime.of(eh, 0));
        }

        try {
            teacher.manageCourse(c, newName, credits, ctNew, lessonPatch);
            ConsoleUi.printlnOk("Курс обновлён (уведомление Observer отправлено).");
            db.recordAudit("TEACHER_MANAGE_COURSE " + teacher.getEmail() + " " + c.getCourseCode());
        } catch (IllegalStateException ex) {
            ConsoleUi.printlnErr(ex.getMessage());
        }
    }

    private static void exportGradeReportFlow(Teacher teacher, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Отчёт по оценкам (exportGradeReport)");
        Course c = pickTaughtCourse(teacher, in);
        if (c == null) {
            return;
        }
        try {
            teacher.exportGradeReport(c);
            db.recordAudit("TEACHER_GRADE_REPORT " + teacher.getEmail() + " " + c.getCourseCode());
        } catch (IllegalStateException ex) {
            ConsoleUi.printlnErr(ex.getMessage());
        }
    }

    private static void putMarksFlow(Teacher teacher, UniversityDatabase db, Scanner in) {
        System.out.print("Student ID: ");
        String sid = ConsoleUi.trim(in.nextLine());
        Student student = db.findStudentByStudentId(sid).orElse(null);
        if (student == null) {
            ConsoleUi.printlnErr("Студент не найден.");
            return;
        }
        System.out.print("Код курса: ");
        String code = ConsoleUi.trim(in.nextLine());
        Course course = db.findCourseByCode(code).orElse(null);
        if (course == null) {
            ConsoleUi.printlnErr("Курс не найден.");
            return;
        }
        Mark mark = new Mark();
        mark.setFirstAttestation(ConsoleUi.promptDouble(in, "Первая аттестация", 0, 40));
        mark.setSecondAttestation(ConsoleUi.promptDouble(in, "Вторая аттестация", 0, 40));
        mark.setFinalExam(ConsoleUi.promptDouble(in, "Экзамен / итог", 0, 40));
        try {
            teacher.putMark(student, course, mark);
            ConsoleUi.printlnOk("Оценка сохранена. Итог: " + mark.calculateFinalScore());
        } catch (IllegalStateException ex) {
            ConsoleUi.printlnErr(ex.getMessage());
        }
    }
}
