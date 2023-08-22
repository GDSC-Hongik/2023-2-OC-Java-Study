## 3.5 형식 검사, 형식 추론, 제약

> *람다 표현식을 제대로 이해하기 위해서 람다의 실제 형식을 파악해보자*
> 

### 3.5.1 형식 검사

- 람다가 사용되는 context → 람다의 type 추론 가능
- `대상 형식` : 어떤 context에서 기대되는 람다 표현식 형식

```java
List<Apple> heavierThan150g =
filter(inventory, (Apple apple) -> apple.getWeight() > 150);
```

<aside>
💡 **위 코드의 형식 확인 과정**

1. 메서드 선언 확인 : filter
2. 해당 메서드의 파라미터 형식 (대상 형식) 확인 : Predicate<Apple>
3. 함수형 인터페이스에 정의된 추상 메서드 확인 : test
4. 추상 메서드의 파라미터, 반환 타입 확인 : Apple → boolean
5. 4의 요구사항을 메서드에 전달받은 인수가 만족하는지 확인
</aside>

### 3.5.2 같은 람다, 다른 함수형 인터페이스

- 대상 형식 → 같은 람다 표현식이어도 호환되는 추상메서드를 가진 `다른` 함수형 인터페이스로 사용 가능
- 하나의 람다 표현식 → 다양한 함수형 인터페이스에 사용 가능

```java
Comparator<Apple> c1 =
	(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
ToIntBiFunction<Apple, Apple> c2 =
	(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
BiFunction<Apple, Apple, Integer> c3 =
	(Apple a1, Apple a2) -> a1.getWeight().compareTO(a2.getWeight());
```

### 3.5.3 형식 추론

- 컴파일러 → 람다의 시그니처 추론 가능
    - 대상 형식으로 함수 디스크립터 알 수 있기 때문
    - 람다 문법에서 파라미터 형식 생략 가능해짐

```java
List<Apple> greenApples =
	filter(inventory, apple) -> GREEN.equals(apple.getColor()));
```

- 상황에 따라 형식을 포함하는 경우가 더 좋을 때가 있음

### 3.5.4 지역 변수 사용

- 람다 표현식 → 자유 변수 활용 가능
    - 외부에서 정의된 변수
    - `람다 캡처링`

```java
int portNumber = 1337;
Runnable r = () -> System.out.println(portNumber);
// portNumber = 31337;
```

- 이때 지역 변수는…
    - 명시적으로 final로 선언되어 있거나
    - 실질적으로 final로 선언된 변수와 동일하게 사용되어야함 (effectively final)
- 람다 표현식은 `한 번`만 할당할 수 있는 지역 변수 캡처 가능

<aside>
💡 **지역 변수의 제약**

- 인스턴스 변수 → 힙에 저장
- 지역 변수 → 스택에 저장
- 변수를 할당한 스레드가 사라졌으나 람다를 실행하는 스레드에서 변수에 접근할 때 → 문제 생김
</aside>

<aside>
💡 **클로저**

- 함수의 비지역 변수를 자유롭게 참조할 수 있는 함수의 인스턴스
    - 이때 해당 변수는 final이어야함
- 자바8의 람다와 익명 클래스
</aside>

## 3.6 메서드 참조

- 기존의 메서드 정의를 재활용해서 람다처럼 전달

```java
// Before
inventory.sort((Apple a1, Apple a2) -> a1.getWeight.compareTo(a2.getWeight()));

// After
inventory.sort(comparing(Apple::getWeight));
```

### 3.6.1 요약

- 특정 메서드만을 호출하는 람다의 축약형
- 명시적으로 메서드명을 참조 → `가독성`↑

<aside>
💡 **메서드 참조를 만드는 방법**

1. 정적 메서드 참조 : `Integer::pareInt`
2. 다양한 형식의 인스턴스 메서드 참조 : `String::length`
3. 기존 객체의 인스턴스 메서드 참조 : `expensiveTransaction::getValue`
</aside>

- 세번째 유형 → 비공개 헬퍼 메서드 정의한 상황에서 유용
    - 반복적인 코드를 줄여주는 메서드

```java
private boolean isValidName(String string) {
	return Character.isUpperCase(string.charAt(0));
}

filter(words, this::isValidName);
```

- 메서드 참조는 context의 형식과 일치해야함

### 3.6.2 생성자 참조

- 기존 생성자의 참조 만들 수 있음 : `ClassName::new`

```java
// 인수가 없는 생성자
Supplier<Apple> c1 = Apple::new;
Apple a1 = c1.get();

// 시그니처를 갖는 생성자
Function<Integer, Apple> c2 = Apple::new;
Apple a2 = c2.apply(110);
```

- 인스턴스화하지 않고도 생성자에 접근 가능

## 3.7. 람다, 메서드 참조 활용하기

- 우리의 목표 코드

```java
inventory.sort(comparing(Apple::getWeight());
```

### 3.7.1. 1단계 : 코드 전달

- sort 메서드의 시그니처 확인해보기

```java
void sort(Comparator<? suprt E> &c)  // 동작이 파라미터화됨
```

```java
public class AppleComparator implements Comparator<Apple> {
	public int compare(Apple a1, Apple a2) {
		return a1.getWeight().compareTo(a2.getWeight());
	}
}
inventory.sort(new AppleComparator());
```

### 3.7.2. 2단계 : 익명 클래스 사용

- Comparator 한번만 사용하기 떄문

```java
inventory.sort(new Comparator<Apple>() {
	public int compare(Apple a1, Apple a2) {
		return a1.getWeight().compareTo(a2.getWeight());
	}
});
```

### 3.7.3. 3단계 : 람다 표현식 사용

- 람다 표현식을 사용하여 코드 전달
- 디스크립터 파악하기 : (Apple, Apple) → int

```java
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));

// 파라미터 형식 추론
inventory.sort((a1, a2) -> a1.getWeight.compareTo(a2.getWeight()));

// comparing 메서드 사용
Comparator<Apple> c = Comparator.comparing((Apple a) -> a.getWeight());
inventory.sort(comparing(apple -> apple.getWeight()));
```

### 3.7.4. 4단계 : 메서드 참조 사용

```java
import static java.util.Comparator.comparing;
inventory.sort(comparing(Apple::getWeight));
```

- 코드의 의미 명확해짐 : Apple을 weight별로 비교해서 inventory를 sort하라

## 3.8. 람다 표현식을 조합할 수 있는 유용한 메서드

- 간단한 여러 개의 람다 표현식 조합 → 복잡한 람다 표현식 제조 가능
- `디폴트 메서드` : 추상메서드x
    - 인터페이스의 기본 구현 제공

### 3.8.1. Comparator 조합

- **역정렬** : 디폴트 메서드 reverse 사용

```java
inventory.sort(comparing(Apple::getWeight).reverse());
```

- **Comparator 연결** : thenComparing 메서드 사용

```java
inventory.sort(comparing(Apple::getWeight)
	.reversed()
	.thenComparing(Apple::getCountry));  // 무게가 같으면 국가별로 정렬
```

### 3.8.2. Predicate 조합

- `negate`, `and`, `or`

```java
Predicate<Apple> notRedApple = redApple.negate();

Predicate<Apple> redAndHeavyAppleOrGreen =
	redApple.and(apple -> applegetWeight() > 150)
					.or(apple -> GREEN.equals(a.getColor()));
```

- and와 or는 왼쪽에서 오른쪽으로 연결

### 3.8.3. Function 조합

- `andThen` : 주어진 함수를 먼저 적용한 결과를 다른 함수 입력으로 전달

```java
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x * 2;
Function<Integer, Integer> h = f.andThen(g);  // g(f(x))
```

- `compose` : 인수로 주어진 함수 먼저 실행한 이후 결과를 외부 함수의 인수로 제공

```java
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x * 2;
Function<Integer, Integer> h = f.compose(g);  // f(g(x))
```

## 3.9. 비슷한 수학적 개념

> *1차 함수의 부분 적분 공식을 자바8의 람다 표현식으로 표현할 수 있다*
> 

# [Ch4] 스트림 소개

<aside>
✅ **이 장의 내용**

- 스트림이란 무엇인가?
- 컬렉션과 스트림
- 내부 반복과 외부 반복
- 중간 연산과 최종 연산
</aside>

## 4.1. 스트림이란 무엇인가?

- `스트림`
    - 선연형으로 컬렉션 데이터 처리 가능
    - 데이터를 투명하게 병렬로 처리 가능

```java
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

List<String> lowCaloricDishesName =
	menu.stream()
			.filter(d -> d.getCalories() < 400)
			.sorted(comparing(Dishes::getCalories)
			.map(Dish::getName)
			.collect(toList());
```

- 스트림 API의 특징
    - `선언형` : 간결, 가독성↑
    - `조립 가능` : 유연성↑
    - `병렬화` : 성능↑ (스레드와 락 걱정 byebye)

## 4.2. 스트림 시작하기

- 스트림의 정의
    - 데이터 처리 연산을 지원하도록 소스에서 추출된 연속된 요소
    - `연속된 요소` : 컬렉션의 주제는 데이터, 스트림의 주제는 계산
    - `소스` : 정렬된 컬렌션으로 스트림 생성 → 정렬 그대로 유지됨
    - `데이터 처리 연산` : db와 비슷한 연산 지원
- 스트림의 주요 특징
    - `파이프라이닝` : 스트림 연산끼리 연결 → 커다란 파이프라인 구성
    - `내부 반복` : 컬렉션은 반복자를 이용한 명시적 반복

```java
import static java.util.stream.Collectors.toList;

List<String> threeHighCaloricDishNames =
	menu.stream()
			.filter(dish -> dish.getCalories() > 300)
			.map(Dish::getName)
			.limit(3)
			.collect(toList());
System.out.println(threeHighCaloricDishNames);
```

## 4.3. 스트림과 컬렉션

- 데이터를 `언제` 계산하느냐
    - 컬렉션 : 자료구조가 포함하는 모든 값을 메모리에 저장 → 컬렉션에 추가되기 `전`에 계산 (적극적 생성)
    - 스트림 : `요청`할 때만 요소를 계산 (게으른 생성)

### 4.3.1. 딱 한 번만 탐색할 수 있다

- 탐색된 스트림의 요소는 소비됨
- 컬렉션은 반복 사용 가능

```java
List<String> title = Arrays.asList("Java8", "In", "Action");
Stream<String> s = title.stream();
s.forEach(System.out::println);  // title의 각 단어 출력
s.forEach(System.out::println);  // java.land.IllegalStateException
```

### 4.3.2. 외부 반복과 내부 반복

- `외부 반복` : 사용자가 직접 요소 반복 → 컬렉션

```java
List<String> names = new ArrayList<>();
for(Dish dish: menu) {
	names.add(dish.getName());
}
```

- `내부 반복` : 반복을 알아서 처리, 스트림값 어딘가에 저장 → 스트림

```java
List<String> names = menu.stream()
										 .map(Dish::getName)
										 .collect(toList());
```

## 4.4. 스트림 연산

- `중간 연산` : 연결할 수 있는 스트림 연산 (filter, map, limit)
- `최종 연산` : 스트림을 닫는 연산 (collect)

### 4.4.1. 중간 연산

- filter, sorted : 다른 스트림 반환
- 여러 중간 연산 연결 가능
- `lazy` : 단말 연산을 스트림 파이프라인에 실행하기 전까지는 아무 연산도 수행하지 않음

### 4.4.2. 최종 연산

- 스트림 파이프라인에서 결과 도출
- 스트림 이외의 결과 반환

### 4.4.3. 스트림 이용하기

- 스트림 이용 과정
    - 질의를 수행할 데이터 소스 (ex. 컬렉션)
    - 스트림 파이프라인을 구성할 중간 연산 연결
    - 스트림 파이프라인을 실행하고 결과를 만들 최종 연산

# 모던 자바 인 액션 소스코드

- 학습용 개인 레포 링크 : https://github.com/ohhamma/Modern-Java-In-Action