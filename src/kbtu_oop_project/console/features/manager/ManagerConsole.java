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
import kbtu_oop_project.domain.value.ManagerType;
import kbtu_oop_project.domain.value.MessageKind;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

public final class ManagerConsole {

    private ManagerConsole() {}

    private static User extractCoreUser(User u) {
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

    public static boolean managerMenu(Manager manager, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Панель менеджера | Тип: " + manager.getTitle());
        System.out.println("  1 — Статистика по оценкам (Генерация отчёта)");
        System.out.println("  2 — Студенты по среднему GPA (Transcript)");
        System.out.println("  3 — Назначить преподавателя на курс");
        System.out.println("  4 — Очередь заявок на курсы (" + db.getPendingCount() + " в ожидании)");
        System.out.println("  5 — Заявления сотрудников (подписанные Деканом/Ректором)");
        System.out.println("  6 — Рейтинг преподавателей (Алфавитный список)");
        System.out.println("  7 — Добавить новый курс в каталог");
        System.out.println("  8 — Управление новостной лентой");
        System.out.println("  9 — Добавить занятие (расписание) к курсу 📅"); 
        System.out.println("  0 — Выйти из аккаунта");
        System.out.print("Выбор: ");
        
        String choice = ConsoleUi.trim(in.nextLine());
        return switch (choice) {
            case "1" -> { printMarksReport(manager, db); yield false; }
            case "2" -> { printStudentsByTranscriptGpa(db); yield false; }
            case "3" -> { assignInstructorFlow(db, in); yield false; }
            case "4" -> { pendingRegistrationFlow(manager, db, in); yield false; }
            case "5" -> { printDeanSignedRequests(db); yield false; }
            case "6" -> { printTeachersAlphabeticalWithRatings(db); yield false; }
            case "7" -> { createCourseCatalogEntry(manager, db, in); yield false; }
            case "8" -> { universityNewsFlow(manager, db, in); yield false; }
            case "9" -> { addLessonToCourseFlow(db, in); yield false; } // ДОБАВЛЕНО
            case "0" -> true;
            default -> { ConsoleUi.printlnErr("Неизвестная команда."); yield false; }
        };
    }

    private static void addLessonToCourseFlow(UniversityDatabase db, Scanner in) {
        ConsoleUi.header("УПРАВЛЕНИЕ РАСПИСАНИЕМ КУРСА");
        String courseCode = ConsoleUi.promptRequired(in, "Введите код курса (например, CS 301)").toUpperCase().trim();
        
        Optional<Course> courseOpt = db.findCourseByCode(courseCode);
        if (courseOpt.isEmpty()) {
            ConsoleUi.printlnErr("Курс с кодом " + courseCode + " не найден в реестре.");
            return;
        }
        Course course = courseOpt.get();

        System.out.println("Выберите тип занятия: 1 — Lecture (Лекция)  2 — Practice (Практика)");
        String lt = ConsoleUi.trim(in.nextLine());
        LessonType type = "1".equals(lt) ? LessonType.LECTURE : LessonType.PRACTICE;

        int dow = ConsoleUi.promptInt(in, "День недели (1=Пн, 2=Вт, 3=Ср, 4=Чт, 5=Пт, 6=Сб, 7=Вс)", 1, 7);
        int sh = ConsoleUi.promptInt(in, "Час начала занятия (8-20)", 8, 20);
        int sm = ConsoleUi.promptInt(in, "Минуты начала занятия (0-59)", 0, 59);

        Lesson lesson = new Lesson();
        lesson.setType(type);
        lesson.setDay(DayOfWeek.of(dow));
        lesson.setStartTime(LocalTime.of(sh, sm));
        
        lesson.setEndTime(LocalTime.of(sh, sm).plusMinutes(90)); 

        course.addLesson(lesson);
        db.recordAudit("MANAGER_ADD_LESSON " + courseCode + " [" + type + " " + DayOfWeek.of(dow) + " " + sh + ":" + sm + "]");
        
        ConsoleUi.printlnOk("Новое занятие успешно добавлено в расписание курса " + course.getCourseName() + "!");
    }

    private static void pendingRegistrationFlow(Manager manager, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Обработка регистрационных заявок");
        List<PendingCourseRegistration> q = db.getPendingCourseRegistrationsView();
        if (q.isEmpty()) {
            System.out.println("(нет активных заявок на подпись)");
            return;
        }

        if (manager.getTitle() != ManagerType.OFFICE_REGISTRATOR) {
            ConsoleUi.printlnErr("Предупреждение: Согласно регламенту, утверждать регистрацию может только менеджер OR.");
            System.out.print("Продолжить операцию вопреки ограничениям? (y/n): ");
            if (!"y".equalsIgnoreCase(ConsoleUi.trim(in.nextLine()))) return;
        }

        for (int i = 0; i < q.size(); i++) {
            PendingCourseRegistration p = q.get(i);
            System.out.println("  " + (i + 1) + ") " + p.getStudentEmail() + " ➔ " + p.getCourseCode());
        }
        
        System.out.print("Введите команду (например, 'a 1' для аппрува или 'r 1' для отклонения): ");
        String line = ConsoleUi.trim(in.nextLine());
        if (line.isEmpty()) return;

        String[] parts = line.split("\\s+");
        if (parts.length < 2) {
            ConsoleUi.printlnErr("Неверный формат команды. Используйте: a [номер] или r [номер]");
            return;
        }

        char cmd = Character.toLowerCase(parts[0].charAt(0));
        int idx;
        try {
            idx = Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException ex) {
            ConsoleUi.printlnErr("Указан некорректный порядковый номер.");
            return;
        }

        if (idx < 0 || idx >= q.size()) {
            ConsoleUi.printlnErr("Заявка с таким номером отсутствует в очереди.");
            return;
        }

        PendingCourseRegistration pend = q.get(idx);
        String email = pend.getStudentEmail();
        String code = pend.getCourseCode();

        var regOffice = kbtu_oop_project.domain.features.registration.RegistrationOffice.getInstance();

        if (cmd == 'a') {
            try {
                if (db.approvePendingCourseRegistration(email, code)) {
                    regOffice.removeRequest(email, code);
                    ConsoleUi.printlnOk("Заявка успешно одобрена. Студент зачислен на курс.");
                } else {
                    ConsoleUi.printlnErr("Ошибка: Не удалось провести транзакцию зачисления.");
                }
            } catch (Exception ex) {
                ConsoleUi.printlnErr("Отклонено системой правил: " + ex.getMessage());
            }
        } else if (cmd == 'r') {
            if (db.rejectPendingCourseRegistration(email, code)) {
                regOffice.removeRequest(email, code);
                ConsoleUi.printlnOk("Заявка успешно отклонена.");
            } else {
                ConsoleUi.printlnErr("Заявка не найдена.");
            }
        } else {
            ConsoleUi.printlnErr("Неизвестный префикс команды. Используйте 'a' или 'r'.");
        }
    }

    private static void printDeanSignedRequests(UniversityDatabase db) {
        ConsoleUi.header("Официальные запросы от сотрудников");
        List<EmployeeMessage> list = db.messagesRequiringDeanSignature().stream()
                .filter(m -> m.getKind() == MessageKind.REQUEST && m.isRequiresDeanSignature())
                .toList();

        if (list.isEmpty()) {
            System.out.println("(нет активных заявлений, подписанных руководством)");
            return;
        }
        for (EmployeeMessage m : list) {
            System.out.println(" [ЗАЯВЛЕНИЕ] От: " + m.getFromEmail());
            System.out.println(" Суть запроса: " + m.getBody());
            System.out.println(" Статус: 🟢 Подписано Деканом. Требуется исполнение менеджером.");
            System.out.println("────────────────────────────────────────────────");
        }
    }

    private static void printMarksReport(Manager manager, UniversityDatabase db) {
        ConsoleUi.header("Генерация академического отчета");
        manager.createStatisticalReport(); 
        
        List<Double> totals = new ArrayList<>();
        long passed = 0;
        for (User u : db.getUsers()) {
            User core = extractCoreUser(u); 
            if (core instanceof Student s) {
                for (Mark m : s.getTranscript().allMarks()) {
                    totals.add((double) m.calculateFinalScore());
                    if (m.isPassed()) passed++;
                }
            }
        }
        if (totals.isEmpty()) {
            System.out.println("База данных оценок пуста. Нет информации для расчета статистики.");
            return;
        }
        double avg = totals.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        System.out.println("Общее количество выставленных оценок: " + totals.size());
        System.out.printf("Средний балл по университету (Total Mean): %.2f%n", avg);
        System.out.printf("Процент успешной сдачи (GPA >= 50.0): %.1f%%%n", (passed * 100.0 / totals.size()));
    }

    private static void createCourseCatalogEntry(Manager manager, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Регистрация новой дисциплины в каталог");
        String code = ConsoleUi.promptRequired(in, "Код курса (например, CS 301)");
        if (db.findCourseByCode(code).isPresent()) {
            ConsoleUi.printlnErr("Курс с таким кодом уже зарегистрирован в системе.");
            return;
        }
        String title = ConsoleUi.promptRequired(in, "Название дисциплины");
        int credits = ConsoleUi.promptInt(in, "Количество кредитов ECTS", 1, 10);
        System.out.print("Специальность (Major, например, 'IS', Enter — для всех): ");
        String major = ConsoleUi.trim(in.nextLine());
        int year = ConsoleUi.promptInt(in, "Рекомендуемый год обучения (1-4, 0 — любой)", 0, 4);

        System.out.println("Категория дисциплины: 1 — MAJOR (Обязательный)  2 — MINOR  Enter — ELECTIVE");
        CourseType ct = switch (ConsoleUi.trim(in.nextLine())) {
            case "1" -> CourseType.MAJOR;
            case "2" -> CourseType.MINOR;
            default -> CourseType.ELECTIVE;
        };

        Lesson lesson = null;
        System.out.println("Тип занятия: 1 — Lecture (Лекция)  2 — Practice (Практика)  Enter — Без расписания");
        String lt = ConsoleUi.trim(in.nextLine());
        if ("1".equals(lt) || "2".equals(lt)) {
            lesson = new Lesson();
            lesson.setType("1".equals(lt) ? LessonType.LECTURE : LessonType.PRACTICE);
            int dow = ConsoleUi.promptInt(in, "День недели (1=Пн ... 7=Вс)", 1, 7);
            lesson.setDay(DayOfWeek.of(dow));
            int sh = ConsoleUi.promptInt(in, "Час начала занятия (8-20)", 8, 20);
            lesson.setStartTime(LocalTime.of(sh, 0));
            lesson.setEndTime(LocalTime.of(sh + 1, 30)); 
        }

        Course course = new Course();
        course.setCourseCode(code.toUpperCase().trim());
        course.setCourseName(title.trim());
        course.setCredits(credits);
        course.setCourseType(ct);
        if (!major.isBlank()) course.setIntendedMajor(major.trim());
        if (year > 0) course.setIntendedYearOfStudy(year);
        if (lesson != null) course.addLesson(lesson);

        db.addCourse(course);
        db.recordAudit("MANAGER_ADD_COURSE " + code.trim());
        ConsoleUi.printlnOk("Дисциплина успешно внесена в академический реестр.");
    }

    private static void printStudentsByTranscriptGpa(UniversityDatabase db) {
        ConsoleUi.header("Студенты по GPA (transcript)");
        List<Student> list = new ArrayList<>();
        for (User u : db.getUsers()) {
            User core = extractCoreUser(u); 
            if (core instanceof Student s) {
                list.add(s);
            }
        }
        list.sort(Comparator.comparingDouble((Student s) -> s.getTranscript().getTotalGPA()).reversed());
        for (Student s : list) {
            System.out.printf("%s %s | studentId=%s | GPA=%.2f%n",
                    s.getFirstName(), s.getLastName(), s.getStudentId(), s.getTranscript().getTotalGPA());
        }
    }

    private static void printTeachersAlphabeticalWithRatings(UniversityDatabase db) {
         ConsoleUi.header("Преподаватели и средний рейтинг");
         Map<String, List<Integer>> ratings = aggregateRatingsByTeacherEmailNormalized(db);
         List<Teacher> teachers = new ArrayList<>();
         for (User u : db.getUsers()) {
             User core = extractCoreUser(u); 
             if (core instanceof Teacher t) {
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
                     t.getFirstName(), t.getLastName(), t.getEmail(), t.getDepartment(), avgStr);
         }
     }

     private static void universityNewsFlow(Manager manager, UniversityDatabase db, Scanner in) {
         ConsoleUi.header("Новости");
         System.out.println("  1 — Показать все   2 — Добавить строку");
         System.out.print("Выбор: ");
         switch (ConsoleUi.trim(in.nextLine())) {
             case "1" -> {
                 var lines = db.getNewsLinesView();
                 if (lines.isEmpty()) {
                     System.out.println("(пусто)");
                 } else {
                     for (String s : lines) System.out.println(" • " + s);
                 }
             }
             case "2" -> {
                 System.out.print("Текст новости: ");
                 String body = ConsoleUi.trim(in.nextLine());
                 try {
                     db.addUniversityNews(body);
                     ConsoleUi.printlnOk("Добавлено.");
                 } catch (IllegalArgumentException ex) {
                     ConsoleUi.printlnErr(ex.getMessage());
                 }
             }
             default -> ConsoleUi.printlnErr("Отмена.");
         }
     }
    
    private static void assignInstructorFlow(UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Назначение преподавателя на курс");
        String teacherEmail = ConsoleUi.promptRequired(in, "Email преподавателя");
        Optional<User> tu = db.findByEmailIgnoreCase(teacherEmail);
        
        if (tu.isEmpty()) {
            ConsoleUi.printlnErr("Преподаватель не найден.");
            return;
        }
        
        User core = extractCoreUser(tu.get()); 
        if (!(core instanceof Teacher teacher)) {
            ConsoleUi.printlnErr("Указанный пользователь не является преподавателем.");
            return;
        }
        
        String courseCode = ConsoleUi.promptRequired(in, "Код курса");
        Optional<Course> co = db.findCourseByCode(courseCode);
        if (co.isEmpty()) {
            ConsoleUi.printlnErr("Курс не найден.");
            return;
        }
        co.get().addInstructor(teacher);
        db.recordAudit("ASSIGN_INSTRUCTOR " + teacherEmail + " → " + courseCode);
        ConsoleUi.printlnOk("Преподаватель назначен на курс.");
    }

    private static Map<String, List<Integer>> aggregateRatingsByTeacherEmailNormalized(UniversityDatabase db) {
        Map<String, List<Integer>> result = new HashMap<>();
        for (User u : db.getUsers()) {
            User core = extractCoreUser(u);
            if (core instanceof Student s) {
                s.getTeacherRatingsSnapshot().forEach((email, rating) ->
                    result.computeIfAbsent(email.toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(rating));
            }
        }
        return result;
    }
}