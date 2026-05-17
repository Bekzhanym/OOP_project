package kbtu_oop_project.console;

import kbtu_oop_project.UniversityApp;
import kbtu_oop_project.console.common.ConsoleUi;

public final class ConsoleDemo {

    private ConsoleDemo() {
        throw new UnsupportedOperationException("ConsoleDemo является утилитарным классом и не может быть инициализирован.");
    }

    public static void main(String[] args) {
        
        boolean testMode = true; 

        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            ConsoleUi.header("КРИТИЧЕСКИЙ СБОЙ СИСТЕМЫ");
            System.err.println("Поток '" + thread.getName() + "' аварийно завершился.");
            System.err.println("Причина: " + throwable.toString());
            System.err.println("\nПожалуйста, свяжитесь с поддержкой КБТУ или перезапустите симулятор.");
            throwable.printStackTrace(); 
        });

        if (testMode) {
            System.out.println("\n🚀 [СИСТЕМА] Принудительная инициализация демонстрационных данных (Seed Data)...");
            UniversityApp.enableTestMode(); 
        }

        try {
            ConsoleApplication.run();
        } catch (Throwable t) {
            ConsoleUi.header("ФАТАЛЬНАЯ ОШИБКА ЯДРА");
            System.err.println("Не удалось запустить симулятор: " + t.getMessage());
        }
    }
}