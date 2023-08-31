import java.util.Arrays;
import java.util.List;

public class Problem11 {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(2, 3, 1, 9, 10, 8, 5);

        Integer max = numbers.stream()
                .max(Integer::compareTo)
                .orElse(0);

        System.out.println(max);

        Integer min = numbers.stream()
                .min(Integer::compareTo)
                .orElse(0);

        System.out.println(min);
    }
}
