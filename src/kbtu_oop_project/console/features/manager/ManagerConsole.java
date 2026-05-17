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
import kbtu_oop_project.domain.value.ManagerType; // Подключаем Enum типов менеджеров
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

public final class ManagerConsole {

    private ManagerConsole() {}

    public static boolean managerMenu(Manager manager, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Панель менеджера | Тип: " + manager.getManagerType());
        System.out.println("  1 — Статистика по оценкам (Генерация отчёта)");
        System.out.println("  2 — Студенты по среднему GPA (Transcript)");
        System.out.println("  3 — Назначить преподавателя на курс");
        System.out.println("  4 — Очередь заявок на курсы (" + db.getPendingCount() + " в ожидании)");
        System.out.println("  5 — Заявления сотрудников (подписанные Деканом/Ректором)");
        System.out.println("  6 — Рейтинг преподавателей (Алфавитный список)");
        System.out.println("  7 — Добавить новый курс в каталог");
        System.out.println("  8 — Управление новостной лентой");
        System.out.println("  0 — Выйти из аккаунта");
        System.out.print("Выбор: ");
        
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1":
                printMarksReport(manager, db); // Передаем менеджера для логирования действия
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
                printDeanSignedRequests(db);
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
        ConsoleUi.header("Обработка регистрационных заявок");
        List<PendingCourseRegistration> q = db.getPendingCourseRegistrationsView();
        if (q.isEmpty()) {
            System.out.println("(нет активных заявок на подпись)");
            return;
        }

        // Ограничение: Утверждать регистрацию по ТЗ имеет право только Office of Registrar (OR)
        if (manager.getManagerType() != ManagerType.OR) {
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

        if (cmd == 'a') {
            // Перехватываем бизнес-исключения (например, превышение 21 кредита)
            try {
                if (db.approvePendingCourseRegistration(email, code)) {
                    manager.approveRegistration(); // Увеличиваем счетчик операций менеджера
                    ConsoleUi.printlnOk("Заявка успешно одобрена. Студент зачислен на курс.");
                } else {
                    ConsoleUi.printlnErr("Ошибка: Не удалось провести транзакцию зачисления.");
                }
            } catch (Exception ex) { // Сюда упадет CreditLimitExceededException, если вы его создали
                ConsoleUi.printlnErr("Отклонено системой правил: " + ex.getMessage());
            }
        } else if (cmd == 'r') {
            if (db.rejectPendingCourseRegistration(email, code)) {
                ConsoleUi.printlnOk("Заявка успешно отклонена и удалена из очереди.");
            } else {
                ConsoleUi.printlnErr("Заявка не найдена.");
            }
        } else {
            ConsoleUi.printlnErr("Неизвестный префикс команды. Используйте 'a' или 'r'.");
        }
    }

    private static void printDeanSignedRequests(UniversityDatabase db) {
        ConsoleUi.header("Официальные запросы от сотрудников");
        // Фильтруем: выводим только REQUEST, которые уже имеют подпись декана
        List<EmployeeMessage> list = db.messagesRequiringDeanSignature().stream()
                .filter(m -> "REQUEST".equalsIgnoreCase(m.getKind()) && m.isRequiresDeanSignature()) 
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
        
        // Вызываем логику создания отчета непосредственно при запросе отчета (High Cohesion)
        manager.createStatisticalReport(); 
        
        List<Double> totals = new ArrayList<>();
        long passed = 0;
        for (User u : db.getUsers()) {
            if (u instanceof Student s) {
                for (Mark m : s.getTranscript().allMarks()) {
                    totals.add(m.calculateFinalScore());
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
        System.out.println("Процент успешной сдачи (GPA >= 50.0): " + (passed * 100 / totals.size()) + "%");
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
            lesson.setType("1".equals(lt) ? LessonType.Lecture : LessonType.Practice);
            int dow = ConsoleUi.promptInt(in, "День недели (1=Пн ... 7=Вс)", 1, 7);
            lesson.setDay(DayOfWeek.of(dow));
            int sh = ConsoleUi.promptInt(in, "Час начала занятия (8-20)", 8, 20);
            lesson.setStartTime(LocalTime.of(sh, 0));
            lesson.setEndTime(LocalTime.of(sh + 1, 30)); // Стандартная пара — 1.5 часа
        }

        Course course = new Course();
        course.setCourseCode(code.toUpperCase().trim());
        course.setCourseName(title.trim());
        course.setCredits(credits);
        course.setCourseType(ct);
        if (!major.isBlank()) course.setIntendedMajor(major.trim());
        if (year > 0) course.setIntendedYearOfStudy(year);
        course.setLesson(lesson);

        db.addCourse(course);
        db.recordAudit("MANAGER_ADD_COURSE " + code.trim());
        ConsoleUi.printlnOk("Дисциплина успешно внесена в академический реестр.");
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


