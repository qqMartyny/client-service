# Client Service API — спецификация

## 1. Общие соглашения

- Базовый путь: `/api/clients`
- Формат запросов и ответов: `application/json`, UTF-8
- Дата и время: ISO-8601 со смещением, в UTC, например `2026-10-27T12:30:00Z`
- Ошибки: `application/problem+json` в формате [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) (см. раздел 5)
- Неизвестные поля в теле запроса игнорируются

## 2. Справочники

| Enum | Значения | Описание |
|---|---|---|
| `Gender` | `M`, `F` | Пол |
| `ClientStatus` | `ACTIVE`, `INACTIVE` | Статус клиента |

## 3. Модели

### ClientResponse — клиент в ответах API

| Поле | Тип | Описание | Колонка БД |
|---|---|---|---|
| `id` | integer (int64) | Идентификатор | `id` |
| `fullName` | string | ФИО | `full_name` |
| `gender` | `Gender` | Пол | `gender` |
| `status` | `ClientStatus` | Статус | `status` |
| `createdAt` | string (date-time) | Дата создания | `create_dttm` |
| `updatedAt` | string (date-time) | Дата последнего изменения | `modify_dttm` |

```json
{
  "id": 1,
  "fullName": "Иванов Иван Иванович",
  "gender": "M",
  "status": "ACTIVE",
  "createdAt": "2026-10-27T12:30:00Z",
  "updatedAt": "2026-10-27T12:30:00Z"
}
```

### CreateClientRequest — создание клиента

| Поле | Обязательно | Правила |
|---|---|---|
| `fullName` | да | не пустое и не из одних пробелов, не длиннее 255 символов |
| `gender` | да | `M` или `F` |

Статус клиент не передаёт: новый клиент всегда создаётся со статусом `ACTIVE`. Переданное поле `status` игнорируется.

### UpdateClientRequest — полная замена данных клиента

| Поле | Обязательно | Правила |
|---|---|---|
| `fullName` | да | не пустое и не из одних пробелов, не длиннее 255 символов |
| `gender` | да | `M` или `F` |
| `status` | да | `ACTIVE` или `INACTIVE` |

Поля `id`, `createdAt`, `updatedAt` через API не изменяются; переданные значения игнорируются.

## 4. Эндпоинты

### 4.1. POST /api/clients — создать клиента

Запрос:

```json
{
  "fullName": "Иванов Иван Иванович",
  "gender": "M"
}
```

| Код | Когда | Тело |
|---|---|---|
| **201 Created** | клиент создан | `ClientResponse`; заголовок `Location: /api/clients/{id}` (абсолютный URI) |
| 400 Bad Request | ошибка валидации, неверное значение enum, некорректный JSON | ProblemDetail |

Побочные эффекты: `status = ACTIVE`, `createdAt = updatedAt = текущее время`.

### 4.2. GET /api/clients — список всех клиентов

| Код | Когда | Тело |
|---|---|---|
| **200 OK** | всегда | массив `ClientResponse`; если клиентов нет — `[]` |

- Возвращаются клиенты во всех статусах, включая `INACTIVE`.
- Порядок элементов не гарантируется.
- Пагинации нет.

### 4.3. GET /api/clients/{id} — клиент по идентификатору

| Код | Когда | Тело |
|---|---|---|
| **200 OK** | клиент найден | `ClientResponse` |
| 400 Bad Request | `id` не является числом | ProblemDetail |
| 404 Not Found | клиента с таким `id` нет | ProblemDetail |

### 4.4. PUT /api/clients/{id} — полная замена данных клиента

Запрос:

```json
{
  "fullName": "Иванов Иван Петрович",
  "gender": "M",
  "status": "INACTIVE"
}
```

| Код | Когда | Тело |
|---|---|---|
| **200 OK** | клиент обновлён | обновлённый `ClientResponse` |
| 400 Bad Request | не передано обязательное поле, ошибка валидации, `id` не число | ProblemDetail |
| 404 Not Found | клиента с таким `id` нет; новая запись **не создаётся** | ProblemDetail |

- Сначала проверяется тело запроса (400), затем наличие клиента (404).
- Запрос идемпотентен: повторный одинаковый запрос даёт тот же результат.
- `updatedAt` меняется, только если данные действительно изменились.

### 4.5. DELETE /api/clients/{id} — удалить клиента

| Код | Когда | Тело |
|---|---|---|
| **204 No Content** | клиент удалён | — |
| 400 Bad Request | `id` не является числом | ProblemDetail |
| 404 Not Found | клиента с таким `id` нет | ProblemDetail |

Удаление физическое (hard delete): запись удаляется из таблицы.

## 5. Формат ошибок

Все ошибки 400 и 404 возвращаются в формате ProblemDetail (RFC 9457) с заголовком `Content-Type: application/problem+json`.

| Поле | Описание |
|---|---|
| `type` | тип ошибки (по умолчанию `about:blank`) |
| `title` | краткое название ошибки |
| `status` | HTTP-статус |
| `detail` | описание конкретной ошибки |
| `instance` | путь запроса |
| `errors` | список ошибок по полям — только для ошибок валидации |

Пример 404:

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Client account with id 42 not found",
  "instance": "/api/clients/42"
}
```

Пример 400 (ошибка валидации):

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "instance": "/api/clients",
  "errors": [
    "fullName: must not be blank",
    "status: must not be null"
  ]
}
```

Пример 400 (некорректный JSON, неверное значение enum, нечисловой `id`):

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Malformed request",
  "instance": "/api/clients"
}
```

Прочие ошибки (неподдерживаемый метод, непредвиденная ошибка сервера) обрабатываются стандартным механизмом Spring Boot; детали ошибки и stack trace клиенту не передаются.

## 6. Сценарии для проверки

| # | Запрос | Ожидаемый результат |
|---|---|---|
| 1 | POST с корректным телом | 201, заголовок `Location`, `status = ACTIVE`, `createdAt = updatedAt` |
| 2 | POST без `fullName` | 400, `errors` содержит `fullName` |
| 3 | POST с `"fullName": "   "` | 400 |
| 4 | POST с `"gender": "X"` | 400, `Malformed request` |
| 5 | POST с некорректным JSON | 400, `Malformed request` |
| 6 | GET списка | 200, массив |
| 7 | GET существующего клиента | 200 |
| 8 | GET `/api/clients/999999` | 404 |
| 9 | GET `/api/clients/abc` | 400 |
| 10 | PUT с новыми данными | 200, `updatedAt` изменился |
| 11 | Повторный такой же PUT | 200, `updatedAt` не изменился |
| 12 | PUT без `status` | 400 |
| 13 | PUT несуществующего клиента | 404, запись не создана |
| 14 | DELETE существующего клиента | 204 |
| 15 | Повторный DELETE того же клиента | 404 |