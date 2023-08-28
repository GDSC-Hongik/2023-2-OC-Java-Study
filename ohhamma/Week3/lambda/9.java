import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        Predicate<Integer> isPrime = n -> {
            if (n <= 1) {
                return false;
            }
            for (int i = 2; i <= Math.sqrt(n); i++) {
                if (n % i == 0) {
                    return false;
                }
            }
            return true;
        };

        int number = 13;
        boolean result = isPrime.test(number);
        System.out.println(number + " is prime: " + result);
    }
}