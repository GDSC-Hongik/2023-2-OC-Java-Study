# CH2 동작 파라미터화 코드 전달하기

---

## 2.1 변화하는 요구사항에 대응하기

❓어떤 상황에서 일을 하든 소비자 요구사항은 항상 바뀐다. 시시각각 변하는 사용자 요구 사항에 어떻게 대응해야 할까??

A: 엔지니어링적인 `비용이 가장 최소화`될 수 있으면 좋다. 또한 `새로 추가한 기능은 쉽게 구현`할 수 있어야하며, 장기적인 관점에서 `유지보수가 쉬어야 한다`.

기존의 농장 재고목록 애플리케이션에서 리스트에서 녹색 사과만 필터링하는 기능을 추가하는 예제를 통해 이해해보자.

## 2.1.1 첫 번째 시도 : 녹색 사과 필터링

```java
//사과 색을 정의된 enum
enum Color {RED, GREEN}

//첫 번째 시도 결과 코드
public static List<Apple> filterGreenApples(List<Apple> inventory) {
	List<Apple> result = new ArrayList<>(); //필터링된 사과를 저장할 리스트
	for(Apple apple : inventory){
		if(GREEN.equals(apple.getColor()){ //녹색사과를 필터링함
			result.add(apple);
		}
	}
	return result;
}
```

### 위 코드가 가질 수 있는 문제점

❓만약 소비자의 요구사항이 녹색 사과에서 빨간 사과를 필터링하는 것으로 바뀐다면 어떻게 수정해야할까???

A: GREEN.equals(apple.getColor())에 해당하는 코드를 RED.equals(apple.getColor()) 로 수정한 새로운 메서드를 만들어야 할 것이다.

❗️소비자가 더 다양한 색을 필터링하도록 요구한다면, 반복되는 코드를 계속해서 작성해야 한다. 이는 유지보수하기도 어려우며, 비용이 많이 든다

<aside>
💡 규칙: 거의 비슷한 코드가 반복 존재한다면 그 코드를 추상화한다

</aside>

## 2.1.2 두 번째 시도: 색을 파라미터화

위 코드에서 반복되는 코드가 존재하는 문제점을 발견했다. 이를 해결할 수 있는 방법 중 하나는, 색을 파라미터 값을 받는 것이다.

```java
//색깔을 파라미터로 받는 코드
public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color)
{
	List<Apple> result = new ArrayList<>();
	for(Apple apple: inventory){
		if(apple.getColor().equals(color)){//파라미터로 받은 color와 일치한 사과 필터링
			result.add(apple);
		}
	}
	return result;
}

//메서드 호출 예시
List<Apple> greenApples = filterApplesByColor(inventory, GREEN);
List<Apple> redApples = filterApplesByColor(inventory, RED);
```

❓만약 소비자의 요구사항이 색깔뿐만 아니라, 무게도 필터링 하고 싶다고 하면 어떻게 해야할까? 

첫번째 해결: 색깔 필터링과 같이 무게 파라미터를 받아 필터링하는 메서드를 만든다

```java
public static List<Apple> filterApplesByWeigth(List<Apple> inventory, int weight)
{
	List<Apple> result = new ArrayList<>();
	for(Apple apple : inventory){
		if(apple.getWeight() > weight){//파라미터로 받은 무게보다 높은 사과 필터링
			result.add(apple);
		}
	}
	return result;
}
```

### 위 코드가 가질 수 있는 문제점

- 필터링 부분만 제외하면 색깔 필터링 함수와 거의 중복된다. → 이는 소프트웨어 공학의 DRY(Dont Repeat Yourself)의 원칙을 어기는 것이다.
- 탐색 과정을 고쳐 성능을 개선하려면, 비슷한 메서드들의 전체 구현을 고쳐야 한다. 즉, 엔지니어링적으로 비싼 대가를 치러야 한다.

## 2.1.3 세 번째 시도: 가능한 모든 속성으로 필터링

위 코드에서 비슷한 메서드의 중복의 문제점을 발견했다. 이를 해결할 한가지 방법으로는 색과 무게를 filter라는 메서드로 합치는 방법이다. 어떤 것을 기준으로 필터링을 할 지는 플래그를 추가하여 구현한다. 

```java
public static List<Apple> filterApples(List<Apple> inventory, Color color,
																				int weight, boolean flag){
	List<Apple> result = new ArrayList<>();
	for(Apple apple : inventory){
		if((flag && apple.getColor().equals(color)) || //flag에 따라 색, 무게 필터링 선택
			 (!flag && apple.getWeight() > weight)){
				result.add(apple);
		}
	}
	return result;
}

//메서드 사용
List<Apple> greenApples = filterApples(inventory, GREEN, 0, true);
List<Apple> heavyApples = filterApples(inventory, null, 150, false);
```

### 위 코드가 가질 수 있는 문제점

- 메서드를 이용하는 코드가 마음에 들지 않는다. false와 true는 무엇을 의미 하는지 파악해야한다.
- 요구사항이 바뀌었을 때 유연하게 대응할 수 없다. 예를 들어 무거운 사과중 초록색 사과를 필터링 해달라는 요구사항에는 이 메서드를 사용할 수 없다.

### ❗️지금까지 문자열, 정수, 불리언 등으로 filterApples 메서드를 파라미터화 했다. 하지만 어떤 기준으로 사과를 필터링할 것인지 효과적으로 전달할 수 있다면 더 좋을 것이다. 또한 요구사항 변경에 더 유연하게 대응 할 수 있다면 좋을 것이다.

→ 동작 파라미터화를 이용해 유연성을 얻을 수 있다.

## 2.2 동작 파라미터화

사과의 어떤 속성에 기초해서 불리언 값을 반환하는 방법이 있다. (사과가 녹색인가? 150그램 이상인가?)

참 또는 거짓을 반환하는 함수를 프레디케이트(Predicate) 라고 한다.

### 선택 조건을 결정하는 인터페이스

```java
public interface ApplePredicate{
	boolean test(Apple apple);
}
```

### 다양한 선택 조건을 대표하는 여러 버전의 ApplePredicate

```java
public class AppleHeavyWeightPredicate implements ApplePredicate{
	public boolean test(Apple apple) {
		return apple.getWeight() > 150;//무거운 사과만 선택
	}
}
```

```java
public class AppleGreenColorPredicate implements ApplePredicate {
	public boolean test(Apple apple){
		return GREEN.equals(apple.getColor()); //녹색 사과만 선택
	}
}
```

<img width="389" alt="스크린샷 2023-08-13 오후 5 35 19" src="https://github.com/xogns1514/test/assets/66353672/79a0a520-c66d-4987-88f8-aecf9f4db3bb">

조건에 따라 filter 메서드가 다르게 동작할 것이라고 예상할 수 있다. 이를 `전략 디자인 패턴(strategy design pattern)` 이라고 부른다.

전략 디자인 패턴: 각 알고리즘을 캡슐화하는 알고리즘 패밀리를 정의해둔 다음, 런타임에 알고리즘을 선택하는 기법

ex) ApplePredicate(알고리즘 패밀리), AppleGreenColorPredicate, AppleHeavyWeightPredicate(전략)

이를 이용해서 filterApples에서 ApplePredicate 객체를 받아 애플의 조건을 검사하도록 동작 파라미터화 할 수 있다. 즉, 메서드가 다양한 동작을 받아서 내부적으로 다양한 동작을 수행할 수 있다.

## 2.2.1 네 번째 시도: 추상적 조건으로 필터링

ApplePredicate를 이용한 필터 메서드

```java
public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p){
	List<Apple> result = new ArrayList<>();
	for(Apple apple : inventory){
		if(p.test(apple)){ //프레디케이드 객체로 사과 조건 검사를 캡슐화 하였다.
			result.add(apple);
		}
	}
	return result;
}
```

### 코드/ 동작 전달하기

위 코드는, 이전의 코드에 비해 유연해졌으며 가독성이 좋아졌다. 또한 사용하기 쉬워졌다.

필요한 대로 ApplePredicate를 만들어서 filterApples 메서드로 전달할 수 있다.

ex)150 그람이 넘는 빨간 사과를 검색

```java
public class AppleRedAndHeavyPredicate implements ApplePredicate{
	pubic boolean test(Apple apple){
		return RED.equals(apple.getColor())
						&& apple.getWeight() > 150;
	}
}

//filterApples에 전달
List<Apple> redAndHeavyApples = filterApples(inventory, new AppleRedAndHeavyPredicate());
```

<img width="410" alt="Untitled" src="https://github.com/xogns1514/test/assets/66353672/0cc12e81-016e-44bd-b4cd-64d91349f11a">


- 객체를 이용해서 boolean 표현식 등을 전달할 수 있으므로, `‘코드를 전달’` 할 수 있는 것이나 다름없다.

### 위 방법의 문제점

- 메서드는 객체만 인수로 받으므로 test 메서드를 ApplePredicate 객체로 감싸서 전달해야 한다. 따라서 로직과 관계없는 많은 코드가 추가되었다.

### 한 개의 파라미터, 다양한 동작

📌동작 파라미터화의 강점: 컬렉션 탐색 로직과 각 항목에 적용할 동작을 분리할 수 있다. 따라서 한 메서드가 다른 동작을 수행할 수 있도록 재활용할 수 있다. 따라서 유연한 API를 만들 때 동작 파라미터화가 중요한 역할을 한다.

## 2.3 복잡한 과정 간소화

앞선 방법에서 filterApples 메서드로 새로운 동작을 전달하려면 ApplePredicate 인터페이스를 구현하는 여러 클래스를 정의한 다음에 인스턴스화 해야하는 귀찮은 작업을 해야한다는 단점을 알았다. 로직과 관련 없는 코드가 많이 추가되는 문제점을 어떻게 해결할 수 있을까?

<aside>
💡 자바는 클래스의 선언과 인스턴스화를 동시에 수행할 수 있도록 `익명 클래스(anonymous class)` 라는 기법을 제공한다.

</aside>

## 2.3.1 익명 클래스

익명 클래스는 자바의 지역 클래스와 비슷한 개념이다. 말 그대로 이름이 없는 클래스이다. 익명 클래스를 이용하면 클래스 선언과 인스턴스화를 동시에 할 수 있다.

## 2.3.2 다섯 번째 시도: 익명 클래스 사용

- 익명 클래스를 이용해서 ApplePredicate를 구현하는 객체를 만드는 방법으로 필터링

```java
List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
	public boolean test(Apple apple){
		return RED.equals(apple.getColor());
	}
});
```

- GUI 애플리케이션 이벤트 핸들러 객체

```java
button.setOnAction(new EventHandler<ActionEvent>() {
	public void handle(ActionEvent event) {
		System.out.println("Whooo a click!");
	}
});
```

### 위 코드에서 문제점

- 여전히 쓸데없는 코드가 많은 공간을 차지한다.
- 많은 프로그래머가 익명 클래스의 사용에 익숙하지 않다. 코드의 장황함은 나쁜 특성이다. 이는 코드를 구현하고 유지보수하는 데 시간이 오래 걸릴 뿐 아니라 읽는 즐거움을 빼앗는 요소로, 개발자로부터 외면받는다.

## 2.3.3 여섯 번째 시도: 람다 표현식 사용

- 자바 8의 람다 표현식을 이용해서 위 예제 코드를 다음처럼 간단하게 재구현할 수 있다.

```java
List<Apple> result = 
	filterApples(inventory, (Apple apple) -> RED.equals(apple.getColor()));
```

간결해지면서 문제를 더 잘 설명하는 코드가 되었다. 

<img width="414" alt="Untitled 1" src="https://github.com/xogns1514/test/assets/66353672/47075088-8098-47e8-9522-e5acdf7574ff">


## 2.3.4 일곱 번째 시도: 리스트 형식으로 추상화

```java
public interface Predicate<T> {
	boolean test(T t);
}

public static <T> List<T> filter(List<T> list, Predicate<T> p){
	List<T> result = new ArrayList<>();
	for(T e: list){
		if(p.test(e)){
			result.add(e);
		}
	}
	return result;
}

//메서드 사용
List<Apple> redApples = 
		filter(inventory, (Apple apple) -> RED.equals(apple.getColor()));
List<Integer> evenNumbers = 
		filter(numbers, (Integer i) -> i % 2 == 0);
```

- 위 코드는 유연성과 간결함을 모두 갖추었다.

## 2.4 실전 예제

## 2.4.1 Comparator로 정렬하기

자바 8의 List에 sort 메서드가 포함되어 있다. 다음과 같은 인터페이스를 갖는 java.util.Comparator 객체를 이용해서 sort의 동작을 파라미터화 할 수 있다.

```java
// java.util.Comparator
public interface Comparator<T> {
	int compare(T o1, T o2);
}
```

- 익명클래스를 이용한, 무게가 적은 순서로 목록에서 사과 정렬

```java
inventory.sort(new Comparator<Apple>(){
	public int compare(Apple a1, Apple a2){
		return a1.getWeight().compareTo(a2.getWeight());
	}
});
```

- 람다 표현식 이용

```java
inventory.sort(
(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

## 2.4.2 Runnable로 코드 블록 실행하기

자바에서는 Runnable 인터페이스를 이용해서 실행할 코드 블록을 지정할 수 있다. 

```java
//java.lang.Runnable
public interface Runnable{
	void run();
}
```

- 익명 클래스 이용

```java
Thread t = new Thread(new Runnable(){
	public void run(){ System.out.println("Hello World");}
});
```

- 람다 표현식 이용

```java
Thread t = new Thread() -> System.out.println("Hello World"));
```

## 2.4.3 Callable을 결과로 반환하기

ExecutorService 인터페이스: 태스크 제출과 실행 과정의 연관성을 끊어줌

- Callable 인터페이스를 이용해 결과를 반환하는 태스크

```java
//java.util.concurrent.Callable
public interface Callable<V>{
	V call();
}
```

- 실행 서비스에 태스크를 제출. 태스크를 실행하는 스레드의 이름 반환

```java
ExecutorService executorService = Executors.newCachedThreadPool();
Future<String> threadName = executorService.submit(new Callable<String>(){
	@Override
		public String call() throws Exception{
		return Thread.currentThread().getName();
	}
});
```

- 람다 이용

```java
Future<String> threadName = executorService.submit(
			() -> Thread.currentThread().getName());
```

## 2.4.4 GUI 이벤트 처리하기

GUI 프로그래밍: 마우스 클릭이나 문자열 위로 이동하는 등의 이벤트에 대응하는 동작 수행.

- 사용자 정송 버튼 클릭시 파업 표시, 동작 로그 저장

```java
Button button = new Button("Send);
button.setOnAction(new EventHandler<ActionEvent>(){
	public void handle(ActionEvent event){
		label.setText("Sent!");
	}
});
```

---