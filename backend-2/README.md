# 🤖 AI Support SaaS — Backend #2 (AI & Integration)

**AI-сервис и интеграции для платформы автоматизации поддержки клиентов**

## Что реализовано в Backend #2

- ✅ **AI-ответы** — генерация ответов через OpenAI GPT + Knowledge Base
- ✅ **Авто-ответы** — автоматическая отправка AI-ответов в тикеты
- ✅ **Классификация тикетов** — AI-определение категории тикета
- ✅ **Аналитика** — статистика тикетов для дашборда
- ✅ **Email-нотификации** — уведомления при создании тикетов
- ✅ **Интеграция с Backend #1** — WebClient + JWT авторизация

---

## 🚀 Быстрый старт

### Требования
- **Java 17+**
- **Maven 3.6+**
- **Backend #1** запущен на `http://localhost:8080`

### Установка и запуск

```bash
cd ai-support-saas/backend-2

# Установить зависимости
mvn clean install

# Запустить
mvn spring-boot:run
```

✅ Сервис запустится на **http://localhost:8081**

### С OpenAI (опционально)
```bash
OPENAI_API_KEY=sk-your-key mvn spring-boot:run
```

---

## 📞 API Endpoints

### 🤖 AI
| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/ai/respond/{ticketId}` | Сгенерировать AI-ответ |
| POST | `/api/ai/auto-respond/{ticketId}` | Сгенерировать + отправить ответ |
| POST | `/api/ai/classify` | Классифицировать тикет |

### 📊 Аналитика
| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/analytics/tickets` | Статистика тикетов |
| GET | `/api/analytics/overview` | Обзор системы |

### 📧 Нотификации
| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/notifications/ticket-created/{ticketId}` | Email о новом тикете |
| POST | `/api/notifications/test` | Тестовый email |

---

## 📁 Структура проекта

```
src/main/java/org/example/backend2/
├── Backend2Application.java        # Точка входа
├── config/
│   ├── CorsConfig.java            # CORS настройки
│   └── WebClientConfig.java       # WebClient биты
├── client/
│   ├── Backend1Client.java        # HTTP-клиент для Backend #1
│   └── OpenAiClient.java         # HTTP-клиент для OpenAI
├── controller/
│   ├── AiController.java          # AI API
│   ├── AnalyticsController.java   # Аналитика API
│   └── NotificationController.java # Нотификации API
├── service/
│   ├── AiService.java             # AI-логика
│   ├── AnalyticsService.java      # Аналитика
│   └── NotificationService.java   # Email
└── dto/
    ├── TicketDto.java
    ├── MessageDto.java
    ├── KnowledgeArticleDto.java
    ├── AuthResponse.java
    ├── AiResponseDto.java
    └── TicketStatsDto.java
```

---

## ⚙️ Конфигурация

### application.properties

| Параметр | По умолчанию | Описание |
|----------|-------------|----------|
| `server.port` | `8081` | Порт сервиса |
| `backend1.url` | `http://localhost:8080` | URL Backend #1 |
| `openai.api.key` | _(пусто)_ | OpenAI API ключ |
| `openai.model` | `gpt-3.5-turbo` | Модель OpenAI |
| `notification.enabled` | `false` | Включить email |

### Переменные окружения

```bash
export OPENAI_API_KEY=sk-...      # OpenAI ключ
export MAIL_HOST=smtp.gmail.com    # SMTP сервер
export MAIL_USERNAME=your@gmail.com
export MAIL_PASSWORD=your-app-password
export NOTIFICATION_ENABLED=true
```

---

## 🔄 Как работает AI-ответ

1. Получает тикет и сообщения из **Backend #1**
2. Ищет релевантные статьи в **Knowledge Base**
3. Формирует контекстный промпт
4. Отправляет в **OpenAI GPT** (или fallback на KB)
5. Возвращает ответ (или автоматически отправляет)

**Без OpenAI ключа** — работает в режиме fallback (поиск по базе знаний).

---

## 📞 Примеры использования

### Сгенерировать AI-ответ
```bash
curl -X POST http://localhost:8081/api/ai/respond/1
```

### Авто-ответ (генерация + отправка)
```bash
curl -X POST http://localhost:8081/api/ai/auto-respond/1
```

### Классифицировать тикет
```bash
curl -X POST http://localhost:8081/api/ai/classify \
  -H "Content-Type: application/json" \
  -d '{"description": "Не работает оплата картой"}'
```

### Статистика тикетов
```bash
curl http://localhost:8081/api/analytics/tickets
```

### Обзор системы
```bash
curl http://localhost:8081/api/analytics/overview
```

---

**Backend #2 готов! 🚀**
