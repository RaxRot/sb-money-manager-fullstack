# 💸 Money Manager – Spring Boot Backend

> 🚧 **UI is currently under development.**  
> ✅ **Backend is fully implemented and actively being improved and refined.**

This project is a personal finance tracker built with Java & Spring Boot. It handles user authentication (with JWT), income and expense tracking, category management, dashboard statistics, and filtering. The backend is designed to be clean, modular, and secure — ready to be integrated with any frontend (e.g. React).

---

## ✨ Features

- 🔐 **JWT Authentication** with login & signup
- 📩 **Email activation** after registration
- 📊 **Dashboard** summary of monthly financial activity
- 💰 **Incomes & Expenses** full CRUD
- 🧾 **Category management** by type (income / expense)
- 🔎 **Transaction filtering** by date and category
- 🧑 **User profile** endpoint
- ❌ **Global Exception Handling**
- ✅ **Unit tests** for all services and repositories

---

## 🔧 Tech Stack

- Java 17
- Spring Boot 3+
- Spring Security + JWT
- JPA + Hibernate
- MySQL / H2 (for dev)
- Lombok
- ModelMapper
- JUnit 5

---

## 🧪 Testing

- ✅ Unit tests for:
    - All service classes
    - All repository interfaces
- 📄 Clean and independent test data
- 🔒 JWT and password encoding covered

---

---

## 🚀 API Endpoints

Here are some of the key API endpoints provided by the backend:

### 🔐 Authentication
- `POST /register` – Register a new user
- `GET /activate?token=...` – Activate user via email token
- `POST /login` – Login and receive JWT

### 👤 User
- `GET /profile` – Get the currently logged-in user

### 📁 Category
- `POST /categories` – Create category
- `GET /categories` – Get all categories
- `GET /categories/{type}` – Filter by type (INCOME/EXPENSE)
- `PUT /categories/{id}` – Update category
- `DELETE /categories/{id}` – Delete category

### 💰 Income & Expense
- `POST /incomes`, `GET /incomes`, `DELETE /incomes/{id}`
- `POST /expenses`, `GET /expenses`, `DELETE /expenses/{id}`

### 📊 Dashboard & Filters
- `GET /dashboard` – Overview of current month's activity
- `POST /filter` – Filter transactions by date range and category

🧠 *…and many more endpoints and features implemented under the hood for full financial tracking and user management.*