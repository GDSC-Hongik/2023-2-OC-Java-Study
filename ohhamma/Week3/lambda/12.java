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

        int product = numbers.stream()
                .reduce(1, (n1, n2) -> n1 * n2);

        int sum = numbers.stream()
                .reduce(0, (n1, n2) -> n1 + n2);

        System.out.println(product);
        System.out.println(sum);
    }
}