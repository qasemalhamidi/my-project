package hospitalsystem;

/**
 * Builder Design Pattern — EmployeeBuilder
 */
public class EmployeeBuilder {
    private final int    id;
    private final String name;
    private final int    age;
    private String role              = "Receptionist";
    private double baseSalary        = 800;
    private String department        = "General";
    private String specialization    = "";
    private int    yearsOfExperience = 0;
    private String shift             = "Day";

    public EmployeeBuilder(int id, String name, int age) {
        this.id = id; this.name = name; this.age = age;
    }

    public EmployeeBuilder role(String v)              { this.role = v;              return this; }
    public EmployeeBuilder baseSalary(double v)        { this.baseSalary = v;        return this; }
    public EmployeeBuilder department(String v)        { this.department = v;        return this; }
    public EmployeeBuilder specialization(String v)    { this.specialization = v;    return this; }
    public EmployeeBuilder yearsOfExperience(int v)    { this.yearsOfExperience = v; return this; }
    public EmployeeBuilder shift(String v)             { this.shift = v;             return this; }

    public Employee build() {
        if (id <= 0)                        throw new IllegalStateException("ID must be positive");
        if (name == null || name.isBlank()) throw new IllegalStateException("Name is required");
        if (baseSalary <= 0)                throw new IllegalStateException("Salary must be positive");
        return switch (role) {
            case "Doctor"       -> new Doctor(specialization, yearsOfExperience, baseSalary, id, name, age, department);
            case "Nurse"        -> new Nurse(shift, baseSalary, id, name, age, department);
            case "Receptionist" -> new Receptionist(baseSalary, id, name, age, department);
            default             -> throw new IllegalStateException("Unknown role: " + role);
        };
    }
}
