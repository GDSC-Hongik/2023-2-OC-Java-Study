import java.util.List;
import java.util.ArrayList;

public class Main {
  public static void main(String args[]) {
    List<String> strings = new ArrayList<>();
    strings.add("Orange");
    strings.add("Grapes");
    strings.add("Banana");
    strings.add("Apple");
    
    strings.sort((s1, s2) -> s1.compareTo(s2));
    System.out.println(strings);
  }
}