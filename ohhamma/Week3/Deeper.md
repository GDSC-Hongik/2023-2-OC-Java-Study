# 아이템 42. 익명 클래스보다는 람다를 사용하라

- 익명 클래스 → 코드가 너무 긺
- 람다 : 익명 클래스와 개념은 비슷하지만 코드 훨씬 `간결`
    - 어떤 동작을 하는지 명확하게 드러남

```java
Collections.sort(words, (s1, s2) -> Integer.compare(s1.length(), s2.length()));
```

- 컴파일러가 타입을 추론해줌 → 람다의 매개변수 타입 생략

```java
Collections.sort(words, comparingInt(String::length));  // 비교자 생성 메서드 사용
words.sort(comparingInt(String::length));  // List 인터페이스의 sort 메서드
```

- 람다는 이름이 없고 문서화도 불가능
    - 코드 자체로 동작이 명확히 설명되지 않거나 코드 줄 수가 많아지면 쓰지 마세요!
- 람다로 대체할 수 없는 곳
    - 람다는 함수형 인터페이스에서만 사용
    - 람다는 자신을 참조할 수 x

# 아이템 44. 표준 함수형 인터페이스를 사용하라

> *필요한 용도에 맞는게 있다면 직접 구현하지 말고 표준 함수형 인터페이스를 활용하라*
> 
- 유용한 디폴트 메서드 많이 제공 → 다른 코드와의 상호운용성↑
- java.util.function 패키지에 담겨있는 다양한 인터페이스

## Operator

- 반환값과 인수의 타입 같은 함수
- 인수 1개 : UnaryOperator / 인수 2개 : BinaryOperator

```java
T apply(T t)  // UnaryOperator<T>
T apply(T t1, T t2)  // BinaryOperator<T>
```

## Predicate

- 인수 하나를 받아 boolean 반환하는 함수

```java
boolean test(T t)  // Predicate<T>
```

## Function

- 인수와 반환 타입이 다른 함수

```java
R apply(T t)  // Function<T, R>
```

## Supplier

- 인수를 받지 않고 값을 반환(혹은 제공)하는 함수

```java
T get()  // Supplier<T>
```

## Consumer

- 인수를 하나 받고 반환값은 없는(인수를 소비하는) 함수

```java
void accept(T t)  // Consumer<T>
```

<aside>
💡 **기본 인터페이스의 변형**

- 기본 타입 `int`, `long`, `double`용으로 각 3개의 변형
- Function 인터페이스의 `SrcToResult` 변형
- 인수를 2개씩 받는 변경 : `Bi`Predicate<T, U>

→ 표준형 함수형 인터페이스 총 43개

</aside>

- 전용 함수형 인터페이스 구현에 대한 고민
    - 자주 쓰이며, 이름 자체가 용도를 명확히 설명해준다
    - 반드시 따라야 하는 규약이 있다
    - 유용한 디폴트 메서드를 제공할 수 있다
- `@FunctionalInterface` 애너테이션
    - 람다용으로 설계된 인터페이스임을 알려줌
    - 추상 메서드를 오직 하나만 가지고 있어야 컴파일됨
    - 유지보수 과정에서 실수로 메서드 추가하지 못하도록 막아줌

> *서로 다른 함수형 인터페이스를 같은 위치의 인수로 받는 메서드들을 다중 정의해서는 안 된다*
>