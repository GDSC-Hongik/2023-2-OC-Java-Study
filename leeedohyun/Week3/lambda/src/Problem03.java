import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// List -> List
public class Problem03 {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("Modern", "In", "Java", "Action");

        words.stream()
                .map(s -> s.toUpperCase())
                .forEach(System.out::println);

        words.stream()
                .map(s -> s.toLowerCase())
                .forEach(System.out::println);
    }
}
