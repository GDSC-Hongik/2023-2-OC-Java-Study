package modern.other.week3.lambda;

import java.util.function.BiFunction;

public class Ques10 {
    //두 스트링 연결?
    public static void main(String[] args) {
        BiFunction<String ,String,String> biFunction = (s1, s2)-> s1 + s2;

        System.out.println(biFunction.apply("Hype","Boy"));
    }
}
