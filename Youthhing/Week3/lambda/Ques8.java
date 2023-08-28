package modern.other.week3.lambda;

import java.util.function.UnaryOperator;

public class Ques8 {
    // long -> long factorial
    public static void main(String[] args) {

        UnaryOperator<Long> factorial = n -> {
            long result = 1;
            for(int i=1;i<=n;i++){
                result*=i;
            }
            return result;
        };
        System.out.println(factorial.apply(5L));;
    }
}
