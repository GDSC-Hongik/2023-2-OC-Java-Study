package modern.other.week3.lambda;

public class Ques24 {
    //가장 큰 소인수?
    public static void main(String[] args) {
        int n = 176;
        System.out.println(findLargestPrimeFactor(n));

    }

    public static long findLargestPrimeFactor(int n){
        for(long i = (long)Math.sqrt(n);i>=2;i--){
            if(n%i == 0 && isPrime(i)){
                return i;
            }
        }
        return n;
    }

    public static boolean isPrime(long n){

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
