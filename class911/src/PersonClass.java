public abstract class PersonClass {
    protected String id;
    protected String name;
    protected String gender;
    protected String birthDate;

    public PersonClass(String id, String name, String gender, String birthDate) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public void displayInfo() {
        System.out.println("编号: " + id);
        System.out.println("姓名: " + name);
        System.out.println("性别: " + gender);
        System.out.println("出生日期: " + birthDate);
    }

    // 抽象方法
    public abstract String getRole();
}
