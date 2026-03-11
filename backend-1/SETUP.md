# 🎯 Инструкция для команды — Как начать работать

## Для Backend #1 (эта папка)

### За 3 минуты:

```bash
# 1. Клонировать репозиторий
git clone https://github.com/Monky-g0d/ai-support-saas.git
cd ai-support-saas/backend-1

# 2. Установить зависимости
mvn clean install

# 3. Запустить
mvn spring-boot:run
```

✅ Приложение на http://localhost:8080

---

## 🧑‍💻 Для Backend #2 (твой друг с AI)

```bash
# 1. Клонировать и перейти на свою ветку
git clone https://github.com/Monky-g0d/ai-support-saas.git
cd ai-support-saas

# 2. Создать свою ветку
git checkout -b backend-2

# 3. Создать папку backend-2
mkdir backend-2
cd backend-2

# Здесь создавать код: AI, интеграции, оптимизация

# 4. Когда готово - загрузить на GitHub
git add .
git commit -m "✨ Backend #2: AI интеграция"
git push origin backend-2
```

**Координация:**
- Backend #1 (эта папка) предоставляет REST API
- Backend #2 должен использовать эти endpoints
- Всё интегрируется через HTTP запросы

---

## 🎨 Для Frontend (твой друг с React)

```bash
# Аналогично:
git clone https://github.com/Monky-g0d/ai-support-saas.git
cd ai-support-saas

# Создать ветку
git checkout -b frontend

# Создать папку проекта
mkdir frontend
cd frontend
npx create-react-app .

# Здесь создавать: админ-панель, виджет, UI

# Когда готово
git add .
git commit -m "✨ Frontend: React админ-панель"
git push origin frontend
```

**Координация с Backend:**
- Base URL: `http://localhost:8080`
- Все requests нужны JWT токены (в header: `Authorization: Bearer {token}`)
- CORS уже включен ✅

---

## 📋 Available API Endpoints

Backend #1 предоставляет эти endpoints:

### Авторизация (public)
```bash
POST /api/auth/register
POST /api/auth/login
```

### Тикеты (protected)
```bash
GET    /api/tickets                 # Все тикеты пользователя
POST   /api/tickets                 # Создать тикет
GET    /api/tickets/{id}            # Получить тикет
PUT    /api/tickets/{id}            # Обновить тикет
DELETE /api/tickets/{id}            # Удалить тикет
```

### Сообщения (protected)
```bash
GET    /api/tickets/{id}/messages          # Все сообщения
POST   /api/tickets/{id}/messages          # Отправить сообщение
DELETE /api/tickets/{id}/messages/{msgId}  # Удалить сообщение
```

### База знаний (public)
```bash
GET  /api/knowledge              # Все статьи
POST /api/knowledge              # Создать статью (admin/agent)
GET  /api/knowledge/search?q=... # Поиск
```

---

## 🔑 JWT Token

При входе получаешь token:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

Используй в запросах:
```bash
curl -H "Authorization: Bearer {YOUR_TOKEN}" \
  http://localhost:8080/api/tickets
```

---

## 🛣️ Workflow в GitHub

### Создание новой ветки:
```bash
git checkout -b backend-2
# или
git checkout -b frontend
```

### Работа и коммиты:
```bash
git add .
git commit -m "✨ Feature: описание"
git push origin backend-2
```

### Pull Request для мержа в main:
GitHub автоматически предложит создать PR, или создайте вручную в веб-интерфейсе

---

## 📱 Roles (Ролевая система)

| Role | Доступ |
|------|--------|
| ROLE_USER | Создавать тикеты, писать в них |
| ROLE_AGENT | Читать все тикеты, писать ответы |
| ROLE_ADMIN | Управление знаниями, все действия |

Регистрация создает ROLE_USER, выдать роли можно через БД

---

## 🆘 Проблемы?

**Port 8080 занят:**
```bash
lsof -i :8080 | grep LISTEN | awk '{print $2}' | xargs kill -9
```

**Нет зависимостей:**
```bash
mvn clean install -U
```

**Ошибка БД:**
- По умолчанию используется H2 (в памяти)
- Проверьте `application.properties`

---

## 📞 Контакты для скоординации

- Backend #1: API & Database — **готов** ✅
- Backend #2: AI & Integration — **в процессе** 
- Frontend: React Admin — **в процессе**

GitHub: https://github.com/Monky-g0d/ai-support-saas

---

**Удачи в разработке! 🚀**
