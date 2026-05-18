package hospitalsystem;

public abstract class Person {
    private final int id;
    private String name;
    private int age;

    protected Person(int id, String name, int age) {
        this.id = id; this.name = name; this.age = age;
    }

    public final int getId()        { return id; }
    public String getName()         { return name; }
    public int getAge()             { return age; }
    public void setName(String n)   { this.name = n; }
    public void setAge(int a)       { this.age = a; }

    public abstract void displayInfo();
    public abstract String toTableRow();
}
