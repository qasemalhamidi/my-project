package hospitalsystem;

import java.util.ArrayList;
import java.util.List;

public class Receptionist extends Employee {

    public Receptionist(double baseSalary, int id, String name, int age, String department) {
        super(baseSalary, id, name, age, department);
    }

    @Override
    public String getRole() { return "Receptionist"; }

    @Override
    public double calculateSalary() { return baseSalary; }

    @Override
    public void displayInfo() {
        System.out.println("Receptionist | ID=" + getId() + ", Name=" + getName());
    }

    @Override
    public String toTableRow() {
        return getId() + " | " + getName() + " | Receptionist | - | - | "
                + String.format("%.2f", calculateSalary());
    }
}
