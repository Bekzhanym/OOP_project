package kbtu_oop_project.console.common;

import kbtu_oop_project.domain.features.research.ResearchPaper;
import kbtu_oop_project.domain.sort.ResearchPaperComparators;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ConsoleUi {

    private static final Pattern KBTU_ID_PATTERN = Pattern.compile("^\\d{2}[B_M_D_T_F]\\d{6}$");

    private ConsoleUi() {
        throw new UnsupportedOperationException("Это утилитарный класс, его нельзя инстанцировать.");
    }

    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    public static void header(String title) {
        System.out.println();
        System.out.println("───────  " + title.toUpperCase() + "  ───────");
    }

    public static void printlnOk(String msg) {
        System.out.println("[OK] " + msg);
    }

    public static void printlnErr(String msg) {
        System.out.println("[!] Ошибка: " + msg);
    }

    /**
     * Проверяет строку на соответствие маске ID КБТУ (например, 24B777777)
     */
    public static boolean isValidKbtuId(String id) {
        if (id == null) return false;
        return KBTU_ID_PATTERN.matcher(id.trim()).matches();
    }

    /**
     * Запрашивает ID КБТУ до тех пор, пока не будет введено значение нужного формата
     */
    public static String promptKbtuId(Scanner in, String label) {
        while (true) {
            System.out.print(label + " (например, 24B777777): ");
            String id = trim(in.nextLine());
            if (isValidKbtuId(id)) {
                return id;
            }
            printlnErr("Неверный формат ID КБТУ! Шаблон: 2 цифры года + буква (B/M/D) + 6 цифр.");
        }
    }

    public static String promptRequired(Scanner in, String label) {
        while (true) {
            System.out.print(label + ": ");
            String v = trim(in.nextLine());
            if (!v.isEmpty()) {
                return v;
            }
            System.out.println("(Ошибка: это поле обязательно для заполнения)");
        }
    }

    public static int promptInt(Scanner in, String label, int min, int max) {
        while (true) {
            System.out.print(label + " (" + min + "-" + max + "): ");
            String line = trim(in.nextLine());
            try {
                int v = Integer.parseInt(line);
                if (v >= min && v <= max) {
                    return v;
                }
                System.out.println("Ошибка: число должно быть в диапазоне от " + min + " до " + max);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное целое число.");
            }
        }
    }

    public static double promptDouble(Scanner in, String label, double min, double max) {
        while (true) {
            System.out.print(label + " (диапазон " + min + " - " + max + "): ");
            String line = trim(in.nextLine()).replace(',', '.');
            try {
                double v = Double.parseDouble(line);
                if (v >= min && v <= max) {
                    return v;
                }
                System.out.println("Ошибка: значение должно быть от " + min + " до " + max);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное дробное число (например, 3.67).");
            }
        }
    }

    public static LocalDate promptDate(Scanner in, String label, LocalDate defaultDate) {
        while (true) {
            System.out.print(label + " (ГГГГ-ММ-ДД) [Enter для " + defaultDate + "]: ");
            String line = trim(in.nextLine());
            if (line.isEmpty()) {
                return defaultDate;
            }
            try {
                return LocalDate.parse(line);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: неверный формат. Используйте шаблон YYYY-MM-DD (например, " + LocalDate.now() + ").");
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

    public static Comparator<ResearchPaper> choosePaperComparator(Scanner in) {
        header("Выбор критерия сортировки статей");
        System.out.println("1 ── По дате публикации");
        System.out.println("2 ── По количеству цитирований");
        System.out.println("3 ── По объёму (количеству страниц)");
        System.out.println("4 ── По длине названия статьи");
        
        while (true) {
            System.out.print("Ваш выбор (1/2/3/4): ");
            String c = trim(in.nextLine());
            switch (c) {
                case "1" -> { return ResearchPaperComparators.BY_DATE; }
                case "2" -> { return ResearchPaperComparators.BY_CITATIONS; }
                case "3" -> { return ResearchPaperComparators.BY_PAGES; }
                case "4" -> { return ResearchPaperComparators.BY_TITLE_LENGTH; }
                default -> System.out.println("Ошибка: выберите пункт 1, 2, 3 или 4.");
            }
        }
    }
}