package hospitalsystem;

public abstract class Person {
    private final int id;
    private String name;
    private int age;

    protected Person(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public final int getId() { return id; }
    public final String getName() { return name; }
    public int getAge() { return age; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }

    public abstract void displayInfo();
    public abstract String toTableRow();
}
