import java.util.function.Function;

public class Problem25 {
    public static void main(String[] args) {
        Function<Integer, String> function = x -> Integer.toBinaryString(x);

        System.out.println(function.apply(4));
    }
}
