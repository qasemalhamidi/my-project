package hospitalsystem;

/**
 * ============================================================
 *  OBSERVER DESIGN PATTERN — Observer Interface
 * ============================================================
 * update()      — يُستدعى من الـ Subject عند حدوث حدث
 * subscribe()   — يسجّل نفسه في الـ Subject
 * unsubscribe() — يلغي تسجيله من الـ Subject
 * ============================================================
 */
public interface HospitalObserver {
    void update(HospitalEvent event);
    void subscribe();
    void unsubscribe();
}
