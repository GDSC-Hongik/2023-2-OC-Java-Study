import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        strings.add("Hello");
        strings.add("Java");
        strings.add("Lambda");
        strings.add("Practice");

        int longest = strings.stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);
        
        int shortest = strings.stream()
                .mapToInt(String::length)
                .min()
                .orElse(0);

        System.out.println("longest: " + longest);
        System.out.println("shortest: " + shortest);
    }
}