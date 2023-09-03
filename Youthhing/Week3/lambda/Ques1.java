package modern.other.week3.lambda;

public class Ques1 {
    public static void main(String[] args) {
        SumTwoInt sumTwoInt = (x,y)->x+y;
        int result = sumTwoInt.sum(10,4);
        System.out.println("result = " + result);
    }

    @FunctionalInterface
    interface SumTwoInt{
        int sum(int a,int b);
    }
}
