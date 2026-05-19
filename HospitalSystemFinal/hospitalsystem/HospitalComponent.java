package hospitalsystem;

/**
 * ============================================================
 *  COMPOSITE DESIGN PATTERN — Component Interface
 * ============================================================
 *
 * هذه الواجهة هي "العقد المشترك" بين:
 *   - Department  (Composite) — قسم يحتوي على موظفين وأقسام فرعية
 *   - Employee    (Leaf)      — موظف واحد لا يحتوي على أطفال
 *
 * الفكرة: نتعامل مع القسم كاملاً ومع الموظف الواحد بنفس الطريقة.
 * مثال: getTotalSalary() على قسم = مجموع رواتب كل من فيه
 *        getTotalSalary() على موظف = راتبه فقط
 * ============================================================
 */
public interface HospitalComponent {
    String getName();
    double getTotalSalary();   // مجموع الرواتب (للقسم = كل أعضائه، للموظف = راتبه)
    int    getHeadCount();     // عدد الموظفين
    void   displayInfo();      // اعرض المعلومات
    String getType();          // "Department" أو role الموظف
}
