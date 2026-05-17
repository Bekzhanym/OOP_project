package kbtu_oop_project.domain.features.registration;

import kbtu_oop_project.domain.features.misc.Log;
import kbtu_oop_project.domain.features.misc.PendingCourseRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RegistrationOffice {

    private final List<PendingCourseRegistration> registrationQueue = new CopyOnWriteArrayList<>();
    private final List<Log> systemLogs = new ArrayList<>();
    
    private RegistrationPeriod currentPeriod = RegistrationPeriod.NONE;

    public RegistrationOffice() {}

    public synchronized boolean addRegistrationRequest(PendingCourseRegistration request, boolean isMinorCourse) {
        if (request == null) return false;

        if (currentPeriod == RegistrationPeriod.NONE) {
            systemLogs.add(new Log("SYSTEM", "Студент " + request.getStudentEmail() + " пытался записаться вне периода регистрации."));
            System.out.println("❌ Ошибка: В данный момент Офис Регистрации КБТУ закрыт для подачи заявок.");
            return false;
        }

        if (currentPeriod == RegistrationPeriod.MINOR_SELECTION && !isMinorCourse) {
            System.out.println("❌ Ошибка: Сейчас открыта регистрация ТОЛЬКО на Minor-дисциплины.");
            return false;
        }

        if (!currentPeriod.allowsMajorCourses() && !isMinorCourse) {
            System.out.println("❌ Ошибка: Текущий период (" + currentPeriod.getDisplayName() + ") не поддерживает запись на профильные дисциплины.");
            return false;
        }

        if (registrationQueue.contains(request)) {
            System.out.println("⚠️ Заявка на этот курс уже находится на рассмотрении в Офисе Регистрации.");
            return false;
        }

        registrationQueue.add(request);
        systemLogs.add(new Log("STUDENT", "Успешно создана заявка на " + request.getCourseCode() + " в период " + currentPeriod.name()));
        return true;
    }

    public void setCurrentPeriod(RegistrationPeriod newPeriod) {
        if (newPeriod == null) {
            this.currentPeriod = RegistrationPeriod.NONE;
        } else {
            this.currentPeriod = newPeriod;
        }
        systemLogs.add(new Log("REG_OFFICE_ADMIN", "Установлен новый статус академического периода: " + this.currentPeriod.name()));
        System.out.println("📢 Системное уведомление КБТУ: Статус регистрации изменен на -> " + this.currentPeriod.getDisplayName());
    }

    public RegistrationPeriod getCurrentPeriod() {
        return currentPeriod;
    }
}