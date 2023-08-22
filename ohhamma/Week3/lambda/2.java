import java.util.function.Predicate;

public class Main {
  public static void main(String args[]) {
    Predicate<String> isEmptyString = s -> s.isEmpty();
    System.out.println("empty string test: " + isEmptyString.test(""));
  }
}