import java.util.function.BiFunction;

// 두 정수의 합
public class Problem01 {
    public static void main(String[] args) {
        BiFunction<Integer, Integer, Integer> biFunction = (a, b) -> a + b;
        System.out.println(biFunction.apply(3, 5));
        System.out.println(biFunction.apply(-1, -4));
    }
}
