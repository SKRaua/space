import java.time.LocalDate;

public abstract class PersonClass {
    protected int perCode;
    protected String name;
    protected boolean sex;
    protected LocalDate birthDate;

    public PersonClass(int perCode, String name, boolean sex, LocalDate birthDate) {
        this.perCode = perCode;
        this.name = name;
        this.sex = sex;
        this.birthDate = birthDate;
    }

    public int getperCode() {
        return perCode;
    }
    // ......

    public String toString() {
        return perCode + ": " + name;
    }

    public abstract String call();
}
