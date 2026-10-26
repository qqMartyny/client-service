# client-service

REST-сервис учёта клиентов: создание, чтение, обновление и удаление записей о клиентах с хранением в PostgreSQL.

## Стек

- Java 21
- Spring Boot 4.1.2 (Web MVC, Data JPA / Hibernate, Validation, Cache, Actuator)
- PostgreSQL 17
- Flyway — миграции схемы БД
- Caffeine — локальный кэш
- springdoc-openapi — OpenAPI-спецификация и Swagger UI
- Maven, Docker, Docker Compose
- JUnit 5, MockMvc, Testcontainers

## Запуск

### Всё в Docker

Требуется только Docker.

```bash
docker compose up --build
```

Поднимутся PostgreSQL и приложение. При старте Flyway применит миграции.

### Локально (для разработки)

Требуются JDK 21 и Maven.

```bash
docker compose up -d db      # только БД
mvn spring-boot:run          # приложение
```

### Адреса

| Что | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI (JSON) | http://localhost:8080/v3/api-docs |
| Health | http://localhost:8080/actuator/health |

### Подключение к БД

```bash
docker exec -it client-db psql -U client -d client_db
```

Параметры: `localhost:5432`, БД `client_db`, пользователь/пароль `client` / `client`.

## API

Базовый путь: `/api/clients`. Формат — JSON, дата/время — ISO-8601 в UTC.

| Метод | Путь | Описание | Успех | Ошибки |
|---|---|---|---|---|
| POST | `/api/clients` | Создать клиента | 201 + `Location` | 400 |
| GET | `/api/clients` | Список клиентов | 200 | — |
| GET | `/api/clients/{id}` | Клиент по id | 200 | 400, 404 |
| PUT | `/api/clients/{id}` | Полная замена данных клиента | 200 | 400, 404 |
| DELETE | `/api/clients/{id}` | Удалить клиента | 204 | 400, 404 |

Подробная спецификация с моделями и примерами — в Swagger UI.

### Модели

**Создание** — `fullName` (обязательно, ≤ 255), `gender` (обязательно, `M` | `F`).

**Обновление** — `fullName`, `gender`, `status` (`ACTIVE` | `INACTIVE`), все поля обязательны.

**Ответ** — `id`, `fullName`, `gender`, `status`, `createdAt`, `updatedAt`.

### Ошибки

Ошибки возвращаются в формате [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) (`application/problem+json`).
Для ошибок валидации дополнительно передаётся список полей `errors`.

## Принятые решения

- **PUT — полная замена.** Клиент передаёт все изменяемые поля (`fullName`, `gender`, `status`). Это соответствует семантике PUT (RFC 9110) и делает запрос идемпотентным. Поля `id`, `createdAt`, `updatedAt` через API не изменяются.
- **PUT не создаёт запись.** Если клиента нет — 404 (не upsert).
- **Статус при создании** всегда `ACTIVE`. Значение `status` в запросе на создание игнорируется.
- **Удаление физическое** (hard delete). Удаление несуществующего клиента — 404.
- **Список** сортируется по `id` по возрастанию и включает клиентов во всех статусах. Пагинации нет — для реальных объёмов её нужно добавить.
- **Схема БД управляется Flyway**, Hibernate работает в режиме `validate`.
- **Генерация id** — `GENERATED ALWAYS AS IDENTITY` в PostgreSQL, `GenerationType.IDENTITY` в JPA.
- **CHECK-ограничения** на `gender` и `status` в БД дублируют валидацию приложения: данные остаются корректными и при вставке напрямую через SQL.
- **Время хранится в UTC** (`timestamptz`, `hibernate.jdbc.time_zone=UTC`).
- В модели данных из задания у `modify_dttm` указан тип `timestmp with time zone` — считаю это опечаткой, используется `timestamp with time zone`.

### Кэш

Кэшируется только получение клиента по id (Caffeine: до 1000 записей, TTL 10 минут). Обновление кладёт в кэш новое значение, удаление вычищает запись.

Ограничение: кэш локальный, поэтому при нескольких экземплярах сервиса возможны устаревшие данные в пределах TTL. Для горизонтального масштабирования нужен распределённый кэш (например, Redis).

## Тесты

```bash
mvn verify
```

Интеграционные тесты используют Testcontainers и требуют запущенного Docker.

## Структура проекта

```
src/main/java/com/example/clientservice
├── config/        # конфигурация (OpenAPI, кэш)
├── controller/    # REST-контроллеры
├── dto/           # запросы и ответы API
├── entity/        # JPA-сущности
├── exception/     # исключения и глобальный обработчик ошибок
├── mapper/        # преобразование Entity <-> DTO
├── repository/    # Spring Data JPA репозитории
└── service/       # бизнес-логика
src/main/resources
├── application.yml
└── db/migration/  # миграции Flyway
```
