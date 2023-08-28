package modern.other.week3.lambda;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Ques19 {

    //find Second Largest and Second Smallest
    public static void main(String[] args) {
        List<Integer> numberList = Arrays.asList(1,2,3,4,5);

        Integer first = numberList.stream()
                .sorted()
                .skip(1)
                .findFirst()
                .orElse(null);

        System.out.println(first);

        Integer secondLarge = numberList.stream()
                .sorted(Comparator.reverseOrder())
                .skip(1)
                .findFirst()
                .orElse(null);
        System.out.println(secondLarge);
    }
}
