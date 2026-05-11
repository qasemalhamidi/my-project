package hospitalsystem;

public class Receptionist extends Employee {

    public Receptionist(double baseSalary, int id, String name, int age, String department) {
        super(baseSalary, id, name, age, department);
    }

    @Override public String getRole() { return "Receptionist"; }
    @Override public double calculateSalary() { return baseSalary; }

    @Override
    public void displayInfo() {
        System.out.println("Receptionist | " + getName()
                + " | Salary: " + String.format(java.util.Locale.US,"%.2f", calculateSalary()));
    }

    @Override
    public String toTableRow() {
        return getId() + " | " + getName() + " | Receptionist | - | - | "
                + String.format(java.util.Locale.US,"%.2f", calculateSalary());
    }
}
