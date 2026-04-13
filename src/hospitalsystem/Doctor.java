package hospitalsystem;

public class Doctor extends Employee {
    private String specialization;
    private int yearsOfExperience;

    public Doctor(String specialization, int yearsOfExperience, double baseSalary, int id, String name, int age, String department) {
        super(baseSalary, id, name, age, department);
        this.specialization = specialization;
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String s) { this.specialization = s; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int y) { this.yearsOfExperience = y; }

    @Override
    public String getRole() { return "Doctor"; }

    @Override
    public double calculateSalary() {
        return baseSalary + (baseSalary * 0.05 * yearsOfExperience);
    }

    @Override
    public void displayInfo() {
        System.out.println("Doctor | ID=" + getId() + ", Name=" + getName()
                + ", Spec=" + specialization + ", Salary=" + calculateSalary());
    }

    @Override
    public String toTableRow() {
        return getId() + " | " + getName() + " | Doctor | " + specialization
                + " | " + yearsOfExperience + " yrs | " + String.format("%.2f", calculateSalary());
    }
}
