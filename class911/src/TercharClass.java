public class TercharClass extends PersonClass implements PersonInterface {
    private String department;
    private String title;
    private double salary;

    public TercharClass(String id, String name, String gender, String birthDate, String department, String title,
            double salary) {
        super(id, name, gender, birthDate);
        this.department = department;
        this.title = title;
        this.salary = salary;
    }

    // 重写父类中方法
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("部门: " + department);
        System.out.println("职称: " + title);
        System.out.println("工资: " + salary);
    }

    // 重写父类中抽象方法
    @Override
    public String getRole() {
        return "教师";
    }

    // 实现接口
    @Override
    public String callInterface() {
        return name + "老师";
    }
}
