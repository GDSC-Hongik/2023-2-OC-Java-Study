import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(5);
        numbers.add(3);
        numbers.add(2);
        numbers.add(4);

        Optional<Integer> maxNum = numbers.stream()
                .max((n1, n2) -> Integer.compare(n1, n2));

        Optional<Integer> minNum = numbers.stream()
                .min((n1, n2) -> Integer.compare(n1, n2));

        System.out.println(maxNum.orElse(null));
        System.out.println(minNum.orElse(null));
    }
}