package modern.other.week3.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Ques21 {
    //주어진 범위에서 모든 소수를 찾고 그 소수의 제곱합을 구하라.
    public static void main(String[] args) {
        int startNum = 10;
        int endNum = 200;

        List<Integer> result = new ArrayList<>();

        int sumOfPrimes = IntStream.rangeClosed(startNum,endNum)
                .filter(Ques21::isPrime)
                .sum();
        System.out.println(sumOfPrimes);
    }

    public static boolean isPrime(int n){

        if(n <= 1){
            return false;
        }
        for(int i = 2; i <= Math.sqrt(n); i++){
            if(n % i == 0){
                return false;
            }
        }
        return true;


    }
}
