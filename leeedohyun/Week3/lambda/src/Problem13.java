import java.util.function.Function;

// String -> int
public class Problem13 {
    public static void main(String[] args) {
        Function<String, Integer> count = s -> {
            String[] words = s.split(" ");
            return words.length;
        };

        System.out.println(count.apply("hello world"));
    }
}
