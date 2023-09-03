package modern.other.week3.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class Ques6 {
    //Double List Average double -> double
    public static void main(String[] args) {
        List<Double> doubleList = Arrays.asList(4.5, 3.0,4.5,4.0,3.5);

//        Function<Double,Double> function =
//        OptionalDouble average =
        double average = doubleList.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        System.out.println("average = " + average);

    }
}
