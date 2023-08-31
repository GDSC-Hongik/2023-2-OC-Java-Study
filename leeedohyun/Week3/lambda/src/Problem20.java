import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// 무게 순으로 정렬
public class Problem20 {
    public static void main(String[] args) {
        List<Apple> apples = new ArrayList<>();
        apples.add(new Apple("Red", 20));
        apples.add(new Apple("Green", 15));
        apples.add(new Apple("Green", 30));
        apples.add(new Apple("Red", 10));
        apples.add(new Apple("Red", 25));

        List<Apple> sortedByWeight = apples.stream()
                .sorted(Comparator.comparingInt(x -> x.getWeight()))
                .collect(Collectors.toList());
        System.out.println("sortedByWeight = " + sortedByWeight);
    }
}

class Apple {

    String color;
    int weight;

    public Apple(String color, int weight) {
        this.color = color;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Apple{" +
                "color='" + color + '\'' +
                ", weight=" + weight +
                '}';
    }
}