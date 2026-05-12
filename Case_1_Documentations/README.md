# Hospital Management System
## Design Patterns Project — Builder Pattern

---

## Project Overview

A Java desktop application (Swing) for managing hospital staff (Doctors, Nurses, Receptionists) and patients.

**Design Pattern Applied:** Builder  
**Pattern Type:** Creational (Gang of Four)  
**Language:** Java 17+  
**UI:** Java Swing

---

## Problem Solved

The system has complex domain objects with many fields:
- `Patient` — 9 constructor parameters (id, name, age, fileNumber, address, diagnosis, phone, bloodType, status)
- `Doctor` — 7 constructor parameters with easily swapped numeric types (salary vs id vs years)

**Without Builder:** Telescoping constructors, silent type-swap bugs, no validation.  
**With Builder:** Self-documenting fluent API, safe defaults, build-time validation.

---

## Design Pattern: Builder

### Participants

| GoF Role | Concrete Class | Responsibility |
|----------|---------------|----------------|
| Builder | `PatientBuilder`, `EmployeeBuilder` | Fluent setters + `build()` |
| Director | `HospitalData` | Orchestrates builder calls |
| Product | `Patient`, `Doctor`, `Nurse`, `Receptionist` | Configured domain objects |

### Usage Example

```java
// Patient creation — 4 mandatory + optional fields
Patient p = new PatientBuilder(1, "Ahmad", 30, "F-001")
    .address("Amman")
    .diagnosis("Flu")
    .phoneNumber("0791234567")
    .bloodType("A+")
    .status("Active")
    .build();

// Employee creation — role determines concrete type
Employee doc = new EmployeeBuilder(1, "Dr. Ahmad", 45)
    .role("Doctor")
    .baseSalary(2000)
    .department("Cardiology")
    .specialization("Cardiology")
    .yearsOfExperience(10)
    .build();
```

---

## Project Structure

```
src/hospitalsystem/
├── Person.java             # Abstract base — id (final), name, age
├── Employee.java           # Abstract — extends Person, implements SalaryCalculator
├── Doctor.java             # salary = base + 5% × yearsOfExperience
├── Nurse.java              # Night shift earns +10% bonus
├── Receptionist.java       # salary = baseSalary (no bonus)
├── Patient.java            # 9-field domain object
├── PatientBuilder.java     # Builder — 4 mandatory + 5 optional fields
├── EmployeeBuilder.java    # Builder — role-based switch → correct subtype
├── SalaryCalculator.java   # Interface — calculateSalary() : double
├── HospitalData.java       # Data layer — CRUD, CSV I/O, uses both builders
└── HospitalSystemUI.java   # Swing UI
```

---

## SOLID Principles Compliance

| Principle | How Applied |
|-----------|-------------|
| **S** — Single Responsibility | `PatientBuilder` only builds patients; `Patient` only holds data |
| **O** — Open/Closed | New roles added to `EmployeeBuilder.build()` without modifying existing classes |
| **L** — Liskov Substitution | `build()` returns `Employee`; all subtypes fully substitutable |
| **I** — Interface Segregation | `SalaryCalculator` exposes only `calculateSalary()` |
| **D** — Dependency Inversion | `HospitalData` depends on `Employee` abstraction, not concrete types |

---

## Salary Calculation

| Role | Formula |
|------|---------|
| Doctor | `baseSalary + (baseSalary × 0.05 × yearsOfExperience)` |
| Nurse (Night) | `baseSalary + (baseSalary × 0.10)` |
| Nurse (Day) | `baseSalary` |
| Receptionist | `baseSalary` |

---

## How to Run

**Windows:**
```bash
run.bat
# or
java -jar HospitalSystem.jar
```

**Linux / macOS:**
```bash
chmod +x run.sh && ./run.sh
# or
java -jar HospitalSystem.jar
```

**Requirements:** Java 17+ (uses switch expressions)

Data is saved automatically to `patients.txt` in the working directory.

---

## Deliverables Checklist

| Deliverable | Status |
|-------------|--------|
| Problem Analysis Report | ✅ `problem analysis and trade offs.docx` |
| Pattern Justification | ✅ Included in `Design Pattern Justification.docx` (Section 2) |
| UML Class Diagram | ✅ `Uml.pdf` |
| Source Code | ✅ `HospitalSystemFinal/src/` |
| README | ✅ This file |
| Presentation Slides | ✅ `HMS Presentationion.pptx` |

---

## Pattern Trade-off Summary

| Pattern | Considered | Verdict |
|---------|-----------|---------|
| Factory Method | Yes | Partial fit — solves *what*, not *how to configure* |
| Abstract Factory | Yes | Overkill — single object family |
| **Builder** | **Yes** | **✅ Best fit — handles optional fields + validation + fluent API** |
| Prototype | Yes | Doesn't solve creation complexity |
