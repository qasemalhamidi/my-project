package hospitalsystem;

/**
 * Builder Design Pattern — PatientBuilder
 */
public class PatientBuilder {
    private final int    id;
    private final String name;
    private final int    age;
    private final String fileNumber;
    private String address     = "";
    private String diagnosis   = "Unknown";
    private String phoneNumber = "";
    private String bloodType   = "Unknown";
    private String status      = "Active";

    public PatientBuilder(int id, String name, int age, String fileNumber) {
        this.id = id; this.name = name; this.age = age; this.fileNumber = fileNumber;
    }

    public PatientBuilder address(String v)     { this.address = v;     return this; }
    public PatientBuilder diagnosis(String v)   { this.diagnosis = v;   return this; }
    public PatientBuilder phoneNumber(String v) { this.phoneNumber = v; return this; }
    public PatientBuilder bloodType(String v)   { this.bloodType = v;   return this; }
    public PatientBuilder status(String v)      { this.status = v;      return this; }

    public Patient build() {
        if (id <= 0)                         throw new IllegalStateException("Patient ID must be positive");
        if (name == null || name.isBlank())  throw new IllegalStateException("Name is required");
        if (fileNumber == null || fileNumber.isBlank()) throw new IllegalStateException("File number is required");
        return new Patient(id, name, age, fileNumber, address, diagnosis, phoneNumber, bloodType, status);
    }
}
