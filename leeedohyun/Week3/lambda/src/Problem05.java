import java.util.Arrays;
import java.util.List;

public class Problem05 {
    public static void main(String[] args) {
        List<String> list = Arrays.asList("hello", "world", "java", "lambda");

        list.sort((s1, s2) -> s1.compareTo(s2));

        System.out.println(list);
    }
}
