package kbtu_oop_project.domain.features.user;

import kbtu_oop_project.domain.features.misc.Log;
import java.util.List;

public class Admin extends Employee {

    private static final long serialVersionUID = 1L;

    public Admin() {
        super();
    }

    public Admin(String id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email, password);
    }

    @Override
    public void login() {
        System.out.println("[SECURITY] Администратор " + getEmail() + " успешно авторизован в панели управления.");
    }

    public void seeLogFiles(List<Log> systemLogs) {
        System.out.println("\n======= СИСТЕМНЫЕ ЖУРНАЛЫ (ЛОГИ AUDIT) =======");
        if (systemLogs == null || systemLogs.isEmpty()) {
            System.out.println("История системных логов пуста.");
            return;
        }
        
        systemLogs.forEach(System.out::println);
        System.out.println("=================================================");
    }

    public boolean removeUserById(List<User> allUsers, String idToDrop, List<Log> systemLogs) {
        if (idToDrop == null || idToDrop.isBlank() || allUsers == null) {
            return false;
        }
        
        String targetId = idToDrop.trim();

        if (targetId.equals(this.getId())) {
            System.out.println("❌ Ошибка безопасности: Вы не можете удалить собственную учетную запись администратора!");
            return false;
        }

        User userToDelete = allUsers.stream()
                .filter(u -> u.getId().equals(targetId))
                .findFirst()
                .orElse(null);

        if (userToDelete == null) {
            System.out.println("⚠️ Пользователь с ID '" + targetId + "' не найден в системе КБТУ.");
            return false;
        }

        boolean removed = allUsers.removeIf(user -> user.getId().equals(targetId));

        if (removed && systemLogs != null) {
            String logAction = String.format("УДАЛЕН ПОЛЬЗОВАТЕЛЬ: %s %s (Роль: %s, Email: %s)", 
                    userToDelete.getFirstName(), 
                    userToDelete.getLastName(), 
                    userToDelete.getClass().getSimpleName(), 
                    userToDelete.getEmail());
            
            systemLogs.add(new Log(this.getId(), logAction));
            System.out.println("✅ Пользователь " + userToDelete.getFirstName() + " успешно деактивирован и удален.");
        }

        return removed;
    }
}