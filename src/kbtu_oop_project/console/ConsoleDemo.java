package kbtu_oop_project.console;

import kbtu_oop_project.console.common.ConsoleUi;


public final class ConsoleDemo {

    private ConsoleDemo() {
        throw new UnsupportedOperationException("ConsoleDemo является утилитарным классом и не может быть инициализирован.");
    }

    public static void main(String[] args) {
        boolean testMode = false;
        for (String arg : args) {
            if ("--test-mode".equalsIgnoreCase(arg)) {
                testMode = true;
            }
        }

        if (testMode) {
            System.out.println("[СИСТЕМА] Запуск в режиме тестирования (генерация дефолтных студентов/преподавателей)...");
        }

        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            ConsoleUi.header("КРИТИЧЕСКИЙ СБОЙ СИСТЕМЫ");
            System.err.println("Поток '" + thread.getName() + "' аварийно завершился.");
            System.err.println("Причина: " + throwable.toString());
            System.err.println("\nПожалуйста, свяжитесь с поддержкой или перезапустите симулятор.");
            throwable.printStackTrace(); 
        });

        try {
            ConsoleApplication.run();
        } catch (Throwable t) {
            System.err.println("Фатальная ошибка при работе приложения: " + t.getMessage());
        }
    }
}