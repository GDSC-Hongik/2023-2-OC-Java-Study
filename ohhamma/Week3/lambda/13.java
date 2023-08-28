import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        String sentence = "Hello Java Lambda Practice";

        Function<String, Integer> wordCount = str -> {
            String[] words = str.split(" ");
            return words.length;
        };

        System.out.println(wordCount.apply(sentence));
    }
}
