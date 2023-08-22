import java.util.function.Predicate;

// 문자열이 비었는 확인
public class Problem02 {
    public static void main(String[] args) {
        Predicate<String> predicate = s -> s.isEmpty();

        System.out.println(predicate.test(""));
        System.out.println(predicate.test("hello"));
    }
}
