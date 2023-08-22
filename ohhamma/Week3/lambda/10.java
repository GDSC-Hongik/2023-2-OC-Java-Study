import java.util.function.BinaryOperator;

public class Main {
    public static void main(String[] args) {
        BinaryOperator<String> concat = (s1, s2) -> s1 + s2;
        
        String result = concat.apply("Hello ", "Java");
        System.out.println(result);
    }
}