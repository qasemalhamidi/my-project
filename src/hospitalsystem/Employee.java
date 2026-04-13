package hospitalsystem;

public abstract class Employee extends Person implements SalaryCalculator {
    protected double baseSalary;
    protected String department;

    protected Employee(double baseSalary, int id, String name, int age, String department) {
        super(id, name, age);
        this.baseSalary = baseSalary;
        this.department = department;
    }

    public double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public abstract String getRole();
}
