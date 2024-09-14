import javax.swing.JButton;
import javax.swing.JFrame;

public class t1 {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JButton button1 = new JButton("1");
        JButton button2 = new JButton("2");
        JButton button3 = new JButton("3");
        JButton button4 = new JButton("4");
        JButton button5 = new JButton("5");
        button1.setBounds(10, 10, 10, 10);
        button2.setBounds(30, 10, 10, 10);
        button3.setBounds(50, 10, 10, 10);
        button4.setBounds(60, 10, 10, 10);
        button5.setBounds(80, 10, 10, 10);

        frame.getContentPane().add(button1);
        frame.getContentPane().add(button2);
        frame.getContentPane().add(button3);
        frame.getContentPane().add(button4);
        frame.getContentPane().add(button5);

        frame.pack();// 窗口自适应大小
        frame.setLocationRelativeTo(null);// 设置位置（到屏幕中间）
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
}
