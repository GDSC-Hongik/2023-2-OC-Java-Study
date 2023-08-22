import java.util.List;
import java.util.ArrayList;
import static java.util.stream.Collectors.toList;

public class Main {
  public static void main(String args[]) {
    List<String> strings = new ArrayList<>();
    strings.add("Hello");
    strings.add("Java");
    strings.add("Lambda");
    strings.add("Practice");
    
    List<String> UpperCaseStrings = strings.stream()
      .map(s -> s.toUpperCase())
      .collect(toList());
    System.out.println(UpperCaseStrings);
    
    List<String> LowerCaseStrings = strings.stream()
      .map(s -> s.toLowerCase())
      .collect(toList());
    System.out.println(LowerCaseStrings);
  }
}