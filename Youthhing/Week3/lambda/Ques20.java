package modern.other.week3.lambda;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Ques20 {
    //특정 속성에 의하여 List 정렬하라
    public static void main(String[] args) {
        List<Person> people = new ArrayList<>();
        people.add(new Person(26,"Shin"));
        people.add(new Person(26,"Lee"));
        people.add(new Person(28,"Lee"));
        people.add(new Person(27,"Hwang"));
        people.add(new Person(22,"Ham"));

        people.sort(Comparator.comparing(Person::getAge)
                .thenComparing(Person::getName));

        for (Person p: people ) {
            System.out.println(p.getAge() + " " + p.getName());
        }
    }


    public static class Person{
        int age;
        String name;

        public Person(int age, String name) {
            this.age = age;
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
