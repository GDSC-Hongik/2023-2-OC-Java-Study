import java.util.function.DoubleToIntFunction;

public class Problem08 {
    public static void main(String[] args) {
        DoubleToIntFunction doubleToIntFunction = n -> {
            int factorial = 1;
            for (int i = 1; i <= n; i++) {
                factorial *= i;
            }
            return factorial;
        };

        System.out.println(doubleToIntFunction.applyAsInt(4));
    }
}
