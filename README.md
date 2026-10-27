# client-service

REST-сервис учёта клиентов: создание, чтение, обновление и удаление записей о клиентах с хранением в PostgreSQL.

## Стек

- Java 21
- Spring Boot 4.1.2 (Web MVC, Data JPA / Hibernate, Validation, Actuator)
- PostgreSQL 17
- Flyway — миграции схемы БД
- Maven, Docker, Docker Compose

## Запуск

### Всё в Docker

Требуется только Docker.

```bash
docker compose up --build
```

Поднимутся PostgreSQL и приложение. При старте Flyway применит миграции.

### Локально (для разработки)

Требуются JDK 21.

```bash
docker compose up -d db      # только БД
./mvnw spring-boot:run       # приложение
```

### Адреса

| Что | URL |
|---|---|
| API | http://localhost:8080/api/clients |
| Health | http://localhost:8080/actuator/health |

### Подключение к БД

```bash
docker exec -it client-db psql -U client -d client_db
```

Параметры: `localhost:5432`, БД `client_db`, пользователь / пароль `client` / `client`.

## API

| Метод | Путь | Описание | Успех | Ошибки |
|---|---|---|---|---|
| POST | `/api/clients` | Создать клиента | 201 + `Location` | 400 |
| GET | `/api/clients` | Список всех клиентов | 200 | — |
| GET | `/api/clients/{id}` | Клиент по id | 200 | 400, 404 |
| PUT | `/api/clients/{id}` | Полная замена данных клиента | 200 | 400, 404 |
| DELETE | `/api/clients/{id}` | Удалить клиента | 204 | 400, 404 |

Полная спецификация — модели, правила валидации, примеры запросов и ответов, формат ошибок и сценарии проверки: [docs/specification.md](docs/specification.md).

Пример создания клиента:

```bash
curl -i -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{"fullName": "Иванов Иван Иванович", "gender": "M"}'
```

## Принятые решения

- **PUT — полная замена.** Клиент передаёт все изменяемые поля (`fullName`, `gender`, `status`). Это соответствует семантике PUT (RFC 9110) и делает запрос идемпотентным. Поля `id`, `createdAt`, `updatedAt` через API не изменяются.
- **PUT не создаёт запись.** Если клиента нет — 404 (не upsert).
- **Статус при создании** всегда `ACTIVE`; значение `status` в запросе на создание игнорируется.
- **Удаление физическое** (hard delete). Удаление несуществующего клиента — 404.
- **Список** возвращает клиентов во всех статусах, без пагинации — как требует задание («список всех клиентов»). Для больших объёмов данных пагинацию нужно добавить.
- **Ответ содержит все поля клиента.** Служебных или чувствительных данных в таблице нет, а `status` и даты нужны потребителю.
- **Ошибки** возвращаются в формате RFC 9457 (`ProblemDetail`).
- **Схема БД управляется Flyway**, Hibernate работает в режиме `validate`.
- **Генерация id** — `GENERATED ALWAYS AS IDENTITY` в PostgreSQL, `GenerationType.IDENTITY` в JPA.
- **CHECK-ограничения** на `gender` и `status` в БД дублируют валидацию приложения: данные остаются корректными и при вставке напрямую через SQL.
- **Даты** выставляются JPA-колбэками `@PrePersist` / `@PreUpdate`. `modify_dttm` обновляется, только если данные клиента действительно изменились.
- **Время хранится в UTC** (`timestamptz`, `hibernate.jdbc.time_zone=UTC`).
- В модели данных из задания у `modify_dttm` указан тип `timestmp with time zone` — считаю это опечаткой, используется `timestamp with time zone`.

## Структура проекта

```
src/main/java/com/ilyanin/client_service
├── controller/    # REST-контроллер
├── dto/           # запросы и ответы API
├── entity/        # JPA-сущность
├── exception/     # исключения и глобальный обработчик ошибок
├── mapper/        # преобразование Entity <-> DTO
├── model/         # перечисления (Gender, ClientStatus)
├── repository/    # Spring Data JPA репозиторий
└── service/       # бизнес-логика
src/main/resources
├── application.yml
└── db/migration/  # миграции Flyway
docs/
└── specification.md    # спецификация API
```