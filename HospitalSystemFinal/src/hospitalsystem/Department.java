package hospitalsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  COMPOSITE DESIGN PATTERN — Composite
 * ============================================================
 * يمثل قسماً في المستشفى (Cardiology, ICU, Reception...).
 * هو "فرع" في الشجرة — يحتوي على أطفال (موظفون أو أقسام فرعية).
 *
 * العملية على القسم تتفرع تلقائياً لكل أطفاله:
 *   getTotalSalary() = مجموع رواتب كل من فيه بشكل recursive
 *   getHeadCount()   = عدد الموظفين الكلي بشكل recursive
 *
 * هذا هو جوهر Composite Pattern:
 *   treat individual objects and compositions uniformly.
 * ============================================================
 */
public class Department implements HospitalComponent {
    private final String name;
    private final List<HospitalComponent> children = new ArrayList<>();

    public Department(String name) {
        this.name = name;
    }

    // ── إدارة الأطفال ──────────────────────────────────────────────
    public void add(HospitalComponent component) {
        children.add(component);
    }

    public void remove(HospitalComponent component) {
        children.remove(component);
    }

    public List<HospitalComponent> getChildren() {
        return new ArrayList<>(children);
    }

    // ── العمليات — تتفرع لكل الأطفال تلقائياً ─────────────────────
    @Override
    public String getName() { return name; }

    @Override
    public String getType() { return "Department"; }

    @Override
    public double getTotalSalary() {
        // Composite: اجمع رواتب كل الأطفال (موظفون + أقسام فرعية)
        return children.stream()
                .mapToDouble(HospitalComponent::getTotalSalary)
                .sum();
    }

    @Override
    public int getHeadCount() {
        // Composite: اعدّ كل الموظفين بشكل recursive
        return children.stream()
                .mapToInt(HospitalComponent::getHeadCount)
                .sum();
    }

    @Override
    public void displayInfo() {
        System.out.printf("[Department] %s | Staff: %d | Payroll: $%.2f%n",
                name, getHeadCount(), getTotalSalary());
        for (HospitalComponent child : children) {
            child.displayInfo();
        }
    }

    // ── Helper: ابحث عن موظف داخل القسم بـ ID ────────────────────
    public EmployeeLeaf findLeafById(int id) {
        for (HospitalComponent c : children) {
            if (c instanceof EmployeeLeaf leaf && leaf.getEmployee().getId() == id)
                return leaf;
            if (c instanceof Department dept) {
                EmployeeLeaf found = dept.findLeafById(id);
                if (found != null) return found;
            }
        }
        return null;
    }
}
