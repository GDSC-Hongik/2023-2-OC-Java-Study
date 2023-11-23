import java.util.Arrays;
import java.util.List;

// List -> Integer
public class Problem17 {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("Modern", "Java", "In", "Action");

        int max = words.stream()
                .mapToInt(s -> s.length())
                .max()
                .orElse(0);
        System.out.println("max = " + max);

        int min = words.stream()
                .mapToInt(s -> s.length())
                .min()
                .orElse(0);
        System.out.println("min = " + min);
    }
}
