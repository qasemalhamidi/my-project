package hospitalsystem;

public class Nurse extends Employee {
    private String shift;

    public Nurse(String shift, double baseSalary, int id, String name, int age, String department) {
        super(baseSalary, id, name, age, department);
        this.shift = shift;
    }

    public String getShift()        { return shift; }
    public void setShift(String s)  { this.shift = s; }

    @Override public String getRole() { return "Nurse"; }

    @Override
    public double calculateSalary() {
        return baseSalary + (shift.equalsIgnoreCase("Night") ? baseSalary * 0.10 : 0);
    }

    @Override
    public void displayInfo() {
        System.out.println("Nurse | " + getName() + " | " + shift
                + " shift | Salary: " + String.format(java.util.Locale.US,"%.2f", calculateSalary()));
    }

    @Override
    public String toTableRow() {
        return getId() + " | " + getName() + " | Nurse | " + shift
                + " | - | " + String.format(java.util.Locale.US,"%.2f", calculateSalary());
    }
}
