import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Problem07 {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 2, 2, 5, 9, 10);

        List<Integer> collect = numbers.stream()
                .distinct()
                .collect(Collectors.toList());

        System.out.println(collect);
    }
}
