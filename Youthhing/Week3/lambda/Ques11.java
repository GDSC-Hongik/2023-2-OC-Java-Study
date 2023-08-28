package modern.other.week3.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Ques11 {

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1,2,3,4,5);

        Integer max = list.stream()
                .max((o1, o2) -> o1.compareTo(o2))
                .orElse(9999999);

        Integer min = list.stream()
                .min((o1, o2) -> o1.compareTo(o2))
                .orElse(-99999999);
        System.out.println("max = " + max);
        System.out.println("min = " + min);
    }
}
