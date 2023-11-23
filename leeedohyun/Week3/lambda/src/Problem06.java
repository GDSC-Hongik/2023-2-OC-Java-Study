import java.util.Arrays;
import java.util.List;

public class Problem06 {
    public static void main(String[] args) {
        List<Double> numbers = Arrays.asList(1.3, 4.5, 1.1, 5.4);

        Double average = numbers.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        System.out.println(average);
    }
}
