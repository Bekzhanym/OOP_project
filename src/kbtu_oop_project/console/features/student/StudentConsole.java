package kbtu_oop_project.console.features.student;

import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.research.ResearchProject;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.Student4thYear;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.domain.exception.NotAResearcherException; // Ваше кастомное исключение
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public final class StudentConsole {
    private static final int MAX_ECTS_LIMIT = 21;

    private StudentConsole() {
    }

    public static boolean studentMenu(Student student, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Панель студента | Макс. нагрузка: " + MAX_ECTS_LIMIT + " ECTS");
        System.out.println("  1 — Профиль, кредиты и мои оценки преподавателям");
        System.out.println("  2 — Подать заявку на курс (ожидает менеджера OR)");
        System.out.println("  3 — Записаться на курс сразу (без очереди / Elective)");
        System.out.println("  4 — Оценки и transcript");
        System.out.println("  5 — Преподаватели курса по коду");
        System.out.println("  6 — Оценить преподавателя (1–5 звёзд)");
        System.out.println("  7 — Исследовательские проекты (Research Group)");
        System.out.println("  8 — Новости университета");
        System.out.println("  9 — Запросить рекомендательное письмо");
        System.out.println("  0 — Выйти из аккаунта");
        System.out.print("Выбор: ");
        
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1":
                printProfile(student, db);
                break;
            case "2":
                pendingEnrollmentFlow(student, db, in);
                break;
            case "3":
                immediateEnrollmentFlow(student, db, in);
                break;
            case "4":
                ConsoleUi.header("Оценки и transcript");
                student.viewTranscript();
                break;
            case "5":
                printTeachersOnCourse(db, in);
                break;
            case "6":
                rateTeacherFlow(student, db, in);
                break;
            case "7":
                studentResearchFlow(student, db, in);
                break;
            case "8":
                printUniversityNews(db);
                break;
            case "9":
                requestRecommendationFlow(student, db, in);
                break;
            case "0":
                return true;
            default:
                ConsoleUi.printlnErr("Неизвестная команда.");
        }
        return false;
    }

    private static void printProfile(Student student, UniversityDatabase db) {
        ConsoleUi.header("Профиль студента");
        System.out.println("Student ID: " + student.getStudentId());
        System.out.println("Текущая нагрузка: " + student.getTotalCredits() + " / " + MAX_ECTS_LIMIT + " ECTS");
        System.out.println("Записан на курсов: " + student.getEnrolledCourses().size());
        
        for (Course c : student.getEnrolledCourses()) {
            String typeInfo = c.getLessonType() != null ? " [" + c.getLessonType() + "]" : "";
            System.out.println("   • " + c.getCourseCode() + " — " + c.getCourseName() + " (" + c.getCredits() + " ECTS)" + typeInfo);
        }
        
        if (student instanceof Student4thYear sy && sy.getResearchSupervisor() != null) {
            var sup = sy.getResearchSupervisor();
            System.out.println("\n[Статус: Выпускной курс / Дипломник]");
            System.out.println("Научный руководитель: " + sup.getClass().getSimpleName() + " | Индекс Хирша (h-index): " + sup.getHIndex());
        }
        
        printTeacherRatings(student, db);
    }

    private static void pendingEnrollmentFlow(Student student, UniversityDatabase db, Scanner in) {
        System.out.print("Код курса (заявка менеджеру): ");
        String code = ConsoleUi.trim(in.nextLine());
        Optional<Course> courseOpt = db.findCourseByCode(code);
        
        if (courseOpt.isEmpty()) {
            ConsoleUi.printlnErr("Курс не найден.");
            return;
        }
        
        Course course = courseOpt.get();
        
        if (student.getTotalCredits() + course.getCredits() > MAX_ECTS_LIMIT) {
            ConsoleUi.printlnErr("Ошибка: Заявка отклонена. Суммарный объем превысит лимит в " + MAX_ECTS_LIMIT + " ECTS.");
            return;
        }

        try {
            db.submitCourseRegistrationRequest(student, course);
            ConsoleUi.printlnOk("Заявка успешно отправлена в Office of Registrar.");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            ConsoleUi.printlnErr(ex.getMessage());
        }
    }

    private static void immediateEnrollmentFlow(Student student, UniversityDatabase db, Scanner in) {
        System.out.print("Код курса: ");
        String code = ConsoleUi.trim(in.nextLine());
        Optional<Course> courseOpt = db.findCourseByCode(code);
        
        if (courseOpt.isEmpty()) {
            ConsoleUi.printlnErr("Курс не найден.");
            return;
        }
        
        Course course = courseOpt.get();

        if (student.getTotalCredits() + course.getCredits() > MAX_ECTS_LIMIT) {
            ConsoleUi.printlnErr("Регистрация заблокирована академической политикой КБТУ (> " + MAX_ECTS_LIMIT + " ECTS).");
            return;
        }

        try {
            student.registerForCourse(course);
            ConsoleUi.printlnOk("Запись успешна. Текущий баланс: " + student.getTotalCredits() + " ECTS");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            ConsoleUi.printlnErr("Ошибка бизнес-логики: " + ex.getMessage());
        }
    }

    private static void studentResearchFlow(Student student, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Научно-исследовательские проекты");
        System.out.println("  1 — Каталог проектов университета");
        System.out.println("  2 — Присоединиться к проекту по номеру");
        System.out.println("  3 — Найти проект по подстроке темы");
        System.out.println("  4 — Мои научные темы");
        System.out.println("  0 — Назад");
        System.out.print("Выбор: ");
        
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1":
                printProjectCatalog(db);
                break;
            case "2":
                joinProjectByIndex(student, db, in);
                break;
            case "3":
                findProjectByTopic(student, db, in);
                break;
            case "4":
                ConsoleUi.header("Мои исследовательские проекты");
                if (student.getResearchProjects().isEmpty()) {
                    System.out.println("(вы не состоите в научных группах)");
                    break;
                }
                for (ResearchProject p : student.getResearchProjects()) {
                    System.out.println(" • " + safeTopic(p));
                }
                break;
            case "0":
                break;
            default:
                ConsoleUi.printlnErr("Неизвестная команда.");
        }
    }

    private static void joinProjectByIndex(Student student, UniversityDatabase db, Scanner in) {
        List<ResearchProject> all = new ArrayList<>(db.getResearchProjectsUnmodifiable());
        if (all.isEmpty()) {
            ConsoleUi.printlnErr("Нет проектов в каталоге.");
            return;
        }
        printProjectCatalog(db);
        int idx = ConsoleUi.promptInt(in, "Номер проекта", 1, all.size()) - 1;
        ResearchProject p = all.get(idx);
        
        try {
            p.joinProject(student); 
            student.addResearchProject(p);
            ConsoleUi.printlnOk("Вы успешно добавлены к проекту: " + safeTopic(p));
        } catch (NotAResearcherException ex) {
            ConsoleUi.printlnErr("Доступ запрещен: " + ex.getMessage());
        }
    }

    private static void findProjectByTopic(Student student, UniversityDatabase db, Scanner in) {
        System.out.print("Подстрока темы: ");
        String needle = ConsoleUi.trim(in.nextLine());
        if (needle.isEmpty()) {
            ConsoleUi.printlnErr("Отмена.");
            return;
        }
        Optional<ResearchProject> hit = db.findResearchProjectByTopicSubstring(needle);
        if (hit.isEmpty()) {
            ConsoleUi.printlnErr("Проект с такой темой не найден.");
            return;
        }
        
        ResearchProject project = hit.get();
        try {
            project.joinProject(student);
            student.addResearchProject(project);
            ConsoleUi.printlnOk("Успешное подключение к группе: " + safeTopic(project));
        } catch (NotAResearcherException ex) {
            ConsoleUi.printlnErr("Ошибка прав доступа: " + ex.getMessage());
        }
    }

    private static void printTeacherRatings(Student student, UniversityDatabase db) {
        Map<String, Integer> ratings = student.getTeacherRatingsSnapshot();
        System.out.println("\nМои рейтинги преподавателям:");
        if (ratings.isEmpty()) {
            System.out.println("   (оценок нет)");
            return;
        }
        for (Map.Entry<String, Integer> e : ratings.entrySet()) {
            String label = db.findByEmailIgnoreCase(e.getKey())
                    .map(u -> u.getFirstName() + " " + u.getLastName() + " <" + u.getEmail() + ">")
                    .orElse("<" + e.getKey() + ">");
            System.out.println("   • " + label + " → " + "★".repeat(e.getValue()) + " (" + e.getValue() + ")");
        }
    }

    private static String safeTopic(ResearchProject p) {
        return p.getTopic() != null ? p.getTopic() : "(без темы)";
    }

    private static void printProjectCatalog(UniversityDatabase db) {
        ConsoleUi.header("Каталог");
        List<ResearchProject> all = new ArrayList<>(db.getResearchProjectsUnmodifiable());
        if (all.isEmpty()) {
            System.out.println("(пусто — попросите преподавателя создать проект)");
            return;
        }
        for (int i = 0; i < all.size(); i++) {
            ResearchProject p = all.get(i);
            System.out.println((i + 1) + ") " + safeTopic(p) + " | участников: " + p.getParticipants().size());
        }
    }

    private static void printUniversityNews(UniversityDatabase db) {
        ConsoleUi.header("Новости");
        var lines = db.getNewsLinesView();
        if (lines.isEmpty()) {
            System.out.println("(пусто)");
            return;
        }
        for (String line : lines) {
            System.out.println(" • " + line);
        }
    }

    private static void requestRecommendationFlow(Student student, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Рекомендательное письмо");
        System.out.print("Email преподавателя (Teacher / Professor): ");
        String em = ConsoleUi.trim(in.nextLine());
        Optional<User> tu = db.findByEmailIgnoreCase(em);
        if (tu.isEmpty() || !(tu.get() instanceof Teacher teacher)) {
            ConsoleUi.printlnErr("Преподаватель не найден.");
            return;
        }
        student.requestRecommendation(teacher);
        ConsoleUi.printlnOk("Запрос обработан.");
    }

    private static void printTeachersOnCourse(UniversityDatabase db, Scanner in) {
        System.out.print("Код курса: ");
        String cc = ConsoleUi.trim(in.nextLine());
        db.findCourseByCode(cc).ifPresentOrElse(c -> {
            ConsoleUi.header("Преподаватели: " + c.getCourseCode());
            if (c.getInstructors().isEmpty()) {
                System.out.println("(не назначены)");
                return;
            }
            for (Teacher t : c.getInstructors()) {
                System.out.println(" • " + t.getFirstName() + " " + t.getLastName()
                        + " | " + t.getEmail()
                        + " | title=" + (t.getTitle() != null ? t.getTitle().name() : "—"));
            }
        }, () -> ConsoleUi.printlnErr("Курс не найден."));
    }

    private static void rateTeacherFlow(Student student, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Оценка преподавателя");
        System.out.print("Email преподавателя: ");
        String em = ConsoleUi.trim(in.nextLine());
        if (em.isEmpty()) {
            ConsoleUi.printlnErr("Отмена.");
            return;
        }
        Optional<User> tu = db.findByEmailIgnoreCase(em);
        if (tu.isEmpty() || !(tu.get() instanceof Teacher teacher)) {
            ConsoleUi.printlnErr("Преподаватель с таким email не найден.");
            return;
        }
        int stars = ConsoleUi.promptInt(in, "Звёзды (1–5)", 1, 5);
        try {
            student.rateTeacher(teacher, stars);
            ConsoleUi.printlnOk("Рейтинг сохранён локально.");
        } catch (IllegalArgumentException ex) {
            ConsoleUi.printlnErr(ex.getMessage());
        }
    }
}