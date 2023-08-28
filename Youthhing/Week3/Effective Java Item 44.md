# Effective Java Item 44

## **표준 함수형 인터페이스를 사용하라**

자바 표준 라이브러리에 여러 표준 함수형 인터페이스가 담겨있다. 따라서, 필요한 용도에 맞는게 이미 존재한다면, 직접 구현하지말고 표준 함수형 인터페이스를 사용하자.

총 43개의 인터페이스 중 대표적으로 기억해야할 기본 인터페이스 6개의 이름과 함수 시그니처는 다음과 같다.

UnaryOperator<T>: T apply(T t)

BinaryOperator<T>: T apply(T t1, T t2)

Predicate<T>: boolean test(T t)

Function<T,R>: R apply(T t)

Supplier<T>: T get()

Consumer<T>: void accept(T t)

이어서, 기본 인터페이스는 기본 타입으로 int, long, double로 각 3개씩 변형이 존재한다. 해당 기본 인터페이스 이름 앞에 기본 타입의 이름을 붙여 지었다. 예를 들어 int를 받는 Predicate는 **IntPredicate**이다.

6개의 기본 인터페이스 중 Function의 반환 타입만 매개변수화 되었는데 무슨 이야기냐 하면 **LongFunction<int[]>**라는 코드가 있으면 long을 인수로 받아 **int[]**를 반환한다는 뜻이다.

또한 Function의 인터페이스는 기본타입의 파라미터, 반환형 각각 3개씩이 가능한 총 9개의 인터페이스 변형이 있다.

이름이 SourceToResult로 Source 자리엔 파라미터, Result 자리엔 반환형이 온다. 예를들면, LongToIntFunction 이렇게 표현할 수 있다. 이렇게 만들어진게 총 6개가 존재한다.

나머지 3개는 입력을 매개변수화 하여 ToResult 형식으로 표현한다. 즉, ToLongFunction<int[]>로 int[]를 인수로 받아 long을 반환한다는 뜻이다.

또한, 인수를 두개씩 받는 변형은 앞에 Bi를 붙인다. Ex: BiFunction<T,U,R>

그러나, 이런 함수형 인터페이스는 총 43개라 외우기도 힘들고 이름의 규칙성도 찾기 어렵다. 따라서, 필요할때 찾아쓸 수 있을만큼만은 알자!

또한, 표준 함수형 인터페이스는 기본 타입만 지원하는데, **기본 함수형 인터페이스에 박싱된 기본타입을 넣어 사용하지 말아야한다.** 박싱을 하는 과정에서 성능이 나빠질 수 있기 때문이다.

물론, 이렇게 43개의 인터페이스에 존재하지 않거나, 예외를 던져야하는 경우엔 직접 함수형 인터페이스를 작성해야한다. 이때는 Comparator의 쓰임새를 기억하면된다. Comparator가 자주 쓰이는 이유는 이름 자체로 용도를 명확히 설명할 수 있다는 점, 반드시 따라야 하는 규약이 있다는 점, 유용한 디폴트 메서드를 제공할 수 있다는 점이다.

이렇게 새로 작성을 할때는 이가 인터페이스임을 명심하고 설계해야한다.

### **Comparator와 Comparable의 차이**

둘 모두 객체를 비교할 수 있게 해주는 인터페이스라는 점에서 공통점을 갖지만 둘의 차이는 무엇일까?


**Comparable**

Comparable의 세부 구현을 보면 해당 추상메서드는 `public int compareTo(T o1)`으로 매개변수를 하나만 받는데 자기 자신과 매개변수로 오는 객체를 비교한다. 구현해야할 메서드는 하나이지만, `@FunctionalInterface`라는 어노테이션이 붙어있지 않은데 이는 Comparable이 구현할 compareTo가 자기자신 this와 파라미터를 비교하는데 람다 표현식 내부에서 `this`를 사용하면 람다를 호출하는 외부객체의 값이 전달되어 원하는 로직대로 비교가 불가능하기 때문이다.
```Java
public class TestComparable {
    public static void main(String[] args) {
        Student youth = new Student("youth",3);
        Student more = new Student("more",5);
        Student kim = new Student("Kim", 1);
        Student kim1 = new Student("kim", 11);

        List<Student> students = new ArrayList<>();
        students.add(youth);
        students.add(more);
        students.add(kim1);
        students.add(kim);

        students.sort(Student::compareTo);

        for (Student s : students) {
            System.out.println(s.getNumber()+" "+s.getName());
        }

    }

    static class Student implements Comparable<Student> {
        private String name;
        private int number;

        @Override
        public int compareTo(Student o) {
            if(this.name.equals(o.name)){
                return this.number - o.number;
            }
            else{
                return this.name.compareTo(o.name);
            }
        }
        //생성자 getter, setter 생략
    }

}
```


**Comparator**

 `int compare(T o1, T o2)`를 추상메서드로 가지고 있어 매개변수를 두 개 받아서 두 객체를 비교한 값을 전달한다. 

 ```java
public class TestComparator {
    public static void main(String[] args) {
        Student youth = new Student("youth",3);
        Student more = new Student("more",5);
        Student kim = new Student("Kim", 1);
        Student kim1 = new Student("kim", 11);

        List<Student> students = new ArrayList<>();
        students.add(youth);
        students.add(more);
        students.add(kim1);
        students.add(kim);

        for (Student s: students) {
            System.out.println(s.getName()+" "+s.getNumber());
        }

        students.sort((s1,s2)->s1.getNumber()-s2.getNumber());

        for (Student s: students) {
            System.out.println(s.getName()+" "+s.getNumber());
        }

    }


    static class Student {
        private String name;
        private int number;
        
        //생성자, getter, setter 생략
    }
}

 ```



**@FunctionalInterface를 사용하는 이유**

1. 해당 클래스의 코드를 읽을때 람다용으로 작성되었음을 명시함

2. 컴파일단계에서 추상 메서드가 오직 하나임을 확인할 수 있게 도움

3. 유지 보수 과정에서 누군가 임의로 추상 메서드를 추가할 수 없게 만듦.

**주의 사항**

함수형 인터페이스의 API를 사용할때, 서로 다른 함수형 인터페이스를 같은 위치의 인수로 받는 메서드를 다중 정의하지말자. 클라이언트에게 모호함을 주어 여러 문제가 발생할 수 있다.