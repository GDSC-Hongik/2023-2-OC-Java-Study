# 람다 표현식
## 함수형 인터페이스 사용
|사용 사례|람다 예제|대응하는 함수형 인터페이스
|----|----|----|
|불리언 표현| (List\<String> list) -> list.isEmpty()| Predicate<List\<String>>
|객체 생성| () -> new Apple(10)| Supplier<Appple\>
|객체에서 소비| (Apple a) -> System.out.println(a.getWeight())| Consummer<Apple\>
|객체에서 선택/추출| (String s) -> s.length()| Function<String, Integer> 또는 ToIntFunction<String\>
|두 값 조회| (int a, int b) -> a * b| IntBinaryOperator
|두 객체 비교| (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())| Comparator<Apple\> 또는 BiFunction<Apple, Apple, Integer> 또는 ToIntBiFunction<Apple, Apple>
|

> 예외, 람다, 함수형 인터페이스의 관계 <br>
함수형 인터페이스는 예외를 던지지 않기 때문에 예외를 던지는 람다식을 만들고 싶다면 직접 예외를 선언하는 함수형 인터페이스를 정의하거나 람다식을 try/catch 블럭으로 감싸야 한다.

## 형식 검사, 형식 추론, 제약
### 형식 검사
컴파일러는 람다가 사용되는 context를 이용해서 람다의 형식을 추론할 수 있다. 대상 형식이란 어떤 context에서 기대되는 람다 표현식의 형식이다. 그렇다면 어떻게 람다의 형식을 추론하는지 예제를 통해서 알아보자.

```java
List<Apple> heavierThan150g = filter(inventory, (Apple apple) -> apple.getWeight() > 150);
```

1. 람다가 사용된 context는 무엇인가? 우선 filter의 정의를 확인하자.
2. 대상 형식은 Predicate<Apple\>이다.
3. Predicate<Apple\> 인터페이스의 추상 메서는 무엇인가?
4. Apple을 인수로 받아 boolean을 반환하는 test 메서드이다!
5. 함수 디스크립터는 Apple -> boolean이므로 람다의 시그니처와 일치한다! 람다도 Apple을 인수로 받아 boolean을 반환하므로 코드 형식 검사가 성공적으로 완료된다.

### 같은 람다, 다른 함수형 인터페이스
대상 형식의 특징 때문에 같은 람다식이더라도 다른 함수형 인터페이스로 사용될 수 있다. 

```java
Callable<Integer> c = () -> 42;
PrivilegedAction<Integer> p = () -> 42;
```

위의 코드에서 사용된 Callable과 PrivilegedAction 인터페이스는 모두 파라미터를 입력받지 않고 제네릭 형식 T를 반환한다. 이 예제를 통해서 하나의 람다 표현식에는 다양한 함수형 인터페이스가 사용될 수 있다는 것을 알 수 있다.

#### 다이아몬드 연산자(<>)
다이아몬드 연산자는 자바 7에서 추가되어, 다이아몬드 연산자를 사용하여 컴파일러가 알아서 타입을 추론이 가능하도록 한다.

#### 특별한 void 호환 규칙
람다의 바디에 일반 표현식이 있으면 void를 반환하는 함수 디스크립터와 호환된다.

```java
// Predicate는 불리언 반환값을 갖는다.
Predicate<String> p = s -> list.add(s);
// Consumer는 void 반환값을 갖는다.
Consumer<String> b = s -> list.add(s);
```

### 형식 추론
자바 컴파일러는 대상 형식을 이용해서 함수 디스크립터를 파악하고 람다의 시그니처를 추론할 수 있다. 그렇기 때문에 람다 표현식의 파라미터 형식에 접근할 수 있어 이를 생략할 수 있다.

```java
Comparator<Apple> c = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()); // 형식을 추론하지 않는다.

Comparator<Apple> c = () a1, a2) -> a1.getWeight().compareTo(a2.getWeight());   // 형식을 추론한다.
```

### 지역 변수 사용
자유 변수는 파라미터로 넘겨진 변수가 아닌 외부에서 정의된 변수를 말하는데, 이를 활용하는 동작을 지난 시간에 effectively final을 통해서 배웠던 람다 캡쳐링이라고 한다. 

```java
int portNumber = 1337;
Runnable r = () -> System.out.println(portNumber);
```

위의 예제처럼 portNumber 변수를 캡쳐할 수 있다. 하지만 항상 캡쳐를 할 수 있는 것은 아니다. 그래서 제약이 존재한다. 그 제약은 지역 변수는 final로 선언되거나 final과 같은 역할을 해야 한다는 것이다. 

#### 지역 변수의 제약
지역 변수에 이런 제약이 발생하는 이유는 무엇일까? 지난 과제에서도 했지만, 인스턴스 변수와 지역 변수의 차이를 이해하면 된다. 인스턴스 변수는 힙에 저장되지만 지역 변수는 스택에 저장된다. 그래서 변수가 할당된 이후에는 스레드가 사라져 람다가 실행되는 스레드에서 지역 변수에 접근하려고 할 수 있다. 이러한 이유로 자바는 지역 변수의 복사본을 활용해야 한다. 즉, 복사본은 변경이 되면 안 된다는 제약이 생긴 것이다.

> 클로저 <br>
클로저란 함수의 비지역 변수를 자유롭게 참조할 수 있는 함수의 인스턴스를 말한다. 클로저는 외부에 정의된 변수의 값에 접근하고, 값을 변경할 수 있으며 클로저를 다른 함수의 인수로 전달할 수 있다. 람다와 익명 클래스는 클로저와 비슷한 동작을 수행한다. 먼저, 람다와 익명 클래스 모두 다른 함수의 인수로 전달될 수 있다. 또한, 외부 영역의 변수에 접근할 수 있다. 다만 람다와 익명 클래스의 지역 변수는 final 혹은 effectively final 이어야 한다.

## 메서드 참조
```java
// 기존 코드
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));

// 메서드 참조를 활용한 코드
inventory.sort(comparing(Apple::getWeight));
```

이렇게 메서드 참조를 활용하면 기존의 메서드 정의를 재활용해서 람다처럼 전달할 수 있고, 람다 표현식보다 가독성이 좋다.

### 요약
메서드 참조가 중요한 이유는 람다가 메서드를 어떻게 호출해아 하는지 설명을 참조하기 보다는 메서드명을 직접 참조하기 때문에 편리하다. 메서드명을 참조함으로써 가독성 또한 높일 수 있다. 메서드 참조는 메서드명 앞에 구분자(::)를 붙여주면 된다. 주의할 점은 메서드를 실제로 호출하는 것이 아니기 때문에 괄호를 붙여줄 필요는 없다.

|람다|메서드 참조 단축 표현
|----|----
|(Apple apple) -> apple.getWeight()| Apple::getWeight
|() -> Thread.currentThread().dumpStack()| Thread.currentThread()::dumpStack
|(str, i) -> str.substring(i)| String::substring
|(String s) -> System.out.println(s)| System.out::println
|(String s) -> this.isValidName(s)| this::isValidName
|

#### 메서드 참조를 만드는 방법
1. 정적 메서드 참조
2. 다양한 형식의 인스턴스 메서드 참조
3. 기존 객체의 인스턴스 메서드 참조

### 생성자 참조
클래스명과 new 키워드를 이용해서 기존 생성자의 참조를 만들 수 있다.

```java
// 생성자 참조
Supplier<Apple> c1 = Apple::new;

// 람다
Supplier<Apple> c2 = () -> new Apple();
```

Apple의 생성자를 map 메서드에 전달하여 다양한 무게를 포함하는 사과 리스트를 만들 수 있다.

```java
List<Integer> weights = Arrays.asList(7, 3, 4, 10);
List<Apple> apples = map(weights, Apple::new);
public List<Apple> map(List<Integer> list, Function<Integer, Apple> f) {
    List<Apple> result = new ArrayList<>();
    for (Integer i : list) {
        result.add(f.apply(i));
    }
    return result;
}
```

만약 Apple(String color, Integer weight)와 같이 Apple의 인수가 2개라면 BiFunction을 사용할 수 있다.

```java
// 위의 예제와 동일하게 람다로 표현 가능
BiFunction<Color, Integer, Apple> c3 = Apple::new;
Apple a3 = c3.apply(GREEN, 110);
```

생성자 참조는 인스턴스화하지 않고도 다양한 상황에 응용할 수 있다. 

## 람다, 메서드 참조 활용하기
### 1단계: 코드 전달
리스트의 sort 메서드의 시그니처는 다음과 같다.

```java
void sort(Comparator<? super E> c)
```

Comparator 객체를 파라미터로 받는다. 이제 sort의 동작은 파라미터화 되었다. 

```java
public class AppleComparator implements Comparator<Apple> {
    public int compare(Apple a1, Apple a2) {
        return a1.getWeight().compareTo(a2.getWeight());
    }
}

inventory.sort(new AppleComparator());
```

### 2단계: 익명 클래스 사용
한 번만 사용할 코드이기 때문에 익명 클래스를 이용하는 것이 좋다.

```java
inventory.sort(new Comparator<Apple>() {
    public int compare(Apple a1, Apple a2) {
        return a1.getWeight().compareTo(a2.getWeight());
    }
});
```

### 3단계: 람다 표현식 사용
람다 표현식을 통해서 익명 클래스를 사용한 코드를 줄이고 가독성을 높여보자. 람다 표현식을 사용하기 위해서는 Comparator의 함수 디스크립터를 파악해야 한다. Comparator의 함수 디스크립터는 (T, T) -> int 이다.

```java
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

자바 컴파일러는 람다 표현식이 사용된 context로 람다의 파라미터 형식을 추론할 수 있기 때문에 Apple을 생략할 수 있다. 여기서 더 줄이려면 정적 메서드인 comparing을 사용해야 한다.

```java
inventory.sort(comparing(apple -> apple.getWeight()));
```

### 4단계: 메서드 참조 사용
메서드 참조를 사용하면 람다 표현식보다 코드를 간결하게 만들 수 있다. 

```java
inventory.sort(comparing(Apple::getWeight));
```

## 람다 표현식을 조합할 수 있는 유용한 메서드
함수형 인터페이스는 하나의 추상 메서드와 디폴트 메서드로 구성되는데, 디폴트 메서드를 이용해서 함수형 인터페이스의 조합을 만들 수 있다.

### Comparator 조합
이전 예제에서 comparing을 이용해서 비교하여 정렬을 해보았다. 

#### 역정렬
반대로 사과를 내림차순으로 정렬해보자.

```java
inventory.sort(comapring(Apple::getWeight).reversed());
```

여기서 reverse 메서드가 바로 디폴트 메서드이다.

#### Comparator 연결
만약 무게가 같은 사과가 있다면 어떻게 정렬하는 것이 좋을까? 여기서 조건을 추가할 수 있다. 예를 들면, 무게가 같은 사과는 국가별로 정렬하는 것이다.

```java
inventory.sort(comapring(Apple::getWeight)
    .reversed()
    .thenComparing(Apple::getCountry));
```

### Predicate 조합
Predicate 인터페이스는 negate, and, or 메서드를 제공한다. 

- 빨간색이 아닌 사과

```java
Predicate<Apple> notRedApple = redApple.negate();
```

- 빨간색이면서 무거운 사과

```java
Predicate<Apple> redAndHeavyApple = redApple.and(apple -> apple.getWeight() > 150);
```

- 빨간색이면서 무거운 사과 또는 녹색 사과

```java
Predicate<Apple> redAndHeavyAppleOrGreen = redApple
    .and(apple -> apple.getWeight() > 150)
    .or(apple -> GREEN.equals(a.getColor()))s;
```

이렇게 람다 표현식을 조합해서 더 다양한 경우를 표현할 수 있다. 그렇지만 코드는 읽기 좋게 구현이 되어 있다.

### Function 조합
- andThen: 주어진 함수를 먼저 적용한 결과를 다른 함수의 입력으로 전달하는 함수를 반환

```java
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x * 2;Function<Integer, Integer> h = f.andThen(g);

int result = h.apply(1);    // 4
```

수학으로는 합성함수와 비슷하다.

- compose: 인수로 주어진 함수를 먼저 실행한 다음에 그 결과를 외부 함수의 인수로 제공

```java
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x * 2;Function<Integer, Integer> h = f.compose(g);

int result = h.apply(1);    // 3
```

andThen과 compose는 반대 개념이다.

## 비슷한 수학적 개념
람다 표현식과 비슷한 수학적 개념에 대해서 알아보자.

### 적분
적분을 람다로 표현해보자. 우선, 함수 f와 적분 범위를 인수로 받는 함수를 만들어야 한다.

### 자바 8 람다로 연결
함수 f를 다음과 같이 람다로 표현할 수 있다.

```java
integrate((double x) -> x + 10, 3, 7);
```

C가 정적 메서드 f를 포함하는 클래스라면 메서드 참조를 이용하여 더욱 간단하게 만들 수 있다.

```java
integrate((C::f, 3, 7);
```

이제 integrate 메서드를 구현해보자.

```java
public double integrate(DoubleFunction<Double> f, double a, double b) {
    return (f.apply(a) + f.apply(b)) * (b - a) / 2.0;
}
```

여기서 DoubleFunction 대신에 DoubleUnaryOperator를 사용해도 된다. 

# 스트림 소개
- 스트림이란 무엇인가?
- 컬렉션과 스트림
- 내부 반복과 외부 반복
- 중간 연산과 최종 연산

## 스트림이란 무엇인가?
`스트림`은 선언형으로 컬렌션 데이터를 처리 가능하게 하는 자바 8 API에 새로 추가된 기능이다. 또한 스트림을 이용하면 멀티 스레드를 구현하지 않아도 데이터를 병렬로 처리할 수 있다. 우선, 자바 7을 이용하여 칼로리가 낮은 요리를 반환하고 칼로리 기준으로 정렬해보자.

```java
List<Dish> lowCaloricDishes = new ArrayList<>();
for (Dish dish : menu) {
    if (dish.getCalories() < 400) {
        lowCaloricDishes.add(dish);
    }
}

Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
    public int compare(Dish dish1, Dish dish2) {
        return Integer.compare(dish1.getCalories(), dish2.getCalories());
    }
});

List<String> lowCaloricDishesName = new ArrayList<>();
for (Dish dish : lowCaloricDishes) {
    lowCaloricDishesName.add(dish);
}
```

위의 예제를 수행하기 위해서 칼로리가 낮은 요리의 리스트를 중간 변수로 사용했다. 이제 자바 8을 적용시켜서 구현해보자.

```java
List<String> lowCaloricDishesName = menu.stream()
        .filter(d -> d.getCalories() < 400)
        .sorted(comparing(Dish:getCalories))
        .map(Dish::getName)
        .collect(toList());
```

스트림을 적용해보았는데, 코드가 간결해지고 가독성이 좋아졌다. 스트림의 장점은 루프와 조건문과 같은 제어 블럭을 사용하지 않고 선언형으로 코드를 구현할 수 있다는 것이다. 그리고 여러 빌딩 블럭 연산을 연결해서 복잡한 데이터를 처리하는 파이프라인을 만들 수 있다. 결과적으로 데이터 저리 과정을 병렬화하면서 스레드와 락을 걱정할 필요가 없어졌다.

```java
Map<Dish.Type, List<Dish>> dishesByType = menu.steam()
        .collect(groupingBy(Dish::getType));
```

자바 8의 특징을 다시 살펴보자.
- 선언형: 더 간결하고 가독성이 좋아진다.
- 조립할 수 있음: 유연성이 좋아진다.
- 병렬화: 성능이 좋아진다.

## 스트림 시작하기
정확히 말해서 스트림이란 **데이터 처리 연산을 지원하도록 소스에서 추출된 연속된 요소**이다. 

- 연속된 요소: 스트림은 컬렉션과 마찬가지로 연속된 값 집합의 인터페이스를 제공한다. 컬렉션과는 다르게 스트림은 표현 계산식이 주를 이룬다.
- 소스: 
- 데이터 처리 연산: 함수형 프로그래밍 언어에서 지원하는 연산과 데이터베이스와 비슷한 연산을 지원한다.

이제 스트림의 특징에 대해서 알아보자.

- 파이프라이닝: 스트림 연산은 스트림 연산끼리 연결해서 커다란 파이프라인을 구성할 수 있다.
- 내부 반복: 반복자를 이용해서 반복하는 컬렉션과는 다르게 스트림은 내부 반복을 지원한다.

```java
List<String> threeHighCaloricDishNames = menu.stream()
        .filter(dish - > dish.getCalories > 300)
        .map(Dish::getName)
        .limit(3)
        .collect(toList());
System.out.println(threeHighCaloricDishNames);
```

이제 앞에서 살펴본 정의와 특징을 코드로 살펴보자. 데이터 소스는 요리 리스트 즉, 메뉴이다. 데이터 소스는 연속된 요소를 스트림에서 제공한다. 그리고 filter, map, limit, collect를 이용하여 데이터 처리 연산을 적용하고, collect를 제외한 연산은 서로 파이프라인을 형성한다. 그리고 collect 연산을 통해서 결과를 반환한다.

## 스트림과 컬렉션
그렇다면 스트림과 컬렉션의 차이는 뭘까? 데이터를 언제 계산하느냐가 가장 큰 차이이다. 컬렉션은 컬렉션에 추가되기 전에 계산되어야 하지만 스트림은 요청할 때만 요소를 계산한다.

### 딱 한 번만 탐색할 수 있다
탐색된 스트림은 소비되기 때문에 한 번만 탐색이 가능하다. 

```java
List<String> title = Arrays.asList("Java8", "In", "Action");
Stream<String> s = title.stream();
s.forEach(System.out::println);
s.forEach(System.out::println);
```

위의 코드에서 처음 s.forEach에서는 title의 각 단어가 출력되지만 두 번째 s.forEach에서는 이미 앞에서 스트림을 소비했기 때문에 IllegalStateException이 발생한다.

### 외부 반복과 내부 반복
컬렉션 인터페이스를 사용하려면 외부 반복을 통해서 사용자가 직접 요소를 반복해야 한다. 반면 스트림은 내부 반복을 사용한다. 함수에 어떤 작업을 할 지만 지정하면 알아서 처리해준다.

```java
List<String> names = new ArrayList<>();
for (Dish dish : menu) {
    name.add(dish.getName());
}
```

우리는 스트림을 통해서 외부 반복하는 코드를 내부 반복을 하도록 변경할 수 있다.

```java
List<String> names = menu.stream()
        .map(Dish::getName)
        .collect(toList());
```

외부 반복보다 내부 반복이 더 좋은 이유는 작업을 투명하게 병렬로 처리하거나 더 최적화된 다양한 순서로 처리할 수 있다는 것이다. 또한 병렬성 구현을 자동으로 선택할 수 있다.

## 스트림 연산
스트림의 연산을 두 가지로 분류할 수 있다.

- 중간 연산: 연결할 수 있는 스트림 연산(파이프라인을 형성)
- 최종 연산: 스트림을 닫는 연산

### 중간 연산
중간 연산은 다른 스트림을 반환한다. 중간 연산은 스트림 파이프라인에 실행하기 전까지는 아무 연산을 수행하지 않고, 중간 연산을 합친 후에 최종 연산으로 한 번에 처리한다. 그래서 중간 연산은 lazy하다고 한다. 스트림 파이프라인에서 어떤 일이 일어나는지 확인해보자.

```java
List<String> names = menu.stream()
        .filter(dish -> {
            System.out.println("filtering: " + dish.getName());
            return dish.getCalories > 300;
        })
        .map(dish -> {
            System.out.println("mapping: " + dish.getName());
            return dish.getName();
        })
        .limit(3)
        .collect(toList());
System.out.println(names);
```

실행하면 다음과 같은 결과가 나온다.

<img src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/116694226/90eaa074-b9f8-4284-b10f-215b79f62841" width=500>

### 최종 연산
최종 연산은 스트림 파이프라인에서 결과를 도출한다. 최종 결과는 List, Integer, void와 같이 스트림 이외의 결과가 반환된다.

### 스트림 이용하기
- 질의를 수행할 데이터 소스
- 스트림 파이프라인을 구성할 중간 연산 연결
- 스트림 파이프라인을 실행하고 결과를 만들 최종 연산

|연산|형식|반환 형식|연산의 인수|함수 디스크립터
|----|---|---|---|---
|filter|중간 연산| Stream<T>| Predicate<T>| T -> boolean
|map| 중간 연산| Stream<R>| Fucntion<T, R>| T -> R
|limit| 중간 연산| Stream<T>| 
|sorted| 중간 연산| Stream<T>| Comparator<T>| (T, T) -> int
|distinct| 중간 연산| Stream<T>| 
|


|연산|형식|반환 형식|목적
|----|---|---|---
|forEach| 최종 연산| void| 스트림의 각 요소를 소비하면서 람다를 적용
|count| 최종 연산| long(generic)| 스트림의 요소 개수 반환
|collect| 최종 연산| | 스트림을 리듀스해서 리스트, 맵, 정수 형식의 컬렉션 생성
|