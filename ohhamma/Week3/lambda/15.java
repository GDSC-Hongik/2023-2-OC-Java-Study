import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(5);
        numbers.add(3);
        numbers.add(2);
        numbers.add(4);

        int sumOdd = numbers.stream()
                .filter(n -> n % 2 == 1)
                .mapToInt(n -> n * n)
                .reduce(0, Integer::sum);

        int sumEven = numbers.stream()
                .filter(n -> n % 2 == 0)
                .mapToInt(n -> n * n)
                .reduce(0, Integer::sum);

        System.out.println("odd: " + sumOdd);
        System.out.println("even: " + sumEven);
    }
}