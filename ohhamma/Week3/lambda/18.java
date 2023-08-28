import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        Predicate<Integer> isSquare = num -> {
            int sqrt = (int)Math.sqrt(num);
            return sqrt * sqrt == num;
        };

        int num1 = 25;
        int num2 = 30;

        System.out.println(num1 + ": " + isSquare.test(num1));
        System.out.println(num2 + ": " + isSquare.test(num2));
    }
}
