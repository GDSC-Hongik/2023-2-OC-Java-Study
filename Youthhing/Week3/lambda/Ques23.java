package modern.other.week3.lambda;

import java.util.Arrays;
import java.util.List;

public class Ques23 {

    //list의 String length 평균
    public static void main(String[] args) {
        List<String> list = Arrays.asList("I","am","so","cool");

        double average = list.stream()
                .mapToInt(String::length)
                .average()
                .orElse(0);
        System.out.println("average = " + average);

    }

}
