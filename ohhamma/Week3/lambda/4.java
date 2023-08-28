import java.util.List;
import java.util.ArrayList;
import static java.util.stream.Collectors.toList;

public class Main {
  public static void main(String args[]) {
    List<Integer> numbers = new ArrayList<>();
    numbers.add(1);
    numbers.add(2);
    numbers.add(3);
    numbers.add(4);
    numbers.add(5);
    
    List<Integer> OddNumbers = numbers.stream()
      .filter(n -> n % 2 == 1)
      .collect(toList());
    System.out.println(OddNumbers);
    
    List<Integer> EvenNumbers = numbers.stream()
      .filter(n -> n % 2 == 0)
      .collect(toList());
    System.out.println(EvenNumbers);
  }
}