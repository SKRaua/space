import java.time.LocalDate;

public class Test {
    public static void main(String[] args) {
        
        PersonInterface zhang = new Teacher(2022214986, "张三", true, LocalDate.of(2004, 8, 24), "国际学院", "辅导员", 9999);
        System.out.println(zhang.toString());
        System.out.println(zhang.call());
        System.out.println(zhang.information());

        Student li = new Student(2022214987, "李四", false, LocalDate.of(2004, 10, 24), 100, "国际学院");
        System.out.println(li.toString());
        System.out.println(li.call());
        System.out.println(li.information());
    }
}
