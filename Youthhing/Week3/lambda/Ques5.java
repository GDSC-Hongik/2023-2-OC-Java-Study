package modern.other.week3.lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Ques5 {
    //알파벳 순서로 정렬
    public static void main(String[] args) {
        List<String> words = Arrays.asList("Ya","Nol","Ja","Ga","Go","Sip","Da");

        words.sort(String::compareTo);
        System.out.println(words);
    }
}
