import java.util.function.Predicate;

// Integer -> boolean
public class Problem18 {
    public static void main(String[] args) {
        Predicate<Integer> predicate = x -> {
            int tmp = (int) Math.sqrt(x);
            return tmp * tmp == x;
        };

        System.out.println(predicate.test(4));  // true
        System.out.println(predicate.test(5));  // false
    }
}
