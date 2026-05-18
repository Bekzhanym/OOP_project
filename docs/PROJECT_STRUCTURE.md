# Структура проекта

Краткая карта: **что в какой папке** и **какой класс за что отвечает**.

---

## Дерево (основное)

```
OOP_final_project/
├── README.md                 ← как запустить (главная для GitHub)
├── docs/
│   ├── PROJECT_STRUCTURE.md  ← этот файл
│   └── html/                 ← Javadoc + матрица требований
├── src/kbtu_oop_project/
│   ├── UniversityApp.java
│   ├── console/              ← интерфейс пользователя (CLI)
│   ├── domain/               ← бизнес-логика
│   ├── infrastructure/       ← БД, сериализация
│   └── application/          ← фабрики
├── data/                     ← university-state.ser (локально)
└── out/                      ← .class после компиляции
```

---

## `console/` — консольное приложение

| Файл / пакет | Назначение |
|--------------|------------|
| `ConsoleApplication.java` | `main`, загрузка/сохранение, первый админ |
| `common/ConsoleUi.java` | Заголовки, ввод, выбор компаратора статей |
| `features/home/GuestConsole.java` | Меню гостя: вход, регистрация |
| `features/session/SessionDispatcher.java` | Маршрутизация по роли после входа |
| `features/student/StudentConsole.java` | Меню студента |
| `features/teacher/TeacherConsole.java` | Меню преподавателя |
| `features/manager/ManagerConsole.java` | Меню менеджера (OR, заявки на курсы) |
| `features/admin/AdminConsole.java` | Панель администратора |
| `features/employee/GenericEmployeeConsole.java` | Корпоративная почта сотрудников |
| `features/user/UserRoleFormatter.java` | Отображение роли в UI |

---

## `domain/features/user/` — пользователи

| Класс | Описание |
|-------|----------|
| `User` | Базовый пользователь, пароль, email, Observer |
| `Student` | Курсы, транскрипт, рейтинги, исследования |
| `Student4thYear` | Выпускник + научный руководитель |
| `Teacher` | Курсы, оценки, публикации |
| `Professor` | Расширение преподавателя (legacy) |
| `Employee` | Сообщения между сотрудниками |
| `Manager` | Заявки на регистрацию, новости |
| `Admin` | Управление системой |
| `ResearchStaff` | Научный сотрудник |
| `PendingUser` | Старые аккаунты «ожидают роли» (наследует Student) |

---

## `domain/features/course/` — учёба

| Класс | Описание |
|-------|----------|
| `Course` | Дисциплина, студенты, Observer |
| `Mark` | Аттестации + экзамен, GPA, буквенная оценка |
| `Transcript` | Транскрипт студента |
| `Lesson`, `Room`, `Subject` | Занятия и аудитории |

---

## `domain/features/research/` — исследования

| Класс | Описание |
|-------|----------|
| `Researcher` | Интерфейс исследователя |
| `ResearchPaper` | Статья |
| `ResearchProject` | Проект, участники |
| `ResearcherProfile` | Профиль у преподавателя |

---

## `domain/features/registration/` — регистрация на курсы

| Класс | Описание |
|-------|----------|
| `RegistrationOffice` | Логика периодов (не подключён к CLI) |
| `RegistrationPeriod` | Enum периодов |
| `PendingCourseRegistration` | Заявка студента на курс |

**В работе используется:** очередь в `UniversityDatabase` + одобрение в `ManagerConsole`.

---

## `domain/features/notification/` — уведомления

| Класс | Описание |
|-------|----------|
| `Observer` | Интерфейс подписчика |
| `Notification` | Сообщение в inbox пользователя |

---

## `domain/sort/` — Strategy для статей

| Класс | Описание |
|-------|----------|
| `PaperComparator` | Базовый компаратор |
| `DateComparator`, `CitationsComparator`, `LengthComparator` | Маркеры стратегий |
| `ResearchPaperComparators` | Готовые стратегии (дата, цитирования, страницы) |

---

## `domain/value/` — перечисления

`Role`, `TeacherTitle`, `ManagerType`, `CourseType`, `LessonType`, `RoomType`, `StartupStatus`, `Language`, `MessageKind` и др.

---

## `infrastructure/persistence/`

| Класс | Описание |
|-------|----------|
| `UniversityDatabase` | **Singleton**: пользователи, курсы, заявки, логи, save/load |

Файл состояния: `data/university-state.ser`.

---

## `application/factory/`

| Класс | Описание |
|-------|----------|
| `UserFactory` | Создание `User` по `Role` |

---

## Поток данных (упрощённо)

```
ConsoleApplication
    → GuestConsole (вход / регистрация)
    → SessionDispatcher
         → StudentConsole | TeacherConsole | ManagerConsole | AdminConsole | ...
    → UniversityDatabase (все изменения)
    → saveData() → data/university-state.ser
```

---

## Что смотреть преподавателю

1. **Код** — `src/kbtu_oop_project/`
2. **Документация API** — [html/index.html](html/index.html)
3. **Требования** — [html/requirements-matrix.html](html/requirements-matrix.html)
