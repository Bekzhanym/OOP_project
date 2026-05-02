package kbtu_oop_project.console.features.teacher;

import kbtu_oop_project.UniversityApp;
import kbtu_oop_project.application.usecase.ResearcherDirectoryUseCase;
import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.research.ResearchPaper;
import kbtu_oop_project.domain.features.user.Teacher;
import kbtu_oop_project.domain.sort.PaperComparator;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public final class TeacherConsole {

    private TeacherConsole() {
    }

    public static boolean teacherMenu(Teacher teacher, Scanner in) {
        ResearcherDirectoryUseCase researchers = UniversityApp.researchers();
        System.out.println("  1 — Мой профиль и курсы");
        System.out.println("  2 — Добавить статью");
        System.out.println("  3 — Все исследователи и их статьи (сортировка)");
        System.out.println("  4 — Лучший по сумме цитирований");
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
                researchers.printAllResearchersPapersSorted(cmp);
                break;
            case "4":
                researchers.findTopResearcherByTotalCitations().ifPresentOrElse(
                        r -> System.out.println("Топ по цитированию: "
                                + r.getClass().getSimpleName()
                                + ", сумма="
                                + r.getPapers().stream().mapToInt(ResearchPaper::getCitations).sum()),
                        () -> ConsoleUi.printlnErr("Нет исследователей."));
                break;
            case "0":
                return true;
            default:
                ConsoleUi.printlnErr("Неизвестная команда.");
        }
        return false;
    }
}
