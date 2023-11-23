package modern.other.week3.lambda;

import java.util.function.Predicate;

public class Ques18 {
    //주어진 수가 완전 제곱인지? Integer -> boolean
    public static void main(String[] args) {
        Predicate<Integer> isPerfectSquare = num -> {
            int sqrt = (int) Math.sqrt(num);
            return sqrt*sqrt == num;
        };
        System.out.println(isPerfectSquare.test(36));
        System.out.println(isPerfectSquare.test(44));

    }
}
