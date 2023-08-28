import java.util.function.IntUnaryOperator;

public class Main {
    public static void main(String[] args) {
        IntUnaryOperator factorial = n -> {
            if (n == 0 || n == 1) {
                return 1;
            }
            else {
                int result = 1;
                for (int i = 2; i <= n; i++) {
                    result *= i;
                }
                return result;
            }
        };

        int number = 5;
        int result = factorial.applyAsInt(number);
        System.out.println(number + "! = " + result);
    }
}