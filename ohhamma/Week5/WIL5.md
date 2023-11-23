# [Ch6] 스트림으로 데이터 수집

<aside>
✅ **이 장의 내용**

---

- Collectors 클래스로 컬렌션을 만들고 사용하기
- 하나의 값으로 데이터 스트림 리듀스하기
- 특별한 리듀싱 요약 연산
- 데이터 그룹화와 분할
- 자신만의 커스텀 컬렉터 개발
</aside>

- 컬렉터 파라미터를 collect()에 전달
- 원하는 연산을 간결하게 구현

```java
Map<Currency, List<Transaction>> transactionsByCurrencies =
	transactions.stream().collect(groupingBy(Transaction::getCurrency));
```

## 6.1 컬렉터란 무엇인가?

- `groupingBy` : 각 키 bucket, 그리고 대응하는 요소 리스트를 값으로 포함하는 맵을 만들라
- 함수형 프로그래밍 : 필요한 컬렉터 쉽게 추가

### 6.1.1 고급 리듀싱 기능을 수행하는 컬렉터

- 스트림에 collect 호출 → 스트림 요소에 리듀싱 연산 수행
- 최종 결과를 저장하는 자료구조에 값 누적

### 6.1.2 미리 정의된 컬렉터

- Collectors에서 제공하는 메서드의 기능
    - 스트림 요소를 하나의 값으로 리듀스하고 요약
    - 요소 그룹화
    - 요소 분할

## 6.2 리듀싱과 요약

- 컬렉터 → 스트림의 항목을 컬렉션으로 재구성
- `counting` : 다른 컬렉터와 함께 사용

```java
// before
long howManyDishes = menu.stream().collect(Collectors.counting());
// after
long howManyDishes = menu.stream().count();
```

### 6.2.1 스트림값에서 최댓값과 최솟값

```java
Commparator<Dish> dishCaloriesComparator =
	Comparator.comparingInt(Dish::getCalories);
Optional<Dish> mostCalorieDish =
	menu.stream()
			.collect(maxBy(dishCaloriesComparator));
```

### 6.2.2 요약 연산

- `summingInt` : 객체를 int로 매핑한 컬렉터 반환
- 합계 계산

```java
int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
```

- 평균값 계산

```java
double avgCalories =
	menu.stream().collect(averagingInt(Dish::getCalories))); 
```

- 두 개 이상의 연산을 한번에 수행할때
- 모든 정보 수집

```java
IntSummaryStatistics menuStatistics =
	menu.stream().collect(summarizingInt(Dish::getCalories));
```

### 6.2.3 문자열 연결

- `joining` : StringBuilder → 문자열을 하나로

```java
String shortMenu = menu.stream().map(Dish::getName).collect(joining());
// Dish 클래스가 toString 메서드 포함하는 경우
// 구분 문자열 삽입 가능
String shortMenu = menu.stream().collect(joining(", "));
```

### 6.2.4 범용 리듀싱 요약 연산

- `reducing`

```java
int totalCalories = menu.stream().collect(reducing(
																	0, Dish::getCalories, (i, j) -> i + j));
```

- 인수 세개
    - 시작값 or 반환값
    - 변환 함수 or 항등 함수
    - BinaryOperator

```java
Optional<Dish> mostCalorieDish =
	menu.stream().collect(reducing(
			(d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));
```

<aside>
💡 `collect` vs `reduce`

---

- collect : 도출하려는 결과를 누적하는 컨테이너를 바꾸도록 설계된 메서드
- reduce : 두 값을 하나로 도출하는 불변형 연산
    - 병렬 수행 x
</aside>

```java
int totalCalories = menu.stream().collect(reducing(0,
																									Dish::getCalories,
																									Integer::sum));
```

```java
public static <T> Collector<T, ?, Long> counting() {
	return reducing(0L, e -> 1L, Long::sum);
}
```

## 6.3 그룹화

- 데이터 집합을 하나의 이상의 특성으로 분류해서 그룹화
- `groupingBy` : 분류함수, 이 함수를 기준으로 스트림이 그룹화

```java
Map<Dish.Type, List<Dish>> dishesByType =
	menu.stream().collect(groupingBy(Dish::GetType));
```

### 6.3.1 그룹화된 요소 조작

- groupingBy 오버로딩
- `filtering` : predicate를 인수로 받음, 각 그룹의 요소와 필터링된 요소 재그룹화

```java
Map<Dish.Type, List<Dish>> caloricDishesByType =
	menu.stream()
			.collect(groupingBy(Dish::getType,
							 filtering(dish -> dish.getCalories() > 500, toList())));
```

- `mapping` : 각 항목에 적용한 함수를 모으는 데 사용하는 또 다른 컬렉터를 인수로 받음

```java
Map<Dish.Type, List<String>> dishNamesByType =
	menu.stream()
			.collect(groupingBy(Dish::getType, mapping(Dish::getName, toList())));
```

- `flatMapping` : 집합으로 중복화, 집합 태그 제거

### 6.3.2 다수준 그룹화

- `Collectors.groupingBy` : 분류함수, 컬렉터를 인수로 받음

```java
Map<Dish.Type, Map<CaloricLevel, List<Dish>> dishesByTypeCaloricLevel =
	menu.stream().collect(
		groupingBy(Dish::getType,
			groupingBy(dish -> {
				if (dish.getCalories() <= 400)
					return CaloricLevel.DIET;
				else if (dish.getCalories() <= 700)
					return CaloricLevel.NORMAL; else return CaloricLevel.FAT;
			})
		)
	);
```

### 6.3.3 서브그룹으로 데이터 수집

```java
Map<Dish.Type, Optional<Dish>> mostCaloricByType =
	menu.stream()
			.collect(groupingBy(Dish::getType,
													maxBy(comparingInt(Dish::getCalories))));
```

- `Collectors.collectingAndThen` : 다른 컬렉터 반환
    - 기존 컬렉터의 wrapper 역할

```java
Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType =
	menu.stream().collect(
		groupingBy(Dish::getType, mapping(dish -> {
			if (dish.getCalories() <= 400) return CaloricLevel.DIET;
			else if (dish.getCalories() <= 700) retirn CaloricLEvel.NORMAL;
			else return CaloricLevel.FAT; },
		toSet() )));
```

## 6.4 분할

- 분할 함수라 불리는 predicate를 분류 함수로 사용하는 특수한 그룹화 기능
- 분류 함수 : boolean 반환 → 두 개의 그룹으로 분류

```java
Map<Boolean, List<Dish>> partitionedMenu =
	menu.stream().collect(partitioningBy(Dish::isVegetarian));
```

- `partitioningBy` : 불리언 반환

### 6.4.1 분할의 장점

- 참, 거짓 두 가지 요소의 스트림 리스트를 모두 유지
- 오버로드된 partitioningBy 메서드

```java
Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType = menu.stream().
collect(
	partitioningBy(Dish::isVegetarian, groupingBy(Dish::getType)));
```

- partitioningBy 다른 컬렉터와 조합해서 사용 → 다수준 분할 수행 가능

### 6.4.2 숫자를 소수와 비소수로 분할하기

```java
public Map<Boolean, List<Integer>> partitionPrimes(int n) {
	return IntStream.rangeClose(2, n).boxed()
									.collect(partitioningBy(candidate -> isPrime(candidate)));
}
```

## 6.5 Collector 인터페이스

- 리듀싱 연산(컬렉터)을 어떻게 구현할지 제공하는 메서드 집합

```java
public interface Collector<T, A, R> {
	Supplier<A> supplier();
	BiConsumer<A, T> accumulator();
	Function<A, R> finisher();
	BinaryOperator<A> combiner();
	Set<Characteristics> characteristics();
}
```

### 6.5.1 Collector 인터페이스의 메서드 살펴보기

- **Supplier** 메서드
    - 새로운 결과 컨테이너 만들기
    - 빈 결과로 이루어진 Supplier 반환
- **Accumulator** 메서드
    - 결과 컨테이너에 요소 추가하기
    - 리듀싱 연산을 수행하는 함수 반환
- **Finisher** 메서드
    - 최종 변환값을 결과 컨테이너로 적용하기
    - 누적 과정을 끝낼 때 호출할 함수 반환
    - 누적자 객체가 이미 최종 결과인 상황 → 항등함수 반환 (변환과정x)
- **Combiner** 메서드
    - 두 결과 컨테이너 병합
    - 스트림 리듀싱 병렬로 수행 가능
- **Characteristics** 메서드
    - 컬렉터의 연산을 정의하는 Characteristics 형식의 불리언 집합 반환
    - 병렬로 리듀스할건지? 어떤 최적화를 선택할건지? 힌트 제공
    - 열거형
        - UNORDERED : 리듀싱 결과는 순서에 영향받지 x
        - CONCURRENT : 병렬 리듀싱 수행 가능 (순서 무의미해야 가능)
        - IDENTIFY_FINISH : 생략 가능, 최종 결과로 누적자 객체 바로 사용 가능

### 6.5.2 응용하기

- 커스텀 Collector 구현 가능
- 컬렉터 구현을 만들지 않고도 커스텀 수집 수행하기
    - 스트림의 모든 항목을 리스트에 수집하는 방법
    - but 가독성 ↓

```java
List<Dish> dishes = menuStream.collect(
	ArrayList::new, // 발행
	List::add,      // 누적
	List::addAll    // 합침
)
```

## 6.6 커스텀 컬렉터를 구현해서 성능 개선하기

### 6.6.1 소수로만 나누기

- 컬렉터 수집 과정에서 부분결과에 접근 → 커스텀 컬렉터 클래스로 가능

```java
public static boolean isPrime(List<Integer> primes, int candidate) {
	return primes.stream().noneMatch(i -> candidate % i == 0);
}
```

- `takeWhile` : 첫 요소에서 predicate 만족하는 가장 긴 요소로 이루어진 리스트 반환
    - 소수가 대상의 루트보다 클 때 검사 중지

```java
public static boolean isPrime(List<Integer> primes, int candidate) {
	int candidateRoot = (int) Math.sqrt((double) candidate);
	return primes.stream()
							 .takeWhile(i -> i <= candidateRoot)
							 .noneMatch(i -> candidate % i == 0);
}
```

- 커스텀 컬렉터를 구현하는 5가지 단계
    - 1단계 : Collector 클래스 시그니처 정의
    - 2단계 : 리듀싱 연산 구현
    - 3단계 : 병렬 실행할 수 있는 컬렉터 만들기(가능하다면)
    - 4단계 : finisher 메서드와 컬렉터의 characteristics 메서드

### 6.2.2 컬렉터 성능 비교

- 컬렉터 성능을 확인할 수 있는 간단한 하니스(harness)를 만들어 성능 비교 가능