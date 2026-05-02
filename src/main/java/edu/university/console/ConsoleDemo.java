package edu.university.console;

import edu.university.application.factory.UserFactory;
import edu.university.application.usecase.ResearcherDirectoryUseCase;
import edu.university.UniversityApp;
import edu.university.domain.model.Course;
import edu.university.domain.model.ResearchPaper;
import edu.university.domain.model.Student;
import edu.university.domain.model.Teacher;
import edu.university.domain.sort.PaperComparator;
import edu.university.domain.sort.ResearchPaperComparators;
import edu.university.domain.value.Role;
import edu.university.infrastructure.persistence.UniversityDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public final class ConsoleDemo {

    private ConsoleDemo() {
    }

    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            UniversityDatabase db = UniversityApp.db();

            System.out.print("Load saved data from data/university-state.ser? (y/n): ");
            if ("y".equalsIgnoreCase(trim(in.nextLine()))) {
                db.loadData();
                System.out.println("Loaded.");
            }

            UserFactory factory = UniversityApp.users();

            System.out.println("\n--- Teacher (researcher) ---");
            Teacher teacher = (Teacher) factory.createUser(Role.TEACHER);
            teacher.setId(promptRequired(in, "Teacher id"));
            teacher.setFirstName(promptRequired(in, "First name"));
            teacher.setLastName(promptRequired(in, "Last name"));
            teacher.setEmail(promptRequired(in, "Email"));
            teacher.setPassword(promptRequired(in, "Password (login)"));
            teacher.setHIndex(promptInt(in, "H-index", 0, 500));

            System.out.println("\n--- Student ---");
            Student student = (Student) factory.createUser(Role.STUDENT);
            student.setId(promptRequired(in, "Student id"));
            student.setFirstName(promptRequired(in, "First name"));
            student.setLastName(promptRequired(in, "Last name"));
            student.setEmail(promptRequired(in, "Email"));
            student.setPassword(promptRequired(in, "Password"));

            System.out.println("\n--- Course ---");
            Course course = new Course();
            course.setCourseCode(promptRequired(in, "Course code"));
            course.setCourseName(promptRequired(in, "Course title"));
            course.setCredits(promptInt(in, "Credits", 1, 21));
            course.addInstructor(teacher);

            System.out.println("\n--- Teacher paper (optional) ---");
            System.out.print("Paper title (Enter to skip): ");
            String paperTitle = trim(in.nextLine());
            if (!paperTitle.isEmpty()) {
                System.out.print("Authors (comma-separated): ");
                List<String> authors = splitAuthors(in.nextLine());
                System.out.print("Journal: ");
                String journal = trim(in.nextLine());
                System.out.print("Publisher (optional): ");
                String publisher = trim(in.nextLine());
                System.out.print("DOI (optional): ");
                String doi = trim(in.nextLine());
                System.out.print("Keywords (optional): ");
                String keywords = trim(in.nextLine());
                int citations = promptInt(in, "Citations count", 0, 1_000_000);
                int pages = promptInt(in, "Pages", 1, 10_000);
                LocalDate pubDate = promptDate(in, "Publication date yyyy-MM-dd (Enter = today)");
                teacher.addPaper(new ResearchPaper(paperTitle, authors, journal, publisher, doi, keywords,
                        citations, pages, pubDate));
            }

            db.add(teacher);
            db.add(student);
            db.add(course);

            System.out.println("\n=== Login check (teacher) ===");
            System.out.print("Type password again: ");
            String tryPass = trim(in.nextLine());
            System.out.println("Authenticated: " + teacher.authenticate(tryPass));

            System.out.println("\n=== Course registration ===");
            try {
                student.registerForCourse(course);
                System.out.println("OK. Student total credits: " + student.getTotalCredits());
            } catch (IllegalStateException ex) {
                System.out.println("Registration failed: " + ex.getMessage());
            }

            PaperComparator comparator = chooseComparator(in);
            ResearcherDirectoryUseCase researchers = UniversityApp.researchers();
            System.out.println("\n--- All researchers papers (sorted) ---");
            researchers.printAllResearchersPapersSorted(comparator);

            researchers.findTopResearcherByTotalCitations().ifPresent(r ->
                    System.out.println("\nTop by total citations: "
                            + r.getClass().getSimpleName()
                            + ", sum="
                            + r.getPapers().stream().mapToInt(ResearchPaper::getCitations).sum()));

            System.out.print("\nSave to data/university-state.ser? (y/n): ");
            if ("y".equalsIgnoreCase(trim(in.nextLine()))) {
                db.saveData();
                System.out.println("Saved.");
            }
            System.out.println("Bye.");
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String promptRequired(Scanner in, String label) {
        while (true) {
            System.out.print(label + ": ");
            String v = trim(in.nextLine());
            if (!v.isEmpty()) {
                return v;
            }
            System.out.println("(cannot be empty)");
        }
    }

    private static int promptInt(Scanner in, String label, int min, int max) {
        while (true) {
            System.out.print(label + ": ");
            String line = trim(in.nextLine());
            try {
                int v = Integer.parseInt(line);
                if (v >= min && v <= max) {
                    return v;
                }
                System.out.println("Must be between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Enter an integer.");
            }
        }
    }

    private static LocalDate promptDate(Scanner in, String label) {
        while (true) {
            System.out.print(label + ": ");
            String line = trim(in.nextLine());
            if (line.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(line);
            } catch (DateTimeParseException e) {
                System.out.println("Use yyyy-MM-dd or empty for today.");
            }
        }
    }

    private static List<String> splitAuthors(String line) {
        String s = trim(line);
        if (s.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(x -> !x.isEmpty())
                .collect(Collectors.toList());
    }

    private static PaperComparator chooseComparator(Scanner in) {
        System.out.println("\nSort papers: 1 = date, 2 = citations, 3 = page count");
        while (true) {
            System.out.print("Choice (1/2/3): ");
            String c = trim(in.nextLine());
            switch (c) {
                case "1":
                    return ResearchPaperComparators.BY_DATE;
                case "2":
                    return ResearchPaperComparators.BY_CITATIONS;
                case "3":
                    return ResearchPaperComparators.BY_PAGES;
                default:
                    System.out.println("Type 1, 2 or 3.");
            }
        }
    }
}
