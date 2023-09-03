package modern.other.week3.lambda;

import java.util.function.Predicate;

public class Ques14 {
    //팰린드롬인지 아닌지 알려달라고요? String -> boolean Predicate?
    public static void main(String[] args) {
        String pWords = "AMAMDADMAMA";
        String npWords = "HypeBoy";

        Predicate<String> isPellidrome = str ->{
            String reversed = new StringBuilder(str).reverse().toString();
            return str.equals(reversed);
        };

        System.out.println(isPellidrome.test(pWords));
        System.out.println(isPellidrome.test(npWords));
    }
}
