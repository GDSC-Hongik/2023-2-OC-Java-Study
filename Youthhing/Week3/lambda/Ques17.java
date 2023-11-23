package modern.other.week3.lambda;

import java.util.Arrays;
import java.util.List;

public class Ques17 {
    //find the length of Longest and Smallest String
    public static void main(String[] args) {
        List<String> testString = Arrays.asList("New","Jeans","Hype","Boy");

        int maxValue = testString.stream()
                .mapToInt(str -> str.length())
                .max()
                .orElse(0);
        System.out.println("maxValue = " + maxValue);
        int minValue = testString.stream()
                .mapToInt(String::length)
                .min()
                .orElse(0);
        System.out.println("minValue = " + minValue);

    }
}
