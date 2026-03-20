# T.I.P. Smart Cafeteria: Ordering & Billing System

A desktop application designed to streamline cafeteria operations, featuring automated ordering and billing functionality.

## Overview

Smart-Cafeteria is a Java-based desktop application that modernizes cafeteria management through an intuitive ordering system and automated billing process. Built with JFrame GUI and MySQL database integration, it provides a complete solution for cafeteria operations.

## Tech Stack

- **Language:** Java
- **GUI Framework:** JFrame (Swing)
- **Build Tool:** Maven
- **Database:** MySQL
- **Local Server:** XAMPP
- **Architecture:** Object-Oriented Programming (OOP)

## Key Features

- **User-friendly ordering interface** - Intuitive JFrame-based GUI for smooth ordering experience
- **Automated billing system** - Real-time calculation and receipt generation
- **Menu management** - Dynamic product catalog with pricing
- **Transaction tracking** - Complete order history and reporting
- **Database integration** - MySQL backend for persistent data storage
- **Inventory monitoring** - Track product availability

## Project Structure

```
Smart-Cafeteria/
├── src/
│   ├── main/
│   │   └── java/
│   └── resources/
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites
- Java JDK 8 or higher
- Maven
- XAMPP (for MySQL)
- MySQL Connector/J

### Installation

1. Clone the repository
```bash
git clone https://github.com/gabbslvm/Smart-Cafeteria.git
cd Smart-Cafeteria
```

2. Start XAMPP and run MySQL service

3. Import the database schema
```sql
-- Import the provided SQL file to create necessary tables
```

4. Configure database connection in the project

5. Build with Maven
```bash
mvn clean install
```

6. Run the application
```bash
mvn exec:java
```

## Purpose

Developed as the final project for Computer Programming 2 (Java) course, this system demonstrates:
- Full-stack desktop application development
- Database design and integration
- OOP principles and design patterns
- GUI development with Swing/JFrame
- Maven project management

## Development Team

**Team JAVAngers**
- Project developed for academic requirements

## Note

This is an academic project created for Computer Programming 2 course.
