package hospitalsystem;

import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * ============================================================
 *  OBSERVER DESIGN PATTERN — Concrete Observer 2
 * ============================================================
 * يحدّث جدول المرضى عند أي تغيير في بيانات المرضى
 * ============================================================
 */
public class PatientTableObserver implements HospitalObserver {

    private final HospitalData subject;
    private final DefaultTableModel tableModel;

    public PatientTableObserver(HospitalData subject, DefaultTableModel tableModel) {
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
            case PATIENT_ADDED, PATIENT_UPDATED,
                 PATIENT_DELETED, DATA_LOADED -> refreshTable();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Patient pt : subject.getAllPatients()) {
            tableModel.addRow(new Object[]{
                pt.getFileNumber(), pt.getId(), pt.getName(), pt.getAge(),
                pt.getPhoneNumber(), pt.getBloodType(),
                pt.getDiagnosis(), pt.getAddress(), pt.getStatus()
            });
        }
    }
}
