package hospitalsystem;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HospitalData {
    private final List<Patient> patients = new ArrayList<>();
    private final List<Employee> staff = new ArrayList<>();
    private static final String PATIENTS_FILE = "patients.txt";
    private static final String STAFF_FILE = "staff.txt";

    public HospitalData() {
        // Default staff — built using EmployeeBuilder (Builder Pattern)
        staff.add(new EmployeeBuilder(1, "Dr. Ahmad", 45)
                .role("Doctor").baseSalary(2000).department("Cardiology")
                .specialization("Cardiology").yearsOfExperience(10).build());

        staff.add(new EmployeeBuilder(2, "Dr. Layla", 38)
                .role("Doctor").baseSalary(1800).department("Neurology")
                .specialization("Neurology").yearsOfExperience(5).build());

        staff.add(new EmployeeBuilder(3, "Sara", 30)
                .role("Nurse").baseSalary(1200).department("ICU")
                .shift("Night").build());

        staff.add(new EmployeeBuilder(4, "Rami", 28)
                .role("Nurse").baseSalary(1100).department("General")
                .shift("Day").build());

        staff.add(new EmployeeBuilder(5, "Ali", 35)
                .role("Receptionist").baseSalary(900).department("Reception").build());
    }

    // ===== PATIENT METHODS =====
    public boolean isDuplicatePatient(int id, String fileNumber) {
        return patients.stream().anyMatch(p ->
                p.getId() == id || p.getFileNumber().equalsIgnoreCase(fileNumber));
    }

    public boolean addPatient(Patient p) {
        if (isDuplicatePatient(p.getId(), p.getFileNumber())) return false;
        patients.add(p);
        return true;
    }

    public Patient searchByFile(String file) {
        return patients.stream()
                .filter(p -> p.getFileNumber().equalsIgnoreCase(file))
                .findFirst().orElse(null);
    }

    public List<Patient> searchPatients(String query) {
        String q = query.toLowerCase();
        return patients.stream()
                .filter(p -> p.getFileNumber().toLowerCase().contains(q)
                        || p.getName().toLowerCase().contains(q)
                        || String.valueOf(p.getId()).contains(q))
                .collect(Collectors.toList());
    }

    public boolean updatePatient(String file, String address, String diagnosis,
                                  String phone, String bloodType, String status) {
        Patient p = searchByFile(file);
        if (p == null) return false;
        p.setAddress(address);
        p.setDiagnosis(diagnosis);
        p.setPhoneNumber(phone);
        p.setBloodType(bloodType);
        p.setStatus(status);
        return true;
    }

    public boolean deletePatient(String file) {
        return patients.removeIf(p -> p.getFileNumber().equalsIgnoreCase(file));
    }

    public List<Patient> getAllPatients() { return new ArrayList<>(patients); }

    public long countActive() {
        return patients.stream().filter(p -> "Active".equals(p.getStatus())).count();
    }

    // ===== STAFF METHODS =====
    public boolean isDuplicateStaff(int id) {
        return staff.stream().anyMatch(e -> e.getId() == id);
    }

    public boolean addEmployee(Employee e) {
        if (isDuplicateStaff(e.getId())) return false;
        staff.add(e);
        return true;
    }

    public boolean deleteEmployee(int id) {
        return staff.removeIf(e -> e.getId() == id);
    }

    public Employee findEmployee(int id) {
        return staff.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    public List<Employee> getAllStaff() { return new ArrayList<>(staff); }

    public List<Employee> searchStaff(String query) {
        String q = query.toLowerCase();
        return staff.stream()
                .filter(e -> e.getName().toLowerCase().contains(q)
                        || e.getRole().toLowerCase().contains(q)
                        || String.valueOf(e.getId()).contains(q))
                .collect(Collectors.toList());
    }

    // ===== FILE I/O =====
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
                        // PatientBuilder — Builder Pattern
                        Patient p = new PatientBuilder(
                                Integer.parseInt(t[0].trim()), t[1].trim(),
                                Integer.parseInt(t[2].trim()), t[3].trim())
                                .address(t[4].trim())
                                .diagnosis(t[5].trim())
                                .phoneNumber(t[6].trim())
                                .bloodType(t[7].trim())
                                .status(t[8].trim())
                                .build();
                        patients.add(p);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
    }
}
