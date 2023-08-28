package modern.other.week3.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ques7 {

    //duplicate int remove
    public static void main(String[] args) {
        List<Integer> integers = Arrays.asList(1,1,2,3,4,4,5,6,6,7,7,7);

        List<Integer> result = new ArrayList<>();
        integers.stream()
                .distinct()
                .forEach((i)->result.add(i));
        System.out.println(result);
    }
}
