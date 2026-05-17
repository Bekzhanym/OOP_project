package kbtu_oop_project.console.features.home;

import kbtu_oop_project.application.factory.UserFactory;
import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.console.features.user.UserRoleFormatter;
import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.course.Lesson;
import kbtu_oop_project.domain.features.misc.Log;
import kbtu_oop_project.domain.features.user.Admin;
import kbtu_oop_project.domain.features.user.ResearchStaff;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.domain.value.CourseType;
import kbtu_oop_project.domain.value.LessonType;
import kbtu_oop_project.domain.value.Role;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Scanner;

public final class GuestConsole {

    private GuestConsole() {
    }

    public static void promptSaveIfDesired(UniversityDatabase db, Scanner in) {
        System.out.print("Сохранить данные в data/university-state.ser перед выходом? y/n: ");
        if ("y".equalsIgnoreCase(ConsoleUi.trim(in.nextLine()))) {
            db.saveData();
            ConsoleUi.printlnOk("Сохранено.");
        }
    }

    public static User guestHome(UniversityDatabase db, Scanner in, UserFactory factory) {
        while (true) {
            boolean needsAdmin = db.findAllUsers().stream().noneMatch(u -> u instanceof Admin);
            ConsoleUi.header("Главное меню");
            System.out.println("Пользователей: " + db.getUsers().size()
                    + "   Курсов: " + db.getCourses().size());
            if (needsAdmin) {
                System.out.println("[!] Нет ни одного администратора — зарегистрируйте роль Admin.");
            }
            System.out.println();
            System.out.println("  1 — Вход (email + пароль)");
            System.out.println("  2 — Регистрация нового пользователя");
            System.out.println("  3 — Быстрое демо (добавить пример преподавателя, студента и курса)");
            System.out.println("  4 — Новости университета (для всех)");
            System.out.println("  0 — Выход из программы");
            System.out.print("Выбор: ");
            String c = ConsoleUi.trim(in.nextLine());
            switch (c) {
                case "1":
                    Optional<User> logged = tryLogin(db, in);
                    if (logged.isPresent()) {
                        ConsoleUi.printlnOk("Добро пожаловать.");
                        return logged.get();
                    }
                    ConsoleUi.printlnErr("Неверный email или пароль.");
                    break;
                case "2":
                    User registered = registerNewUser(db, in, factory);
                    if (registered != null) {
                        ConsoleUi.printlnOk("Вы автоматически вошли после регистрации.");
                        return registered;
                    }
                    break;
                case "3":
                    seedQuickDemo(db, factory);
                    break;
                case "4":
                    printUniversityNews(db);
                    break;
                case "0":
                    promptSaveIfDesired(db, in);
                    return null;
                default:
                    ConsoleUi.printlnErr("Выберите 0–4.");
            }
        }
    }

    private static void printUniversityNews(UniversityDatabase db) {
        ConsoleUi.header("Новости университета");
        var lines = db.getNewsLinesView();
        if (lines.isEmpty()) {
            System.out.println("(новостей пока нет — менеджер может добавить)");
            return;
        }
        for (String line : lines) {
            System.out.println(" • " + line);
        }
    }

    private static Optional<User> tryLogin(UniversityDatabase db, Scanner in) {
        System.out.print("Email: ");
        String email = ConsoleUi.trim(in.nextLine());
        System.out.print("Пароль: ");
        String password = ConsoleUi.trim(in.nextLine());
        Optional<User> hit = db.findByEmailIgnoreCase(email).filter(u -> u.authenticate(password));
        if (hit.isPresent()) {
            db.recordAudit("LOGIN_OK " + email.trim());
            Log lg = new Log();
            lg.setAction("login_ok");
            lg.setUserId(hit.get().getId());
            lg.setTimestamp(LocalDate.now());
            db.recordStructured(lg);
        } else {
            db.recordAudit("LOGIN_FAIL " + email.trim());
            Log lg = new Log();
            lg.setAction("login_fail");
            lg.setUserId(null);
            lg.setTimestamp(LocalDate.now());
            db.recordStructured(lg);
        }
        return hit;
    }

    public static User registerNewUser(UniversityDatabase db, Scanner in, UserFactory factory) {
        boolean isDbEmpty = db.findAllUsers().isEmpty();
        
        ConsoleUi.header("Регистрация нового аккаунта");
        System.out.println("  1 — Студент (Bachelor)");
        System.out.println("  2 — Преподаватель (Teacher)");
        if (isDbEmpty) {
            System.out.println("  3 — Администратор (Первоначальная настройка системы)");
        }
        
        System.out.print("Выберите тип аккаунта: ");
        String choice = ConsoleUi.trim(in.nextLine());
        
        Role role = switch (choice) {
            case "1" -> Role.STUDENT;
            case "2" -> Role.TEACHER;
            case "3" -> isDbEmpty ? Role.ADMIN : null;
            default -> null;
        };

        if (role == null) {
            ConsoleUi.printlnErr("Регистрация данной роли запрещена или некорректна.");
            return null;
        }

        User user = factory.createUser(role);
        
        String email = ConsoleUi.promptRequired(in, "Email (логин)");
        if (db.findByEmailIgnoreCase(email).isPresent()) {
            ConsoleUi.printlnErr("Пользователь с таким email уже существует.");
            return null;
        }
        user.setEmail(email);
        user.setId(ConsoleUi.promptRequired(in, "ID (например, 22B...)"));
        user.setFirstName(ConsoleUi.promptRequired(in, "Имя"));
        user.setLastName(ConsoleUi.promptRequired(in, "Фамилия"));
        user.setPassword(ConsoleUi.promptRequired(in, "Пароль"));

        if (user instanceof Student student) {
            student.setStudentId(user.getId());
            System.out.print("Укажите курс обучения (1-4): ");
            int year = ConsoleUi.promptInt(in, "Курс", 1, 4);
            student.setYearOfStudy(year);
            
            if (year == 4) {
                System.out.println("ℹ️ Вы зарегистрированы как студент 4-го курса. Вам потребуется научный руководитель.");
            }
        }

        if (user instanceof Teacher teacher) {
            teacher.setDepartment(ConsoleUi.promptRequired(in, "Кафедра (SITE, ISE и др.)"));
            
            System.out.println("Выберите должность:");
            System.out.println("  1 — Tutor  2 — Lector  3 — Senior Lector  4 — Professor");
            int titleChoice = ConsoleUi.promptInt(in, "Должность", 1, 4);
            
            teacher.setTitle(switch (titleChoice) {
                case 1 -> TeacherTitle.TUTOR;
                case 2 -> TeacherTitle.LECTOR;
                case 3 -> TeacherTitle.SENIOR_LECTOR;
                default -> TeacherTitle.PROFESSOR;
            });

            if (teacher.getTitle() == TeacherTitle.PROFESSOR) {
                System.out.println("ℹ️ Профессора автоматически являются исследователями.");
                teacher.setHIndex(ConsoleUi.promptInt(in, "Введите начальный h-index", 0, 100));
            } else {
                System.out.print("Является ли преподаватель активным исследователем? (y/n): ");
                if ("y".equalsIgnoreCase(ConsoleUi.trim(in.nextLine()))) {
                    teacher.setHIndex(ConsoleUi.promptInt(in, "Введите h-index", 0, 100));
                }
            }
        }

       db.add(user);
        db.recordAudit("REGISTER " + user.getClass().getSimpleName() + " " + email.trim());
        
        Log lg = new Log();
        lg.setAction("register");
        lg.setUserId(user.getId());
        lg.setTimestamp(LocalDate.now());
        db.recordStructured(lg);
        
        ConsoleUi.printlnOk("Регистрация успешно завершена.");
        return user;
    }

    private static void seedQuickDemo(UniversityDatabase db, UserFactory factory) {
        String suffix = Long.toString(System.currentTimeMillis() % 100_000);
        Teacher teacher = (Teacher) factory.createUser(Role.TEACHER);
        teacher.setId("t-demo-" + suffix);
        teacher.setFirstName("Demo");
        teacher.setLastName("Teacher");
        teacher.setEmail("teacher.demo." + suffix + "@uni.local");
        teacher.setPassword("teacher123");
        teacher.setHIndex(12);
        teacher.setDepartment("CS");

        Student student = (Student) factory.createUser(Role.STUDENT);
        student.setId("s-demo-" + suffix);
        student.setFirstName("Demo");
        student.setLastName("Student");
        student.setEmail("student.demo." + suffix + "@uni.local");
        student.setPassword("student123");
        student.setStudentId(student.getId());

        Lesson lesson = new Lesson();
        lesson.setType(LessonType.Lecture);
        lesson.setDay(DayOfWeek.MONDAY);
        lesson.setStartTime(LocalTime.of(9, 0));
        lesson.setEndTime(LocalTime.of(10, 30));

        Course course = new Course();
        course.setCourseCode("CS-" + suffix);
        course.setCourseName("Demo course");
        course.setCredits(6);
        course.setCourseType(CourseType.MAJOR);
        course.setIntendedMajor("Computer Science");
        course.setIntendedYearOfStudy(2);
        course.setLesson(lesson);
        course.addInstructor(teacher);

        db.add(teacher);
        db.add(student);
        db.add(course);
        ConsoleUi.printlnOk("Добавлены демо-пользователи и курс.");
        System.out.println("  Преподаватель: " + teacher.getEmail() + " / teacher123");
        System.out.println("  Студент:       " + student.getEmail() + " / student123");
        System.out.println("  Курс:          " + course.getCourseCode() + " — " + course.getCourseName());
        System.out.println("    (Lesson=Lecture, CourseType=MAJOR — как на UML)");
    }
}
