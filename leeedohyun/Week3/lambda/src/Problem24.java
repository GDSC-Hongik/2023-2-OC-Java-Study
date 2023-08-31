public class Problem24 {
    public static void main(String[] args) {
        System.out.println(findLargestPrimeFactor(34));
    }

    public static int findLargestPrimeFactor(int n) {
        for (int i = (int) Math.sqrt(n); i >= 2; i--) {
            if (isPrime(i) && n % i == 0)
                return i;
        }
        return n;
    }

    public static boolean isPrime(int n) {
        if (n <= 1)
            return false;

        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0)
                return false;
        }
        return true;
    }
}
