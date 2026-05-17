package kbtu_oop_project.console.features.employee;

import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.domain.features.misc.EmployeeMessage;
import kbtu_oop_project.domain.features.user.Employee;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.domain.value.MessageKind;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.util.List;
import java.util.Scanner;

public final class GenericEmployeeConsole {

    private GenericEmployeeConsole() {
        throw new UnsupportedOperationException("Это утилитарный класс для CLI сотрудников.");
    }

    public static void start(User employee, UniversityDatabase db, Scanner in) {
        while (true) {
            ConsoleUi.header("Корпоративная почта сотрудника");
            System.out.println("  1 — Посмотреть входящие сообщения");
            System.out.println("  2 — Отправить сообщение / жалобу / запрос");
            System.out.println("  0 — Вернуться в главное меню");
            
            System.out.print("Выбор: ");
            String choice = ConsoleUi.trim(in.nextLine());
            
            if ("0".equals(choice)) {
                break;
            }
            
            handleMenuChoice(choice, employee, db, in);
        }
    }

    private static void handleMenuChoice(String choice, User employee, UniversityDatabase db, Scanner in) {
        
        switch (choice) {
            case "1" -> printInbox(db, employee.getEmail());
            case "2" -> sendEmployeeMessageFlow(employee, db, in);
            default -> ConsoleUi.printlnErr("Неизвестная команда. Повторите ввод.");
        }
    }

    private static void printInbox(UniversityDatabase db, String email) {
        ConsoleUi.header("Входящие сообщения");
        List<EmployeeMessage> inbox = db.messagesForRecipientEmailIgnoreCase(email);
        
        if (inbox.isEmpty()) {
            System.out.println("  (Ваш ящик пуст)");
            return;
        }
        
        int i = 1;
        for (EmployeeMessage m : inbox) {
            System.out.println(i++ + ") [" + m.getKind().name() + "] от " + m.getFromEmail());
            System.out.println("     Статус: " + (m.isRequiresDeanSignature() ? "⚠️ Требуется подпись декана/ректора" : "ℹ️ Информационное"));
            System.out.println("     Текст: " + m.getBody());
            System.out.println("  ────────────────────────────────────────");
        }
    }

    private static void sendEmployeeMessageFlow(User from, UniversityDatabase db, Scanner in) {
        ConsoleUi.header("Новое отправление");
        
        String to = ConsoleUi.promptRequired(in, "Email получателя (Сотрудника)");
        
        System.out.println("Выберите тип отправления:");
        System.out.println("  1 — Обычное сообщение");
        System.out.println("  2 — Официальная жалоба (Complaint)");
        System.out.println("  3 — Запрос/Заявление (Request)");
        
        System.out.print("Выбор (по умолчанию 1): ");
        String kindChoice = ConsoleUi.trim(in.nextLine());
        
        MessageKind kind = switch (kindChoice) {
            case "2" -> MessageKind.COMPLAINT;
            case "3" -> MessageKind.REQUEST;
            default -> MessageKind.MESSAGE;
        };
        
        String body = ConsoleUi.promptRequired(in, "Введите текст сообщения");
        
        boolean dean = false;
        if (kind == MessageKind.REQUEST) {
            dean = true;
            System.out.println("ℹ️ Для данного типа отправления (Запрос) автоматически требуется подпись декана/ректора.");
        } else {
            System.out.print("Требуется официальное подписание деканом/ректором? (y/n): ");
            dean = "y".equalsIgnoreCase(ConsoleUi.trim(in.nextLine()));
        }
        
        try {
            db.postEmployeeMessage(from, to, kind, body, dean);
            ConsoleUi.printlnOk("Отправление успешно зарегистрировано в системе.");
        } catch (IllegalArgumentException ex) {
            ConsoleUi.printlnErr("Ошибка отправки: " + ex.getMessage());
        }
    }
}