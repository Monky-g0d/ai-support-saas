# 🚀 AI Support SaaS — Backend #1 (Spring Boot)

**Полнофункциональный REST Backend для AI-автоматизации саппорта клиентов**

Это часть проекта команды:
- 👤 **Backend #1** (эта папка) - API, База Данных, Авторизация 
- 👤 **Backend #2** - AI интеграция, интеграции, оптимизация
- 👤 **Frontend** - админ-панель, виджет, UI

## Что реализовано в Backend #1

- ✅ **REST API** - полный API для управления тикетами, сообщениями, знаниями  
- ✅ **База данных** - H2 (разработка), PostgreSQL (production)
- ✅ **JWT Авторизация** - безопасные токены + ролевое управление доступом
- ✅ **CORS** - встраиваемый виджет в другие сайты
- ✅ **Spring Security 6** - современная система безопасности

---

## 🚀 Быстрый старт

### Требования
- **Java 17+**
- **Maven 3.6+**

### 1️⃣ Установка

```bash
# Клонировать репозиторий и перейти в ветку backend-1
git clone https://github.com/Monky-g0d/ai-support-saas.git
cd ai-support-saas
git checkout backend-1

# Перейти в папку Backend #1
cd backend-1

# Установить зависимости
mvn clean install
```

### 2️⃣ Запуск

```bash
# Собрать и запустить приложение
mvn spring-boot:run
```

Или:
```bash
# Собрать JAR
mvn clean package -DskipTests

# Запустить JAR
java -jar target/Spinglesson-1.0-SNAPSHOT.jar
```

✅ Приложение запустится на **http://localhost:8080**

---

## � Командная работа

### Структура репозитория
```
ai-support-saas/
├── backend-1/          👈 Эта папка (Backend #1)
├── backend-2/          ← Backend #2 (когда будет загружен)
├── frontend/           ← Frontend (когда будет загружен)
└── README.md
```

### Как присоединиться (для друзей)

1. **Backend #2** (AI & Интеграции):
```bash
git checkout -b backend-2
# ...работа в своей ветке...
git push origin backend-2
```

2. **Frontend** (React Admin):
```bash
git checkout -b frontend
# ...работа в своей ветке...
git push origin frontend
```

### 📋 Требования для интеграции

**Backend #1 предоставляет:**
- ✅ REST API на `http://localhost:8080`
- ✅ JWT авторизация (токены)
- ✅ Endpoints для тикетов, сообщений, знаний
- ✅ CORS включен для всех источников

**Backend #2 должен:**
- Использовать эти endpoints для AI логики
- Возвращать улучшенные ответы в Knowledge API

**Frontend должен:**
- Запрашивать данные у Backend #1
- Использовать JWT токены для авторизации
- Встраивать виджет на другие сайты

---

### 🔐 Авторизация (PublicAPI)

**Регистрация:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "securepass123"
  }'
```

Ответ:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

**Вход:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "securepass123"
  }'
```

---

### 🎫 Тикеты (Protected - требуется JWT токен)

**Получить все тикеты пользователя:**
```bash
curl -X GET http://localhost:8080/api/tickets \
  -H "Authorization: Bearer {YOUR_JWT_TOKEN}"
```

**Создать новый тикет:**
```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "Authorization: Bearer {YOUR_JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Не работает оплата",
    "description": "При оплате картой выдает ошибку 500",
    "priority": "HIGH"
  }'
```

**Получить конкретный тикет:**
```bash
curl -X GET http://localhost:8080/api/tickets/{ticketId} \
  -H "Authorization: Bearer {YOUR_JWT_TOKEN}"
```

---

### 💬 Сообщения в тикетах

**Отправить сообщение в тикет:**
```bash
curl -X POST http://localhost:8080/api/tickets/{ticketId}/messages \
  -H "Authorization: Bearer {YOUR_JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "body": "Спасибо за помощь!"
  }'
```

**Получить все сообщения тикета:**
```bash
curl -X GET http://localhost:8080/api/tickets/{ticketId}/messages \
  -H "Authorization: Bearer {YOUR_JWT_TOKEN}"
```

---

### 📚 База знаний (Public API для поиска)

**Поиск статей:**
```bash
curl -X GET "http://localhost:8080/api/knowledge/search?q=оплата" \
  -H "Content-Type: application/json"
```

**Получить все статьи:**
```bash
curl -X GET http://localhost:8080/api/knowledge
```

**Создать новую статью (Protected - ROLE_ADMIN/ROLE_AGENT):**
```bash
curl -X POST http://localhost:8080/api/knowledge \
  -H "Authorization: Bearer {YOUR_JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Как оплатить",
    "content": "Для оплаты нужно...",
    "published": true,
    "tags": ["оплата", "билет"]
  }'
```

---

## 🔧 Конфигурация

### application.properties (src/main/resources/)

```properties
# Spring Profile
spring.profiles.active=dev

# Database (H2 для dev)
spring.datasource.url=jdbc:h2:mem:spinglesson
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update

# JWT Security
app.jwtSecret=Z0xXOWFSbUdycHk1UWJDMlg4UmRPMjBFQkx0TXdab3g0MDJWRmRvTT0=
app.jwtExpirationMs=3600000

# Server
server.port=8080

# CORS (для встраиваемого виджета)
spring.web.cors.allowed-origins=*
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allow-credentials=true
```

### Изменение JWT Secret (для Production)

```bash
# Сгенерировать новый 256-битный ключ
openssl rand -base64 32
```

Результат вставить в `app.jwtSecret`

---

## 📊 Структура проекта

```
src/main/java/org/example/
├── Application.java              # Spring Boot точка входа
├── controller/
│   ├── AuthController.java       # Логин/Регистрация
│   ├── TicketController.java     # Управление тикетами
│   ├── MessageController.java    # Сообщения
│   └── KnowledgeController.java  # База знаний
├── service/
│   ├── AuthService.java          # Бизнес-логика авторизации
│   └── TicketService.java        # Бизнес-логика тикетов
├── model/
│   ├── User.java                 # Пользователь
│   ├── Ticket.java               # Тикет
│   ├── Message.java              # Сообщение
│   ├── KnowledgeArticle.java     # Статья
│   └── Role.java                 # Роли
├── repository/                   # JPA Repositories
└── security/
    ├── SecurityConfig.java       # Spring Security конфиг
    ├── JwtTokenProvider.java     # JWT генератор/валидатор
    └── JwtAuthenticationFilter.java # Фильтр JWT
```

---

## 🛡️ Ролевая система

| Роль | Доступ | Описание |
|------|--------|---------|
| `ROLE_USER` | Может создавать тикеты, писать сообщения | Обычный пользователь |
| `ROLE_AGENT` | Может видеть все тикеты, писать ответы | Агент саппорта |
| `ROLE_ADMIN` | Полный доступ, управление статьями | Администратор |

---

## 🔒 Security Features

✅ **JWT Tokens** - безопасная авторизация без сессий  
✅ **CORS** - настроен для встраивания в другие сайты  
✅ **Stateless** - каждый запрос независим  
✅ **PasswordEncoder** - BCrypt хеширование паролей  
✅ **Role-based Access** - ролевое управление доступом  

---

## 📖 Для Production

Для prod-окружения нужно:

1. **Изменить БД** на PostgreSQL:
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
</dependency>
```

2. **Создать application-prod.properties**:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/spinglesson_db
spring.datasource.username=postgres
spring.datasource.password=your_password
app.jwtSecret=<ваш_сгенерированный_ключ>
```

3. **Запустить с prod профилем**:
```bash
java -jar target/Spinglesson-1.0-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## 🎯 Что дальше?

**Ваша часть (Backend #1):** ✅ Завершено
- ✅ API полностью функционален
- ✅ БД настроена
- ✅ Авторизация работает

**Часть друга #2 (Backend #2):**
- AI интеграция
- Интеграции с внешними сервисами
- Оптимизация

**Часть друга #3 (Frontend):**
- React админ-панель
- Встраиваемый виджет
- UI для пользователя

---

## 🚨 Troubleshooting

**Ошибка: Port 8080 already in use**
```bash
# Убить процесс на порту 8080
lsof -i :8080 | grep LISTEN | awk '{print $2}' | xargs kill -9
```

**Ошибка: JWT key слишком короткий**
```bash
# Сгенерировать новый ключ и обновить application.properties
openssl rand -base64 32
```

**Ошибка БД подключения**
- Проверьте spring.datasource.url
- Для PostgreSQL убедитесь, что БД существует

---

## 📚 Документация

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://tools.ietf.org/html/rfc7518)
- [CORS Specification](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)

---

**Backend готов к работе! 🎉**

Для вопросов по интеграции обращайтесь к документации API выше.

