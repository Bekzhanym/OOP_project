package kbtu_oop_project.console;

import kbtu_oop_project.UniversityApp;
import kbtu_oop_project.console.common.ConsoleUi;
import kbtu_oop_project.console.features.home.GuestConsole;
import kbtu_oop_project.console.features.session.SessionDispatcher;
import kbtu_oop_project.domain.features.user.Admin;
import kbtu_oop_project.domain.features.user.User;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.util.Scanner;

public final class ConsoleApplication {

    private ConsoleApplication() {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        try (Scanner in = new Scanner(System.in)) {
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
                System.out.println("Запуск с чистой базой данных...");
            }

            
            if (db.findAllUsers() == null || db.findAllUsers().isEmpty()) {
                ConsoleUi.header("⚠️ ПЕРВИЧНАЯ НАСТРОЙКА СИСТЕМЫ ПЛАТОНУС КБТУ");
                System.out.println("[!] Обнаружена пустая база данных. Создайте главный аккаунт Администратора:");
                
                System.out.print("Введите ваше Имя: ");
                String firstName = ConsoleUi.trim(in.nextLine());
                
                System.out.print("Введите вашу Фамилию: ");
                String lastName = ConsoleUi.trim(in.nextLine());
                
                
                String email = "";
                while (true) {
                    System.out.print("Введите ваш корпоративный Email (@kbtu.kz): ");
                    email = ConsoleUi.trim(in.nextLine()).toLowerCase(); 
                    
                    if (email.endsWith("@kbtu.kz") && email.length() > 8) {
                        break; 
                    } else {
                        ConsoleUi.printlnErr("[Ошибка] Недопустимый Email! Доступ разрешен только для домена @kbtu.kz (например, a_tulep@kbtu.kz)");
                        System.out.println(); 
                    }
                }
                
                System.out.print("Придумайте пароль: ");
                String password = ConsoleUi.trim(in.nextLine());
                
                String generatedId = "A01"; 

                try {
                    Admin rootAdmin = new Admin(generatedId, firstName, lastName, email, password);
                    db.addUser(rootAdmin);
                    
                    ConsoleUi.printlnOk("\n[УСПЕХ] Главный администратор успешно зарегистрирован в системе!");
                    System.out.printf("Владелец системы: %s %s (ID: %s)%n", firstName, lastName, generatedId);
                    System.out.println("Сейчас откроется стандартное окно авторизации КБТУ.\n");
                    
                    
                    db.saveData();
                    
                } catch (Exception e) {
                    ConsoleUi.printlnErr("Ошибка инициализации первого пользователя: " + e.getMessage());
                    System.out.println("Приложение завершает работу. Исправьте ошибки.");
                    return;
                }
            }

            
            boolean quitApp = false;

            while (!quitApp) {
                try {
                    
                    User session = GuestConsole.guestHome(db, in);
                    if (session == null) {
                        quitApp = true; 
                    } else {
                        SessionDispatcher.runSessionForUser(session, db, in);
                    }
                } catch (Exception ex) {
                    ConsoleUi.printlnErr("Произошла непредвиденная ошибка сессии: " + ex.getMessage());
                    if (in.hasNextLine()) {
                        in.nextLine(); 
                    }
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
}