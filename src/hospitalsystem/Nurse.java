package hospitalsystem;

public class Nurse extends Employee {
    private String shift;

    public Nurse(String shift, double baseSalary, int id, String name, int age, String department) {
        super(baseSalary, id, name, age, department);
        this.shift = shift;
    }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    @Override
    public String getRole() { return "Nurse"; }

    @Override
    public double calculateSalary() {
        double bonus = shift.equalsIgnoreCase("Night") ? baseSalary * 0.10 : 0;
        return baseSalary + bonus;
    }

    @Override
    public void displayInfo() {
        System.out.println("Nurse | ID=" + getId() + ", Name=" + getName()
                + ", Shift=" + shift + ", Salary=" + calculateSalary());
    }

    @Override
    public String toTableRow() {
        return getId() + " | " + getName() + " | Nurse | " + shift + " shift | - | "
                + String.format("%.2f", calculateSalary());
    }
}
