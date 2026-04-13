package hospitalsystem;

/**
 * Builder Design Pattern — PatientBuilder
 *
 * المشكلة التي يحلها:
 * كلاس Patient لديه 9 حقول في constructor، بعضها إلزامي (id, name, fileNumber)
 * والباقي اختياري (phone, bloodType, address...).
 * بدون Builder، كل مكان في الكود يحتاج يتذكر ترتيب الـ 9 arguments وهذا مصدر أخطاء.
 *
 * الحل مع Builder:
 *   Patient p = new PatientBuilder(1, "Ahmad", 30, "F-001")
 *                   .address("Amman")
 *                   .diagnosis("Flu")
 *                   .phoneNumber("0791234567")
 *                   .bloodType("A+")
 *                   .status("Active")
 *                   .build();
 */
public class PatientBuilder {

    // ── الحقول الإلزامية ─────────────────────────────────────────
    private final int    id;
    private final String name;
    private final int    age;
    private final String fileNumber;

    // ── الحقول الاختيارية (لها قيم افتراضية) ─────────────────────
    private String address     = "";
    private String diagnosis   = "Unknown";
    private String phoneNumber = "";
    private String bloodType   = "Unknown";
    private String status      = "Active";

    // ── Constructor يأخذ فقط الحقول الإلزامية ────────────────────
    public PatientBuilder(int id, String name, int age, String fileNumber) {
        this.id         = id;
        this.name       = name;
        this.age        = age;
        this.fileNumber = fileNumber;
    }

    // ── Setter methods — كل واحد يرجع this لدعم method chaining ──
    public PatientBuilder address(String address) {
        this.address = address;
        return this;
    }

    public PatientBuilder diagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
        return this;
    }

    public PatientBuilder phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public PatientBuilder bloodType(String bloodType) {
        this.bloodType = bloodType;
        return this;
    }

    public PatientBuilder status(String status) {
        this.status = status;
        return this;
    }

    // ── build() — ينشئ الكائن النهائي بعد اكتمال التهيئة ─────────
    public Patient build() {
        if (id <= 0)               throw new IllegalStateException("Patient ID must be positive");
        if (name == null || name.isBlank()) throw new IllegalStateException("Patient name is required");
        if (fileNumber == null || fileNumber.isBlank()) throw new IllegalStateException("File number is required");

        return new Patient(id, name, age, fileNumber, address, diagnosis, phoneNumber, bloodType, status);
    }
}
