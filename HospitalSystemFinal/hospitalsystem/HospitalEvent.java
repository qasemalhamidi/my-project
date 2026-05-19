package hospitalsystem;

/**
 * ============================================================
 *  OBSERVER DESIGN PATTERN — Event Types
 * ============================================================
 *
 * يمثّل أنواع الأحداث التي يُطلقها الـ Subject (HospitalData).
 * كل Observer يستقبل الحدث ويقرر بناءً على نوعه ماذا يفعل.
 *
 * مثال:
 *   PATIENT_ADDED   → يحدّث PatientTable + Dashboard
 *   EMPLOYEE_ADDED  → يحدّث StaffTable + DeptTree + Dashboard
 *   DATA_LOADED     → يحدّث كل شيء
 * ============================================================
 */
public enum HospitalEvent {

    // ── Patient Events ─────────────────────────────────────────
    PATIENT_ADDED    ("Patient Added",    "A new patient was registered."),
    PATIENT_UPDATED  ("Patient Updated",  "Patient information was modified."),
    PATIENT_DELETED  ("Patient Deleted",  "A patient record was removed."),

    // ── Employee Events ────────────────────────────────────────
    EMPLOYEE_ADDED   ("Employee Added",   "A new staff member was added."),
    EMPLOYEE_UPDATED ("Employee Updated", "Staff member information was modified."),
    EMPLOYEE_DELETED ("Employee Deleted", "A staff member was removed."),

    // ── System Events ──────────────────────────────────────────
    DATA_LOADED      ("Data Loaded",      "Patient data was loaded from file.");

    // ──────────────────────────────────────────────────────────
    private final String label;
    private final String description;

    HospitalEvent(String label, String description) {
        this.label       = label;
        this.description = description;
    }

    public String getLabel()       { return label; }
    public String getDescription() { return description; }

    @Override
    public String toString() { return label; }
}
