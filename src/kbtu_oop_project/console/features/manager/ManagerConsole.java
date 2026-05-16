package kbtu_oop_project.console.features.manager;

import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.course.Lesson;
import kbtu_oop_project.domain.features.course.Mark;
import kbtu_oop_project.domain.features.misc.EmployeeMessage;
import kbtu_oop_project.domain.features.misc.PendingCourseRegistration;
import kbtu_oop_project.domain.features.user.Manager;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.domain.value.CourseType;
import kbtu_oop_project.domain.value.LessonType;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public final class ManagerConsole {

    private ManagerConsole() {
    }

    public static boolean managerMenu(Manager manager, UniversityDatabase db, Scanner in) {
        System.out.println("  1 — Статистика по оценкам (простой отчёт)");
        System.out.println("  2 — Студенты по среднему GPA (transcript)");
        System.out.println("  3 — Назначить преподавателя на курс");
        System.out.println("  4 — Очередь заявок на курсы (утвердить / отклонить)");
        System.out.println("  5 — Сообщения, помеченные для декана");
        System.out.println("  6 — Преподаватели (алфавит) и средний студенческий рейтинг");
        System.out.println("  7 — Добавить курс в каталог (major / год / тип / занятие)");
        System.out.println("  8 — Новости университета (просмотр / добавить)");
        System.out.println("  0 — Выйти из аккаунта");
        System.out.print("Выбор: ");
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1":
                printMarksReport(db);
                break;
            case "2":
                printStudentsByTranscriptGpa(db);
                break;
            case "3":
                assignInstructorFlow(db, in);
                break;
            case "4":
                pendingRegistrationFlow(manager, db, in);
                break;
            case "5":
                printDeanFlaggedMail(db);
                break;
            case "6":
                printTeachersAlphabeticalWithRatings(db);
                break;
            case "7":
                createCourseCatalogEntry(manager, db, in);
                break;
            case "8":
                universityNewsFlow(manager, db, in);
                break;
            case "0":
                return true;
            default:
                ConsoleUi.printlnErr("Неизвестная команда.");
        }
        return false;
    }

    private static void pendingRegistrationFlow(Manager manager, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Заявки на запись");
        List<PendingCourseRegistration> q = db.getPendingCourseRegistrationsView();
        if (q.isEmpty()) {
            System.out.println("(очередь пуста)");
            return;
        }
        for (int i = 0; i < q.size(); i++) {
            PendingCourseRegistration p = q.get(i);
            System.out.println((i + 1) + ") " + p.getStudentEmail() + " → " + p.getCourseCode());
        }
        System.out.println("Команда: a номер — утвердить   r номер — отклонить   Enter — назад");
        System.out.print("> ");
        String line = ConsoleUi.trim(in.nextLine());
        if (line.isEmpty()) {
            return;
        }
        String[] parts = line.split("\\s+");
        if (parts.length < 2) {
            ConsoleUi.printlnErr("Формат: a 1 или r 2");
            return;
        }
        char cmd = Character.toLowerCase(parts[0].charAt(0));
        int idx;
        try {
            idx = Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException ex) {
            ConsoleUi.printlnErr("Неверный номер.");
            return;
        }
        if (idx < 0 || idx >= q.size()) {
            ConsoleUi.printlnErr("Нет такой заявки.");
            return;
        }
        PendingCourseRegistration pend = q.get(idx);
        String email = pend.getStudentEmail();
        String code = pend.getCourseCode();
        if (cmd == 'a') {
            if (db.approvePendingCourseRegistration(email, code)) {
                manager.approveRegistration();
                ConsoleUi.printlnOk("Утверждено и студент записан.");
            } else {
                ConsoleUi.printlnErr("Не удалось утвердить (правила записи или данные изменились).");
            }
        } else if (cmd == 'r') {
            if (db.rejectPendingCourseRegistration(email, code)) {
                ConsoleUi.printlnOk("Заявка отклонена.");
            } else {
                ConsoleUi.printlnErr("Заявка не найдена.");
            }
        } else {
            ConsoleUi.printlnErr("Неизвестная команда.");
        }
    }

    private static void printDeanFlaggedMail(UniversityDatabase db) {
        ConsoleUi.header("Декан / подпись");
        List<EmployeeMessage> list = db.messagesRequiringDeanSignature();
        if (list.isEmpty()) {
            System.out.println("(нет сообщений с deanFlag)");
            return;
        }
        for (EmployeeMessage m : list) {
            System.out.println("[" + m.getKind() + "] " + m.getFromEmail() + " → " + m.getToEmail());
            System.out.println(m.getBody());
            System.out.println("---");
        }
    }

    private static Map<String, List<Integer>> aggregateRatingsByTeacherEmailNormalized(UniversityDatabase db) {
        Map<String, List<Integer>> acc = new HashMap<>();
        for (User u : db.getUsers()) {
            if (!(u instanceof Student s)) {
                continue;
            }
            for (Map.Entry<String, Integer> e : s.getTeacherRatingsSnapshot().entrySet()) {
                String key = e.getKey() != null ? e.getKey().trim().toLowerCase(Locale.ROOT) : "";
                if (key.isEmpty()) {
                    continue;
                }
                acc.computeIfAbsent(key, k -> new ArrayList<>()).add(e.getValue());
            }
        }
        return acc;
    }

    private static void printTeachersAlphabeticalWithRatings(UniversityDatabase db) {
        ConsoleUi.header("Преподаватели и средний рейтинг");
        Map<String, List<Integer>> ratings = aggregateRatingsByTeacherEmailNormalized(db);
        List<Teacher> teachers = new ArrayList<>();
        for (User u : db.getUsers()) {
            if (u instanceof Teacher t) {
                teachers.add(t);
            }
        }
        teachers.sort(Comparator.comparing(Teacher::getLastName, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(Teacher::getFirstName, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER)));
        if (teachers.isEmpty()) {
            System.out.println("Нет преподавателей.");
            return;
        }
        for (Teacher t : teachers) {
            String emailKey = t.getEmail() != null ? t.getEmail().trim().toLowerCase(Locale.ROOT) : "";
            List<Integer> vals = ratings.getOrDefault(emailKey, List.of());
            double avg = vals.isEmpty()
                    ? Double.NaN
                    : vals.stream().mapToInt(Integer::intValue).average().orElse(Double.NaN);
            String avgStr = vals.isEmpty() ? "—" : String.format(Locale.ROOT, "%.2f★ (%d)", avg, vals.size());
            System.out.printf("%s %s | %s | dept=%s | avgRating=%s%n",
                    t.getFirstName(),
                    t.getLastName(),
                    t.getEmail(),
                    t.getDepartment(),
                    avgStr);
        }
    }

    private static void printMarksReport(UniversityDatabase db) {
        ConsoleUi.header("Отчёт по оценкам");
        List<Double> totals = new ArrayList<>();
        long passed = 0;
        for (User u : db.getUsers()) {
            if (u instanceof Student s) {
                for (Mark m : s.getTranscript().allMarks()) {
                    totals.add(m.calculateFinalScore());
                    if (m.isPassed()) {
                        passed++;
                    }
                }
            }
        }
        if (totals.isEmpty()) {
            System.out.println("Нет данных об оценках.");
            return;
        }
        double avg = totals.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        System.out.println("Всего отметок курсов: " + totals.size());
        System.out.println("Средний итоговый балл: " + String.format("%.2f", avg));
        System.out.println("Зачётов (>=50 баллов): " + passed + " / " + totals.size());
    }

    private static void printStudentsByTranscriptGpa(UniversityDatabase db) {
        ConsoleUi.header("Студенты по GPA(transcript)");
        List<Student> list = new ArrayList<>();
        for (User u : db.getUsers()) {
            if (u instanceof Student s) {
                list.add(s);
            }
        }
        list.sort(Comparator.comparingDouble((Student s) -> s.getTranscript().getTotalGPA()).reversed());
        for (Student s : list) {
            System.out.printf("%s %s | studentId=%s | GPA=%.2f%n",
                    s.getFirstName(),
                    s.getLastName(),
                    s.getStudentId(),
                    s.getTranscript().getTotalGPA());
        }
    }

    private static void assignInstructorFlow(UniversityDatabase db, Scanner in) {
        System.out.print("Email преподавателя: ");
        String email = ConsoleUi.trim(in.nextLine());
        User user = db.findByEmailIgnoreCase(email).orElse(null);
        if (!(user instanceof Teacher teacher)) {
            ConsoleUi.printlnErr("Пользователь не найден или не Teacher.");
            return;
        }
        System.out.print("Код курса: ");
        String code = ConsoleUi.trim(in.nextLine());
        Course course = db.findCourseByCode(code).orElse(null);
        if (course == null) {
            ConsoleUi.printlnErr("Курс не найден.");
            return;
        }
        course.addInstructor(teacher);
        ConsoleUi.printlnOk("Назначено: " + teacher.getEmail() + " → " + course.getCourseCode());
    }

    private static void createCourseCatalogEntry(Manager manager, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Новый курс (регистрация)");
        manager.manageNews();
        String code = ConsoleUi.promptRequired(in, "Код курса");
        if (db.findCourseByCode(code).isPresent()) {
            ConsoleUi.printlnErr("Курс с таким кодом уже есть.");
            return;
        }
        String title = ConsoleUi.promptRequired(in, "Название");
        int credits = ConsoleUi.promptInt(in, "Кредиты", 1, 30);
        System.out.print("Целевая специальность (major, Enter — пропуск): ");
        String major = ConsoleUi.trim(in.nextLine());
        int year = ConsoleUi.promptInt(in, "Курс для года обучения (0 — не задано)", 0, 6);

        System.out.println("Тип курса (UML CourseType): 1 MAJOR   2 MINOR   Enter — ELECTIVE");
        CourseType ct = switch (ConsoleUi.trim(in.nextLine())) {
            case "1" -> CourseType.MAJOR;
            case "2" -> CourseType.MINOR;
            default -> CourseType.ELECTIVE;
        };

        Lesson lesson = null;
        System.out.println("Занятие (Lesson): 1 Lecture   2 Practice   Enter — без расписания");
        String lt = ConsoleUi.trim(in.nextLine());
        if ("1".equals(lt) || "2".equals(lt)) {
            lesson = new Lesson();
            lesson.setType("1".equals(lt) ? LessonType.Lecture : LessonType.Practice);
            int dow = ConsoleUi.promptInt(in, "День недели (1=Пн … 7=Вс)", 1, 7);
            lesson.setDay(DayOfWeek.of(dow));
            int sh = ConsoleUi.promptInt(in, "Час начала", 8, 21);
            int eh = ConsoleUi.promptInt(in, "Час окончания", sh + 1, 23);
            lesson.setStartTime(LocalTime.of(sh, 0));
            lesson.setEndTime(LocalTime.of(eh, 0));
        }

        Course course = new Course();
        course.setCourseCode(code.trim());
        course.setCourseName(title.trim());
        course.setCredits(credits);
        course.setCourseType(ct);
        if (!major.isBlank()) {
            course.setIntendedMajor(major.trim());
        }
        if (year > 0) {
            course.setIntendedYearOfStudy(year);
        }
        course.setLesson(lesson);
        db.addCourse(course);
        manager.createStatisticalReport();
        db.recordAudit("MANAGER_ADD_COURSE " + code.trim());
        ConsoleUi.printlnOk("Курс добавлен в каталог.");
    }

    private static void universityNewsFlow(Manager manager, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Новости");
        manager.manageNews();
        System.out.println("  1 — Показать все   2 — Добавить строку");
        System.out.print("Выбор: ");
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1":
                var lines = db.getNewsLinesView();
                if (lines.isEmpty()) {
                    System.out.println("(пусто)");
                } else {
                    for (String s : lines) {
                        System.out.println(" • " + s);
                    }
                }
                break;
            case "2":
                System.out.print("Текст новости: ");
                String body = ConsoleUi.trim(in.nextLine());
                try {
                    db.addUniversityNews(body);
                    manager.manageNews();
                    ConsoleUi.printlnOk("Добавлено.");
                } catch (IllegalArgumentException ex) {
                    ConsoleUi.printlnErr(ex.getMessage());
                }
                break;
            default:
                ConsoleUi.printlnErr("Отмена.");
        }
    }
}
