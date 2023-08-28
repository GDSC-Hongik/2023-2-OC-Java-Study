import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        int number = 7;

        Function<Integer, String> toBinary = num -> Integer.toBinaryString(num);

        System.out.println(number + " -> " + toBinary.apply(number));
    }
}
