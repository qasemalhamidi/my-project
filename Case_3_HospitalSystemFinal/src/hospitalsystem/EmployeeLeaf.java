package hospitalsystem;

/**
 * ============================================================
 *  COMPOSITE DESIGN PATTERN — Leaf
 * ============================================================
 * يمثل موظفاً واحداً داخل هيكل الأقسام.
 * هو "ورقة" في الشجرة — لا يحتوي على أطفال.
 *
 * يغلّف (wraps) كائن Employee الموجود ويجعله متوافقاً
 * مع HospitalComponent دون تغيير كلاس Employee الأصلي.
 * ============================================================
 */
public class EmployeeLeaf implements HospitalComponent {
    private final Employee employee;

    public EmployeeLeaf(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() { return employee; }

    @Override public String getName()         { return employee.getName(); }
    @Override public double getTotalSalary()  { return employee.calculateSalary(); }
    @Override public int    getHeadCount()    { return 1; }
    @Override public String getType()         { return employee.getRole(); }

    @Override
    public void displayInfo() {
        System.out.printf("  [%s] %s — Salary: $%.2f%n",
                employee.getRole(), employee.getName(), employee.calculateSalary());
    }
}
