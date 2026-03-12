# 🎯 Инструкция для команды — Backend #2 (AI & Integration)

## За 3 минуты:

```bash
# 1. Клонировать репозиторий
git clone https://github.com/Monky-g0d/ai-support-saas.git
cd ai-support-saas/backend-2

# 2. Установить зависимости
mvn clean install

# 3. Запустить (убедись что Backend #1 запущен на :8080)
mvn spring-boot:run
```

✅ Сервис на http://localhost:8081

### С OpenAI (опционально):
```bash
OPENAI_API_KEY=sk-your-key mvn spring-boot:run
```

---

## ⚠️ Важно: Backend #1 должен быть запущен!

Backend #2 зависит от Backend #1 (`http://localhost:8080`).

```bash
# Сначала запусти Backend #1:
cd ai-support-saas/backend-1
mvn spring-boot:run
# → http://localhost:8080

# Потом Backend #2 (в другом терминале):
cd ai-support-saas/backend-2
mvn spring-boot:run
# → http://localhost:8081
```

---

## 📞 API Endpoints Backend #2

### 🤖 AI (генерация ответов)
```bash
POST /api/ai/respond/{ticketId}       # Сгенерировать AI-ответ
POST /api/ai/auto-respond/{ticketId}  # Сгенерировать + отправить в тикет
POST /api/ai/classify                 # Классифицировать тикет
```

### 📊 Аналитика
```bash
GET  /api/analytics/tickets           # Статистика тикетов
GET  /api/analytics/overview          # Обзор системы (health-check)
```

### 📧 Нотификации
```bash
POST /api/notifications/ticket-created/{ticketId}  # Email о новом тикете
POST /api/notifications/test                       # Тестовый email
```

---

## 🔄 Как это работает

### AI-ответы:
1. Backend #2 получает тикет из Backend #1 через REST API
2. Ищет релевантные статьи в Knowledge Base
3. Формирует контекстный промпт
4. Отправляет в OpenAI GPT (или fallback на KB)
5. Возвращает (или автоматически отправляет) ответ

### Fallback без OpenAI:
Если `OPENAI_API_KEY` не задан — AI-ответы формируются из базы знаний (Knowledge Base). Работает без внешних API.

---

## 📋 Примеры использования

### Сгенерировать AI-ответ:
```bash
curl -X POST http://localhost:8081/api/ai/respond/1
```

Ответ:
```json
{
  "ticketId": 1,
  "ticketTitle": "Не работает оплата",
  "aiResponse": "Здравствуйте! По вашему вопросу...",
  "sentToBackend": false,
  "source": "openai"
}
```

### Авто-ответ (генерация + отправка в тикет):
```bash
curl -X POST http://localhost:8081/api/ai/auto-respond/1
```

### Классификация тикета:
```bash
curl -X POST http://localhost:8081/api/ai/classify \
  -H "Content-Type: application/json" \
  -d '{"description": "Не могу зайти в аккаунт"}'
```

Ответ:
```json
{
  "category": "ACCOUNT"
}
```

### Статистика:
```bash
curl http://localhost:8081/api/analytics/tickets
```

### Обзор системы:
```bash
curl http://localhost:8081/api/analytics/overview
```

---

## ⚙️ Конфигурация

### application.properties

| Параметр | По умолчанию | Описание |
|----------|-------------|----------|
| `server.port` | `8081` | Порт сервиса |
| `backend1.url` | `http://localhost:8080` | URL Backend #1 |
| `backend1.username` | `ai-agent` | Сервисный аккаунт |
| `backend1.password` | `ai-agent-secret` | Пароль сервисного аккаунта |
| `openai.api.key` | _(пусто)_ | OpenAI API ключ |
| `openai.model` | `gpt-3.5-turbo` | Модель OpenAI |
| `notification.enabled` | `false` | Включить email-нотификации |

### Переменные окружения:
```bash
export OPENAI_API_KEY=sk-...           # OpenAI ключ
export MAIL_HOST=smtp.gmail.com        # SMTP сервер
export MAIL_USERNAME=your@gmail.com    # Email
export MAIL_PASSWORD=your-app-password # Пароль приложения
export NOTIFICATION_ENABLED=true       # Включить нотификации
```

---

## 🛣️ Workflow в GitHub

### Работа в своей ветке:
```bash
git checkout backend-2
# ...код...
git add .
git commit -m "✨ Backend #2: AI интеграция"
git push origin backend-2
```

### Pull Request:
GitHub предложит создать PR для мержа в `main`.

---

## 📁 Структура проекта

```
backend-2/
├── pom.xml
├── README.md
├── SETUP.md                ← Этот файл
└── src/main/java/org/example/backend2/
    ├── Backend2Application.java
    ├── config/
    │   ├── CorsConfig.java
    │   └── WebClientConfig.java
    ├── client/
    │   ├── Backend1Client.java     # HTTP-клиент для Backend #1
    │   └── OpenAiClient.java       # HTTP-клиент для OpenAI
    ├── controller/
    │   ├── AiController.java
    │   ├── AnalyticsController.java
    │   └── NotificationController.java
    ├── service/
    │   ├── AiService.java
    │   ├── AnalyticsService.java
    │   └── NotificationService.java
    └── dto/
        ├── TicketDto.java
        ├── MessageDto.java
        ├── KnowledgeArticleDto.java
        ├── AuthResponse.java
        ├── AiResponseDto.java
        └── TicketStatsDto.java
```

---

## 🆘 Проблемы?

**Port 8081 занят:**
```bash
lsof -i :8081 | grep LISTEN | awk '{print $2}' | xargs kill -9
```

**Нет зависимостей:**
```bash
mvn clean install -U
```

**Backend #1 не доступен:**
- Убедись что Backend #1 запущен на `http://localhost:8080`
- Проверь `backend1.url` в `application.properties`
- Endpoint `/api/analytics/overview` покажет статус подключения

**OpenAI не работает:**
- Проверь `OPENAI_API_KEY`
- Без ключа — всё работает в режиме fallback (Knowledge Base)

---

## 📞 Координация с командой

| Компонент | Порт | Статус |
|-----------|------|--------|
| Backend #1 (API/DB/Auth) | `:8080` | ✅ Готов |
| **Backend #2 (AI/Integration)** | **`:8081`** | **✅ Готов** |
| Frontend (React Admin) | `:3000` | 🔄 В работе |

GitHub: https://github.com/Monky-g0d/ai-support-saas

---

**Удачи в разработке! 🚀**
