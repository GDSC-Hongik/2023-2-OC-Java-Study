import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        String str1 = "abcba";
        String str2 = "aab";

        Predicate<String> isPalindrome = str -> {
            String reversed = new StringBuilder(str).reverse().toString();
            return str.equals(reversed);
        };

        System.out.println(str1 + ": " + isPalindrome.test(str1));
        System.out.println(str2 + ": " + isPalindrome.test(str2));
    }
}
