import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// List -> List
public class Problem03 {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("Modern", "In", "Java", "Action");

        List<String> upperCase = words.stream()
                .map(s -> s.toUpperCase())
                .collect(Collectors.toList());
        System.out.println("words = " + upperCase);

        List<String> lowerCase = words.stream()
                .map(s -> s.toLowerCase())
                .collect(Collectors.toList());
        System.out.println("words = " + lowerCase);
    }
}
