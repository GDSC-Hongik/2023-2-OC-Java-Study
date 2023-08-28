import java.util.function.BinaryOperator;

public class Main {
  public static void main(String args[]) {
    BinaryOperator<Integer> sum = (i1, i2) -> i1 + i2;
    System.out.println("3 + 4 = " + sum.apply(3, 4));
  }
}