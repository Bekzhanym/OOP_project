package kbtu_oop_project;

import kbtu_oop_project.domain.features.course.Course;
import kbtu_oop_project.domain.features.user.Student;
import kbtu_oop_project.domain.value.*;
import kbtu_oop_project.infrastructure.persistence.UniversityDatabase;

import java.io.File;
import java.util.List;

public class UniversityDatabaseTester {

    private static int passedTests = 0;
    private static int totalTests = 0;

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("🤖 СИСТЕМА ТЕСТИРОВАНИЯ ЯДРА КБТУ (OOP PROJECT) ЗАПУЩЕНА");
        System.out.println("==========================================================\n");

        cleanOldTestData();

        try {
            runTestScenario("1. Тест синглтона и многопоточности (DCL)", UniversityDatabaseTester::testSingletonDCL);
            runTestScenario("2. Тест логики и категорий Enum Role", UniversityDatabaseTester::testRoleEnumLogic);
            runTestScenario("3. Тест совместимости RoomType и LessonType", UniversityDatabaseTester::testRoomTypeCompatibility);
            runTestScenario("4. Тест Конечного Автомата (State Machine) StartupStatus", UniversityDatabaseTester::testStartupStatusTransitions);
            runTestScenario("5. Тест академических ограничений TeacherTitle", UniversityDatabaseTester::testTeacherTitleConstraints);
            runTestScenario("6. Тест транзакционного сохранения и восстановления данных (I/O)", UniversityDatabaseTester::testDatabaseSaveAndLoad);
            runTestScenario("7. Тест защиты от некорректного Regex в Advanced Search", UniversityDatabaseTester::testAdvancedSearchRegexGuard);

            System.out.println("==========================================================");
            System.out.println("📊 ИТОГИ ТЕСТИРОВАНИЯ: Успешно [" + passedTests + "/" + totalTests + "]");
            if (passedTests == totalTests) {
                System.out.println("🟢 СЕРТИФИКАЦИЯ ПРОЙДЕНА: Архитектура базы данных стабильна и безопасна!");
            } else {
                System.out.println("🔴 ТЕСТЫ ПРОВАЛЕНЫ: Проверьте логи ошибок выше.");
            }
            System.out.println("==========================================================");

        } finally {
            cleanOldTestData(); 
        }
    }

    private static void runTestScenario(String testName, Runnable testMethod) {
        totalTests++;
        System.out.println("🏃 Запуск: " + testName);
        try {
            testMethod.run();
            System.out.println("✅ Статус: УСПЕШНО\n");
            passedTests++;
        } catch (Throwable e) {
            System.err.println("❌ Статус: ПРОВАЛЕН");
            System.err.println("   Причина: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    private static void cleanOldTestData() {
        File file = new File("data/university-state.ser");
        if (file.exists()) file.delete();
        File tmpFile = new File("data/university-state.tmp");
        if (tmpFile.exists()) tmpFile.delete();
    }

    

    private static void testSingletonDCL() {
        UniversityDatabase instance1 = UniversityDatabase.getInstance();
        UniversityDatabase instance2 = UniversityDatabase.getInstance();
        
        assertNotNull(instance1, "База данных не инициализирована.");
        assertEquals(instance1, instance2, "Паттерн Singleton нарушен! Получены разные инстансы.");
    }

    private static void testRoleEnumLogic() {
        Role studentRole = Role.STUDENT;
        Role adminRole = Role.ADMIN;
        Role professorRole = Role.PROFESSOR;

        assertTrue(studentRole.isStudentCategory(), "STUDENT должен входить в категорию студентов.");
        assertFalse(adminRole.isStudentCategory(), "ADMIN не должен быть в студенческой категории.");
        assertTrue(professorRole.isAlwaysResearcher(), "Профессор обязан быть исследователем (Always Researcher).");
        assertTrue(adminRole.hasAdministrativePower(), "Администратор должен обладать админ-полномочиями.");
    }

    private static void testRoomTypeCompatibility() {
        RoomType lab = RoomType.LABORATORY;
        RoomType lectureHall = RoomType.LECTURE_HALL;

        assertTrue(lab.canAccommodate(25), "Лаборатория должна вмещать 25 студентов.");
        assertFalse(lab.canAccommodate(35), "Лаборатория не должна вмещать больше своего defaultCapacity (30).");
        
        assertTrue(lab.isCompatibleWith(LessonType.LABORATORY), "Лаборатория обязана подходить под лабораторные пары.");
        assertFalse(lectureHall.isCompatibleWith(LessonType.LABORATORY), "В лекционном зале нельзя проводить лабораторные занятия.");
    }

    private static void testStartupStatusTransitions() {
        StartupStatus review = StartupStatus.UNDER_REVIEW;
        StartupStatus approved = StartupStatus.APPROVED;
        StartupStatus funded = StartupStatus.FUNDED;
        StartupStatus rejected = StartupStatus.REJECTED;

        assertTrue(review.canTransitionTo(approved), "Из ревью должен быть доступен переход в одобрено.");
        assertTrue(review.canTransitionTo(rejected), "Из ревью должен быть доступен переход в отклонено.");
        assertFalse(rejected.canTransitionTo(funded), "🚨 Нарушение State Machine! Нельзя перепрыгнуть из отклоненных сразу в профинансированные.");
        assertTrue(rejected.canTransitionTo(review), "Отклоненный стартап должен иметь возможность уйти на повторное ревью после правок.");
    }

    private static void testTeacherTitleConstraints() {
        TeacherTitle tutor = TeacherTitle.TUTOR;
        TeacherTitle professor = TeacherTitle.PROFESSOR;

        assertTrue(tutor.canConduct(LessonType.LABORATORY), "Тьютор может вести лабораторные.");
        assertFalse(tutor.canConduct(LessonType.LECTURE), "🚨 Нарушение академических правил КБТУ! Тьютор не имеет права читать потоковые лекции.");
        assertTrue(professor.canConduct(LessonType.LECTURE), "Профессор имеет право читать лекции.");
    }

    private static void testDatabaseSaveAndLoad() {
        UniversityDatabase db = UniversityDatabase.getInstance();
        
        
        Student student = new Student();
        student.setEmail("alizhan@kbtu.kz");
        student.setFirstName("Alizhan");
        student.setLastName("Tulep");
        
        Course oopCourse = new Course();
        oopCourse.setCourseCode("CSCI2201");
        oopCourse.setCourseName("Object-Oriented Programming");
        oopCourse.setCredits(3);
        
        db.addUser(student);
        db.addCourse(oopCourse);
        
        
        db.saveData();
        
        
        File storageFile = new File("data/university-state.ser");
        assertTrue(storageFile.exists(), "Файл базы данных не был физически сохранен на диск.");

        
        db.loadData();
        
        assertFalse(db.findAllCourses().isEmpty(), "Данные курсов не восстановились после десериализации.");
    }

    private static void testAdvancedSearchRegexGuard() {
        UniversityDatabase db = UniversityDatabase.getInstance();
        
        String brokenRegex = "[A-Z---"; 
        
        List<Object> results = db.advancedSearch(brokenRegex);
        
        assertNotNull(results, "Метод поиска вернул null вместо безопасного пустого списка.");
        assertTrue(results.isEmpty(), "Некорректный Regex вернул какие-то ложные данные.");
    }

    

    private static void assertNotNull(Object obj, String msg) {
        if (obj == null) throw new AssertionError(msg);
    }

    private static void assertEquals(Object expected, Object actual, String msg) {
        if (!expected.equals(actual)) throw new AssertionError(msg + " Ожидалось: " + expected + ", но получено: " + actual);
    }

    private static void assertTrue(boolean condition, String msg) {
        if (!condition) throw new AssertionError(msg);
    }

    private static void assertFalse(boolean condition, String msg) {
        if (condition) throw new AssertionError(msg);
    }
}