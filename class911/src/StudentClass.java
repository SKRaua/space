public class StudentClass extends PersonClass implements PersonInterface {
    private double score;
    private String major;

    public StudentClass(String id, String name, String gender, String birthDate, double score,
            String major) {
        super(id, name, gender, birthDate);
        this.score = score;
        this.major = major;
    }

    // 重写父类中方法
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("高考成绩: " + score);
        System.out.println("专业: " + major);
    }

    // 重写父类中抽象方法
    @Override
    public String getRole() {
        return "学生";
    }

    // 实现接口
    @Override
    public String callInterface() {
        return name + "同学";
    }
}