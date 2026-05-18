package kbtu_oop_project.console.features.student;

import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.research.ResearchProject;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.Student4thYear;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.domain.exception.NotAResearcherException;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public final class StudentConsole {
    private static final int MAX_ECTS_LIMIT = 21;

    private StudentConsole() {
        throw new UnsupportedOperationException("Это утилитарный класс для CLI Студента.");
    }

    private static User extractCoreUser(User u) {
        if (u == null) return null;
        if (u.getClass().getSimpleName().contains("Researcher")) {
            try {
                var method = u.getClass().getMethod("getOriginalUser");
                return (User) method.invoke(u);
            } catch (Exception e) {
                return u;
            }
        }
        return u;
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
        
        String choice = ConsoleUi.trim(in.nextLine());
        return switch (choice) {
            case "1" -> { printProfile(student, db); yield false; }
            case "2" -> { pendingEnrollmentFlow(student, db, in); yield false; }
            case "3" -> { immediateEnrollmentFlow(student, db, in); yield false; }
            case "4" -> {
                ConsoleUi.header("Оценки и transcript");
                student.viewTranscript();
                yield false;
            }
            case "5" -> { printTeachersOnCourse(db, in); yield false; }
            case "6" -> { rateTeacherFlow(student, db, in); yield false; }
            case "7" -> { studentResearchFlow(student, db, in); yield false; }
            case "8" -> { printUniversityNews(db); yield false; }
            case "9" -> { requestRecommendationFlow(student, db, in); yield false; }
            case "0" -> true;
            default -> { ConsoleUi.printlnErr("Неизвестная команда."); yield false; }
        };
    }

    private static void printProfile(Student student, UniversityDatabase db) {
        ConsoleUi.header("Профиль студента");
        
        User core = extractCoreUser(student);
        
        System.out.println("Student ID: " + student.getStudentId());
        System.out.println("Текущая нагрузка: " + student.getTotalCredits() + " / " + MAX_ECTS_LIMIT + " ECTS");
        
        var enrolled = student.getEnrolledCourses();
        System.out.println("Записан на курсов: " + (enrolled != null ? enrolled.size() : 0));
        
        if (enrolled != null) {
            for (Course c : enrolled) {
                var lessons = c.getLessons();
                String typeInfo = (lessons != null && !lessons.isEmpty() && lessons.get(0).getType() != null)
                        ? " [" + lessons.get(0).getType() + "]" : "";
                System.out.println("   • " + c.getCourseCode() + " — " + c.getCourseName() + " (" + c.getCredits() + " ECTS)" + typeInfo);
            }
        }
        
        if (core instanceof Student4thYear sy && sy.getResearchSupervisor() != null) {
            var sup = sy.getResearchSupervisor();
            System.out.println("\n[Статус: Выпускной курс / Дипломник]");
            
            User coreSup = extractCoreUser((User) sup);
            System.out.println("Научный руководитель: " + coreSup.getFirstName() + " " + coreSup.getLastName() + " | h-index: " + sup.getHIndex());
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
            kbtu_oop_project.domain.features.misc.PendingCourseRegistration request = 
                    new kbtu_oop_project.domain.features.misc.PendingCourseRegistration(student.getEmail(), course.getCourseCode());
            
            boolean isMinorCourse = (course.getCourseType() == kbtu_oop_project.domain.value.CourseType.MINOR);

            var regOffice = kbtu_oop_project.domain.features.registration.RegistrationOffice.getInstance();

            if (regOffice.addRegistrationRequest(request, isMinorCourse)) {
                db.submitCourseRegistrationRequest(student, course);
                ConsoleUi.printlnOk("Заявка успешно отправлена в Office of Registrar.");
            } else {
                ConsoleUi.printlnErr("Офис Регистрации отклонил операцию (проверьте текущий академический период).");
            }

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
        
        String subChoice = ConsoleUi.trim(in.nextLine());
        switch (subChoice) {
            case "1" -> printProjectCatalog(db);
            case "2" -> joinProjectByIndex(student, db, in);
            case "3" -> findProjectByTopic(student, db, in);
            case "4" -> {
                ConsoleUi.header("Мои исследовательские проекты");
                var projects = student.getResearchProjects();
                if (projects == null || projects.isEmpty()) {
                    System.out.println("(вы не состоите в научных группах)");
                } else {
                    for (ResearchProject p : projects) {
                        System.out.println(" • " + safeTopic(p));
                    }
                }
            }
            case "0" -> {}
            default -> ConsoleUi.printlnErr("Неизвестная команда.");
        }
    }

    private static void joinProjectByIndex(Student student, UniversityDatabase db, Scanner in) {
        var rawProjects = db.getResearchProjectsUnmodifiable();
        if (rawProjects == null || rawProjects.isEmpty()) {
            ConsoleUi.printlnErr("Нет проектов в каталоге.");
            return;
        }
        List<ResearchProject> all = new ArrayList<>(rawProjects);
        printProjectCatalog(db);
        int idx = ConsoleUi.promptInt(in, "Номер проекта", 1, all.size()) - 1;
        ResearchProject p = all.get(idx);
        
        try {
            p.joinProject(student); 
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
            ConsoleUi.printlnOk("Успешное подключение к группе: " + safeTopic(project));
        } catch (NotAResearcherException ex) {
            ConsoleUi.printlnErr("Ошибка прав доступа: " + ex.getMessage());
        }
    }

    private static void printTeacherRatings(Student student, UniversityDatabase db) {
        Map<String, Integer> ratings = student.getTeacherRatingsSnapshot();
        System.out.println("\nМои рейтинги преподавателям:");
        if (ratings == null || ratings.isEmpty()) {
            System.out.println("   (оценок нет)");
            return;
        }
        for (Map.Entry<String, Integer> e : ratings.entrySet()) {
            String label = db.findByEmailIgnoreCase(e.getKey())
                    .map(u -> {
                        User coreU = extractCoreUser(u);
                        return coreU.getFirstName() + " " + coreU.getLastName() + " <" + coreU.getEmail() + ">";
                    })
                    .orElse("<" + e.getKey() + ">");
            System.out.println("   • " + label + " → " + "★".repeat(e.getValue()) + " (" + e.getValue() + ")");
        }
    }

    private static String safeTopic(ResearchProject p) {
        return p != null && p.getTopic() != null ? p.getTopic() : "(без темы)";
    }

    private static void printProjectCatalog(UniversityDatabase db) {
        ConsoleUi.header("Каталог");
        var rawProjects = db.getResearchProjectsUnmodifiable();
        if (rawProjects == null || rawProjects.isEmpty()) {
            System.out.println("(пусто — попросите преподавателя создать проект)");
            return;
        }
        List<ResearchProject> all = new ArrayList<>(rawProjects);
        for (int i = 0; i < all.size(); i++) {
            ResearchProject p = all.get(i);
            int partSize = (p.getParticipants() != null) ? p.getParticipants().size() : 0;
            System.out.println((i + 1) + ") " + safeTopic(p) + " | участников: " + partSize);
        }
    }

    private static void printUniversityNews(UniversityDatabase db) {
        ConsoleUi.header("Новости");
        List<String> lines = db.getNewsLinesView();
        if (lines == null || lines.isEmpty()) {
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
        
        if (tu.isEmpty()) {
            ConsoleUi.printlnErr("Преподаватель не найден.");
            return;
        }
        
        User coreTeacher = extractCoreUser(tu.get());
        if (!(coreTeacher instanceof Teacher teacher)) {
            ConsoleUi.printlnErr("Указанный пользователь не является академическим преподавателем.");
            return;
        }
        
        teacher.writeRecommendation(student, null);
        ConsoleUi.printlnOk("Рекомендательное письмо успешно запрошено и оформлено.");
    }

    private static void printTeachersOnCourse(UniversityDatabase db, Scanner in) {
        System.out.print("Код курса: ");
        String cc = ConsoleUi.trim(in.nextLine());
        db.findCourseByCode(cc).ifPresentOrElse(c -> {
            ConsoleUi.header("Преподаватели: " + c.getCourseCode());
            var instructors = c.getInstructors();
            if (instructors == null || instructors.isEmpty()) {
                System.out.println("(не назначены)");
                return;
            }
            for (Teacher t : instructors) {
                User coreT = extractCoreUser((User) t);
                Teacher targetTeacher = (coreT instanceof Teacher) ? (Teacher) coreT : t;
                System.out.println(" • " + coreT.getFirstName() + " " + coreT.getLastName()
                        + " | " + coreT.getEmail()
                        + " | title=" + (targetTeacher.getTitle() != null ? targetTeacher.getTitle().name() : "—"));
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
        
        if (tu.isEmpty()) {
            ConsoleUi.printlnErr("Преподаватель с таким email не найден.");
            return;
        }
        
        User coreT = extractCoreUser(tu.get());
        if (!(coreT instanceof Teacher teacher)) {
            ConsoleUi.printlnErr("Оценивать можно только сотрудников профессорско-преподавательского состава.");
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