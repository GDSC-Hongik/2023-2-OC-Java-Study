import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        strings.add("hi");
        strings.add("my");
        strings.add("name");
        strings.add("is");
        strings.add("ohhamma");

        String target = "ohhamma";

        Predicate<String> contains = word -> word.equals(target);

        System.out.println("contains " + target + ": " + strings.stream().anyMatch(contains));
    }
}