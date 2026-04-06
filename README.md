# 💰 Personal Finance Tracker

A Spring Boot web application designed to help users manage their daily transactions, set monthly budgets, and monitor their financial health. This project was developed as part of the Backend Development course at **Haaga-Helia University of Applied Sciences**.

## 🚀 Features

- **User Authentication:** Secure Sign-up and Login system using Spring Security and BCrypt password encoding.
- **Transaction Management:** Full CRUD (Create, Read, Update, Delete) functionality for daily income and expenses.
- **Budgeting:** Set monthly limits for different categories (Food, Rent, Travel, etc.).
- **Data Persistence:** Supports both **H2 (In-memory/File)** for development and **MariaDB/MySQL** for production.
- **Responsive UI:** Built with **Thymeleaf** and **Bootstrap** for a clean, mobile-friendly experience.

## 🛠️ Tech Stack

- **Backend:** Java 21, Spring Boot 3.x
- **Security:** Spring Security (Role-based access control)
- **Database:** H2 / MariaDB
- **Frontend:** Thymeleaf, Bootstrap 5
- **Build Tool:** Maven

## 🏃 Getting Started

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/lunapham10/personal-finance-tracker.git](https://github.com/lunapham10/personal-finance-tracker.git)

2. **Configure Database:**
By default, the app uses H2 (File mode). Dabase files are stored in the ./data folder. To switch to MariaDB, update src/main/resources/application.properties.

3. **Run the application:**
    ```bash
    mvn spring-boot:run

4. **Access the app:**
Open your browser and navigate to https://softala.haaga-helia.fi:7012/login
