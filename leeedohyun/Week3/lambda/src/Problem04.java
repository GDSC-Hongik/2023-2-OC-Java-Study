import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// 홀수, 짝수 필터링
public class Problem04 {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // 홀수
        List<Integer> oddNumbers = numbers.stream()
                .filter(x -> x % 2 != 0)
                .collect(Collectors.toList());

        System.out.println("oddNumbers = " + oddNumbers);

        // 짝수
        List<Integer> evenNumbers = numbers.stream()
                .filter(x -> x % 2 == 0)
                .collect(Collectors.toList());

        System.out.println("evenNumbers = " + evenNumbers);
    }
}
