public class Test {
    public static void main(String[] args) {

        // PersonClass zhang0 = new PersonClass("0001",
        // "张三","女","1980-09-22");//无法再实例抽象类

        // 测试父类引用子类对象
        System.out.println("\n>>>测试父类引用子类对象：");
        PersonClass zhang1 = new TercharClass("0001", "张三", "女", "1980-09-22", "国际学院", "辅导员", 8999);
        PersonClass li1 = new StudentClass("0002", "李四", "男", "2000-01-01", 580.5, "软件工程");
        System.out.println(">教师信息：");
        zhang1.displayInfo();// 将调用子类重写的方法
        System.out.println("角色：" + zhang1.getRole());// 子类重写的抽象方法
        System.out.println("教师编号：" + zhang1.getID());// 父类方法
        System.out.println(">学生信息：");
        li1.displayInfo();
        System.out.println("角色：" + li1.getRole());

        // 测试子类对象引用
        System.out.println("\n>>>测试子类对象引用：");
        TercharClass zhang2 = new TercharClass("0001", "张三", "女", "1980-09-22", "国际学院", "辅导员", 8999);
        StudentClass li2 = new StudentClass("0002", "李四", "男", "2000-01-01", 580.5, "软件工程");
        System.out.println(">教师信息：");
        zhang2.displayInfo();// 将调用子类重写的方法
        System.out.println("角色：" + zhang2.getRole());// 子类重写的抽象方法
        System.out.println("教师编号：" + zhang2.getID());// 父类方法
        System.out.println(">学生信息：");
        li2.displayInfo();
        System.out.println("角色：" + li2.getRole());

        // 测试接口引用子类对象
        System.out.println("\n>>>测试接口引用子类对象：");
        PersonInterface zhang3 = new TercharClass("0001", "张三", "女", "1980-09-22", "国际学院", "辅导员", 8999);
        PersonInterface li3 = new StudentClass("0002", "李四", "男", "2000-01-01", 580.5, "软件工程");
        System.out.println("教师称呼：" + zhang3.callInterface());// 实现的接口方法
        System.out.println("学生称呼：" + li3.callInterface());// 实现的接口方法

        // 接口引用将无法调用子类重写父类的方法以及父类的方法
        // zhang3.displayInfo();// 子类重写的方法
        // System.out.println("角色：" + zhang3.getRole());// 子类重写的抽象方法
        // System.out.println("教师编号：" + zhang3.getID());// 父类方法
    }
}
