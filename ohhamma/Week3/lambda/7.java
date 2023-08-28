import java.util.List;
import java.util.ArrayList;
import static java.util.stream.Collectors.toList;

public class Main {
  public static void main(String args[]) {
    List<Integer> numbers = new ArrayList<>();
    numbers.add(1);
    numbers.add(1);
    numbers.add(2);
    numbers.add(2);
    numbers.add(3);
    
    List<Integer> RemoveDup = numbers.stream()
                  .distinct()
                  .collect(toList());

    System.out.println(RemoveDup);
  }
}