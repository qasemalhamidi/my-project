package hospitalsystem;

import javax.swing.table.DefaultTableModel;
import java.util.Locale;

/**
 * ============================================================
 *  OBSERVER DESIGN PATTERN — Concrete Observer 3
 * ============================================================
 * يحدّث جدول الموظفين عند أي تغيير في بيانات الموظفين
 * ============================================================
 */
public class StaffTableObserver implements HospitalObserver {

    private final HospitalData subject;
    private final DefaultTableModel tableModel;

    public StaffTableObserver(HospitalData subject, DefaultTableModel tableModel) {
        this.subject    = subject;
        this.tableModel = tableModel;
    }

    @Override
    public void subscribe() { subject.registerObserver(this); }

    @Override
    public void unsubscribe() { subject.removeObserver(this); }

    @Override
    public void update(HospitalEvent event) {
        switch (event) {
            case EMPLOYEE_ADDED, EMPLOYEE_UPDATED,
                 EMPLOYEE_DELETED, DATA_LOADED -> refreshTable();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Employee e : subject.getAllStaff()) {
            String details = "";
            if (e instanceof Doctor d)
                details = d.getSpecialization() + " (" + d.getYearsOfExperience() + " yrs)";
            else if (e instanceof Nurse n)
                details = n.getShift() + " shift";

            tableModel.addRow(new Object[]{
                e.getId(), e.getName(), e.getAge(), e.getRole(),
                e.getDepartment(), details,
                String.format(Locale.US, "%.2f", e.getBaseSalary()),
                String.format(Locale.US, "%.2f", e.calculateSalary())
            });
        }
    }
}
