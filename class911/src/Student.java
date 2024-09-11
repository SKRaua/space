import java.time.LocalDate;

public class Student extends PersonClass implements PersonInterface {
    private float grade;
    private String major;

    public Student(int perCode, String name, boolean sex, LocalDate birthDate, float grade, String major) {
        super(perCode, name, sex, birthDate);
        this.grade = grade;
        this.major = major;
    }

    public String toString() {
        return perCode + ": " + name + "同学";
    }

    @Override
    public String call() {
        return name + "同学";
    }

    @Override
    public String information() {
        return "学号" + perCode + major + "学院" + name + "同学分数为: " + grade;
    }

}
