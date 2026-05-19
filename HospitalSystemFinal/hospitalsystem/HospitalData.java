package hospitalsystem;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * HospitalData — Subject in Observer Pattern + manages Composite tree.
 *
 * ============================================================
 *  OBSERVER DESIGN PATTERN — Concrete Subject
 * ============================================================
 * HospitalData هو الـ Subject (مصدر الأحداث).
 * عند أي تغيير في البيانات (إضافة/حذف/تعديل مريض أو موظف)
 * يستدعي notifyObservers() تلقائياً فيُبلّغ كل المكوّنات
 * المسجّلة في الـ UI دون الحاجة لاستدعائها يدوياً.
 * ============================================================
 */
public class HospitalData implements HospitalSubject {

    // ── OBSERVER PATTERN — قائمة المراقبين ──────────────────────────
    private final List<HospitalObserver> observers = new ArrayList<>();

    // ── EVENT LOG — سجل دائم للأحداث طول عمر البرنامج ──────────────
    public static class EventRecord {
        public final HospitalEvent event;
        public final String        time;
        EventRecord(HospitalEvent e, String t) { event = e; time = t; }
    }
    private final List<EventRecord> eventLog = new ArrayList<>();

    public List<EventRecord> getEventLog() { return Collections.unmodifiableList(eventLog); }
    public void clearEventLog()            { eventLog.clear(); }
    public String getDataDir()             { return DATA_DIR.getAbsolutePath(); }

    @Override
    public void registerObserver(HospitalObserver o)  { observers.add(o); }

    @Override
    public void removeObserver(HospitalObserver o)    { observers.remove(o); }

    @Override
    public void notifyObservers(HospitalEvent event) {
        // سجّل الحدث في الـ log الدائم (ما عدا DATA_LOADED)
        if (event != HospitalEvent.DATA_LOADED) {
            java.time.LocalTime now = java.time.LocalTime.now();
            String time = String.format(java.util.Locale.US, "%02d:%02d:%02d",
                    now.getHour(), now.getMinute(), now.getSecond());
            eventLog.add(new EventRecord(event, time));
        }
        for (HospitalObserver o : new java.util.ArrayList<>(observers))
            o.update(event);  // update() كما في الـ UML
    }

    private final List<Patient>  patients   = new ArrayList<>();
    private final List<Employee> staffList  = new ArrayList<>();

    // ── COMPOSITE TREE ROOT ──────────────────────────────────────────
    // الـ Hospital هو Department جذري يحتوي على كل الأقسام
    private final Department hospitalRoot = new Department("Hospital");

    // الأقسام الرئيسية
    private final Department cardiologyDept  = new Department("Cardiology");
    private final Department neurologyDept   = new Department("Neurology");
    private final Department icuDept         = new Department("ICU");
    private final Department generalDept     = new Department("General");
    private final Department receptionDept   = new Department("Reception");

    private static final String PATIENTS_FILE = "patients.dat";
    private static final String STAFF_FILE    = "staff.dat";

    // مجلد ثابت في بيت المستخدم — يعمل دائماً على كل Windows/Mac/Linux
    private static final File DATA_DIR = new File(
            System.getProperty("user.home"), "HospitalSystemData");

    private static String dataPath(String filename) {
        if (!DATA_DIR.exists()) DATA_DIR.mkdirs();
        return new File(DATA_DIR, filename).getAbsolutePath();
    }

    // الموظفون الافتراضيون — يُضافون فقط إذا لم يوجد ملف محفوظ
    private void loadDefaultStaff() {
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

    public HospitalData() {
        // بناء شجرة الـ Composite
        hospitalRoot.add(cardiologyDept);
        hospitalRoot.add(neurologyDept);
        hospitalRoot.add(icuDept);
        hospitalRoot.add(generalDept);
        hospitalRoot.add(receptionDept);
        // البيانات تُحمَّل لاحقاً من loadAll()
    }

    // ── PATIENT OPERATIONS ───────────────────────────────────────────
    public boolean addPatient(Patient p) {
        boolean dup = patients.stream().anyMatch(x ->
                x.getId() == p.getId() || x.getFileNumber().equalsIgnoreCase(p.getFileNumber()));
        if (dup) return false;
        patients.add(p);
        notifyObservers(HospitalEvent.PATIENT_ADDED);   // Observer notify
        return true;
    }

    public boolean deletePatient(String file) {
        boolean removed = patients.removeIf(p -> p.getFileNumber().equalsIgnoreCase(file));
        if (removed) notifyObservers(HospitalEvent.PATIENT_DELETED); // Observer notify
        return removed;
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
        notifyObservers(HospitalEvent.PATIENT_UPDATED); // Observer notify
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
     *  3. يُبلّغ كل الـ Observers — Observer Pattern
     */
    public boolean addEmployee(Employee e) {
        if (staffList.stream().anyMatch(x -> x.getId() == e.getId())) return false;
        staffList.add(e);
        Department dept = findOrCreateDept(e.getDepartment());
        dept.add(new EmployeeLeaf(e));
        notifyObservers(HospitalEvent.EMPLOYEE_ADDED);  // Observer notify
        return true;
    }

    /**
     * يحذف موظف من الـ flat list ومن شجرة الـ Composite
     */
    public boolean deleteEmployee(int id) {
        Employee emp = findEmployee(id);
        if (emp == null) return false;
        staffList.removeIf(e -> e.getId() == id);
        removeLeafFromTree(hospitalRoot, id);
        notifyObservers(HospitalEvent.EMPLOYEE_DELETED); // Observer notify
        return true;
    }

    /** يُبلّغ الـ Observers بتحديث موظف (تُستدعى من الـ UI بعد التعديل) */
    public void notifyEmployeeUpdated() {
        notifyObservers(HospitalEvent.EMPLOYEE_UPDATED); // Observer notify
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

    /** حفظ المرضى والموظفين معاً */
    public void saveAll() throws IOException {
        String pPath = dataPath(PATIENTS_FILE);
        String sPath = dataPath(STAFF_FILE);
        System.out.println("[SAVE] Dir  : " + DATA_DIR.getAbsolutePath());
        System.out.println("[SAVE] Writable: " + DATA_DIR.canWrite());
        System.out.println("[SAVE] Patients: " + patients.size() + " → " + pPath);
        System.out.println("[SAVE] Staff   : " + staffList.size() + " → " + sPath);
        // ── حفظ المرضى ──────────────────────────────────────────────
        try (PrintWriter pw = new PrintWriter(new FileWriter(pPath))) {
            for (Patient p : patients) {
                pw.println(String.join(",",
                    String.valueOf(p.getId()), p.getName(), String.valueOf(p.getAge()),
                    p.getFileNumber(), p.getAddress(), p.getDiagnosis(),
                    p.getPhoneNumber(), p.getBloodType(), p.getStatus()));
            }
        }

        // ── حفظ الموظفين ────────────────────────────────────────────
        try (PrintWriter pw = new PrintWriter(new FileWriter(sPath))) {
            for (Employee e : staffList) {
                String extra1 = "", extra2 = "";
                if (e instanceof Doctor d)  { extra1 = d.getSpecialization(); extra2 = String.valueOf(d.getYearsOfExperience()); }
                else if (e instanceof Nurse n) { extra1 = n.getShift(); extra2 = "-"; }
                else { extra1 = "-"; extra2 = "-"; }
                pw.println(String.join(",",
                    String.valueOf(e.getId()), e.getName(), String.valueOf(e.getAge()),
                    e.getRole(), e.getDepartment() != null ? e.getDepartment() : "",
                    String.valueOf(e.getBaseSalary()), extra1, extra2));
            }
        }
    }

    /** تحميل المرضى والموظفين — إذا لم توجد ملفات يُحمَّل الموظفون الافتراضيون */
    public void loadAll() throws IOException {
        File staffFile = new File(dataPath(STAFF_FILE));

        // ── تحميل الموظفين ──────────────────────────────────────────
        if (staffFile.exists()) {
            staffList.clear();
            // امسح الـ leaves من الشجرة مع الإبقاء على الأقسام الفارغة
            for (Department d : getDepartments()) {
                for (HospitalComponent c : new ArrayList<>(d.getChildren()))
                    d.remove(c);
            }
            try (BufferedReader br = new BufferedReader(new FileReader(staffFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] t = line.split(",", -1);
                    if (t.length < 8) continue;
                    try {
                        int    id     = Integer.parseInt(t[0].trim());
                        String name   = t[1].trim();
                        int    age    = Integer.parseInt(t[2].trim());
                        String role   = t[3].trim();
                        String dept   = t[4].trim();
                        double sal    = Double.parseDouble(t[5].trim());
                        String extra1 = t[6].trim();
                        String extra2 = t[7].trim();

                        EmployeeBuilder eb = new EmployeeBuilder(id, name, age)
                                .role(role).baseSalary(sal).department(dept);
                        if ("Doctor".equals(role)) {
                            int yrs = extra2.equals("-") ? 0 : Integer.parseInt(extra2);
                            eb.specialization(extra1).yearsOfExperience(yrs);
                        } else if ("Nurse".equals(role)) {
                            eb.shift(extra1.equals("-") ? "Day" : extra1);
                        }
                        Employee emp = eb.build();
                        staffList.add(emp);
                        findOrCreateDept(dept).add(new EmployeeLeaf(emp));
                    } catch (Exception ignored) {}
                }
            }
        } else {
            // لا يوجد ملف محفوظ — حمّل الموظفين الافتراضيين
            loadDefaultStaff();
        }

        // ── تحميل المرضى ────────────────────────────────────────────
        File pFile = new File(dataPath(PATIENTS_FILE));
        if (pFile.exists()) {
            patients.clear();
            try (BufferedReader br = new BufferedReader(new FileReader(pFile))) {
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

        // DATA_LOADED لا يُسجَّل في الـ event log (راجع notifyObservers)
        // لكنه يُحدّث كل الـ Observers في الـ UI
        notifyObservers(HospitalEvent.DATA_LOADED);
    }
}
