package kbtu_oop_project.console.common;

import kbtu_oop_project.domain.sort.PaperComparator;
import kbtu_oop_project.domain.sort.ResearchPaperComparators;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public final class ConsoleUi {

    private ConsoleUi() {
    }

    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    public static void header(String title) {
        System.out.println();
        System.out.println("─── " + title + " ───");
    }

    public static void printlnOk(String msg) {
        System.out.println("[OK] " + msg);
    }

    public static void printlnErr(String msg) {
        System.out.println("[!] " + msg);
    }

    public static String promptRequired(Scanner in, String label) {
        while (true) {
            System.out.print(label + ": ");
            String v = trim(in.nextLine());
            if (!v.isEmpty()) {
                return v;
            }
            System.out.println("(обязательное поле)");
        }
    }

    public static int promptInt(Scanner in, String label, int min, int max) {
        while (true) {
            System.out.print(label + ": ");
            String line = trim(in.nextLine());
            try {
                int v = Integer.parseInt(line);
                if (v >= min && v <= max) {
                    return v;
                }
                System.out.println("От " + min + " до " + max);
            } catch (NumberFormatException e) {
                System.out.println("Введите число.");
            }
        }
    }

    public static LocalDate promptDate(Scanner in, String label) {
        while (true) {
            System.out.print(label + ": ");
            String line = trim(in.nextLine());
            if (line.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(line);
            } catch (DateTimeParseException e) {
                System.out.println("Формат yyyy-MM-dd или пусто.");
            }
        }
    }

    public static List<String> splitAuthors(String line) {
        String s = trim(line);
        if (s.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(s.split(","))
                .map(String::trim)
                .filter(x -> !x.isEmpty())
                .collect(Collectors.toList());
    }

    public static PaperComparator choosePaperComparator(Scanner in) {
        System.out.println("Сортировка статей: 1 — дата, 2 — цитирования, 3 — страницы");
        while (true) {
            System.out.print("Выбор (1/2/3): ");
            String c = trim(in.nextLine());
            switch (c) {
                case "1":
                    return ResearchPaperComparators.BY_DATE;
                case "2":
                    return ResearchPaperComparators.BY_CITATIONS;
                case "3":
                    return ResearchPaperComparators.BY_PAGES;
                default:
                    System.out.println("Только 1, 2 или 3.");
            }
        }
    }
}
