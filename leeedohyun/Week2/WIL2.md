# 동작 파라미터화 코드 전달하기
- 변화하는 요구사항에 대응
- 동작 파라미터화
- 익명 클래스
- 람다 표현식 미리보기
- 실전 예제: Comparator, Runnable, GUI

소비자의 요구사항은 항상 변한다. 우리는 그 변화에 대응하기 위해서 엔지니어링적인 비용을 최소화할 수 있어야 한다. 또한, 새로운 기능을 쉽게 추가할 수 있어야 하며, 유지보수가 쉬워야 한다. **`동작 파라미터`를 이용하면 변하는 요구사항에 쉽게 대응**할 수 있다. 

그렇다면 `동작 파리미터`란 무엇일까? 아직은 어떻게 실행할 것인지 결정하지 않은 코드 블럭을 의미한다. 즉, 코드 블럭은 나중에 실행된다. 그렇기 때문에 메서드의 인수로 코드 블럭을 전달할 수 있으며, 이에 따라 메서드의 동작이 파라미터화된다. 동작 파라미터는 필요없는 코드가 발생한다. 그러나, 걱정할 필요없이 자바 8이 제공하는 `람다 표현식`을 사용하여 코드를 간결하게 만들 수 있다.

## 변화하는 요구사항에 대응하기
다음 예제를 통해서 변화에 대응하는 코드를 어떻게 구현하는지 살펴보자.

### 첫 번째 시도: 녹색 사과 필터링
```java
public static List<Apple> filterGreenApples(List<Apple> inventory) {
    List<Apple> result = new ArrayList<>();     // 사과 누적 리스트
    for (Apple apple : inventory) {
        if (GRREN.equals(apple.getColor())) {   // 녹색 사과 선택
            result.add(apple);
        }
    }
    return result;
}
```

> enum을 비교할 때는 `==` 비교가 가능하다. `==` 비교는 런타임에 NPE(NullPointerException)이 발생시키지 않는다. 또한, 컴파일 타임에 타입을 비교하여 타입의 미스매치를 잡아준다.

이렇게 녹색 사과만을 선택하는 코드를 구현할 수 있다. 하지만 녹색 사과가 아니라 빨간 사과도 필터링하고 싶다면 어떻게 해야 할까?

> 거의 비슷한 코드가 반복 존재한다면 그 코드를 추상화한다.

### 두 번째 시도: 색을 파라미터화
앞서 구현한 코드를 반복하지 않고 빨간 사과를 필터링할 수 있다면 이전 보다 요구사항에 유연하게 대응할 수 있을 것이다.

```java
public static List<Apple> filterAppleByColor(List<Apple> inventory, Color color) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : invetory) {
        if (apple.getColor().equals(color)) {
            result.add(apple);
        }
    }
    return result;
}

// 구현한 메서드 호출
List<Apple> greenApples = filterApplesByColor(inventory, GREEN);
List<Apple> redApples = filterApplesByColor(inventory, RED);
```

이렇게 간단하게 메서드를 호출할 수 있다. 만약 여기에서 무게의 기준도 추가가 된다면 어떻게 해야 될까? 색을 파라미터화한 것과 동일하게 무게를 파라미터화하면 된다. 하지만 색을 필터링하는 코드와 무게를 필터링하는 코드는 대부분 중복될 것이다. 이 문제를 해결해보자.

### 세 번째 시도: 가능한 모든 속성으로 필터링
```java
public static List<Apple> filterApples(List<Apple> inventory, Color color, int weight, boolean flag) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : invetory) {
        if ((flag && apple.getColor().equals(color)) || (!flag && apple.getWeight() > weight)) {
            result.add(apple);
        }
    }
    return result;
}
```

flag 변수를 통해서 true일 때는 사과 색을 false일 때는 사과의 무게를 필터링할 수 있다. 그런데, 다른 사람이 봤을 때 true와 false가 무슨 의미인지 알지 못할 것이다. 이때 필요한 것이 `동작 파라미터화`이다.

## 동작 파리미터화
사과의 어떤 속송에 기초해서 불리언 값을 반환하는 방법이 있다. 바로 Predicate를 사용하는 것이다. 

```java
public interface ApplePredicate {
    boolean test(Apple apple);
}

// 무거운 사과 선택
public class AppleHeavyWeightPredicate implements ApplePredicate {
    public boolean test(Apple apple) {
        return apple.getWeight() > 150;
    }
}


// 녹색 사과 선택
public class AppleGreenColorPredicate implements ApplePredicate {
    public boolean test(Apple apple) {
        return GREEN.equals(apple.getColor());
    }
}
```

이렇게 ApplePredicate를 통해서 사과 선택 전략을 캡슐화할 수 있다. 이제 filterApples 메서드가 ApplePredicate 객체를 인수로 받도록 고쳐보자.

### 네 번째 시도: 추상적 조건으로 필터링
```java
public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : invetory) {
        if (p.test(apple)) {
            result.add(apple);
        }
    }
    return result;
}
```

우리는 이제 요구사항이 어떻게 바뀌더라도 ApplePredicate를 구현하는 클래스를 만들기만 하면 된다. 이렇게 컬력션 탐색 로직과 각 항목에 적용할 동작을 분리할 수 있다는 것이 동작 파라미터화의 강점이다. 지금까지 요구사항에 대응할 수 있도록 코드를 구현했지만 여러 클래스를 구현해서 인스턴스화하는 과정이 거추장스럽게 느껴질 수 있다. 이 부분을 어떻게 개선하는지 알아보자.

## 복잡한 과정 간소화
### 익명 클래스
`익명 클래스`란 말 그대로 이름이 없는 클래스이다. 익명 클래스는 클래스 선언과 동시에 인스턴스화 할 수 있다.

### 다섯 번째 시도: 익명 클래스 사용
```java
List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
    public boolean test(Apple apple) {
        return RED.equals(apple.getColor());
    }
});
}
```

익명 클래스를 이용하여 ApplePredicate를 한 번만 사용하고 버리는 객체로 만들어 메서드의 동작을 파라미터화했다. 하지만 익명 클래스에도 부족한 점이 있다.
- 익명 클래스 자체가 많은 공간을 차지한다.
- 많은 프로그래머가 익명 클래스의 사용에 익숙하지 않다.

람다 표현식을 사용해서 이러한 문제들을 해결하고 코드를 더 간결하게 할 수 있다.

### 여섯 번째 시도: 람다 표현식 사용
```java
List<Apple> result = 
    filterApples(inventory, (Apple apple) -> RED.eqauls(apple.getColor()));
```

이렇게 앞서 봤던 코드들을 간결하게 구현할 수 있다.

### 일곱 번째 시도: 리스트 형식으로 추상화
```java
public interface Predicate<T> {
    boolean test(T t);
}

public static <T>List<T> filter(List<T> list, Predicate<T> p) {
    List<Apple> result = new ArrayList<>();
    for (T e : list) {
        if (p.test(e)) {
            result.add(e);
        }
    }
    return result;
}
```

이렇게 리스트 형식을 추상화 함으로써 바나나, 오렌지, 정수, 문자열 등의 리스트에 필터 메서드를 사용할 수 있게 되었다. 그에 따라 유연하고 간결한 코드를 구현할 수 있게 되었다.

## 실전 예제
### Comparator로 정렬하기
변화하는 요구사항에 쉽게 대응할 수 있는 다양한 정렬 동작을 수행하는 코드를 만들어보자.

```java
// java.util.Comparator
public interface Comparator<T> {
    int compare(T o1, T o2);
}

inventory.sort(new Comparator<Apple>() {
    @Override
    public int compare(Apple a1, Apple a2) {
        return a1.getWeight().compareTo(a2.getWeight());
    }
});

// 람다 표현식 적용
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

### Runnable러 코드 블럭 실행하기
Runnable 인터페이스를 이용해서 실행할 코드 블럭을 지정할 수 있다.

```java
// java.lang.Runnable
public interface Runnable {
    void run();
}

Thread t = new Thread(new Runnable() {
    @Override
    public void run() {
        System.out.println("Hello World");
    }
});

// 람다 적용
Thread t = new Thread(() -> System.out.println("Hello World"));
```

### Callable을 결과로 반환하기
Callable 인터페이스를 이용해 결과를 반환하는 태스크를 만들어 보기만 해보자.

```java
// java.util.concurrent.Callable
public interface Callable<V> {
    V call();
}

ExecutorService executorService = Exectors.newCachedThreadPool();
Future<String> threadName = executorSevice.submit(new Callable<String>() {
    @Override
    public String call() throws Exception {
        return Thread.currentThread().getName();
    }
})

// 람다 적용
Future<String> threadName = executorService.submit(
    () -> Thread.currentThread().getName());
```

여기서 ExecturotService는 뭘까? ExecutorService 인터페이스는 태스크 제출과 실행 과정의 연관성을 끊어주는 역할을 한다. ExectorService를 이용하면 태스크를 스레드 풀로 보내고 결과를 Future로 저장할 수 있다는 점이 스레드와 Runnable을 이용하는 방식과는 다르다.

# 람다 표현식
- 람다란 무엇인가?
- 어디에, 어떻게 람다를 사용하는가?
- 실행 어라운드 패턴
- 함수형 인터페이스, 형식 추론

`람다 표현식`을 사용해서 앞서 배웠던 동작 파라미터화와 익명 클래스를 더 깔끔한 코드로 구현할 수 있다. 이 챕터에서 핵심은 람다 표현식을 어디에, 어떻게 사용하는지 이해하는 것이라고 생각한다. 

## 람다란 무엇인가?
`람다 표현식`은 메서드로 전달할 수 있는 익명 함수를 단순화한 것이다. 람다 표현식에는 이름은 없지만, 파라미터 리스트, 바디, 반환 형식, 발생할 수 있는 예외 리스트를 가질 수 있다. 람다의 특징을 살펴보자.

- 익명: 보통의 메서드와 달리 이름이 없다.
- 함수: 메서드처럼 특정 클래스에 종속되지 않기 때문에 함수라 부른다.
- 전달: 메서드 인수로 전달하거나 변수로 저장할 수 있다.
- 간결성: 지저분하게 구현할 필요가 없다.

람다를 이용해서 이전의 코드를 얼마나 간결하게 표현할 수 있는지 확인해보자.

```java
// 기존 코드
Comparator<Apple> byWeight = new Comparator<Apple>() {
    public int compare(Apple a1, Apple a2) {
        return a1.getWeight().compareTo(a2.getWeight());
    }
};

// 람다 적용 코드
Comparator<Apple> byWeight = 
   (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
```

람다 표현식을 이용하여 compare 메서드의 바디를 직접 전달하는 것처럼 코드를 전달할 수 있다. 이제 람다가 어떻게 구성되는지 알아보자.

- 파라미터 리스트 : 화살표 이전의 메서드 파라미터
- 화살표: 파라미터 리스트와 람다 바디를 구분하는 역할
- 람다 바디: 람다 반환값에 해당하는 표현식

람다의 기본 문법은 다음과 같이 표현된다.

- (parameters) -> expression
- (parameters) -> { statements; }

### Expression & Statement 
식(Expresision)은 적어도 하나의 값을 산출하는 것을 의미한다. **식은 반드시 컴파일러에 의해 값으로 평가될 수 있어야 한다.** 함수 호출, 객체 할당, 산술식, null value, 변수 접근, instanceof 연산자 그리고 익명 클래스와 같이 값을 산출할 수 있는 것은 식에 포함된다.

문장(Statement)은 프로그램 내에서 하나의 동작을 기술하는 것을 의미한다. 지바에서 문장은 블럭 안에 위치하여 메서드와 클래스를 구성한다.

![image](https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/116694226/e1fa7735-2274-4926-9eda-c1a07fd90639)

Statement는 흔히 한 개 이상의 expression과 프로그래밍 키워드를 포함하는 경우가 많다. 그렇기에 expression은 statement의 부분 집합이다.

## 어디에, 어떻게 람다를 사용할까?
### 함수형 인터페이스
저번 스터디 과제에서 얕게 공부했던 Predicate가 함수형 인터페이스이다. 함수형 인터페이스는 하나의 추상 메서드를 지정하는 인터페이스라고 이해하면 된다. 함수형 인터페이스에는 Comparator, Runnable 등이 있다. 그렇다면 함수형 인터페이스는 다른 메서드들을 가질 수는 없을까? 아니다. 가질 수 있다. 다만, 디폴트 메서드를 포함할 수 있는데, 디폴트 메서드가 몇 개인지는 상관이 없고 **추상 메서드가 하나이기만 하면** 함수형 인터페이스라고 할 수 있다.

그렇다면 함수형 인터페이스로 뭘 할 수 있을까? 바로 앞서 예제로 잠깐 보았던 람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달할 수 있다.

### 함수 디스크립터
함수형 인터페이스의 추상 메서드 시그니처는 람다 표현식의 시그니처를 가리킨다. 람다 표현식의 시그니처를 서술하는 메서드를 함수 디스크립터라고 한다. 예시를 살펴보자.

- () -> void
- (Apple, Apple) -> int

## 람다 활용: 실행 어라운드 패턴
```java
public String processFile() throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
        return br.readLine();
    }
}
```
### 1단계: 동작 파라미터화를 기억하라
현재 코드는 파일에서 한 줄만 읽을 수 있는데, 한 번에 두 줄을 읽거나 가장 자주 사용되는 단어를 반환하려면 어떻게 해야 될까? processFile을 파라미터화하면 된다.

```java
String result = processFile((BufferedReader br) -> br.readLine() + br.readLine());
```

### 2단계: 함수형 인터페이스를 이용해서 동작 전달
```java
@FunctionalInterface
public interface BufferedReaderProcessor {
    String process(BufferedReader b) throws IOException;
}
```]

이렇게 함수형 인터페이스를 만들면 processFile 메서드의 인수로 인터페이스를 전달할 수 있다.

### 3단계: 동작 실행
BufferedReaderProcessor에 정의된 process 메서드의 시그니처와 일치하는 람다를 전달할 수 있게 되었다.

```java
public String processFile(BufferedReaderProcessor p) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
        return p.process(br);
    }
}
```

### 4단계: 람다 전달
이제 람다를 이용해서 다양한 동작들을 수행해보자.

```java
// 한 행을 처리하는 코드
String oneLine = processFile((BufferedReader br) -> br.readLine());

// 두 행을 처리하는 코드
String twoLine = processFile((BufferedReader br) -> br.readLine() + br.readLine());
```

## 함수형 인터페이스 사용
함수형 인터페이스는 오직 하나의 추상 메서드를 지정한다. 그리고 그 추상 메서드는 람다 표현식의 시그니처를 묘사한다. 이를 함수 디스크립터라고 앞에서 공부했다. 이제 다양한 함수형 인터페이스에 대해서 알아보자.

### Predicate
java.util.function.Predicate<T> 인터페이스는 test라는 추상 메서드를 정의한다. Predicate의 함수 디스크립터는 `T -> boolean`이다. 예제를 살펴보자.

```java
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}

public <T>List<T> filter(List<T> list, Predicate<T> p) {
    List<T> results = new ArrayList<>();
    for (T t : list) {
        if (p.test(t)) {
            results.add(t);
        }
    }
    return results;
}

Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();
List<String> nonEmpty = filter(listOfStrings, nonEmptyStringPredicate);
```

### Consumer
java.util.function.Consumer<T> 인터페이스는 제네릭 형식 T 객체를 받아서 void를 반환하는 accept 추상 메서드를 정의한다. 예제로 살펴보자.

```java
public interface Consumer<T> {
    void accept(T t);
}

public <T> void forEach(List<T> list, Consumer<T> c) {
    for (T t : list) {
        c.accept(t);
    }
}

forEach(
    Arrays.asList(1, 2, 3, 4, 5), 
    (Integer i) -> System.out.println(i)
);
```

### Function
java.util.function.Function<T, R> 인터페이스는 제네릭 형식 T를 인수로 받아서 제네릭 형식 R 객체를 반환하는 추상 메서드 apply를 정의한다. 이는 입력을 출력으로 매핑하는 람다를 정의할 때 사용할 수 있다.

```java
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}

public <T, R> List<R> map(List<T> list, Function<T, R> f) {
    List<R> result = new ArrayList<>();
    for (T t : list) {
        result.add(f.apply(t));
    }
    return result;
}

List<Integer> l = map(
    Arrays.asList("lambdas", "in", "action"),
    (String s) -> s.length()
);
```

### 기본형 특화
자바의 모든 형식은 참조형 아니면 기본형에 해당한다. 제네릭 파라미터는 참조형만 사용할 수 있다. 하지만 걱정할 필요가 없이 자바는 기본형을 참조형으로 변환하는 기능을 제공한다. 박싱은 기본형을 참조형으로 변환시켜주고, 그 반대로 변환하는 동작은 언박싱이라고 한다. 박싱과 언박싱을 자동으로 동작시키는 것을 오토박싱이라고 부르며, 이 기능들은 모두 자바가 제공해준다. 

```java
List<Integer> list = new ArrayList<>();
for (int i = 300; i < 400; i++) {
    list.add(i);
}
```

이렇게 int를 Integer로 박싱할 수 있다. 하지만 이런 변환 과정은 비용이 소모된다. 박싱한 값은 메모리를 더 소비하게 되고 기본형을 가져올 때도 메모리를 탐색하는 과정이 필요하다.

자바 8이 제공하는 기본형을 입출력으로 사용하는 상황에서 오토박싱 동작을 피할 수 있도록 특별한 버전의 함수형 인터페이스를 제공한다. 예제를 통해 살펴보자.

```java
public interface IntPredicate {
    boolean test(int t);
}

IntPredicate evenNumbers = (int i) -> i % 2 == 0;
evenNumbers.test(1000);

Predicate<Integer> oddNumbers = (Integer i) -> i % 2 != 0;
evenNumbers.test(1000);
```

위의 예제에서 evenNumbers는 int로 파라미터가 들어오고 리턴 타입도 int로 되기 떄문에 박싱이 일어나지 않아 참이 된다. 하지만, oddNumber는 int 파라미터가 Integer 타입으로 박싱이 일어나면서 거짓이 된다.