import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        int start = 1;
        int end = 10;

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

        int primeSum = IntStream.rangeClosed(start, end)
                .filter(isPrime)
                .sum();

        System.out.println(primeSum);
    }
}
