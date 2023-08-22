import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Problem22 {
    public static void main(String[] args) {
        // 섞인 경우
        List<String> words = Arrays.asList("Modern", "JAVA", "in", "Action");
        System.out.println("======mixed=======");
        boolean isAllUppercase = checkCase(words, s -> s.equals(s.toUpperCase()));
        System.out.println("Is all uppercase? " + isAllUppercase);

        boolean isAllLowercase = checkCase(words, s -> s.equals(s.toLowerCase()));
        System.out.println("Is all lowercase? " + isAllLowercase);

        boolean isMixedCase = !isAllUppercase && !isAllLowercase;
        System.out.println("Is mixed case? " + isMixedCase);

        // 대문자
        List<String> words1 = Arrays.asList("MODERN", "JAVA", "IN" , "ACTION");
        System.out.println("======upper case=======");
        isAllUppercase = checkCase(words1, s -> s.equals(s.toUpperCase()));
        System.out.println("Is all uppercase? " + isAllUppercase);

        isAllLowercase = checkCase(words1, s -> s.equals(s.toLowerCase()));
        System.out.println("Is all lowercase? " + isAllLowercase);

        isMixedCase = !isAllUppercase && !isAllLowercase;
        System.out.println("Is mixed case? " + isMixedCase);

        // 소문자
        List<String> words2 = Arrays.asList("modern", "java", "in", "action");
        System.out.println("======lower case=======");
        isAllUppercase = checkCase(words2, s -> s.equals(s.toUpperCase()));
        System.out.println("Is all uppercase? " + isAllUppercase);

        isAllLowercase = checkCase(words2, s -> s.equals(s.toLowerCase()));
        System.out.println("Is all lowercase? " + isAllLowercase);

        isMixedCase = !isAllUppercase && !isAllLowercase;
        System.out.println("Is mixed case? " + isMixedCase);

    }
    public static boolean checkCase(List<String> strings, Predicate<String> checkFunction) {
        return strings.stream()
                .allMatch(s -> checkFunction.test(s));
    }
}
