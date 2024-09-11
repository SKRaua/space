import java.time.LocalDate;

public class Teacher extends PersonClass implements PersonInterface {

    private String department;
    private String job;
    private double salary;

    public Teacher(int perCode, String name, boolean sex, LocalDate birthDate,
            String department, String job, double salary) {
        super(perCode, name, sex, birthDate);
        this.department = department;
        this.job = job;
        this.salary = salary;
    }

    public String toString() {
        return perCode + ": " + name + "老师";
    }

    @Override
    public String call() {
        return name + "老师";
    }

    @Override
    public String information() {
        return "编号" + perCode + department + job + name + "老师的薪资为" + salary;
    }

}
