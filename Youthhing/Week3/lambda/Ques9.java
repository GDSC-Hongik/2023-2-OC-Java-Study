package modern.other.week3.lambda;

import java.util.function.Predicate;

public class Ques9 {
    //Check Integer is Prime or not 아 이거 알고리즘 뭐였더라
    public static void main(String[] args) {
        Predicate<Integer> p = n ->{
            if(n <= 1){
                return false;
            }
            for(int i = 2; i <= Math.sqrt(n); i++){
                if(n % i == 0){
                    return false;
                }
            }
            return true;

        };


        int tmp = 17;
        p.test(tmp);
        System.out.println(p.test(tmp));
//        System.out.println(p2.test(tmp));

        int tmp2 = 28;
        System.out.println(p.test(tmp2));
//        System.out.println(p2.test(tmp2));
    }
}
