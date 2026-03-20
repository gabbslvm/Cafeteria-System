# T.I.P. Smart Cafeteria: Ordering & Billing System

A Java desktop application for managing cafeteria orders and billing, built with Java Swing (JFrame), Maven, and MySQL via XAMPP.

> **Group:** JAVAngers — Technological Institute of the Philippines  
> **Course:** Computer Programming 2

---

## Features

- **Staff Login** — Secure authentication for cafeteria staff
- **Menu Management** — Add, update, and remove menu items
- **Order Placing** — Browse the menu and build customer orders
- **Payment Processing** — Calculate totals and handle payments
- **Receipt Generation** — Print or display order receipts
- **Order History** — View past transactions
- **Queue Number System** — Auto-generated queue numbers per order

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| UI | Java Swing (JFrame) |
| Database | MySQL (via XAMPP) |
| Build Tool | Maven |
| Connector | MySQL Connector/J 8.0.33 |

---

## Project Structure

```
src/main/java/
├── main.java                  # Entry point
├── model/                     # Data models (MenuItem, Order, Staff, etc.)
├── database/                  # DAO classes (DB queries per model)
├── service/                   # Business logic (Menu, Order, Payment)
├── userinterface/             # Swing frames (Login, Ordering, Payment, etc.)
└── util/                      # Helpers (DBConnection, ReceiptPrinter, etc.)
```

---

## Setup & Installation

### Prerequisites
- Java 21+
- Maven
- XAMPP (MySQL running on port `3306`)

### Steps

1. **Clone the repository**
   ```bash
   git clone -b Project_Dev https://github.com/gabbslvm/Smart-Cafeteria.git
   cd Smart-Cafeteria
   ```

2. **Start XAMPP** and make sure MySQL is running.

3. **Create the database**  
   Open phpMyAdmin or MySQL CLI and create a database named:
   ```sql
   CREATE DATABASE tip_cafeteria;
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn exec:java -Dexec.mainClass="main"
   ```
   Or run `main.java` directly from your IDE (Eclipse / IntelliJ).

---

## Database Configuration

Default connection settings in `DBConnection.java`:

| Setting | Value |
|---------|-------|
| Host | `localhost:3306` |
| Database | `tip_cafeteria` |
| Username | `root` |
| Password | *(empty — XAMPP default)* |

If your XAMPP setup uses a different password, update `DB_PASS` in `src/main/java/util/DBConnection.java`.

---

## License

For academic use only — Technological Institute of the Philippines.
