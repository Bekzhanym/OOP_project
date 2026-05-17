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
import kbtu_oop_project.domain.value.CourseType;
import kbtu_oop_project.domain.value.LessonType;
import kbtu_oop_project.domain.value.MessageKind;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public final class TeacherConsole {

    private TeacherConsole() {
    }

    public static boolean teacherMenu(Teacher teacher, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Панель преподавателя: " + teacher.getFirstName() + " " + teacher.getLastName());
        System.out.println("  1 — Мой профиль и курсы");
        System.out.println("  2 — Добавить научную статью (Research Paper)");
        System.out.println("  3 — Все исследователи и их статьи (сортировка)");
        System.out.println("  4 — Лучший по сумме цитирований");
        System.out.println("  5 — Лучший по цитированиям за конкретный год");
        System.out.println("  6 — Выставить / обновить баллы студенту");
        System.out.println("  7 — Список студентов на моих курсах");
        System.out.println("  8 — Внутренняя почта (входящие)");
        System.out.println("  9 — Отправить сообщение / жалобу сотруднику");
        System.out.println(" 10 — Управление исследовательскими проектами");
        System.out.println(" 11 — Модифицировать курс (Паттерн Observer / manageCourse)");
        System.out.println(" 12 — Экспорт ведомости оценок (exportGradeReport)");
        System.out.println("  0 — Выйти из аккаунта");
        System.out.print("Выбор: ");
        
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1":
                printProfile(teacher);
                break;
            case "2":
                addPaperFlow(teacher, in);
                break;
            case "3":
                printSortedPapers(db, in);
                break;
            case "4":
                printTopResearcher(db);
                break;
            case "5":
                printTopResearcherByYear(db, in);
                break;
            case "6":
                putMarksFlow(teacher, db, in);
                break;
            case "7":
                printEnrolledStudents(teacher);
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

    private static void printProfile(Teacher teacher) {
        ConsoleUi.header("Профиль преподавателя");
        System.out.println("Кафедра: " + teacher.getDepartment());
        System.out.println("Ученое звание / Title: " + (teacher.getTitle() != null ? teacher.getTitle() : "Нет"));
        System.out.println("Индекс Хирша (h-index): " + teacher.getHIndex());
        System.out.println("Ведёт курсов: " + teacher.getTaughtCourses().size());
        for (Course c : teacher.getTaughtCourses()) {
            System.out.println("   • " + c.getCourseCode() + " — " + c.getCourseName());
        }
    }

    private static void addPaperFlow(Teacher teacher, Scanner in) {
        System.out.print("Название статьи: ");
        String paperTitle = ConsoleUi.trim(in.nextLine());
        if (paperTitle.isEmpty()) {
            ConsoleUi.printlnErr("Пустое название — отмена.");
            return;
        }
        System.out.print("Авторы (через запятую): ");
        List<String> authors = ConsoleUi.splitAuthors(in.nextLine());
        System.out.print("Журнал: ");
        String journal = ConsoleUi.trim(in.nextLine());
        System.out.print("Издатель (опционально): ");
        String publisher = ConsoleUi.trim(in.nextLine());
        System.out.print("DOI (опционально): ");
        String doi = ConsoleUi.trim(in.nextLine());
        System.out.print("Ключевые слова (опционально): ");
        String keywords = ConsoleUi.trim(in.nextLine());
        
        int citations = ConsoleUi.promptInt(in, "Количество цитирований", 0, 1_000_000);
        int pages = ConsoleUi.promptInt(in, "Количество страниц", 1, 10_000);
        LocalDate pubDate = ConsoleUi.promptDate(in, "Дата yyyy-MM-dd (Enter = сегодня)", LocalDate.now());
        
        ResearchPaper paper = new ResearchPaper(paperTitle, authors, journal, publisher, doi, keywords, citations, pages, pubDate);
        teacher.addPaper(paper);
        ConsoleUi.printlnOk("Статья успешно добавлена исследователю.");
    }

    private static void printSortedPapers(UniversityDatabase db, Scanner in) {
        Comparator<ResearchPaper> cmp = ConsoleUi.choosePaperComparator(in);
        ConsoleUi.header("Статьи всех исследователей университета");
        db.printAllResearchersPapersSorted(cmp);
    }

    private static void printTopResearcher(UniversityDatabase db) {
        db.findTopResearcherByTotalCitations().ifPresentOrElse(
                r -> System.out.println("Топ по цитированию: "
                        + r.getClass().getSimpleName() + " [" + r.toString() + "]"
                        + ", Общая сумма цитирований = "
                        + r.getPapers().stream().mapToInt(ResearchPaper::getCitations).sum()),
                () -> ConsoleUi.printlnErr("В базе данных нет зарегистрированных исследователей."));
    }

    private static void printTopResearcherByYear(UniversityDatabase db, Scanner in) {
        int year = ConsoleUi.promptInt(in, "Введите год (YYYY)", 1900, 2100);
        db.findTopResearcherByCitationsInYear(year).ifPresentOrElse(
                r -> System.out.println("Топ за " + year + " год: "
                        + r.getClass().getSimpleName()
                        + ", сумма цитирований за указанный год = "
                        + r.getPapers().stream()
                        .filter(p -> p.getDate() != null && p.getDate().getYear() == year)
                        .mapToInt(ResearchPaper::getCitations).sum()),
                () -> ConsoleUi.printlnErr("Нет подтвержденных научных публикаций за " + year + " год."));
    }

    private static void printEnrolledStudents(Teacher teacher) {
        ConsoleUi.header("Студенты на ваших курсах");
        if (teacher.getTaughtCourses().isEmpty()) {
            System.out.println("(у вас нет назначенных курсов)");
            return;
        }
        for (Course c : teacher.getTaughtCourses()) {
            System.out.println(c.getCourseCode() + " — " + c.getCourseName());
            if (c.getEnrolledStudents().isEmpty()) {
                System.out.println("   (нет записавшихся студентов)");
                continue;
            }
            for (Student st : c.getEnrolledStudents()) {
                System.out.println("   • ID: " + st.getStudentId() + " | " + st.getFirstName() + " " + st.getLastName());
            }
        }
    }

    private static void printTeacherInbox(UniversityDatabase db, String email) {
        ConsoleUi.header("Внутренняя почта (Входящие)");
        List<EmployeeMessage> inbox = db.messagesForRecipientEmailIgnoreCase(email);
        if (inbox.isEmpty()) {
            System.out.println("(нет новых сообщений)");
            return;
        }
        int i = 1;
        for (EmployeeMessage m : inbox) {
            System.out.println(i++ + ") [" + m.getKind() + "] от: " + m.getFromEmail());
            System.out.println("   Требуется подпись декана: " + (m.isRequiresDeanSignature() ? "ДА" : "НЕТ"));
            System.out.println("   Содержимое: " + m.getBody());
            System.out.println();
        }
    }

    private static void sendTeacherEmployeeMail(Teacher teacher, UniversityDatabase db, Scanner in) {
        System.out.print("Email получателя (Employee): ");
        String to = ConsoleUi.trim(in.nextLine());
        if (to.isEmpty()) {
            ConsoleUi.printlnErr("Отмена операции.");
            return;
        }
        System.out.println("Категория отправления: 1 — Сообщение   2 — Официальная жалоба (Complaint)");
        System.out.print("Выбор: ");
        String kindChoice = ConsoleUi.trim(in.nextLine());
        MessageKind kind = "2".equals(kindChoice) ? MessageKind.COMPLAINT : MessageKind.MESSAGE;
        
        System.out.print("Текст отправления: ");
        String body = ConsoleUi.trim(in.nextLine());
        System.out.print("Требуется ли верификация/подпись декана? (y/n): ");
        boolean dean = "y".equalsIgnoreCase(ConsoleUi.trim(in.nextLine()));
        
        try {
            db.postEmployeeMessage(teacher, to, kind, body, dean);
            ConsoleUi.printlnOk("Сообщение успешно отправлено в инфраструктуру почты.");
        } catch (IllegalArgumentException ex) {
            ConsoleUi.printlnErr("Ошибка валидации: " + ex.getMessage());
        }
    }

    private static void teacherResearchFlow(Teacher teacher, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Управление научными проектами");
        System.out.println("  1 — Инициализировать проект и внести в реестр");
        System.out.println("  2 — Мои исследовательские группы");
        System.out.println("  3 — Посмотреть общеуниверситетский каталог");
        System.out.println("  0 — Вернуться");
        System.out.print("Выбор: ");
        
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1":
                System.out.print("Научная тема проекта (Topic): ");
                String topic = ConsoleUi.trim(in.nextLine());
                if (topic.isEmpty()) {
                    ConsoleUi.printlnErr("Тема не может быть пустой. Отмена.");
                    break;
                }
                ResearchProject p = new ResearchProject(topic, teacher); 
                teacher.addResearchProject(p);
                db.registerGlobalResearchProject(p);
                ConsoleUi.printlnOk("Проект успешно зарегистрирован и открыт для вступления (Research Group).");
                break;
            case "2":
                ConsoleUi.header("Мои исследовательские проекты");
                if (teacher.getResearchProjects().isEmpty()) {
                    System.out.println("(вы не курируете научные проекты)");
                    break;
                }
                for (ResearchProject rp : teacher.getResearchProjects()) {
                    String t = rp.getTopic() != null ? rp.getTopic() : "(тема не указана)";
                    System.out.println(" • ТЕМА: " + t + " | Активных участников: " + rp.getParticipants().size());
                }
                break;
            case "3":
                ConsoleUi.header("Общеуниверситетский каталог проектов");
                List<ResearchProject> globalList = db.getResearchProjectsUnmodifiable();
                if (globalList.isEmpty()) {
                    System.out.println("(в университете нет активных проектов)");
                    break;
                }
                int n = 1;
                for (ResearchProject rp : globalList) {
                    String t = rp.getTopic() != null ? rp.getTopic() : "(без темы)";
                    System.out.println((n++) + ") " + t + " | Руководитель: " + rp.getLeaderName());
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
            ConsoleUi.printlnErr("За вами не закреплено ни одного учебного курса.");
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            Course c = list.get(i);
            System.out.println((i + 1) + ") " + c.getCourseCode() + " — " + c.getCourseName());
        }
        int idx = ConsoleUi.promptInt(in, "Выберите порядковый номер курса", 1, list.size()) - 1;
        return list.get(idx);
    }

    private static void manageCourseFlow(Teacher teacher, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Модификация параметров курса (Паттерн Observer)");
        Course c = pickTaughtCourse(teacher, in);
        if (c == null) {
            return;
        }
        System.out.println("Текущие метаданные курса: " + c);
        System.out.print("Новое название курса (Enter — оставить прежним): ");
        String nn = ConsoleUi.trim(in.nextLine());
        String newName = nn.isEmpty() ? null : nn;

        System.out.print("Новое количество кредитов ECTS (Enter — без изменений): ");
        String crLine = ConsoleUi.trim(in.nextLine());
        Integer credits = null;
        if (!crLine.isEmpty()) {
            try {
                credits = Integer.parseInt(crLine);
            } catch (NumberFormatException ex) {
                ConsoleUi.printlnErr("Формат числа нарушен. Изменения сброшены.");
                return;
            }
        }

        CourseType ctNew = null;
        System.out.println("Изменить тип курса: 1 MAJOR | 2 MINOR | 3 ELECTIVE | Enter — пропустить");
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1" -> ctNew = CourseType.MAJOR;
            case "2" -> ctNew = CourseType.MINOR;
            case "3" -> ctNew = CourseType.ELECTIVE;
            default -> { }
        }

        Lesson lessonPatch = null;
        System.out.println("Обновить расписание занятий (Lesson Slot): 1 Лекция | 2 Практика | Enter — не менять");
        String ltPick = ConsoleUi.trim(in.nextLine());
        if ("1".equals(ltPick) || "2".equals(ltPick)) {
            lessonPatch = new Lesson();
            lessonPatch.setType("1".equals(ltPick) ? LessonType.LECTURE : LessonType.PRACTICE);
            int dow = ConsoleUi.promptInt(in, "День проведения (1=Пн … 7=Вс)", 1, 7);
            lessonPatch.setDay(DayOfWeek.of(dow));
            int sh = ConsoleUi.promptInt(in, "Час начала занятия", 8, 21);
            int eh = ConsoleUi.promptInt(in, "Час окончания занятия", sh + 1, 22);
            lessonPatch.setStartTime(LocalTime.of(sh, 0));
            lessonPatch.setEndTime(LocalTime.of(eh, 0));
        }

        try {
            teacher.manageCourse(c, newName, credits, ctNew, lessonPatch);
            ConsoleUi.printlnOk("Параметры курса изменены. Студенты-подписчики уведомлены.");
        } catch (IllegalStateException ex) {
            ConsoleUi.printlnErr("Ошибка доменной логики: " + ex.getMessage());
        }
    }

    private static void exportGradeReportFlow(Teacher teacher, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Генерация академической ведомости");
        Course c = pickTaughtCourse(teacher, in);
        if (c == null) {
            return;
        }
        try {
            teacher.exportGradeReport(c);
            ConsoleUi.printlnOk("Ведомость успешно выгружена в файл / систему хранения.");
        } catch (IllegalStateException ex) {
            ConsoleUi.printlnErr("Отказ экспорта: " + ex.getMessage());
        }
    }

    private static void putMarksFlow(Teacher teacher, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Выставление промежуточных и итоговых баллов");
        System.out.print("Введите Student ID: ");
        String sid = ConsoleUi.trim(in.nextLine());
        Student student = db.findStudentByStudentId(sid).orElse(null);
        if (student == null) {
            ConsoleUi.printlnErr("Студент с таким ID не зарегистрирован в базе данных.");
            return;
        }
        System.out.print("Введите код курса: ");
        String code = ConsoleUi.trim(in.nextLine());
        Course course = db.findCourseByCode(code).orElse(null);
        if (course == null) {
            ConsoleUi.printlnErr("Курс не найден.");
            return;
        }

        Mark currentMark = student.getMarkForCourse(course).orElse(new Mark());

        System.out.println("Выберите контрольную точку для изменения:");
        System.out.println("  1 — Первая аттестация (Текущее значение: " + currentMark.getFirstAttestation() + " / 30)");
        System.out.println("  2 — Вторая аттестация (Текущее значение: " + currentMark.getSecondAttestation() + " / 30)");
        System.out.println("  3 — Финальный экзамен (Текущее значение: " + currentMark.getFinalExam() + " / 40)");
        System.out.print("Выбор: ");
        
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1" -> currentMark.setFirstAttestation(ConsoleUi.promptDouble(in, "Балл за 1-ю аттестацию", 0.0, 30.0));
            case "2" -> currentMark.setSecondAttestation(ConsoleUi.promptDouble(in, "Балл за 2-ю аттестацию", 0.0, 30.0));
            case "3" -> currentMark.setFinalExam(ConsoleUi.promptDouble(in, "Балл за Финальный экзамен", 0.0, 40.0));
            default -> {
                ConsoleUi.printlnErr("Операция отменена. Баллы не изменены.");
                return;
            }
        }

        try {
            teacher.putMark(student, course, currentMark);
            ConsoleUi.printlnOk("Изменения внесены. Текущий совокупный балл по шкале 100: " + currentMark.calculateFinalScore());
        } catch (IllegalStateException ex) {
            ConsoleUi.printlnErr("Ошибка сохранения оценки: " + ex.getMessage());
        }
    }
}