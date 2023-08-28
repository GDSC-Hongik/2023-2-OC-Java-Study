import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        strings.add("hello");
        strings.add("java");
        strings.add("lambda");
        strings.add("practice");

        double avgLength = strings.stream()
                .mapToInt(String::length)
                .average()
                .orElse(0);
        
        System.out.println(avgLength);
    }
}