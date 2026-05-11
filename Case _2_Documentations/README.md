# 🏥 Hospital Management System

نظام إدارة مستشفى مبني بـ **Java Swing** يطبّق ثلاثة أنماط تصميم رئيسية:
**Composite**, **Builder**, و **Observer** (مقترح).

---

## 📋 محتويات المشروع

```
hospitalsystem/
├── Person.java              # Abstract base class
├── Employee.java            # Abstract employee (extends Person)
├── Doctor.java              # Concrete employee
├── Nurse.java               # Concrete employee
├── Receptionist.java        # Concrete employee
├── Patient.java             # Patient entity (extends Person)
├── SalaryCalculator.java    # Interface for salary calculation
├── HospitalComponent.java   # Composite interface
├── EmployeeLeaf.java        # Composite - Leaf
├── Department.java          # Composite - Composite node
├── EmployeeBuilder.java     # Builder for employees
├── PatientBuilder.java      # Builder for patients
├── HospitalData.java        # Data layer (Director + Subject)
└── HospitalSystemUI.java    # Swing UI (Main entry point)
```

---

## 🏗️ أنماط التصميم المستخدمة

### 1. Composite Pattern

يتيح التعامل مع موظف واحد وقسم كامل بنفس الطريقة.

```
HospitalComponent (interface)
├── EmployeeLeaf       → يمثل موظفاً واحداً (Leaf)
└── Department         → يمثل قسماً يحتوي على موظفين أو أقسام فرعية (Composite)
```

**الفائدة العملية:**
```java
// نفس الاستدعاء على موظف أو قسم كامل
hospitalRoot.getTotalSalary();   // مجموع رواتب كل المستشفى
cardiologyDept.getTotalSalary(); // مجموع رواتب قسم القلب فقط
employeeLeaf.getTotalSalary();   // راتب موظف واحد
```

---

### 2. Builder Pattern

يحل مشكلة بناء كائنات معقدة ذات حقول اختيارية كثيرة.

**EmployeeBuilder** — يبني Doctor أو Nurse أو Receptionist:
```java
Employee emp = new EmployeeBuilder(1, "Dr. Ahmad", 45)
    .role("Doctor")
    .baseSalary(2000)
    .department("Cardiology")
    .specialization("Cardiology")
    .yearsOfExperience(10)
    .build();
```

**PatientBuilder** — يبني Patient بحقول اختيارية:
```java
Patient patient = new PatientBuilder(101, "Sara Ali", 30, "P-2024-001")
    .address("Amman, Jordan")
    .diagnosis("Hypertension")
    .bloodType("A+")
    .status("Active")
    .build();
```

---

### 3. Observer Pattern *(مقترح للتطوير)*

يُقترح تطبيقه لإزالة الاستدعاءات اليدوية لـ `refreshDashboard()` و `refreshStaffTable()` في كل مكان، واستبدالها بإشعار تلقائي عند تغيُّر البيانات.

```java
// بدلاً من:
data.addEmployee(emp);
refreshStaffTable(data.getAllStaff());
refreshDashboard();
refreshDeptTree();

// يصبح:
data.addEmployee(emp); // يُخطر كل المشتركين تلقائياً
```

**المكونات المقترحة:**
- `HospitalSubject` — interface يحتوي على `registerObserver`, `notifyObservers`
- `HospitalObserver` — interface يحتوي على `update(HospitalData)`
- `HospitalData` — ConcreteSubject
- `HospitalSystemUI` — ConcreteObserver

---

## 🚀 تشغيل المشروع

### المتطلبات
- Java 17 أو أحدث (يستخدم `instanceof` pattern matching)
- لا توجد مكتبات خارجية — Java Swing فقط

### التجميع والتشغيل

```bash
# تجميع
javac -d out hospitalsystem/*.java

# تشغيل
java -cp out hospitalsystem.HospitalSystemUI
```

---

## 📊 هيكل البيانات (Composite Tree)

```
Hospital (Root Department)
├── Cardiology
│   └── [Doctor] Dr. Ahmad — $3,000
├── Neurology
│   └── [Doctor] Dr. Layla — $2,250
├── ICU
│   └── [Nurse]  Sara — $1,320 (night bonus)
├── General
│   └── [Nurse]  Rami — $1,100
└── Reception
    └── [Receptionist] Ali — $900
```

---

## 💡 حساب الرواتب

| الدور        | الصيغة                                      |
|-------------|---------------------------------------------|
| Doctor      | `baseSalary + (baseSalary × 0.05 × years)`  |
| Nurse       | `baseSalary + (baseSalary × 0.10)` إذا كانت وردية ليلية |
| Receptionist| `baseSalary` (ثابت)                         |

---

## 💾 حفظ البيانات

بيانات المرضى تُحفظ وتُقرأ من ملف `patients.txt` بصيغة CSV.  
بيانات الموظفين تُخزَّن في الذاكرة فقط (يمكن توسيعها).

```
id,name,age,fileNumber,address,diagnosis,phone,bloodType,status
101,Sara Ali,30,P-2024-001,Amman,Hypertension,0791234567,A+,Active
```

---

## 🖥️ واجهة المستخدم

| الصفحة        | الوصف                                      |
|--------------|---------------------------------------------|
| Dashboard    | إحصائيات سريعة: المرضى، الموظفون، الرواتب  |
| Patients     | إضافة / تعديل / حذف / بحث في المرضى        |
| Staff        | إدارة الموظفين باستخدام Builder Pattern    |
| Departments  | عرض شجرة الـ Composite مع تفاصيل كل قسم   |
| Reports      | مخططات دائرية وأعمدة للرواتب والإحصائيات  |

---

## 👥 فريق التطوير

مشروع أكاديمي — Design Patterns in Java
