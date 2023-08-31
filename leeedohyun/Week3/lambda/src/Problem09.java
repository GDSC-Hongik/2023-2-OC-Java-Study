import java.util.function.Predicate;

public class Problem09 {
    public static void main(String[] args) {
        Predicate<Integer> prime = integer -> {
            if (integer <= 1)
                return false;

            for (int i = 2; i <= Math.sqrt(integer); i++) {
                if (integer % i == 0)
                    return false;
            }
            return true;
        };

        System.out.println(prime.test(4));  // false
        System.out.println(prime.test(5));  // true
    }
}
