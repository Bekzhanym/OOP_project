package kbtu_oop_project.console;

import kbtu_oop_project.UniversityApp;
import kbtu_oop_project.application.factory.UserFactory;
import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.console.features.home.GuestConsole;
import kbtu_oop_project.console.features.session.SessionDispatcher;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.util.Scanner;

public final class ConsoleApplication {

    private ConsoleApplication() {
    }

    public static void run() {
        Scanner in = new Scanner(System.in);
        UniversityDatabase db = UniversityApp.db();

        ConsoleUi.header("Загрузить сохранение системы (data/university-state.ser)?");
        System.out.print("Выполнить загрузку? (y/n): ");
        String loadChoice = ConsoleUi.trim(in.nextLine());
        
        if ("y".equalsIgnoreCase(loadChoice)) {
            try {
                db.loadData();
                ConsoleUi.printlnOk("База данных КБТУ успешно восстановлена из файла сериализации.");
            } catch (Exception ex) {
                ConsoleUi.printlnErr("Не удалось загрузить состояние: " + ex.getMessage());
                System.out.println("Запуск системы с пустой базой данных...");
            }
        } else {
            System.out.println("Запуск с чистой базой данных (дефолтное состояние)...");
        }

        UserFactory factory = UniversityApp.users();
        boolean quitApp = false;

        while (!quitApp) {
            try {
                User session = GuestConsole.guestHome(db, in, factory);
                if (session == null) {
                    quitApp = true;
                } else {
                    SessionDispatcher.runSessionForUser(session, db, in);
                }
            } catch (Exception ex) {
                ConsoleUi.printlnErr("Произошла непредвиденная ошибка сессии: " + ex.getMessage());
            }
        }

        ConsoleUi.header("Завершение работы системы");
        System.out.print("Сохранить текущие изменения в data/university-state.ser? (y/n): ");
        String saveChoice = ConsoleUi.trim(in.nextLine());
        
        if ("y".equalsIgnoreCase(saveChoice)) {
            try {
                db.saveData(); 
                ConsoleUi.printlnOk("Данные успешно сериализованы. Изменения сохранены.");
            } catch (Exception ex) {
                ConsoleUi.printlnErr("Критическая ошибка при сохранении данных: " + ex.getMessage());
            }
        }

        ConsoleUi.printlnOk("Выход из объектно-ориентированного симулятора КБТУ выполнен.");
    }
}