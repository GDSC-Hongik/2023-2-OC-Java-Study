import java.util.function.Predicate;

public class Problem14 {
    public static void main(String[] args) {
        Predicate<String> palindrome = s -> {
            return new StringBuilder(s).reverse().toString().equals(s);
        };

        System.out.println(palindrome.test("test"));    // false
        System.out.println(palindrome.test("wow"));     // true
    }
}
