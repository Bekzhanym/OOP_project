package kbtu_oop_project.console.features.employee;

import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.domain.features.misc.EmployeeMessage;
import kbtu_oop_project.domain.features.user.Employee;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.util.List;
import java.util.Scanner;

public final class GenericEmployeeConsole {

    private GenericEmployeeConsole() {
    }

    public static boolean menu(Employee employee, UniversityDatabase db, Scanner in) {
        System.out.println("  1 — Внутренняя почта (входящие)");
        System.out.println("  2 — Отправить сообщение / жалобу сотруднику");
        System.out.println("  0 — Выйти из аккаунта");
        System.out.print("Выбор: ");
        switch (ConsoleUi.trim(in.nextLine())) {
            case "1":
                printInbox(db, employee.getEmail());
                break;
            case "2":
                sendEmployeeMessageFlow(employee, db, in);
                break;
            case "0":
                return true;
            default:
                ConsoleUi.printlnErr("Неизвестная команда.");
        }
        return false;
    }

    private static void printInbox(UniversityDatabase db, String email) {
        ConsoleUi.header("Входящие");
        List<EmployeeMessage> inbox = db.messagesForRecipientEmailIgnoreCase(email);
        if (inbox.isEmpty()) {
            System.out.println("(нет сообщений)");
            return;
        }
        int i = 1;
        for (EmployeeMessage m : inbox) {
            System.out.println(i++ + ") [" + m.getKind() + "] от " + m.getFromEmail());
            System.out.println("   → вам | deanFlag=" + m.isRequiresDeanSignature());
            System.out.println("   " + m.getBody());
            System.out.println();
        }
    }

    private static void sendEmployeeMessageFlow(Employee from, UniversityDatabase db, Scanner in) {
        System.out.print("Email получателя (Employee): ");
        String to = ConsoleUi.trim(in.nextLine());
        if (to.isEmpty()) {
            ConsoleUi.printlnErr("Отмена.");
            return;
        }
        System.out.println("Тип: 1 — сообщение   2 — жалоба");
        System.out.print("Выбор: ");
        String kindChoice = ConsoleUi.trim(in.nextLine());
        String kind = switch (kindChoice) {
            case "2" -> EmployeeMessage.KIND_COMPLAINT;
            default -> EmployeeMessage.KIND_MESSAGE;
        };
        System.out.print("Текст: ");
        String body = ConsoleUi.trim(in.nextLine());
        System.out.print("Требуется подпись декана? y/n: ");
        boolean dean = "y".equalsIgnoreCase(ConsoleUi.trim(in.nextLine()));
        try {
            db.postEmployeeMessage(from, to, kind, body, dean);
            ConsoleUi.printlnOk("Отправлено.");
        } catch (IllegalArgumentException ex) {
            ConsoleUi.printlnErr(ex.getMessage());
        }
    }
}
