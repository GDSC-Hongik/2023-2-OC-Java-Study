import java.util.Arrays;
import java.util.Comparator;

public class Problem19 {
    // 정렬 -> skip
    public static void main(String[] args) {
        Integer[] numbers = {10, 3, 4, 21, 15, 29, 8};

        Integer secondSmallest = Arrays.stream(numbers)
                .sorted()
                .skip(1)
                .findFirst()
                .orElse(null);

        System.out.println("secondSmallest = " + secondSmallest);

        Integer secondLargest = Arrays.stream(numbers)
                .sorted(Comparator.reverseOrder())
                .skip(1)
                .findFirst()
                .orElse(null);

        System.out.println("secondLargest = " + secondLargest);
    }
}
