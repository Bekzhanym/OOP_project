# KBTU OOP Final Project — Dream University

Консольный симулятор академической системы КБТУ: пользователи, курсы, регистрация, оценки, исследования, сообщения сотрудников.  
Проект на **Java 17** без Maven/Gradle — исходники в `src/`, сборка через `javac`.

---

## Быстрый старт

### Требования

- **JDK 17** или новее (`java` и `javac` в PATH)
- Терминал (macOS / Linux / Windows)

Проверка:

```bash
java -version
javac -version
```

### Сборка

Из корня репозитория:

```bash
cd OOP_final_project
mkdir -p out
find src -name "*.java" -print0 | xargs -0 javac --enable-preview --source 17 -d out -encoding UTF-8
```

### Запуск

```bash
java --enable-preview -cp out kbtu_oop_project.console.ConsoleApplication
```

### Первый запуск

1. Спросит, загружать ли сохранение — `n` для чистой базы.
2. Если база пустая — создаётся **первый администратор** (email `@kbtu.kz`).
3. Гостевое меню:
   - **1** — вход
   - **2** — регистрация (создаётся **студент 1 курса**; роль меняет админ)
   - **3** — выход
4. При выходе можно сохранить состояние в `data/university-state.ser`.

---

## Структура репозитория

| Путь | Назначение |
|------|------------|
| [`src/kbtu_oop_project/`](src/kbtu_oop_project/) | Весь исходный код |
| [`src/.../console/`](src/kbtu_oop_project/console/) | CLI: меню гостя, студента, преподавателя, админа |
| [`src/.../domain/`](src/kbtu_oop_project/domain/) | Доменная модель (пользователи, курсы, исследования) |
| [`src/.../infrastructure/`](src/kbtu_oop_project/infrastructure/) | `UniversityDatabase`, сериализация |
| [`src/.../application/`](src/kbtu_oop_project/application/) | `UserFactory` |
| [`docs/html/`](docs/html/) | Javadoc + материалы для защиты |
| [`data/`](data/) | Файл сохранения `university-state.ser` (в git не коммитится) |
| [`out/`](out/) | Скомпилированные `.class` (в git не коммитится) |

Подробная карта пакетов: **[docs/PROJECT_STRUCTURE.md](docs/PROJECT_STRUCTURE.md)**.

---

## Точка входа

| Класс | Роль |
|-------|------|
| `kbtu_oop_project.console.ConsoleApplication` | `main`, цикл приложения |
| `kbtu_oop_project.console.features.home.GuestConsole` | Вход / регистрация |
| `kbtu_oop_project.infrastructure.persistence.UniversityDatabase` | Singleton, хранение данных |
| `kbtu_oop_project.UniversityApp` | Доступ к БД (`db()`) |

---

## Паттерны проектирования

| Паттерн | Реализация |
|---------|------------|
| **Singleton** | `UniversityDatabase.getInstance()` (DCL) |
| **Observer** | `Course.notifyObservers()` → `User.update()` |
| **Factory** | `UserFactory`, назначение роли (`UserRoleAssignment`) |
| **Strategy** | `PaperComparator`, `ResearchPaperComparators` |
| **State** | `StartupStatus.canTransitionTo()` |

---

## Документация

| Файл | Описание |
|------|----------|
| [docs/html/index.html](docs/html/index.html) | **Javadoc** — открыть в браузере |
| [docs/html/requirements-matrix.html](docs/html/requirements-matrix.html) | Матрица требований |
| [docs/html/uml-sync.html](docs/html/uml-sync.html) | Соответствие ТЗ / кода / UML |
| [docs/html/project-overview.html](docs/html/project-overview.html) | Краткий обзор + ссылки |

Пересобрать Javadoc:

```bash
javadoc --enable-preview --source 17 -sourcepath src -d docs/html \
  -encoding UTF-8 -charset UTF-8 -subpackages kbtu_oop_project
```

---

## Запуск в VS Code / Cursor

В `.vscode/settings.json` уже указано:

- `java.project.sourcePaths`: `src`
- `java.project.outputPath`: `out`

Запуск класса `ConsoleApplication` через Run Java или из терминала (команды выше).

---

## Роли в системе

| Роль | Как появляется |
|------|----------------|
| Администратор | Первый запуск с пустой БД |
| Студент | Саморегистрация (по умолчанию 1 курс) |
| Преподаватель, менеджер, … | Назначает **админ** (пункт 10 в панели) |

---

## Лицензия / авторы

Учебный проект по дисциплине OOP, KBTU.
