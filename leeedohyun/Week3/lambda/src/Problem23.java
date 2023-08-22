import java.util.Arrays;
import java.util.List;

// List -> Integer
public class Problem23 {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("Modern", "In", "Java", "Action");

        double average = words.stream()
                .mapToInt(String::length)
                .average()
                .orElse(0);
        System.out.println("average = " + average);
    }
}
