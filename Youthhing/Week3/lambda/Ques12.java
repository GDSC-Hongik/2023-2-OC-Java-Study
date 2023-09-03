package modern.other.week3.lambda;

import java.util.Arrays;
import java.util.List;

public class Ques12 {
    //모든 수 곱하고 더하고
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1,2,3,4,5);

        int multiply = list.stream()
                .reduce(1, (x, y) -> x * y)
                .intValue();
        int sum = list.stream()
                .reduce(0,(x,y)->x+y)
                .intValue();
        System.out.println("multiply = " + multiply);
        System.out.println("sum = " + sum);
    }
}
