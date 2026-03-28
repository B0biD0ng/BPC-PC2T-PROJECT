# 👥 Employee Database System (OOP Project)

![License](https://img.shields.io/badge/license-MIT-green)
![Language](https://img.shields.io/badge/language-C%23%20%2F%20Java-blue)
![Database](https://img.shields.io/badge/database-SQL-lightgrey)

A sophisticated employee management system designed for a technology firm. This application manages personnel, tracks complex collaborations, and provides specialized analytical tools for different employee roles.

---

## 📈 Project Progress (Roadmap)

- [x] **Initial Project Setup** (Base OOP structure)
- [x] **Employee Models** (Abstract class & Inheritance)
- [ ] **Specialized Roles Logic** (Data Analysts & Security Specialists)
- [ ] **Collaboration Engine** (Dynamic data structures implementation)
- [ ] **Management Logic** (Add/Remove/Search employees)
- [ ] **Statistics & Algorithms** (Risk score & connection analysis)
- [ ] **File Persistence** (Save/Load from local files)
- [ ] **SQL Database Integration** (Backup & Startup load)
- [ ] **Final Testing & Documentation**

---

## ✨ Key Features

### 🏢 Employee Management
* **Dynamic Registration:** Add employees with auto-generated IDs, names, and birth years.
* **Role Assignment:** Choose between **Data Analysts** and **Security Specialists** upon hiring.
* **Collaboration Tracking:** Link employees with three levels of cooperation: `Poor`, `Average`, and `Good`.
* **Clean Removal:** Complete deletion of employees, including all their relational links.

### 🧠 Specialized Skills
* **📊 Data Analysts:** Can identify which colleague they share the highest number of common collaborators with.
* **🛡️ Security Specialists:** Use a custom algorithm to calculate a **Risk Score** based on the number of collaborators and average cooperation quality.

### 📊 Data Analysis & Output
* **Alphabetical Lists:** Displays employees grouped by role, sorted by surname.
* **Global Stats:** Identifies the most "connected" employee and the prevailing cooperation quality.
* **Search Engine:** Instant lookup by ID with detailed stats.

---

## 💾 Data Persistence Strategy

The system uses a **Hybrid Storage Model**:
1.  **Local Files:** Ability to save and load individual employee records to text/local files.
2.  **SQL Database:** Acts as a robust backup. Data is automatically loaded on startup and backed up to SQL upon exit.
3.  **Independence:** The program is designed to be fully functional even without an active SQL connection.

---

## 🛠️ Technical Implementation (OOP)

This project strictly adheres to modern programming principles:
* **Abstraction:** Core logic defined through an `Abstract Class` or `Interface`.
* **Polymorphism:** Unique behaviors for different employee groups.
* **Dynamic Structures:** Efficient data handling using Lists, Sets, or Maps (no fixed arrays).
* **Encapsulation:** Secure data handling within objects.

---
