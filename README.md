# 🚀 AI Support SaaS — Полная платформа

**AI-автоматизация поддержки клиентов (SaaS)**

Проект делают 3 человека:
- **Backend #1** 👤 — API, База данных, Авторизация (ВЫ)
- **Backend #2** 👤 — AI, Интеграции, Оптимизация  
- **Frontend** 👤 — React админ-панель, виджет, UI

---

## 📁 Структура проекта

```
ai-support-saas/
├── backend-1/           ← Backend #1 (API & Database) ✅
│   ├── pom.xml
│   ├── README.md
│   ├── SETUP.md         ← Инструкции для команды
│   ├── src/
│   └── ...
├── backend-2/           ← Backend #2 (AI & Integration) 🔄
├── frontend/            ← Frontend (React Admin) 🔄
├── LICENSE
└── README.md            ← Этот файл
```

---

## 🚀 Быстрый старт

### Backend #1 (эта папка)

```bash
git clone https://github.com/Monky-g0d/ai-support-saas.git
cd ai-support-saas/backend-1

# Установить и запустить
mvn clean install
mvn spring-boot:run
```

✅ API запустится на **http://localhost:8080**

**Подробнее:** см. [backend-1/README.md](backend-1/README.md) и [backend-1/SETUP.md](backend-1/SETUP.md)

---

### Backend #2 (AI & Integration)

```bash
git clone https://github.com/Monky-g0d/ai-support-saas.git
cd ai-support-saas

# Создать свою ветку
git checkout -b backend-2
mkdir backend-2
cd backend-2

# Создавать код здесь...
# Использовать REST API из Backend #1
```

---

### Frontend (React)

```bash
git clone https://github.com/Monky-g0d/ai-support-saas.git
cd ai-support-saas

# Создать свою ветку
git checkout -b frontend
mkdir frontend
cd frontend
npx create-react-app .

# Создавать React код здесь...
# Авторизация через JWT токены
```

---

## 🔧 Ветки в GitHub

| Ветка | Статус | Описание |
|-------|--------|---------|
| `main` | 📝 Base | Главная ветка (для мержа готовых компонентов) |
| `backend-1` | ✅ Готово | Backend #1 - API, DB, Auth |
| `backend-2` | 🔄 В работе | Backend #2 - AI, Integration |
| `frontend` | 🔄 В работе | Frontend - React Admin Panel |

---

## 🔄 Workflow

1. **Каждый** создает свою ветку (`backend-1`, `backend-2`, `frontend`)
2. **Каждый** работает в своей папке и ветке
3. **После готовности** → Pull Request в `main`
4. **Reviewers** проверяют и мержат в `main`

---

## 📞 API для интеграции

Backend #1 предоставляет REST API:

**Base URL:** `http://localhost:8080`

### Авторизация
- POST `/api/auth/register` — Регистрация
- POST `/api/auth/login` — Вход

### Тикеты
- GET `/api/tickets` — Все тикеты пользователя
- POST `/api/tickets` — Создать тикет
- GET `/api/tickets/{id}` — Получить тикет

### Сообщения
- GET `/api/tickets/{id}/messages` — Сообщения в тикете
- POST `/api/tickets/{id}/messages` — Отправить сообщение

### База знаний
- GET `/api/knowledge` — Все статьи
- GET `/api/knowledge/search?q=...` — Поиск
- POST `/api/knowledge` — Создать статью (admin/agent)

**Подробивая документация:** [backend-1/README.md](backend-1/README.md#-api-endpoints)

---

## 🛡️ Ролевая система

| Роль | Доступ |
|------|--------|
| `ROLE_USER` | Создавать тикеты, писать сообщения |
| `ROLE_AGENT` | Видеть все тикеты, писать ответы |
| `ROLE_ADMIN` | Полный доступ + управление знаниями |

---

## 📚 Требования

- **Java 17+** (для Backend #1)
- **Maven 3.6+** (для Backend #1)
- **Node.js 18+** (для Frontend)
- **React 18+** (для Frontend)

---

## 🔐 Security

✅ JWT токены  
✅ CORS настроен  
✅ Role-based access control  
✅ BCrypt password hashing  
✅ Stateless authentication  

---

## 📖 Документация

- [Backend #1 — полная инструкция](backend-1/README.md)
- [Инструкции для команды](backend-1/SETUP.md)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [React Docs](https://react.dev)

---

## 🎯 Текущий статус

| Компонент | Статус | Ответственный |
|-----------|--------|--------------|
| Backend #1 (API/DB/Auth) | ✅ **ГОТОВО** | Backend #1 |
| Backend #2 (AI/Integration) | 🔄 В работе | Backend #2 |
| Frontend (Admin Panel) | 🔄 В работе | Frontend |

---

## 📞 Контакты и вопросы

Для вопросов по интеграции читай инструкции в папке соответствующего компонента!

---

**Удачи в разработке! 🚀**

```
git clone && happy coding!
```

