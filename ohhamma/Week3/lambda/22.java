import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        List<String> upperStrings = new ArrayList<>();
        upperStrings.add("HELLO");
        upperStrings.add("JAVA");

        List<String> lowerStrings = new ArrayList<>();
        lowerStrings.add("hello");
        lowerStrings.add("java");

        List<String> mixedStrings = new ArrayList<>();
        mixedStrings.add("hello");
        mixedStrings.add("JAVA");

        Predicate<String> isUpper = str -> str.equals(str.toUpperCase());
        Predicate<String> isLower = str -> str.equals(str.toLowerCase());

        System.out.println("isUpper: " + upperStrings.stream().allMatch(isUpper));
        System.out.println("isLower: " + lowerStrings.stream().allMatch(isLower));
        
        if (!mixedStrings.stream().allMatch(isUpper) && !mixedStrings.stream().allMatch(isLower)) {
            System.out.println("isMixed: true");
        } else {
            System.out.println("isMixed: false");
        }
    }
}