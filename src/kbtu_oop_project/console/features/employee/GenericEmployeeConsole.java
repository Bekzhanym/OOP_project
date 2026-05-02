package kbtu_oop_project.console.features.employee;

import kbtu_oop_project.console.common.ConsoleUi;

import java.util.Scanner;

public final class GenericEmployeeConsole {

    private GenericEmployeeConsole() {
    }

    public static boolean menu(Scanner in) {
        System.out.println("  (Для этой роли доступен только просмотр профиля.)");
        System.out.println("  0 — Выйти из аккаунта");
        System.out.print("Выбор: ");
        if ("0".equals(ConsoleUi.trim(in.nextLine()))) {
            return true;
        }
        ConsoleUi.printlnErr("Неизвестная команда.");
        return false;
    }
}
