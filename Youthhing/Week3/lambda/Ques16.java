package modern.other.week3.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class Ques16 {

    //String이 특정 words를 가지고 있는지 확인 String -> Boolean?
    public static void main(String[] args) {
        List<String> testString = Arrays.asList("New","Jeans","Hype","Boy");

        String searchWord = "Hype";
//        Predicate<String> contain = str -> str.contains(searchWord);

        boolean answer = testString.stream()
                .anyMatch(str -> str.contains(searchWord));


        System.out.println(answer);

    }
}
