package hospitalsystem;

import javax.swing.JLabel;

/**
 * ============================================================
 *  OBSERVER DESIGN PATTERN — Concrete Observer 1
 * ============================================================
 * ConcreteObserver في الـ UML
 *
 * - يحتفظ بمرجع للـ Subject (HospitalData)
 * - subscribe()   → يسجّل نفسه في الـ Subject
 * - unsubscribe() → يلغي تسجيله
 * - update()      → يحدّث بطاقات الإحصاء في الـ Dashboard
 * ============================================================
 */
public class DashboardObserver implements HospitalObserver {

    // مرجع للـ ConcreteSubject — كما في الـ UML
    private final HospitalData subject;

    // المكوّنات التي يحدّثها هذا الـ Observer
    private final JLabel lblPatients;
    private final JLabel lblActive;
    private final JLabel lblStaff;
    private final JLabel lblDoctors;

    public DashboardObserver(HospitalData subject,
                             JLabel lblPatients, JLabel lblActive,
                             JLabel lblStaff, JLabel lblDoctors) {
        this.subject     = subject;
        this.lblPatients = lblPatients;
        this.lblActive   = lblActive;
        this.lblStaff    = lblStaff;
        this.lblDoctors  = lblDoctors;
    }

    /** يسجّل نفسه في الـ Subject */
    @Override
    public void subscribe() {
        subject.registerObserver(this);
    }

    /** يلغي تسجيله من الـ Subject */
    @Override
    public void unsubscribe() {
        subject.removeObserver(this);
    }

    /** يُستدعى تلقائياً من Subject.notifyObservers() */
    @Override
    public void update(HospitalEvent event) {
        switch (event) {
            case PATIENT_ADDED, PATIENT_DELETED,
                 EMPLOYEE_ADDED, EMPLOYEE_DELETED,
                 DATA_LOADED -> refreshDashboard();
        }
    }

    private void refreshDashboard() {
        lblPatients.setText(String.valueOf(subject.getAllPatients().size()));
        lblActive.setText(String.valueOf(subject.countActive()));
        lblStaff.setText(String.valueOf(subject.getTotalHeadCount()));
        lblDoctors.setText(String.valueOf(
            subject.getAllStaff().stream().filter(e -> e instanceof Doctor).count()));
    }
}
