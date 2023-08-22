import java.util.List;
import java.util.ArrayList;

public class Main {
  public static void main(String args[]) {
    List<Double> numbers = new ArrayList<>();
    numbers.add(1.0);
    numbers.add(1.5);
    numbers.add(2.0);
    numbers.add(2.5);
    numbers.add(3.0);
    
    double average = numbers.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

    System.out.println("Average: " + average);
  }
}