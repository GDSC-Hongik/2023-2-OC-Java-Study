import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Problem07 {
    public static void main(String[] args) {
        Arrays.stream(new int[] {1, 2, 3, 4, 5, 2, 2, 5, 9, 10})
                .distinct()
                .forEach(System.out::println);
    }
}
