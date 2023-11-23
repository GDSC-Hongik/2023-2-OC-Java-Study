import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Problem15 {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<Integer> evenNumbers = numbers.stream()
                .filter(x -> x % 2 == 0)
                .map(x -> x * x)
                .collect(Collectors.toList());
        System.out.println("evenNumbers = " + evenNumbers);

        List<Integer> oddNumbers = numbers.stream()
                .filter(x -> x % 2 != 0)
                .map(x -> x * x)
                .collect(Collectors.toList());
        System.out.println("oddNumbers = " + oddNumbers);
    }
}
