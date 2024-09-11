import java.util.Arrays;

public class App {
    public static void main(String[] args) throws Exception {
        int[] a = new int[11];
        a[0] = 2;
        a[1] = 2;
        int t = 2;
        while (t <= 10) {
            a[t] = a[t - 1] * 2 - 8;
            a[0] += a[t];
            t++;
        }

        int[] b = new int[11];
        b[0] = 2;
        b[1] = 2;
        for (int i = 2; i <= 10; i++) {
            b[i] = b[i - 1] * 2 - 8;
            b[0] += b[i];
        }

        System.out.println(Arrays.toString(a));
        System.out.println(Arrays.toString(b));
    }
}
