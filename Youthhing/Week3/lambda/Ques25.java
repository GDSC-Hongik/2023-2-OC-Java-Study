package modern.other.week3.lambda;

import java.util.function.Function;

public class Ques25 {
    //Integer -> binary
    public static void main(String[] args) {
        Function<Integer,String > function = Integer::toBinaryString;
        int n =18;
        System.out.println(function.apply(n));
    }
}
