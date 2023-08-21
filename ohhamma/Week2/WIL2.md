# [Ch2] 동작 파라미터화 코드 전달하기

<aside>
✅ **이 장의 내용**

- 변화하는 요구사항에 대응
- 동작 파라미터화
- 익명 클래스
- 람다 표현식 미리보기
- 실전 예제 : Comparator, Runnable, GUI
</aside>

- `동적 파라미터화` : 아직은 어떻게 실행할 것인지 결정하지 않은 코드 블록

    → 나중에 프로그램에서 호출

    → 코드 블록에 따라 메서드의 동적 파라미터화

    → 자주 바뀌는 요구사항에 효과적으로 대응 가능

- 그러나, 동적 파라미터화를 추가하려면 쓸데없는 코드가 늘어남

    → 자바8의 람다 표현식으로 문제 해결!
    

## 2.1 변화하는 요구사항에 대응하기

<aside>
🍏 (예시) 농장 재고목록 리스트에서 녹색 사과만 필터링하는 기능 추가

</aside>

### 2.1.1 첫 번째 시도 : 녹색 사과 필터링

```java
public static List<Apple> filterGreenApples(List<Apple> inventory) {
	List<Apple> result = new ArrayList<>();
	for (Apple apple: inventory) {
		if (**GREEN.equals(apple.getColor()**) {
			result.add(apple);
		}
	}
	return result;
}
```

<aside>
🍎 그런데 갑자기 농부가 변심하여 빨간 사과도 필터링하고 싶어진다면?

</aside>

→ 단순히 메서드를 복사 (filterRedApples)

→ 그러나 다양한 변화에는 적절하게 대응 x

→ 비슷한 코드가 반복 존재한다면 그 코드를 `추상화`하자!

### 2.1.2 두 번째 시도 : 색을 파라미터화

```java
public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
	List<Apple> result = new ArrayList<>();
	for (Apple apple: inventory) {
		if (**apple.getColor().equals(color)**) {
			result.add(apple);
		}
	}
	return result;
}
```

```java
List<Apple> greenApples = filterApplesByColor(inventory, GREEN);
List<Apple> redApples = filterApplesByColor(inventory, RED);
```

<aside>
⚖️ 그런데 갑자기 농부가 색 이외에도 가벼운 사과와 무거운 사과로 구분하고 싶어진다면?

</aside>

→ 무게 정보 파라미터 추가

→ 그러나 색 필터링 코드와 대부분 중복

→ `DRY`(don’t repeat yourself) 원칙을 어기는 것!

### 2.1.3 세 번째 시도 : 가능한 모든 속성으로 필터링

```java
public static List<Apple> filterApples(List<Apple> inventory, Color color, int weight, boolean flag) {
	List<Apple> result = new ArrayList<>();
	for (Apple apple: inventory) {
		if (**(flag && apple.getColor().equals(color)) ||
				(!flag && apple.getWeight() > weight)**) {
			result.add(apple);
		}
	}
}
```

```java
List<Apple> greenApples = filterApples(inventory, GREEN, 0, true);
List<Apple> heavyApples = filterApples(inventory, null, 150, false);
```

→ 모든 속성을 메서드 파라미터로 추가한 모습 (정말 형편없는 코드이다!)

→ 요구사항이 바뀌었을 때에 유연하게 대응 x

→ 어떤 기준으로 사과를 필터링할 것인지 효과적으로 전달해보자

→ `동적 파라미터화`

## 2.2 동작 파라미터화

<aside>
💡 변화하는 요구사항에 좀 더 `유연하게` 대응할 수 있는 방법 필요

</aside>

- `Predicate` 함수 : 참 or 거짓 반환
    
    → 사과의 어떤 속성에 기초해서 불리언값 반환
    
- 선택 조건을 결정하는 `인터페이스` 정의

```java
public interface ApplePredicate {
	boolean test (Apple apple);
}
```

- 여러 버전의 ApplePredicate 정의 가능

```java
// 무거운 사과만 선택
public class AppleHeavyWeightPredicate implements ApplePredicate {
	public boolean test(Apple apple) {
		return apple.getWeight() > 150;
	}
}

//녹색 사과만 선택
public class AppleGreenColorPredicate implements ApplePredicate {
	public boolean test(Apple apple) {
		return GREEN.equals(apple.getColor());
	}
}
```

→ ApplePredicate라는 사과 선택 전략을 `캡슐화`함

→ `전략 디자인 패턴` : 런타임에 알고리즘 선택

### 2.2.1 네 번째 시도 : 추상적 조건으로 필터링

```java
public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
	List<Apple> result = new ArrayList<>();
	for(Apple apple: inventory) {
		if(p.test(apple)) {		// Predicate 객체로 사과 검사 조건 캡슐화
			result.add(apple);
		}
	}
	return result;
}
```

→ filterApples 메서드의 동작을 파라미터화

→ 안타깝게도 메서드는 객체만 인수로 받으므로 test 메서드를 객체로 감싸서 전달해야함

<aside>
💡 `람다`를 이용해서 여러 개의 클래스 정의하지 않고도 표현식 메서드로 전달 가능

</aside>

- 유연한 API를 만들 때 → 동작 파라미터화 중요

<aside>
💯 (**퀴즈 2-1) 사과 리스트를 인수로 받아 다양한 방법으로 문자열 생성**

```java
public interface AppleFormatter {
	String accept(Apple apple);
}

public class AppleHeavyFormatter implements AppleFormatter {
	public String accept(Apple apple) {
		String characteristic = apple.getWeight() > 150 ? "heavy" : "light";
		return "A " + chararcteristic + " apple";
	}
}

public class AppleWeightFormatter implements AppleFormatter {
	public String accept(Apple apple) {
		return "An apple that weighs " + apple.getWeight();
	}
}

public static void prettyPrintApple(List<Apple> inventory, AppleFormatter f) {
	for(Apple apple: inventory) {
		String output = f.accpet(apple);
		System.out.println(output);
	}
}
```

</aside>

## 2.3 복잡한 과정 간소화

- 메서드로 새로운 동작을 전달하려면 → 인터페이스를 구현하는 여러 클래스 정의 → 인스턴스화
    - 상당히 번거로움

<aside>
💡 `익명 클래스`로 코드의 양을 줄이자!

</aside>

### 2.3.1 익명 클래스

- 자바의 local class(블록 내부에 선언)와 유사
- 이름이 없는 클래스
- 클래스 선언과 동시에 인스턴스화 → 필요한 구현 만들어 사용

### 2.3.2 다섯 번째 시도 : 익명 클래스 사용

```java
// 메서드의 동작 직접 파라미터화
List<Apple> redApples = filterApples(inventory, new Apple Predicate() {
	public boolean test(Apple apple) {
		return RED.equals(apple.getColor());
	}
});
```

- GUI 앱에서 이벤트 핸들러 객체 구현할 때 종종 사용
- 그러나 여전히 많은 공간 차지
- 많은 프로그래머가 익명 클래스 사용에 익숙하지 x
    
    ~~→ 익명 클래스 문제 나도 틀렸다..~~
    
    - 코드의 장황함(verbosity)은 나쁜 특성
    - 유지보수에 시간이 오래걸림

### 2.3.3 여섯 번째 시도 : 람다 표현식 사용

```java
List<Apple> result =
		filterApples(inventory, (Apple apple) -> RED.equals(apple.getColor()));
```

- 코드가 간결해졌을 뿐만 아니라 문제를 더 설명함

### 2.3.4 일곱 번째 시도 : 리스트 형식으로 추상화

```java
public interface Predicate<T> {
	boolean test(T t);
}

// 형식 파라미터 T 등장
public static <T> List<T> filter(List<T> list, Predicate<T> p) {
	List<T> result = new ArrayList<>();
	for(T e: list) {
		if(p.test(e)) {
			result.add(e);
		}
	}
	return result;
}
```

```java
// 사과, 정수, 문자열 등의 리스트에 필터 메서드 사용 가능
List<Integer> evenNumbers =
		filter(numbers, (Integer i) -> i % 2 == 0);
```

- `유연성`과 `간결함`이라는 두 마리 토끼를 모두 잡음!

## 2.4 실전 예제

- 동작 파라미터화 패턴
    
    → 동작을 캡슐화
    
    → 메서드로 전달
    
    → 메서드의 동작 파라미터화
    

### 2.4.1 Comparator로 정렬하기

- 컬렉션 정렬: 반복된 프로그래밍 작업
- 어떤 기준으로 정렬할지?
    
    → 변화하는 요구사항에 쉽게 대응할 수 있도록 짜보자
    

```java
// java.util.Comparator
// sort의 동작 파라미터화
public interface Comparator<T> {
	int compare(T o1, T o2);
}
```

```java
// Comparator 구현 -> sort 메서드 동작 다양화
// 무게가 적은 순서로 목록에서 사과 정렬
// 익명 클래스 사용
inventory.sort(new Comparator<Apple>() {
	public int compare(Apple a1, Apple a2) {
		return a1.getWeight().compareTo(a2.getWeight());
	}
});

// 람다 표현식 사용
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

### 2.4.2 Runnable로 코드 블록 실행하기

- 자바 스레드로 병렬로 코드 블록 실행
- 여러 스레드가 각자 다른 코드 실행

```java
// java.lang.Runnable
// Runnable 인터페이스를 이용해서 실행할 코드 블록 지정
public interface Runnable {
	void run();
}
```

```java
// Runnable을 이용해서 다양한 동작 스레드로 실행
// 익명 클래스 사용
Thread t = new Thread(new Runnable() {
	public void run() {
		System.out.println("Hello world");
	}
});

// 람다 표현식 이용
Thread t = new Thread(() -> System.out.println("Hello world"));
```

### 2.4.3 Callable을 결과로 반환하기

- ExecutorService → 태스크를 스레드 풀로 보내고 결과를 Future로 저장
- Callable 인터페이스를 이용해 결과를 반환하는 태스크 생성
- Runnable 업그레이드 버전

```java
// java.util.concurrent.Callable
public interface Callable<V> {
	V call();
}
```

```java
// 실행 서비스에 태스크 제출
// 태스크를 실행하는 스레드 이름 반환
// 익명 클래스 사용
ExecutorService executorService = Executors.newCachedThreadPool();
Future<String> threadName = executorService.submit(new Callable<String>() {
	@Override
		public String call() throws Exception {
			return Thread.currentThread().getName();
	}
});

// 람다 표현식 이용
Future<String> threadName = executorService.submit(
				() -> Thread.currentThread().getName());
```

### 2.4.4 GUI 이벤트 처리하기

- GUI 프로그래밍 : 마우스 클릭, 문자열 위로 이동하는 이벤트에 대응하는 동작 수행
- 모든 동작에 반응할 수 있어야함
- (자바FX) setOnAction 메서드에 EventHandler 전달
    
    → 이벤트에 어떻게 반응할지 설정
    

```java
Button button = new Button("Send");

// EventHandler -> setOnACtion 메서드의 동작 파라미터화
// 익명 클래스 사용
button.setOnAction(new EventHandler<ActionEvent>() {
	public void handle(ActionEvent event) {
		label.setText("Sent!!");
	}
});

// 람다 표현식 이용
button.setOnAction((ActionEvent event) -> label.setText("Sent!!"));
```

## 2.5 마치며

- 동작 파라미터화 : 코드를 메서드 인수로 전달
    
    → 메서드 내부적으로 다양한 동작 수행
    
    → 변화하는 요구사항에 더 잘 대응하는 코드 구현 (비용↓)
    
- 익명 클래스로 어느 정도 코드 깔끔하게
- 자바 8에서 람다 표현식 제공
- 자바 API의 많은 메서드
    
    → 정렬, 스레드, GUI 처리 등의 다양한 동작으로 파라미터화 가능
    

# [Ch3] 람다 표현식

<aside>
✅ **이 장의 내용**

- 람다란 무엇인가?
- 어디에, 어떻게 람다를 사용하는가?
- 실행 어라운드 패턴
- 함수형 인터페이스, 형식 추론
- 메서드 참조
- 람다 만들기
</aside>

## 3.1 람다란 무엇인가?

- `람다 표현식` : 메서드로 전달할 수 있는 익명함수를 단순화한 것
- 특징
    - `익명` : 이름이 없음
    - `함수` : 특정 클래스에 종속되지 x
        - 파라미터 리스트, 바디, 반환 형식, 발생할 수 있는 예외 리스트 포함
    - `전달` : 메서드 인수로 전달하거나 변수로 저장 가능
    - `간결성` : 자질구레한 코드 out
        - 동작 파라미터 형식의 코드를 더 쉽게 구현 가능 → 간결성↑ 유연성↑

```java
// 람다는 세 부분으로 이루어진다
(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
```

- `파라미터 리스트` : compare 메서드 파라미터
- `화살표` : 람다의 파라미터 리스트와 바디 구분
- `람다 바디` : 람다의 반환값에 해당하는 표현식

```java
// 람다의 기본 문법
// expression style
(parameters) -> expression

// block-style
(parameters) -> { statements; }
```

<aside>
💯 (**퀴즈 3-1) 람다 규칙에 맞지 않는 람다 표현식 고르기**

---

1. `() → {}` : no param, void 반환, 바디 없는 메서드
2. `() → “Raoul”` : no param, String 반환
3. `() → {return “Mario”;}` : no param, String 반환
4. `(Integer i) → return “Alan” + i;` : should be block-style (INCORRECT)
5. `(String s) → {”Iron Man”;}` : should be expression style (INCORRECT)
</aside>

## 3.2 어디에, 어떻게 람다를 사용할까?

- `함수형 인터페이스`라는 문맥에서 람다 표현식 사용 가능

### 3.2.1 함수형 인터페이스

- 정확히 `하나의 추상 메서드`를 지정하는 인터페이스

```java
public interface Predicate<T> {
	boolean test (T t);
}
```

- 자바 API의 함수형 인터페이스 : Comparator, Runnable 등
- 람다 표현식 → 전체 표현식을 함수형 인터페이스의 인스턴스로 취급할 수 있음

```java
// 람다 사용
Runnable r1 = () -> System.out.println("Hello World 1");

// 익명 클래스 사용
Runnable r2 = new Runnable() {
	public void run() {
		System.out.println("Hello World 2");
	}
};

public static void process(Runnable r) {
	r.run();
}

process(r1);
process(r2);

// 직접 전달된 람다 표현식
process(() -> System.out.println("Hello World 3"));
```

### 3.2.2 함수 디스크립터

- 람다 표현식의 시그니처를 서술하는 메서드
    
    ex) `() → void` : no param, void 반환 함수
    
- 람다 표현식
    - 변수에 할당 or 함수형 인터페이스를 인수로 받는 메서드로 전달 가능
    - 함수형 인터페이스의 추상 메서드와 같은 시그니처를 가짐

<aside>
❓ `@FunctionalInterface`

- 함수형 인터페이스임을 가리키는 어노테이션
- 실제로 함수형 인터페이스가 아니면 에러 발생
</aside>

## 3.3 람다 활용 : 실행 어라운드 패턴

- `순환 패턴` : 자원을 연다 → 처리한다 → 자원을 닫는다
    
    ex) 데이터베이스의 파일 처리
    
- `실행 어라운드 패턴` : 실제 자원을 처리하는 코드를 `설정`과 `정리` 두 과정이 둘러싸는 형태
    
    → 자원을 명시적으로 닫을 필요가 없어짐
    
    → 간결한 코드 구현
    

```java
public String processFile() throws IOException {
	// 파일에서 한 행 읽기
	try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
			return br.readLine();		// 실제 필요한 작업을 하는 행
	}
}
```

### 3.3.1 1단계 : 동작 파라미터화를 기억하라

<aside>
❓ 현재 코드: 파일에서 한 번에 한 줄만 읽을 수 있음

→ 한 번에 두 줄을 읽거나

→ 가장 자주 사용되는 단어를 반환하려면?

</aside>

→ 기존의 설정, 정리 과정 재사용

→ processFile 메서드만 다른 동작을 수행하도록

→ processFile의 동작 파라미터화

### 3.3.2 2단계 : 함수형 인터페이스를 이용해서 동작 전달

- 함수 인터페이스를 만들어야함
    - `BufferedReader → String`
    - IOException을 던질 수 있도록

```java
@FunctionalInterface
public interface BufferedReaderProcessor {
	String process(BufferedReader b) throws IOException;
}
```

- 정의된 인터페이스를 메서드의 인수로 전달

```java
public String processFile(BufferedReaderProcessor p) throws IOException {
	...
}
```

### 3.3.3 3단계 : 동작 실행

- process 메서드의 시그니처와 일치하는 람다 전달 가능

```java
// processFile 바디 내에서 p의 process 호출 가능
public String processFile(BufferedReaderProcessor p) throws IOException {
	try (BufferedReader br =
					new BufferedReader(new FileReader("data.txt"))) {
		return p.process(br);		//BufferedReader 객체 처리
	} 
}
```

### 3.3.4 4단계 : 람다 전달

- 람다를 이용해서 다양한 동작을 processFile 메서드로 전달 가능

```java
// 한 행을 처리하는 코드
String oneLine = processFile((BufferedReader br) -> br.readLine());

// 두 행을 처리하는 코드
String twoLines = processFile((BufferedReader br) -> br.readLine() + br.readLine());
```

## 3.4 함수형 인터페이스 사용

- `함수 디스크립터` : 함수형 인터페이스의 추상 메서드 시그니처
- java.util.function 패키지 → 여러 가지 함수형 인터페이스 제공
    - Predicate, Consumer, Function

### 3.4.1 Predicate

- `java.util.function.Predicate<T>` 인터페이스
    - 추상 메서드 `test`
    - T → boolean
- 따로 정의할 필요 없이 바로 사용 가능
- `boolean` 표현식이 필요한 상황

```java
@FunctionalInterface
public interface Predicate<T> {
	boolean test(T t);
}
public <T> List<T> filter(List<T> list, Predicate<T> p) {
	List<T> results = new ArrayList<>();
	for(T t: list) {
		if(p.test(t)) {
			results.add(t);
		}
	}
	return results;
}

// String 객체를 인수로 받는 람다 정의
Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();
List<String> nonEmpty = filter(listOfStrings, nonEmptyStringPredicate);
```

### 3.4.2 Consumer

- `java.util.function.Consumer<T>` 인터페이스
    - 추상 메서드 `accept`
    - T → void
- 어떤 동작을 수행하고 싶을 때 사용

```java
@FunctionalInterface
public interface Consumer<T> {
	void accept(T t);
}

// Integer 리스트를 인수로 받아
// 각 항목에 어떤 동작을 수행하는 forEach 메서드 정의
public <T> void forEach(List<T> list, Consumer<T> c) {
	for(T t: list) {
		c.accept(t);
	}
}

// 리스트의 모든 항목 출력
forEach(
		Arrays.asList(1, 2, 3, 4, 5),
		(Integer i) -> System.out.println(i)
);
```

### 3.4.3 Function

- `java.util.function.Function<T, R>` 인터페이스
    - 추상 메서드 `apply`
    - T → R
- 입력을 출력으로 매핑하는 람다 정의할 때 활용 가능

```java
@FunctionalInterface
public interface Function<T, R> {
	R apply(T t);
}

public <T, R> List<R> map(List<T> list, Function<T, R> f) {
	List<R> result = new ArrayList<>();
	for(T t: list) {
		result.add(f.apply(t));
	}
	return result;
}

// String 리스트를 인수로 받아
// 각 String의 길이를 포함하는 Integer 리스트로 변환
// [7, 2, 6]
List<Integer> I = map(
		Arrays.asList("lambdas", "in", "action"),
		(String s) -> s.length()
);
```

### 기본형 특화

- 제네릭 함수형 인터페이스 : `Predicate<T>` `Consumer<T>` `Function<T, R>`
- 특화된 형식의 함수형 인터페이스 존재
- 자바의 모든 형식 `참조형` or `기본형`
    - 참조형 : Byte, Integer, Object, List
    - 기본형 : int, double, byte, char
- 제네릭 파라미터 → `참조형`만 사용 가능

<aside>
💡 **박싱** & **언박싱** & **오토박싱**

- 박싱 : 기본형 → 참조형으로 변환
- 언박싱 : 참조형 → 기본형으로 변환
- 오토박싱 : 박싱&언박싱이 자동으로 이루어짐

```java
List<Integer> list = new ArrayList<>();
for (int i = 300; i < 400; i++) {
	list.add(i);		// int -> Integer (박싱)
}
```

- 변환 과정에 비용이 소모됨
    - 박싱한 값 : 기본형을 감싸는 wrapper, 힙에 저장
    - 기본형 가져올 때 메모리 탐색 과정 필요
</aside>

- 자바 8에서 제공하는 특별한 버전의 함수형 인터페이스
    - 기본형을 입출력으로 사용
    - 오토방식 동작 피할 수 있도록

```java
public interface IntPredicate {
	boolean test(int t);
}

IntPredicate eventNumbers = (int i) -> i % 2 == 0;
eventNumbers.test(1000);	// 참 (박싱x)

Predicate<Integer> oddNumbers = (Integer i) -> i % 2 != 0;
oddNumbers.test(1000);		// 거짓 (박싱o)
```

- 특정 형식을 입력으로 받는 함수형 인터페이스의 이름 앞에 `형식명`이 붙음
    - DoublePredicate
    - IntConsumer
    - LongBinaryOperator
    - IntFunction
- Function 인터페이스 : 다양한 출력 형식 파라미터 제공
    - ToIntFunction<T>
    - InttoDoubleFunction
- 필요하다면 우리가 직접 함수형 인터페이스 만들 수 있음
    - `(T, U) → R`로 함수 디스크립터 설명