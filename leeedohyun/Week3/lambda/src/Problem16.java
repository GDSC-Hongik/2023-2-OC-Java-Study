import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Problem16 {
    public static void main(String[] args) {
        List<String> numbers = Arrays.asList("Modern", "In", "Java", "Action");

        Predicate<String> predicate = s -> numbers.contains(s);

        System.out.println(predicate.test("Java"));     // true
        System.out.println(predicate.test("modern"));   // false
    }
}
