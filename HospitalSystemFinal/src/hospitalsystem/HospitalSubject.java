package hospitalsystem;

/**
 * ============================================================
 *  OBSERVER DESIGN PATTERN — Subject Interface
 * ============================================================
 * registerObserver() — تسجيل Observer
 * removeObserver()   — إلغاء تسجيل Observer
 * notifyObservers()  — إخطار كل المسجّلين
 * ============================================================
 */
public interface HospitalSubject {
    void registerObserver(HospitalObserver observer);
    void removeObserver(HospitalObserver observer);
    void notifyObservers(HospitalEvent event);
}
