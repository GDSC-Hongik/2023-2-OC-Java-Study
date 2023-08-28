package modern.other.week3.lambda;

import java.util.function.Function;

public class Ques13 {
    //count words String -> Integer
    public static void main(String[] args) {
        String content = "I remember I remember When I remember";

//        content.split("\\s+")
        Function<String,Integer> function = str -> {
            return str.split("\\s+").length;
        };

        System.out.println(function.apply(content));


    }

}
