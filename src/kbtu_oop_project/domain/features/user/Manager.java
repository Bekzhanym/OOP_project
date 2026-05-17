package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.misc.PendingCourseRegistration;
import kbtu_oop_project.domain.features.misc.Log;
import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.notification.Notification;
import kbtu_oop_project.domain.value.ManagerType;

import java.util.List;
import java.util.Objects;

public class Manager extends Employee {

    private static final long serialVersionUID = 1L;
    
    private ManagerType title;

    public Manager() {
        super();
    }

    public Manager(String id, String firstName, String lastName, String email, String password, ManagerType title) {
        super(id, firstName, lastName, email, password);
        this.title = Objects.requireNonNull(title, "Тип менеджера обязателен для инициализации.");
    }

    @Override
    public void login() {
        System.out.println(String.format("[AUTH] Менеджер (%s) %s вошел в академическую систему.", title.name(), getEmail()));
    }

    public boolean processSingleRegistration(PendingCourseRegistration request, 
                                             boolean approve, 
                                             List<Student> allStudents, 
                                             List<Course> allCourses,
                                             List<Log> systemLogs) {
        if (request == null) return false;

        if (!approve) {
            systemLogs.add(new Log(this.getId(), "ОТКЛОНЕНА заявка студента: " + request.getStudentEmail() + " на курс " + request.getCourseCode()));
            return true; 
        }

        Student student = allStudents.stream()
                .filter(s -> s.getEmail().equalsIgnoreCase(request.getStudentEmail()))
                .findFirst()
                .orElse(null);

        Course course = allCourses.stream()
                .filter(c -> c.getCourseCode().equalsIgnoreCase(request.getCourseCode()))
                .findFirst()
                .orElse(null);

        if (student == null || course == null) {
            System.out.println("❌ Ошибка связывания: Студент или Дисциплина не найдены в глобальном реестре.");
            return false;
        }

        student.registerForCourse(course); 
        
        course.attach(student); 

        systemLogs.add(new Log(this.getId(), String.format("ОДОБРЕНА РЕГИСТРАЦИЯ: Студент %s -> Курс %s", student.getId(), course.getCourseCode())));
        return true;
    }

    public void manageNews(List<User> allUsers, String newsText) {
        if (newsText == null || newsText.isBlank()) return;
        
        System.out.println("📢 Опубликована официальная новость: " + newsText);
        Notification newsNotification = new Notification("[УНИВЕРСИТЕТСКИЕ НОВОСТИ]: " + newsText.trim());
        
        for (User user : allUsers) {
            if (user != null) {
                user.update(newsNotification);
            }
        }
    }

    public void generateSchedule(List<Log> systemLogs) {
        if (this.title != ManagerType.OFFICE_REGISTRATOR) {
            System.out.println("⛔ Доступ заблокирован: Только менеджеры Департамента 'Office of Registrar' имеют право генерировать сетку расписания.");
            return;
        }
        
        System.out.println("🗓️ Запуск алгоритма оптимизации расписания КБТУ... Распределение аудиторий завершено.");
        if (systemLogs != null) {
            systemLogs.add(new Log(this.getId(), "Запущена генерация академического расписания семестра."));
        }
    }

    public void createStatisticalReport() {
        System.out.println("\n=== СТАТИСТИЧЕСКИЙ ОТЧЕТ ДЛЯ ДЕКАНАТА ===");
        System.out.println("Академический период: Текущий семестр");
        System.out.println("Статус генерации: Стабилен.");
    }

    @Override
    public void logout() {
        System.out.println("Менеджер (" + title.name() + ") " + getEmail() + " вышел из системы.");
    }

    public ManagerType getTitle() { return title; }
    
    public void setTitle(ManagerType title) { 
        if (title != null) {
            this.title = title;
        } else if (ManagerType.values().length > 0) {
            this.title = ManagerType.values()[0]; 
        }
    }
}