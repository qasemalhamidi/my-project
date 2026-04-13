package hospitalsystem;

/**
 * Builder Design Pattern — EmployeeBuilder
 *
 * يحل نفس مشكلة PatientBuilder لكن لكائنات Employee (Doctor/Nurse/Receptionist).
 * Constructor الـ Doctor مثلاً يأخذ 7 arguments بترتيب غير واضح:
 *   new Doctor("Cardiology", 10, 2000, 1, "Dr. Ahmad", 45, "Cardiology")
 *                 ^spec       ^yrs ^sal ^id  ^name      ^age  ^dept
 *
 * مع Builder:
 *   Employee doc = new EmployeeBuilder(1, "Dr. Ahmad", 45)
 *                      .role("Doctor")
 *                      .baseSalary(2000)
 *                      .department("Cardiology")
 *                      .specialization("Cardiology")
 *                      .yearsOfExperience(10)
 *                      .build();
 */
public class EmployeeBuilder {

    // ── الحقول الإلزامية ─────────────────────────────────────────
    private final int    id;
    private final String name;
    private final int    age;

    // ── الحقول الاختيارية ────────────────────────────────────────
    private String role             = "Receptionist";
    private double baseSalary       = 800;
    private String department       = "General";
    private String specialization   = "";
    private int    yearsOfExperience= 0;
    private String shift            = "Day";

    // ── Constructor يأخذ فقط الحقول الإلزامية ────────────────────
    public EmployeeBuilder(int id, String name, int age) {
        this.id   = id;
        this.name = name;
        this.age  = age;
    }

    public EmployeeBuilder role(String role) {
        this.role = role;
        return this;
    }

    public EmployeeBuilder baseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
        return this;
    }

    public EmployeeBuilder department(String department) {
        this.department = department;
        return this;
    }

    public EmployeeBuilder specialization(String specialization) {
        this.specialization = specialization;
        return this;
    }

    public EmployeeBuilder yearsOfExperience(int years) {
        this.yearsOfExperience = years;
        return this;
    }

    public EmployeeBuilder shift(String shift) {
        this.shift = shift;
        return this;
    }

    // ── build() — ينشئ النوع الصحيح من Employee ──────────────────
    public Employee build() {
        if (id <= 0)               throw new IllegalStateException("Employee ID must be positive");
        if (name == null || name.isBlank()) throw new IllegalStateException("Employee name is required");
        if (baseSalary <= 0)       throw new IllegalStateException("Base salary must be positive");

        return switch (role) {
            case "Doctor"       -> new Doctor(specialization, yearsOfExperience, baseSalary, id, name, age, department);
            case "Nurse"        -> new Nurse(shift, baseSalary, id, name, age, department);
            case "Receptionist" -> new Receptionist(baseSalary, id, name, age, department);
            default             -> throw new IllegalStateException("Unknown role: " + role);
        };
    }
}
