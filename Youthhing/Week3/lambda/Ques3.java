package modern.other.week3.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;


public class Ques3 {
    //List<String> --> void?
    public static void main(String[] args) {
        List<String> list = Arrays.asList("AbC","dEf","GhI","jKl");

        list.replaceAll(str -> str.toLowerCase());
//        Consumer<String> toLow = str -> str.toLowerCase();
//        for(String s : list){
//            toLow.accept(s);
//        }

        System.out.println("lower "+list);

        list.replaceAll(str -> str.toUpperCase());
        System.out.println("upper "+ list);
    }
}
