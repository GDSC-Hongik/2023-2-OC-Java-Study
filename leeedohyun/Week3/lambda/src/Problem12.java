import java.util.Arrays;
import java.util.List;

public class Problem12 {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        double sum = numbers.stream()
                .mapToDouble(Integer::doubleValue)
                .sum();

        System.out.println("sum = " + sum);

        double multiply = numbers.stream()
                .reduce(1, (x, y) -> x * y)
                .doubleValue();

        System.out.println("multiply = " + multiply);
    }
}
