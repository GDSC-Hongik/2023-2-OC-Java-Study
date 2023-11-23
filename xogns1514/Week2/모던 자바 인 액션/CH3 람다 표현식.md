# CH3 람다 표현식

익명 클래스로 다양한 동작을 구현할 수 있지만 만족할 만큼  코드가 깔끔하지 않다. 이번 장에선느 더 깔끔한 코드로 동작을 구현하고 전달하는 자바 8의 새로운 기능인 람다 표현식을 설명한다.

---

## 3.1 람다란 무엇인가?

<aside>
💡 람다 표현식란? 메서드로 전달할 수 있는 익명 함수를 단순화한 것.

</aside>

특징

- 익명: 보통의 메서드와 달리 이름이 없으므로 익명이라 표현한다.
- 함수: 람다는 메서드처럼 특정 클래스에 종속되지 않으므로 함수라고 부른다.
- 전달: 람다 표현식을 메서드 인수로 전달하거나 변수로 저장할 수 있다.
- 간결성: 익명 클래스처럼 많은 자잘구레한 코드를 구현할 필요가 없다.

기존 익명함수를 통한 구현

```java
Comparator<Apple> byWeight = new Comparator<Apple>(){
	public int compare(Apple a1, Apple a2){
		return a1.getWeight().compareTo(a2.getWeight());
	}
};
```

람다를 이용한 코드

```java
Comparator<Apple> byWeight = 
	(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
```

- 람다를 이용하니 코드가 훨씬 간단해졌다.;

람다 코드는 3부분으로 나눠진다.

- 파라미터 리스트: Comparator의 compare 메서드 파라미터(사과 두개)
- 화살표: 화살표(→)는 람다의 파라미터 리스트와 바디를 구분한다
- 람다 바다: 두 사과의 무게를 비교한다. 람다의 반환값에 해당하는 표현식이다.

**자바 8에서 지원하는 다섯 가지 람다 표현식 예제**

```java
/*
1. String 형식의 파라미터 하나를 가지며 int를 반환한다. 
람다 표현식에는 return이 함축되어 있으므로 return 문을 명시적으로 사용하지 않아도 된다.
*/
(String s) -> s.length() 
/*
2. Apple 형식의 파라미터 하나를 가지며
boolean(사과가 150그램 보다 무거운지 결정) 을 반환
*/
(Apple a) -> a.getWeight() > 150
/*
3. int 형식의 파라미터 두 개를 가지며 리턴값이 없다.(void 리턴)
람다 표현식은 여러 행의 문장을 포함할 수 있다.
*/
(int x, int y) -> {
	System.out.println("Result:");
	System.out.println(x + y);
}
/*
4. 파라미터가 없으며 int 42를 반환한다.
*/
() -> 42
/*
5. Apple 형식의 파라미터 두개를 가지며 int(두 사과의 무게 비교 결과)를 반환한다.
*/
(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())
```

- 표현식 스타일(람다의 기본 문법)

(parameters) → expression

- 블록 스타일

(parameters) → {statements;}

## 3.2 어디에, 어떻게 람다를 사용할까?

<aside>
💡 함수형 인터페이스라는 문맥에서 람다 표현식을 사용할 수 있다.

</aside>

## 3.2.1 함수형 인터페이스

Predicate<T> 인터페이스로 필터 메서드를 파라미터화할 수 있다. Predicate<T>가 함수형 인터페이스이다. Predicate<T>는 오직 하나의 추상메서드만 지정한다.

```java
public interface Predicate<T>{
	boolean test(T t);
}
```

- 함수형 인터페이스란? 정확히 하나의 추상 메서드를 지정하는 인터페이스이다. ex) Comparator, Runnable

```java
public interface Comparator<T>
	int compare(T o1, T o2);
}

public interface Runnable {
	void run();
}

public interface ActionListner extends EventListener{
	void actionPerformed(ActionEvent e);
}

public interface Callable<V>{
	V call() throws Exception;
}

public interface PrivilegedAction<T> {
	T run();
}
```

❓함수형 인터페이스로 뭘 할 수 있을까? 람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달할 수 있으므로 **전체 표현식을 인터페이스의 인스턴스로 취급할 수 있다.**

## 3.2.2 함수 디스크립터

함수형 인터페이스의 추상 메서드 시그니처는 라마 표현식의 시그니처를 가리킨다.

❓함수 시그니처란: 함수의 매개변수와 반환값의 타입을 정의하는 방법.

- 람다 표현식의 시그니처를 서술하는 메서드를 `함수 디스크립터`(function descriptor) 라고 부른다.

ex) () → void: 파라미터 리스트가 없으며 void를 반환하는 함수를 의미

ex) (Apple, Apple) → int: 두 개의 Apple을 인수로 받아 int를 반환하는 함수

```java
public void process(Runnable r){
	r.run();
}

process(() -> System.out.println("This is awesome!!"));
//위 코드를 실행하면 "This is awesome!!"이 출력된다. 인수가 없으며 void를 반환하는 람다 표현식이다.
//Runnable 인터페이스의 run 메서드 시그니처와 같다
```

### @FunctionalIterface란?

- 함수형 인터페이스임을 가리키는 어노테이션이다.
- 위 어노테이션을 선언했지만 실제로 함수형 인터페이스가 아니면 컴파일러가 에러를 발생시킨다.

## 3.3 람다 활용: 실행 어라운드 패턴

- 자원 처리에 사용하는 `순환패턴`(recurrent pattern)은 자원을 열고, 처리한 다음에, 자원을 닫는 순서로 이루어진다.
- 설정(setup)과 정리(cleanup) 과정은 대부분 비슷하다.
    
<img width="319" alt="스크린샷 2023-08-14 오후 2 11 08" src="https://github.com/xogns1514/test/assets/66353672/7240040e-fffd-480b-8f93-64081147cfea">

    
- 실제 자원을 처리하는 코드를 설정과 정리 두 과정이 둘러싸는 형태를 `실행 어라운드 패턴` 이라고 한다.
- 준비와 정리는 틀처럼 정해져 있고, 실행 부분만 바뀐다
- 공통적으로 사용되는 코드를 template처럼 만들어 두고, 변경되는 부분은 호출(caller)하는 곳에서 구현해서 넘겨준다.

```java
public String processFile() throws IOException{
	try (BufferedReader br = 
		new BufferedReader(new FileReader("data.txt"))){
		return br.readLine(); //실제 필요한 작업을 하는 행이다
	}
}
//자바 7의 try-with-resources 구문
```

## 3.3.1 1단계: 동작 파라미터화를 기억하라

❓위 코드는 한 번에 한 줄만 읽을 수 있다. 한 번에 두줄을 읽거나 가장 자주 사용되는 단어를 반환하려면 어떻게 해야 할까?

→ 기존의 설정, 정리 과정은 재사용하고 processFile의 동작을 파라미터화 하는 것이다. BufferedReader를 이용해 다른 동작을 수행할 수 있도록 processFile 메서드로 동작을 전달해야 한다

- 람다를 이용한 동작 전달

```java
String result = processFile((BufferedReader br ->
				br.readLine() + br.readLine()));
//한 번에 두 행을 읽게 하는 코드
```

## 3.3.2 2단계: 함수형 인터페이스를 이용해서 동작 전달

함수형 인터페이스(@Functional Interface) 자리에 람다를 사용할 수 있다. 

- BufferedReader → String과 IOException을 던질 수 있는 시그니처와 일치하는 함수형 인터페이스를 만들어야 한다.

```java
@FunctionalInterface
public interface BufferedReaderProcessor {
	String process(BufferedReader b) throws IOException;
}

//위 인터페이스를 processFile 메서드의 인수로 전달할 수 있다
public String proccessFile(BufferedReaderProcessor p)throws IOExeption{
	...
}
```

## 3.3.3 3단계: 동작 실행

위 process 메서드의 시그니처 (BufferedReader → String)와 일치하는 람다를 전달할 수 있다.

람다 표현식으로 전달된 코드는 함수형 인터페이스의 인스턴스로 전달된 코드와 같은 방식으로 처리한다.

```java
public String processFile(BufferedReaderProcessor p) throws 
	IOException{
try (BufferedReader br = 
		new BufferedReader(new FileReader("data.txt"))){
	return p.process(br); //BufferedReader 객체 처리
	}
}
```

## 3.3.4 4단계: 람다 전달

람다를 이용한 다양한 동작 processFile에 전달

```java
//한행 처리
String oneLine = processFile((BufferedReader br) -> br.readLine());
//두줄 처리
String twoLines = processFile((BufferedReader br) -> br.readLine() + br.readLine());
```

## 3.4 함수형 인터페이스 사용

함수형 인터페이스의 추상 메서드는 람다 표현식의 시그니처를 묘사한다. 함수형 인터페이스의 추상 메서드 시그니처를 `함수 디스크립터` 라고 한다. 자바 8 라이브러리에서 java.util.function 패키지로 여러 가지 새로운 함수형 인터페이스를 포함하고 있다.

### 3.4.1 Predicate

- test는 제네릭 형식 T의 객체를 인수로 받아 불리언을 반환

```java
@FunctionalInterface
public interface Predicate<T> {
	boolean test(T t);
}
public <T> List<T> filter(List<T> list, Predicate<T> p){
	List<T> results = new ArrayList<>();
	for(T t: list){
		if(p.test(t)){
			results.add(t);
		}
	}
	return results;
}
//String 객체를 인수로 받는 람다
Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();
List<String> nonEmpty = filter(listOfStrings, nonEmptyStringPredicate);
```

## 3.4.2 Consumer

java.util.function.Consumer<T> 제네릭 형식 T 객체를 받아 void 를 반환하는 accept 추상 메서드 정의

T형식의 객체를 인수로 받아 어떤 동작 사용하고 싶을 때 사용

```java
@FunctionalInterface
public interface Consumer<T> {
	void accept();
}

public <T> void forEach(List<T> list, Consumer<T> c){
	for(T t: list){
		c.accept(t);
	}
}
//Integer 리스트 인수로 받아 각 항목 출력
forEach(
	Arrays.asList(1,2,3,4,5),
	(Integer i) -> System.out.println(i)
);
```

## 3.4.3 Function

java.util.function.Function<T,R> 제네릭 형식 T를 인수로 받아 제네릭 형식 R객체를 반환하는 추상 메서드 apply 정의

```java
@FunctionalInterface
public interface Function<T, R>{
	R apply(T t);
}
public <T, R> List<T> map(List<T> list, Function<T, R> f){
	List<R> result = new ArrayList<>();
	for(T t: list){
		result.add(f.apply(t));
	}
	return result;
}
//String 리스트 인수로 받아, 각 String 길이 포함하는 Integer 리스트로 변환하는 map 메서드
List<Integer> l = map(
		Arrays.asList("lambdas", "in", "action"),
		(String s) -> s.length()
);
```

### 기본형 특화

자바의 형식은 참조형(reference type)(Byte, Integer, Object, List) 아니면 기본형(primitive type)(int, double, byte, char)에 해당된다. 제네릭 파라미터에는 참조형만 사용할 수 있다.

자바에서는 기본형을 참조형으로 변환하는 기능을 제공한다.

박싱(boxing): 기본형을 참조형으로 변환하는 기능

언박싱(unboxing): 참조형을 기본형으로 변환하는 반대 동작

오토박싱(autoboxing): 박싱과 언박싱이 자동으로 이루어지는 기능

```java
List<Integer> list = new ArrayList<>();
for(int i = 300; i < 400; i++){
	list.add(i);
}
//int가 Integer로 박싱됨
```

### 문제점

- 박싱한 값은 기본형을 감싸는 래퍼(Wrapper)며 힙에 저장된다. 박싱한 값은 메모리를 더 소비하며 기본형을 가져올 때도 메모리를 탐색하는 과정이 필요하다.

📌 자바 8에서는 기본형을 입출력으로 사용하는 상황에서 오토박싱 동작을 피할 수 있도록 특별한 버전의 함수형 인터페이스를 제공한다.

```java
public interface IntPredicate {
	boolean test(int t);
}

IntPredicate evenNumbers = (int i) -> i % 2 == 0;
evenNumbers.test(1000);//참(박싱이 없다)

Predicate<Integer> oddNumbers = (Integer i) -> i % 2 != 0;
evenNumbers.test(1000);//거짓(박싱이 있다)
```

일반적으로 특정 형식을 입력으로 받는 함수형 인터페이스의 이름 앞에는 DoublePredicate, IntConsumer, LongBinaryOperators, IntFuction 처럼 형식명이 붙는다.

### 예외, 람다, 함수형 인터페이스의 관계

📌 함수형 인터페이스는 확인된 예외(checked Exception)를 던지는 동작을 허용하지 않는다. 즉, 예외를 던지는 람다 표현식을 만들려면 확인된 예외를 선언하는 함수형 인터페이스를 직접 정의하거나 람다를 try/catch 블록으로 감싸야 한다.
![image](https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/71186266/a551f67a-4c2b-4e2c-8e27-e6a46ef564ee)

- 에러(Error): 에러는 시스템에 비정상적인 상황이 발생했을 경우에 발생.
    
    ex) `메모리 부족(OutofMemoryError)`이나 `스택오버플로우(StackOverflowError)`
    
- 체크 예외(checked Exception): RuntimeException의 하위 클래스가 아니면서 Exception 클래스의 하위 클래스들. 반드시 에러 처리를 해야함.
    
    ex) `존재하지 않는 파일의 이름을 입력(FileNotFoundException), 실수로 클래스의 이름을 잘못 적음(ClassNotFoundException)`
    
- 언체크 예외(unchecked exception): 언체크 예외는 RuntimeException의 하위 클래스들. 에러 처리를 강제하지 않는다.
    
    ex) `배열의 범위를 벗어난(ArrayIndexOutOfBoundsException), 값이 null이 참조변수를 참조(NullPointerException)`
    

```java
@FunctionalInterface
public interface BufferedReaderProcessor {
	String process(BufferedReader b) throws IOException;
}
BufferedReaderProcessor p = (BufferedReader br) -> br.readLine();

//Function<T,R> 형식의 함수형 인터페이스
Fuction<BufferedReader, String> f = (BufferedReader b) -> {
	try{
		return b.readLine();
	}
	catch(IOException e){
		throws new RuntimeException(e);
	}
};
```