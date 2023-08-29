# [Ch5] 스트림 활용

<aside>
✅ **이 장의 내용**

---

- 필터링, 슬라이싱, 매칭
- 검색, 매칭 리듀싱
- 특정 범위의 숫자와 같은 숫자 스트림 사용하기
- 다중 소스로부터 스트림 만들기
- 무한 스트림
</aside>

## 5.1 필터링

- 스트림 요소를 `선택`하는 방법

### 5.1.1 프레디케이트로 필터링

- `filter` : Predicate를 인수로 받아서 일치하는 모든 요소를 포함하는 스트림 반환

```java
List<Dish> vegetarianMenu = menu.stream()
																.filter(Dish::isVegetarian)
																.collect(toList());
```

### 5.1.2 고유 요소 필터링

- `distinct` : 고유 요소로 이루어진 스트림을 반환

```java
List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
numbers.stream()
			 .filter(i -> i % 2 == 0)
			 .distinct()
			 .forEach(System.out::println);
```

- 중복 제거해주는 역할

## 5.2 스트림 슬라이싱

- 스트림 요소를 선택하거나 스킵하는 방법

### 5.2.1 프레디케이트를 이용한 슬라이싱

- `takeWhile` : predicate가 참일 때까지 슬라이싱
    - 무한 스트림에도 적용 가능

```java
List<Dish> slicedMenu1 = specialMenu.stream()
																		.takeWhile(dish -> dish.getCalories() < 320)
																		.collect(toList());
```

<aside>
☝🏻 filter 연산과의 차이점

- filter : 전체 스트림을 반복하면서 각 요소에 predicate 적용
- takeWhile : 조건에 대해 참이 아닐 경우 바로 거기서 멈춤
</aside>

- `dropWhile` : takeWhile과 정반대의 작업 수행
    - predicate가 거짓이 되면 남은 모든 요소 반환 및 작업 중단
    - 무한 스트림에도 적용 가능

```java
List<Dish> slicedMenu2 = specialMenu.stream()
																		.dropWhile(dish -> dish.getCalories() < 320)
																		.collect(toList());
```

### 5.2.2 스트림 축소

- `limit(n)` : 주어진 값 이하의 크기를 갖는 새로운 스트림 반환
    - 최대 항목 수에 도달하는 즉시 더이상 element를 소비하지 않고 결과 스트림 반환

```java
List<Dish> dishes = specialMenu.stream()
															 .filter(dish -> dish.getCalories() > 300)
															 .limit(3)
															 .collect(toList());
```

- 정렬되지 않은 스트림에도 limit 사용 가능 → 정렬되지 않은 상태로 반환

### 5.2.3 요소 건너뛰기

- `skip(n)` : 처음 n개 요소를 제외한 스트림 반환
- n개 이하의 요소를 포함하는 스트림에 호출하면 빈 스트림 반환

```java
List<Dish> dishes = menu.stream()
												.filter(d -> d.getCalories() > 300)
												.skip(2)
												.collect(toList());
```

## 5.3 매핑

- 특정 객체에서 특정 데이터를 선택하는 작업

### 5.3.1 스트림의 각 요소에 함수 적용하기

- `map` : 인수로 제공된 함수를 각 요소에 적용 → 함수를 적용한 결과가 새로운 요소로 매핑됨

```java
List<String> dishNames = menu.stream()
														 .map(Dish::getName)
														 .map(String::length)
														 .collect(toList());
```

- 다른 map 연결 가능 (chaining)

### 5.3.2 스트림 평면화

- 리스트에서 고유 문자로 이루어진 리스트 반환

```java
// 반환된 리스트 형식 : List<String[]>
words.stream()
		 .map(word -> word.split(""))
		 .distinct()
     .collect(toList());
```

- `Arrays.stream()` : 문자열을 받아 스트림을 만들어줌

```java
// 반환된 리스트 형식 : List<Stream<String>>
words.stream()
		 .map(word -> word.split(""))
		 .map(Arrays::stream) // 각 배열을 별도의 스트림으로 생성
		 .distinct()
		 .collect(toList());
```

- `flatMap` : 각 배열을 스트림이 아니라 스트림의 콘텐츠로 매핑
    - 평면화된 스트림
    - 스트림의 각 값을 다른 스트림으로 만듦 → 모든 스트림을 하나의 스트림으로 연결

```java
List<String> uniqueCharacters =
	words.stream()
			 .map(word -> word.split(""))
			 .flatMap(Arrays::stream) // 생성된 스트림을 하나의 스트림으로 평면화
			 .distinct()
			 .collect(toList());
```

- 두개의 숫자 리스트 → 모든 숫자 쌍의 리스트 반환

```java
List<Integer> numbers1 = Arrays.asList(1, 2, 3);
List<Integer> numbers2 = Arrays.asList(3, 4);
List<int[]> pairs = numbers1.stream()
														.flatMap(i -> numbers2.stream()
																									.map(j -> new int[]{i, j})
														)
														.collect(toList());
```

<aside>
☝🏻 **collect(toList())** vs **collect(toUnmodifiableList())** vs **toList()**

---

- `collect(toList())`
    - 수정 허용
    - null 값 허용
- `collect(toUnmodifiableList())`
    - 수정 불가능
    - null 값 허용x
- `toList()`
    - 수정 불가능
    - null 값 허용
</aside>

## 5.4 검색과 매칭

- 특정 속성이 데이터 집합에 있는지 여부 검색

### 5.4.1 프레디케이트가 적어도 한 요소와 일치하는지 확인

- `anyMatch` : predicate가 주어진 스트림에서 적어도 한 요소와 일치하는지 확인
    - 최종 연산임 (boolean 반환)

```java
if (menu.stream().anyMatch(Dish::isVegetarian)) {
	System.out.println("The menu is (somewhat) vegetarian friendly!!");
}
```

### 5.4.2 프레디케이트가 모든 요소와 일치하는지 검사

- `allMatch` : 스트림의 모든 요소가 주어진 predicate와 일치하는지 검사

```java
boolean isHealthy = menu.stream()
												.allMatch(dish -> dish.getCalories() < 1000);
```

- `noneMatch` : 주어진 predicate와 일치하는 요소가 없는지 확인
    - allMatch와 반대 연산 수행

```java
boolean isHealthy = menu.stream()
												.noneMatch(d -> d.getCalories() >= 1000);
```

<aside>
☝🏻 `anyMatch` `allMatch` `noneMatch` → 모두 스트림 **쇼트서킷** 기법

- 자바의 `&&` `||` 와 같은 연산 활용
- 모든 스트림의 요소를 처리하지 않고도 결과 반환
- limit도 쇼트서킷 연산
</aside>

### 5.4.3 요소 검색

- `findAny` : 현재 스트림에서 임의의 요소 반환
    - 다른 스트림 연산과 연결해서 사용 가능

```java
Optional<Dish> dish =
	menu.stream()
			.filter(Dish::isVegetarian)
			.findAny();
```

<aside>
☝🏻 **Optional이란?**

---

- `Optional<T>` : 값의 존재나 부재 여부를 표현하는 컨테이너 크래스
- null 확인 관련 버그를 피할수있음
- 값이 존재하는지 확인, 값이 없을 때 어떻게 처리할지 강제
- `isPresent()` : 값을 포함하면 true, 포함하지 않으면 false
- `ifPresent(Consumer<T> block)` : 값이 있으면 주어진 블록 실행
- `T get()` : 값이 존재하면 값 반환, 값이 없으면 NoSuchElementException
- `T orElse(T other)` : 값이 있으면 값 반환, 값이 없으면 기본값 반환

```java
menu.stream()
		.filter(Dish::isVegetarian)
		.findAny() // Optional<Dish> 반환
		.ifPresent(dish -> System.out.println(dish.getName());
```

</aside>

### 5.4.4 첫 번째 요소 찾기

- `findFirst` : 논리적인 아이템 순서가 정해져있는 경우 첫번쨰 요소 반환

```java
List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5);
Optional<Integer> firstSquareDivisibleByThree =
	someNumbers.stream()
						 .map(n -> n * n)
						 .filter(m -> n % 3 == 0)
						 .findFirst();
```

<aside>
☝🏻 **findFirst** vs **findAny**

---

- 병렬 실행에서는 첫번째 요소를 찾기 어려움
- 병렬 스트림에서는 제약이 적은 findAny 사용
    - 요소의 반환 순서가 상관없다면
</aside>

## 5.5 리듀싱

- `리듀싱 연산` : 모든 스트림 요소를 처리해서 값으로 도출
    - 다른 말로는 `fold`라고 부름
    - 스트림 요소를 조합해서 더 복잡한 질의를 표현할 때 사용
    - Integer 같은 결과가 나올 때까지 스트림의 모든 요소를 반복적으로 처리

### 5.5.1 요소의 합

- sum 변수의 `초깃값` 0
- 리스트의 모든 요소를 조합하는 `연산`(+)

```java
int sum = numbers.stream().reduce(0, (a, b) -> a + b);
```

- `reduce` → 반복된 패턴 추상화
- 스트림이 하나의 값으로 줄어들 때까지 람다는 각 요소를 반복해서 조합

```java
// 자바 8에서 제공하는 Integer 클래스의 정적 sum 메서드 참조
int sum = numbers.stream().reduce(0, Integer::sum);
```

<aside>
☝🏻 **초기값이 없는 경우**

---

- 초기값을 받지 않도록 오버로드된 reduce → Optional 객체 반환

```java
Optional<Integer> sum = numbers.stream().reduce((a, b) -> a + b);
```

- Optional 객체를 반환하는 이유
    - 스트림에 아무 요소도 없는 상황 → 초깃값 없음 → 합계 반환 불가능
</aside>

### 5.5.2 최댓값과 최솟값

- reduce를 이용해서 스트림의 최댓값(최솟값) 찾기
    - 두 요소에서 최댓값(최솟값)을 반환하는 람다 필요

```java
Optional<Integer> max = numbers.stream().reduce(Integer::max);
Optional<Integer> min = numbers.stream().reduce(Integer::min);
```

<aside>
☝🏻 **map-reduce 패턴**

---

- map과 reduce를 연결하는 기법
- 쉽게 병렬화 가능

```java
// 스트림의 각 요소를 1로 매핑
// reduce로 합계를 계산 -> 스트림의 요리 개수 계산
int count =
	menu.stream()
			.map(d -> 1)
			.reduce(0, (a, b) -> a + b);
```

</aside>

## 5.6 실전 연습

### 5.6.1 거래자와 트랜잭션

### 5.6.2 실전 연습 정답

```java
// 2011에 일어난 모든 트랜잭션을 찾아서 값을 오름차순으로 정렬하시오
List<Transaction> tr2011 =
	transactions.stream()
							.filter(transaction -> transaction.getYear() == 2011)
							.sorted(comparing(Transaction::getValue))
							.collect(toList());
```

```java
// 거래자가 근무하는 모든 도시를 중복 없이 나열하시오
List<String> cities =
	transactions.stream()
							.map(transaction -> transaction.getTrader().getCity())
							.distinct()
							.collect(toList());
```

```java
// 케임브리지에서 근무하는 모든 거래지를 찾아서 이름순으로 정렬하시오
List<Trader> traders =
	transactions.stream()
							.map(Transaction::getTrader)
							.filter(trader -> trader.getCity().equals("Cambridge"))
							.distinct()
							.sorted(comparing(Trader::getName)
							.collect(toList());
```

```java
// 모든 거래자의 이름을 알파벳순으로 정렬해서 반환하시오
String traderStr =
	transactions.stream()
							.map(transaction -> transaction.getTrader().getName())
							.distinct()
							.sorted()
							.collect(joining()); // 모든 이름 연결
```

```java
// 밀라노에 거래자가 있는가?
boolean milanBased =
	transactions.stream()
							.anyMatch(transaction -> transaction.getTrader()
																									.getCity()
																									.equals("Milano"));
```

```java
// 케임브리지에 거주하는 거래자의 모든 트랜잭션값을 출력하시오
transactions.stream()
						.filter(t -> "Cambridge".equals(t.getTrader().getCity()))
						.map(Transaction::getValue)
						.forEach(System.out::println);
```

```java
// 전체 트랜잭션 중 최댓값은 얼마인가?
Optional<Integer> highestValue =
	transactions.stream()
						.map(Transaction::getValue)
						.reduce(Integer::max);
```

```java
// 전체 트랜잭션 중 최솟값은 얼마인가?
Optional<Integer> smallestTransaction =
	transactions.stream()
							.min(comparing(Transaction::getValue));
```

## 5.7 숫자형 스트림

- 기본형 특화 스트림 : 스트림 API 숫자 스트림을 효율적으로 처리 가능

### 5.7.1 기본형 특화 스트림

- `IntStream` `DoubleStream` `LongStream`
    - 박싱 과정에서 일어나는 효율성과 관련
    - 자주 사용하는 숫자 관련 리듀싱 연산 수행 메서드 제공 (sum, max, average)
    - 다시 객체 스트림으로 복원하는 기능 제공
- `mapToInt` `mapToDouble` `mapToLong`
    - 스트림 → 특화 스트림으로 변환할 때 사용
    - map과 정확히 같은 기능
    - Stream<T> 대신 특화된 스트림 반환

```java
int calories = menu.stream()
									 .mapToInt(Dish::getCalories)
									 .sum();
```

- 객체 스트림으로 복원
    - `boxed` 메서드 이용

```java
IntStream intStream = menu.stream().mapToInt(Dish::GetCalories);
Stream<Integer> stream = intStream.boxed();
```

- `OptionalInt` `OptionalDouble` `OptionalLong`
    - 기본형 특화 스트림 버전 제공

```java
OptionalInt maxCalories = menu.stream()
															.mapToInt(Dish::getCalories)
															.max();

// 값이 없을 때 기본값을 명시적으로 정의 가능
int max = maxCalories.orElse(1);
```

### 5.7.2 숫자 범위

- 특정 범위의 숫자 이용
    - `range` : 시작값과 종교값이 결과에 포함x
    - `rangeClosed` : 시작값과 종료값이 결과에 포함

```java
IntStream evenNumbers = IntStream.rangeClose(1, 100)
																 .filter(n -> n % 2 == 0);
System.out.println(evenNumbers.count());
```

### 5.7.3 숫자 스트림 활용 : 피타고라스 수

```java
Stream<double[]> pythagoreanTriples2 =
	IntStream.rangeClosed(1, 100).boxed()
					 .flatMap(a -> IntStream.rangeClosed(a, 100)
																	.mapToObj(b -> new double[]{a, b, Math.sqrt(a*a + b*b)})
																	.filter(t -> t[2] % 1 == 0));
```

## 5.8 스트림 만들기

### 5.8.1 값으로 스트림 만들기

- `Stream.of` : 임의의 수를 인수로 받는 정적 메서드

```java
Stream<String> stream = Stream.of("Modern", "Java", "In", "Action");
stream.map(String::toUpperCase).forEach(System.out::println);

// empty 메서드로 스트림 비우기
Stream<String> emptyStream = Stream.empty();
```

### 5.8.2 null이 될 수 있는 객체로 스트림 만들기

- `Stream.ofNullable`

```java
Stream<String> homeValueStream = Stream.ofNullable(System.getProperty("home"));
```

### 5.8.3 배열로 스트림 만들기

- `Arrays.stream` : 배열을 인수로 받는 정적 메서드

```java
int[] numbers = {2, 3, 5, 7, 11, 13};
int sum = Arrays.stream(numbers).sum();
```

### 5.8.4 파일로 스트림 만들기

- `Files.lines` : 주어진 파일의 행 스트림을 문자열로 반환

```java
// 스트림의 고유 단어 수 계산
long uniqueWords = 0;
try (Stream<String> lines =
		Files.lines(Paths.get("data.txt"), Charset.defaultCharset())) {
	uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" ")))
										 .distinct()
										 .count();
}
	catch (IOException e) {
	// 예외 처리
}
```

### 5.8.5 함수로 무한 스트림 만들기

- 무한 스트림 : 크기가 고정되지 않은 스트림
    - 언바운드 스트림
- `Stream.iterate` `Stream.generate` : 요청할 때마다 주어진 함수를 이용해서 값 생성
    - limit(n)을 함께 연결해서 사용

```java
// iterate : 연속된 일련의 값을 만들 때 사용
Stream.iterate(0, n -> n + 2)
			.limit(10)
			.forEach(System.out::println);
```

```java
// 피보나치 수열 집합
Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0]+t[1]})
			.limit(10)
			.map(t -> t[0])
			.forEach(System.out::println);
```

```java
// 언제까지 작업을 수행할 것인지
IntStream.iterate(0, n -> n < 100, n -> n + 4)
				 .forEach(System.out::println);

// takeWhile 사용
IntStream.iterate(0, n -> n + 4)
				 .takeWhile(n -> n < 100)
				 .forEach(System.out::println);
```

```java
// generate : 생산된 각 값을 연속적으로 계산하지 x
// Supplier<T>를 인수로 받아 새로운 값 생산
Stream.generate(Math::random)
			.limit(5)
			.forEach(System.out::println);
```

```java
// 요소 값 추적 -> 가변 상태 객체
IntSupplier fib = new IntSupplier() {
	private int previous = 0;
	private int current = 1;
	public int getAsInt() {
		int oldPrevious = this.previous;
		int nextValue = this.previous + this.current;
		this.previous = this.current;
		this.current = nextValue;
		return oldPrevious;
	}
};
```

- 무한 스트림 → 무한적으로 계산 반복
    - 정렬하거나 리듀스 x