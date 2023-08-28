import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class Main {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(5);
        numbers.add(3);
        numbers.add(2);
        numbers.add(4);

        int secondLargest = numbers.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(1)
                .findFirst()
                .orElse(null);

        int secondSmallest = numbers.stream()
                .distinct()
                .sorted()
                .skip(1)
                .findFirst()
                .orElse(null);

        System.out.println(secondLargest);
        System.out.println(secondSmallest);
    }
}