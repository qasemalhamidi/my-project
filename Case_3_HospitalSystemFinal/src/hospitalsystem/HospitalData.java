package hospitalsystem;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * HospitalData manages:
 *  - A flat list of all employees (for quick lookup)
 *  - A Composite tree of Departments (for hierarchical operations)
 *  - A list of patients
 */
public class HospitalData {

    private final List<Patient>  patients   = new ArrayList<>();
    private final List<Employee> staffList  = new ArrayList<>();  // flat list

    // ── COMPOSITE TREE ROOT ──────────────────────────────────────────
    // الـ Hospital هو Department جذري يحتوي على كل الأقسام
    private final Department hospitalRoot = new Department("Hospital");

    // الأقسام الرئيسية
    private final Department cardiologyDept  = new Department("Cardiology");
    private final Department neurologyDept   = new Department("Neurology");
    private final Department icuDept         = new Department("ICU");
    private final Department generalDept     = new Department("General");
    private final Department receptionDept   = new Department("Reception");

    private static final String PATIENTS_FILE = "patients.txt";

    public HospitalData() {
        // بناء شجرة الـ Composite
        hospitalRoot.add(cardiologyDept);
        hospitalRoot.add(neurologyDept);
        hospitalRoot.add(icuDept);
        hospitalRoot.add(generalDept);
        hospitalRoot.add(receptionDept);

        // Default staff — باستخدام Builder Pattern
        addEmployee(new EmployeeBuilder(1, "Dr. Ahmad", 45)
                .role("Doctor").baseSalary(2000).department("Cardiology")
                .specialization("Cardiology").yearsOfExperience(10).build());

        addEmployee(new EmployeeBuilder(2, "Dr. Layla", 38)
                .role("Doctor").baseSalary(1800).department("Neurology")
                .specialization("Neurology").yearsOfExperience(5).build());

        addEmployee(new EmployeeBuilder(3, "Sara", 30)
                .role("Nurse").baseSalary(1200).department("ICU")
                .shift("Night").build());

        addEmployee(new EmployeeBuilder(4, "Rami", 28)
                .role("Nurse").baseSalary(1100).department("General")
                .shift("Day").build());

        addEmployee(new EmployeeBuilder(5, "Ali", 35)
                .role("Receptionist").baseSalary(900).department("Reception").build());
    }

    // ── PATIENT OPERATIONS ───────────────────────────────────────────
    public boolean addPatient(Patient p) {
        boolean dup = patients.stream().anyMatch(x ->
                x.getId() == p.getId() || x.getFileNumber().equalsIgnoreCase(p.getFileNumber()));
        if (dup) return false;
        patients.add(p);
        return true;
    }

    public boolean deletePatient(String file) {
        return patients.removeIf(p -> p.getFileNumber().equalsIgnoreCase(file));
    }

    public Patient searchByFile(String file) {
        return patients.stream()
                .filter(p -> p.getFileNumber().equalsIgnoreCase(file))
                .findFirst().orElse(null);
    }

    public List<Patient> searchPatients(String q) {
        String lq = q.toLowerCase();
        return patients.stream().filter(p ->
                p.getFileNumber().toLowerCase().contains(lq) ||
                p.getName().toLowerCase().contains(lq) ||
                String.valueOf(p.getId()).contains(lq))
                .collect(Collectors.toList());
    }

    public boolean updatePatient(String file, String address, String diagnosis,
                                  String phone, String bloodType, String status) {
        Patient p = searchByFile(file);
        if (p == null) return false;
        p.setAddress(address); p.setDiagnosis(diagnosis);
        p.setPhoneNumber(phone); p.setBloodType(bloodType); p.setStatus(status);
        return true;
    }

    public List<Patient> getAllPatients() { return new ArrayList<>(patients); }

    public long countActive() {
        return patients.stream().filter(p -> "Active".equals(p.getStatus())).count();
    }

    // ── EMPLOYEE / COMPOSITE OPERATIONS ─────────────────────────────

    /**
     * يضيف موظف:
     *  1. للـ flat list (للبحث السريع)
     *  2. كـ EmployeeLeaf في القسم المناسب في شجرة الـ Composite
     */
    public boolean addEmployee(Employee e) {
        if (staffList.stream().anyMatch(x -> x.getId() == e.getId())) return false;
        staffList.add(e);

        // أضفه كـ Leaf في القسم المناسب في شجرة الـ Composite
        Department dept = findOrCreateDept(e.getDepartment());
        dept.add(new EmployeeLeaf(e));
        return true;
    }

    /**
     * يحذف موظف من الـ flat list ومن شجرة الـ Composite
     */
    public boolean deleteEmployee(int id) {
        Employee emp = findEmployee(id);
        if (emp == null) return false;
        staffList.removeIf(e -> e.getId() == id);
        // احذفه من الشجرة
        removeLeafFromTree(hospitalRoot, id);
        return true;
    }

    private boolean removeLeafFromTree(Department dept, int id) {
        for (HospitalComponent c : new ArrayList<>(dept.getChildren())) {
            if (c instanceof EmployeeLeaf leaf && leaf.getEmployee().getId() == id) {
                dept.remove(leaf);
                return true;
            }
            if (c instanceof Department sub && removeLeafFromTree(sub, id)) return true;
        }
        return false;
    }

    public Employee findEmployee(int id) {
        return staffList.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    public List<Employee> getAllStaff() { return new ArrayList<>(staffList); }

    public List<Employee> searchStaff(String q) {
        String lq = q.toLowerCase();
        return staffList.stream().filter(e ->
                e.getName().toLowerCase().contains(lq) ||
                e.getRole().toLowerCase().contains(lq) ||
                String.valueOf(e.getId()).contains(lq))
                .collect(Collectors.toList());
    }

    // ── COMPOSITE TREE ACCESS ────────────────────────────────────────

    /** الجذر — يمثل المستشفى كاملاً */
    public Department getHospitalRoot() { return hospitalRoot; }

    /** كل الأقسام المباشرة */
    public List<Department> getDepartments() {
        return hospitalRoot.getChildren().stream()
                .filter(c -> c instanceof Department)
                .map(c -> (Department) c)
                .collect(Collectors.toList());
    }

    /** راتب المستشفى كله (من الجذر) */
    public double getTotalPayroll() { return hospitalRoot.getTotalSalary(); }

    /** إجمالي الموظفين من الشجرة */
    public int getTotalHeadCount() { return hospitalRoot.getHeadCount(); }

    /** راتب قسم معين بالاسم */
    public double getDeptSalary(String deptName) {
        return getDepartments().stream()
                .filter(d -> d.getName().equalsIgnoreCase(deptName))
                .mapToDouble(Department::getTotalSalary)
                .findFirst().orElse(0);
    }

    private Department findOrCreateDept(String deptName) {
        return getDepartments().stream()
                .filter(d -> d.getName().equalsIgnoreCase(deptName))
                .findFirst()
                .orElseGet(() -> {
                    Department newDept = new Department(deptName);
                    hospitalRoot.add(newDept);
                    return newDept;
                });
    }

    // ── FILE I/O ─────────────────────────────────────────────────────
    public void savePatients() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PATIENTS_FILE))) {
            for (Patient p : patients) {
                pw.println(p.getId() + "," + p.getName() + "," + p.getAge() + ","
                        + p.getFileNumber() + "," + p.getAddress() + ","
                        + p.getDiagnosis() + "," + p.getPhoneNumber() + ","
                        + p.getBloodType() + "," + p.getStatus());
            }
        }
    }

    public void loadPatients() throws IOException {
        File f = new File(PATIENTS_FILE);
        if (!f.exists()) return;
        patients.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] t = line.split(",", -1);
                if (t.length >= 9) {
                    try {
                        Patient p = new PatientBuilder(
                                Integer.parseInt(t[0].trim()), t[1].trim(),
                                Integer.parseInt(t[2].trim()), t[3].trim())
                                .address(t[4].trim()).diagnosis(t[5].trim())
                                .phoneNumber(t[6].trim()).bloodType(t[7].trim())
                                .status(t[8].trim()).build();
                        patients.add(p);
                    } catch (Exception ignored) {}
                }
            }
        }
    }
}
