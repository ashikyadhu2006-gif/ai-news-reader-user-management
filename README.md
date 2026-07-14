# AI-Powered News & Notice Reader (Module 1: User Management)

A modern, responsive desktop application for user registration, authentication, database storage, and profile settings built using **Java**, **JavaFX FXML**, **SQLite**, and **JDBC**.

## 🚀 Features

- **Encapsulated User Model**: Full Object-Oriented design using JavaFX Properties for direct TableView component bindings.
- **Role-Based Routing**: Dynamic routing mechanism to dispatch users to either the **Admin Dashboard** or **User Dashboard** upon authentication.
- **Client-Side Validations**: Dynamic checking for empty fields, email syntax, password lengths, and confirmation matches.
- **SQLite Database Integration**: Local file database automatically initialized with schema tables and seeded default admin credentials on first-run.
- **Database Security**: Fully parameterized `PreparedStatement` interfaces preventing SQL injection vulnerabilities.
- **Password Security**: Credentials saved securely using `SHA-256` hashing logic.
- **Premium Glassmorphic Theme**: Clean custom stylesheet (`styles.css`) utilizing modern HSL slate dark shades, button hover effects, TableView custom headers, and layout transitions.

## 🛠️ Tech Stack

- **Core**: Java 21 (JPMS Module descriptor)
- **Framework**: JavaFX 21 (controls, fxml)
- **Database**: SQLite (via JDBC driver version 3.46.0.0)
- **Build Tool**: Apache Maven

---

## 🏃 Getting Started

### 📋 Prerequisites
- **Java SE Development Kit (JDK 21 or later)**
- **Maven** (optional; the repository contains a bootstrap script to run portable Maven automatically)

### Method 1: Console Execution (PowerShell)
1. Clone this repository and open PowerShell in the project directory.
2. Run the bootstrapper script to compile and run:
   ```powershell
   powershell -ExecutionPolicy Bypass -File .\run.ps1
   ```
   *Note: This script downloads a portable Maven binaries if not globally set in your system environment, then calls the `javafx:run` goal.*

### Method 2: Import to IntelliJ IDEA
1. Open **IntelliJ IDEA**.
2. Click **Open...** and select this directory.
3. Import as a **Maven Project** to resolve JavaFX and SQLite dependencies.
4. Locate and run `src/main/java/com/newsreader/Main.java`.

---

## 🔑 Default Accounts

On first launch, the local database connection initializes `newsreader.db` and inserts two test records:

| Role | Username | Email | Password |
|---|---|---|---|
| **Admin** | `admin` | `admin@newsreader.com` | `admin123` |
| **User** | `john_doe` | `john@newsreader.com` | `user123` |
