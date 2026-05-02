package kbtu_oop_project.console;

import kbtu_oop_project.UniversityApp;
import kbtu_oop_project.application.factory.UserFactory;
import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.console.features.home.GuestConsole;
import kbtu_oop_project.console.features.session.SessionDispatcher;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.util.Scanner;

final class ConsoleApplication {

    private ConsoleApplication() {
    }

    static void run() {
        try (Scanner in = new Scanner(System.in)) {
            UniversityDatabase db = UniversityApp.db();

            ConsoleUi.header("Загрузить сохранение data/university-state.ser?");
            System.out.print("y/n: ");
            if ("y".equalsIgnoreCase(ConsoleUi.trim(in.nextLine()))) {
                db.loadData();
                ConsoleUi.printlnOk("Данные загружены.");
            }

            UserFactory factory = UniversityApp.users();
            boolean quitApp = false;
            while (!quitApp) {
                User session = GuestConsole.guestHome(db, in, factory);
                if (session == null) {
                    quitApp = true;
                } else {
                    SessionDispatcher.runSessionForUser(session, db, in);
                }
            }
            ConsoleUi.printlnOk("Выход из программы.");
        }
    }
}
