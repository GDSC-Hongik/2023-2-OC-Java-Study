import java.util.function.IntPredicate;

public class Main {
    public static void main(String[] args) {
        int number = 32;

        IntPredicate isPrime = num -> {
            if (num <= 1) {
                return false;
            }
            for (int i = 2; i <= Math.sqrt(num); i++) {
                if (num % i == 0) {
                    return false;
                }
            }
            return true;
        };

        int largestPrime = 0;
        for (int i = 2; i <= number; i++) {
            if (number % i == 0 && isPrime.test(i)) {
                largestPrime = i;
            }
        }

        System.out.println(largestPrime);
    }
}
