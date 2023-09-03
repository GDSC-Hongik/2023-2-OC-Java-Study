package modern.other.week3.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Ques11 {

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1,2,3,4,5);

        Integer max = list.stream()
                .reduce(0,Integer::max);
        Integer min = list.stream()
                        .reduce(0,Integer::min);

        System.out.println("max = " + max);
        System.out.println("min = " + min);
    }
}
