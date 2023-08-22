import java.util.function.BiFunction;

public class Problem10 {
    public static void main(String[] args) {
        BiFunction<String, String, String> concatenatedString = (s1, s2) -> s1 + s2;

        System.out.println(concatenatedString.apply("hello ", "world"));    // hello world
    }
}
