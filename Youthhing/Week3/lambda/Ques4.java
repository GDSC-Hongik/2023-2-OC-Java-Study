package modern.other.week3.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Ques4 {
    //filter even or odd number
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,7,8,9,10);

        List<Integer> evenNumber = numbers.stream()
                .filter(num -> num % 2 == 0)
                .collect(toList());

        List<Integer> oddNumbers = numbers.stream()
                .filter(num -> num % 2 == 1)
                .collect(toList());
        System.out.println("evenNumber = " + evenNumber);
        System.out.println("oddNumbers = " + oddNumbers);
    }
}
