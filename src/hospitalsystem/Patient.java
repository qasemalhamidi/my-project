package hospitalsystem;

import java.time.LocalDate;

public class Patient extends Person {
    private final String fileNumber;
    private String address;
    private String diagnosis;
    private String phoneNumber;
    private String bloodType;
    private LocalDate admissionDate;
    private String status;

    public Patient(int id, String name, int age, String fileNumber,
                   String address, String diagnosis, String phoneNumber,
                   String bloodType, String status) {
        super(id, name, age);
        this.fileNumber    = fileNumber;
        this.address       = address;
        this.diagnosis     = diagnosis;
        this.phoneNumber   = phoneNumber;
        this.bloodType     = bloodType;
        this.admissionDate = LocalDate.now();
        this.status        = status;
    }

    public String getFileNumber()          { return fileNumber; }
    public String getAddress()             { return address; }
    public String getDiagnosis()           { return diagnosis; }
    public String getPhoneNumber()         { return phoneNumber; }
    public String getBloodType()           { return bloodType; }
    public LocalDate getAdmissionDate()    { return admissionDate; }
    public String getStatus()              { return status; }
    public void setAddress(String a)       { this.address = a; }
    public void setDiagnosis(String d)     { this.diagnosis = d; }
    public void setPhoneNumber(String p)   { this.phoneNumber = p; }
    public void setBloodType(String b)     { this.bloodType = b; }
    public void setStatus(String s)        { this.status = s; }
    public void setAdmissionDate(LocalDate d) { this.admissionDate = d; }

    @Override
    public void displayInfo() {
        System.out.println("Patient | " + getName() + " | File: " + fileNumber
                + " | " + diagnosis + " | " + status);
    }

    @Override
    public String toTableRow() {
        return fileNumber + " | " + getId() + " | " + getName() + " | "
                + getAge() + " | " + diagnosis + " | " + status;
    }
}
