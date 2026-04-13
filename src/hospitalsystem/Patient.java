package hospitalsystem;

import java.time.LocalDate;

public class Patient extends Person {
    private final String fileNumber;
    private String address;
    private String diagnosis;
    private String phoneNumber;
    private String bloodType;
    private LocalDate admissionDate;
    private String status; // Active, Discharged

    public Patient(int id, String name, int age, String fileNumber,
                   String address, String diagnosis, String phoneNumber,
                   String bloodType, String status) {
        super(id, name, age);
        this.fileNumber = fileNumber;
        this.address = address;
        this.diagnosis = diagnosis;
        this.phoneNumber = phoneNumber;
        this.bloodType = bloodType;
        this.admissionDate = LocalDate.now();
        this.status = status;
    }

    public String getFileNumber() { return fileNumber; }
    public String getAddress() { return address; }
    public String getDiagnosis() { return diagnosis; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getBloodType() { return bloodType; }
    public LocalDate getAdmissionDate() { return admissionDate; }
    public String getStatus() { return status; }

    public void setAddress(String address) { this.address = address; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public void setStatus(String status) { this.status = status; }
    public void setAdmissionDate(LocalDate d) { this.admissionDate = d; }

    @Override
    public void displayInfo() {
        System.out.println("Patient | ID=" + getId() + ", Name=" + getName()
                + ", File=" + fileNumber + ", Diagnosis=" + diagnosis + ", Status=" + status);
    }

    @Override
    public String toTableRow() {
        return fileNumber + " | " + getId() + " | " + getName() + " | " + getAge()
                + " | " + diagnosis + " | " + status;
    }
}
