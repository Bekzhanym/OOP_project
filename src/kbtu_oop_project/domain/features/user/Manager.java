package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.misc.PendingCourseRegistration;
import kbtu_oop_project.domain.features.notification.Notification;
import kbtu_oop_project.domain.value.ManagerType;

import java.util.List;
import java.util.Scanner;

public class Manager extends Employee {

    private static final long serialVersionUID = 1L;
    
    private ManagerType title;

    public Manager() {
        super();
    }

    public Manager(String id, String firstName, String lastName, String email, String password, ManagerType title) {
        super(id, firstName, lastName, email, password);
        this.title = title;
    }

    @Override
    public void login() {
        System.out.println("Менеджер (" + title + ") " + getEmail() + " вошел в академическую систему.");
    }

    public void approveRegistration(List<PendingCourseRegistration> pendingQueue, Scanner scanner) {
        System.out.println("\n======= ОЧЕРЕДЬ ЗАЯВОК НА РЕГИСТРАЦИЮ =======");
        if (pendingQueue == null || pendingQueue.isEmpty()) {
            System.out.println("Нет активных заявок на курсы.");
            return;
        }

        for (int i = 0; i < pendingQueue.size(); i++) {
            System.out.println((i + 1) + ". " + pendingQueue.get(i));
        }

        System.out.print("\nВыберите номер заявки для обработки (или 0 для отмены): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); 

        if (choice > 0 && choice <= pendingQueue.size()) {
            PendingCourseRegistration selectedRequest = pendingQueue.get(choice - 1);
            
            System.out.println("1. Одобрить (Approve)");
            System.out.println("2. Отклонить (Reject)");
            System.out.print("Ваше решение: ");
            int decision = scanner.nextInt();
            scanner.nextLine();

            if (decision == 1) {
                System.out.println("Заявка одобрена! Курс " + selectedRequest.getCourseCode() + " добавлен студенту.");
            } else {
                System.out.println("Заявка отклонена.");
            }
            
            pendingQueue.remove(selectedRequest);
        }
    }

    public void manageNews(List<User> allUsers, String newsText) {
        System.out.println("Опубликована новость: " + newsText);
        
        Notification newsNotification = new Notification("[ОБЩАЯ НОВОСТЬ]: " + newsText);
        for (User user : allUsers) {
            user.update(newsNotification);
        }
    }

    public void generateSchedule() {
        if (this.title != ManagerType.OR) {
            System.out.println("Ошибка: Только менеджеры Office of Registrar могут изменять расписание.");
            return;
        }
        System.out.println("Генерация академического расписания семестра успешно запущена...");
    }

    public void createStatisticalReport() {
        System.out.println("=== СТАТИСТИЧЕСКИЙ ОТЧЕТ ДЛЯ ДЕКАНАТА ===");
        System.out.println("Средний GPA по университету: [Расчетные данные]");
        System.out.println("Количество студентов на грани отчисления: [Данные]");
    }

    public ManagerType getTitle() { return title; }
    public void setTitle(ManagerType title) { this.title = title; }
}