import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }

    @Override
    public String toString() {
        return "{" + "name: " + name + ", age:" + age + "}";
    }
}

public class Main {
    public static void main(String[] args) {
        List<Person> people = new ArrayList<>();
        people.add(new Person("aaa", 30));
        people.add(new Person("bbb", 10));
        people.add(new Person("ccc", 20));

        people.sort(Comparator.comparing(Person::getAge));

        System.out.println(people.toString());
    }
}