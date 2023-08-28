package modern.other.week3.lambda;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Ques15 {
    //짝수와 홀수의 제곱합을 구하자
    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(1,2,3,4,5,6);

        int evenSum = nums.stream()
                .filter(n -> n % 2 == 0)
                .mapToInt(n -> n * n)
                .sum();

        int oddSum = nums.stream()
                .filter(n -> n % 2 == 1)
                .mapToInt(n -> n * n)
                .sum();
        System.out.println("evenSum = " + evenSum);
        System.out.println("oddSum = " + oddSum);
    }
}
