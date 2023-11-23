package modern.other.week3.lambda;

import java.util.function.Predicate;

public class Ques2 {

    //String --> Boolean
    public static void main(String[] args) {
        String emptyString = "";
        String nonEmpty ="Hi Java";
        Predicate<String> isEmptyString = String::isEmpty;
        System.out.println("isEmptyString.test(emptyString) = " + isEmptyString.test(emptyString));
        System.out.println("isEmptyString.test(nonEmpty) = " + isEmptyString.test(nonEmpty));
    }

//    static Boolean isEmptyString(String)
}
