import java.util.function.BiFunction;
import java.util.function.Predicate;

// (Integer, Integer) -> Integer
public class Problem21 {
    public static void main(String[] args) {
        Predicate<Integer> predicate = x -> {
            if (x <= 1)
                return false;

            for (int i = 2; i <= Math.sqrt(x); i++) {
                if (x % i == 0)
                    return false;
            }
            return true;
        };

        BiFunction<Integer, Integer, Integer> biFunction = (a, b) -> {
            int sum = 0;
            for (int i = a; i <= b; i++) {
                if (predicate.test(i)) {
                    sum += i;
                }
            }
            return sum;
        };

        System.out.println("biFunction = " + biFunction.apply(100, 200));
    }
}