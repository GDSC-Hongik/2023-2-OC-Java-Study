# CH4 스트림 소개

거의 모든 자바 애플리케이션은 컬렉션을 만들고 처리하는 과정을 포함한다. 컬렉션으로 데이터를 그룹화하고 처리한다. 많은 요소를 포함하는 커다란 컬렉션을 효율적으로 처리하기 위해서는 멀티코어 아키텍처를 활용해 병렬로 처리해야 한다. 하지만 병렬 처리 코드를 구현하는 것은 단순 반복 처리 코드에 비해 복잡하고 어렵다.

→ 이를 해결하기 위해 스트림이 탄생하였다

## 4.1 스트림이란 무엇일까?

- 자바 8 API 에 새로 추가된 기능이다.
- 스트림을 이용하면 선언형(질의로 표현)으로 컬렉션 데이터를 처리할 수 있다.
- 스트림을 이용하면 멀티 스레드 코드를 구현하지 않아도 데이터를 투명하게 병렬로 처리할 수 있다.

```java
//자바 8 이전의 코드
List<Dish> lowCaloricDishes = new ArrayList<>();
for(Dish dish : menu) {
	if(dish.getCalories() < 400){ // 누적자로 요소 필터링
		lowCaloricDishes.add(dish);
	}
}
//익명 클래스로 요리 정렬
Collections.sort(lowCaloricDishes, new Comparator<Dish>(){
	public int compare(Dish dish1, Dish dish2){
		return Integer.compare(dish1.getCalories(), dish2.getCalories());
	}
});
List<String> lowCaloricDishName = new ArrayList<>();
for(Dish dish : lowCaloricDishes){
	lowCaloricDishesName.add(dish.getName()); //정렬된 리스트를 처리하면서 요리 이름 선택
}
```

- ‘lowCaloricDishes’ 는 중간 변수로 ‘가비지 변수’이다
    
    → 자바 8에서 이러한 세부 구현은 라이브러리 내에서 모두 처리한다
    

```java
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
List<String> lowCaloricDishesName =
	menu.stream()
		.filter(d -> d.getCalories() < 400) //400칼로리 이하 요리 필터링
		.sorted(comparing(Dish::getCalories)) //칼로리로 요리 정렬
		.map(Dish::getName) //요리명 추출
		.collect(toList()); //모든 요리명을 리스트에 저장
```

→ stream()을 parallelStream()으로 바꾸면 멀티코어 아키텍처에서 병렬로 실행 가능

📌 스트림의 새로운 기능의 소프트웨어공학적 이득

- `선언형`으로 코드를 구현할 수 있다
    - 선언형 코드와 동작 파라미터화를 이용하면 변하는 요구사항에 쉽게 대응할 수 있다.
    - 람다 표현식을 이용해 쉽게 필터링 할 수 있다
- filter, sorted, map, collect 같은 여러 빌딩 블록 연산을 연결해서 복잡한 데이터 처리 파이프라인을 만들 수 있다.
    - 고수준 빌딩 블록으로 이루어져 있어, 스레딩 모델에 제한되지 않는다. 따라서 데이터를 병렬 처리할 때 스레드와 락을 걱정할 필요가 없다.

📌 기타 라이브러리: 구아바, 아파치, 람다제이

- 컬렉션을 제어하는 데 도움이 되는 다양한 라이브러리

### 스트림 API 특징 요약

- 선언형: 더 간결하고 가독성이 좋아진다
- 조립할 수 있음: 유연성이 좋아진다
- 병렬화: 성능이 좋아진다

## 4.2 스트림 시작하기

<aside>
💡 스트림: 데이터 처리 연산을 지원하도록 소스에서 추출된 연속된 요소(Sequence of elements)

</aside>

- `연속된 요소` : 스트림은 특정 요소 형식으로 이루어진 연속된 값 집합의 인터페이스를 제공한다.
    - 컬렉션의 주제는 데이터이고 스트림의 주제는 계산이다.
- `소스` : 스트림은 컬렉션, 배열, I/O 자원 등의 데이터 제공 소스로부터 데이터를 소비한다.
- `데이터 처리 연산` : 스트림은 함수형 프로그래밍 언어에서 일반적으로 지원하는 연산과 데이터베이스와 비슷한 연산을 지원한다.

📌 중요 특징

1. 파이프 라이닝(Pipelining): 대부분 스트림 연산은 스트림 연산끼리 연결해서 커다란 파이프라인을 구성할 수 있도록 스트림 자신을 반환한다. → 게이름(laziness), 쇼트서킷(short-circuiting)과 같은 최적화도 얻을 수 있다.
2. 내부 반복: 반복자를 이용해 명시적으로 반복하는 컬렉션과 달리 스트림은 내부 반복을 지원한다.

```java
import static java.util.stream.Collectors.toList;
List<String> threeHighCaloricDishNames = 
	menu.stream() //메뉴에서 스트림을 얻는다
		.filter(dish -> dish.getCalories() > 300) //고칼로리 음식 필터링
		.map(Dish::getName) //요리명 추출
		.limit(3) //선착순 3개만 선택
		.collect(toList()); //결과를 다른 리스트로 저장
System.out.println(threeHighCaloricDishNames); //[pork, beef, chicken]
```

<img width="462" alt="1" src="https://github.com/xogns1514/modernJava/assets/66353672/a5b6b9de-8718-424f-8080-ee37f75c6e90">

- filter: 람다를 인수로 받아 스트림에서 특정 요소를 제외시킨다.
- map: 람다를 이용해서 한 요소를 다른 요소로 변환하거나 정보를 추출한다
- limit: 정해진 개수 이상의 요소가 스트림에 저장되지 못하게 스트림 크기를 축소한다.
- collect: 다양한 변환 방법을 인수로 받아 스트림에 누적된 요소를 특정 결과로 변환시킨다.

## 4.3 스트림 vs 컬렉션

- 자바의 기존 컬렉션과 새로운 스트림 모두 `연속된` 요소 형식의 값을 저장하는 자료구조의 인터페이스를 제공한다.
- 연속된: 순서와 상관없이 아무 값에나 접속하는 것이 아니라 `순차적`으로 값에 접근한다는 것을 의미함

### ❓데이터를 언제 계산

- 컬렉션: 현재 자료구조가 포함하는 모든 값을 메모리에 저장하는 자료구조
    - 컬렉션의 모든 요소는 컬렉션에 `추가하기 전`에 계산되어야 한다.
    
    → `적극적 생성` : 모든 값을 계산할 때까지 기다림
    
- 스트림: 요철할 때만 요소를 계산하는 고정된 자료구조
    - `필요할 때` 값을 계산한다.
    
    → `게으른 생성` :  필요할 때만 계산
    

## 4.3.1 딱 한 번만 탐색할 수 있다

- 반복자와 마찬가지로 스트림도 한번만 탐색할 수 있다.

→ 한 번 탐색한 요소를 다시 탐색하기 위해서는, 초기 데이터 소스에서 새로운 스트림을 만들어야 함

```java
List<String> title = Arrays.asList("Java8", "In", "Action");
Stream<String> s = title.stream();
s.forEach(System.out::println); //title의 각 단어를 출력
s.forEach(System.out::println); //java.lang.IllegalStateException: 스트림이 이미 소비되었거나 닫힘
```

## 4.3.2 외부 반복과 내부 반복

- 컬렉션 인터페이스를 사용하려면 사용자가 `직접 요소를 반복`해야함

→ `외부 반복(external iteration)`

```java
//for-each를 이용한 외부 반복
List<String> names = new ArrayList<>();
for(Dish dish: menu) { //메뉴 리스트를 명시적으로 순차 반복
    names.add(dish.getName()); //이름 추출후 리스트에 추가
}
```

```java
//내부적으로 숨겨져있던 반복자를 사용한 외부 반복
List<String> names = new ArrayList<>();
Iterator<String> iterator = menu.iterator();
while(iterator.hasNext()) {//명시적 반복
	Dish dish = iterator.next();
	names.add(dish.getName());
}
```

- 스트림 라이브러리는 반복을 알아서 처리하고 결과 스트림값을 어딘가에 저장해 줌

→ `내부 반복(internal iteration)`

- filter와 map같이 반복을 숨겨주는 연산 리스트가 미리 정의되어 있어야 한다. 반복을 숨겨주는 대부분의 연산은 람다 표현식을 인수로 받는다.

```java
List<String> names = menu.stream()
	.map(Dish::getName) //getName 메서드로 파라미터화해서 요리명 추출
	.collect(toList()); //파이프라인 실행, 반복자 필요없음
```

<img width="462" alt="2" src="https://github.com/xogns1514/modernJava/assets/66353672/cac2d738-ad9e-4f10-9d62-6e67c9beb9bd">

📌 내부 반복을 이용하면 작업을 더 `투명하게 병렬로 처리`하거나 더 `최적화된 다양한 순서로 처리` 가능

→ 스트림 라이브러리 내부 반복은 데이터 표현과 하드웨어를 활용한 병렬성 구현을 `자동`으로 선택

    for-each를 사용하는 외부 반복에서는 병렬성을 스스로 관리해야 한다.

## 4.4 스트림 연산

스트림 연산은 두 그룹으로 구분할 수 있다.

<img width="305" alt="3" src="https://github.com/xogns1514/modernJava/assets/66353672/e8a7deaa-6b9f-48c3-bf36-da7fe67a6ffe">


1. 중간 연산(intermediate operation): 연결할 수 있는 스트림 연산
2. 최종 연산(terminal operation): 스트림을 닫는 연산

## 4.4.1 중간 연산

- 중간 연산은 다른 스트림을 반환한다. 따라서 여러 중간 연산을 연결해서 질의를 만들 수 있다.
- 단말 연산을 스트림 파이프라인에 실행하기 전까지는 아무 연산도 수행하지 않는다

→ 게으른 생성을 한다. 중간 연산을 합친 다음에 합쳐진 중간 연산을 최종 연산으로 한 번에 처리하기 때문이다.

```java
List<String> names = 
	menu.stream()
	.filter(dish -> { System.out.println("filtering:" + dish.getName());
										return dish.getCalories() > 300;}) //필터링한 요리명 출력
	.map(dish -> { System.out.println("mapping:" + dish.getName());
								 return dish.getName();}) //추출한 요리명 출력
	.limit(3)
	.collect(toList());
System.out.println(names);
/*
출력 결과
filtering:pork
mapping:pork
filtering:beef
mapping:beef
filtering:chicken
mapping:chicken
[pork, beef, chicken]
*/
```

- 게으른 특성으로 인한 최적화
    - limit연산, 쇼트 서킷: 300 칼로리가 넘는 요리는 여러 개지만 처음 3개만 선택됨
    - 루프 퓨전(loop fusion): filter와 map은 서로 다은 연산이지만 한 과정으로 병합

## 4.4.2 최종 연산

최종 연산은 스트림 파이프라인에서 결과를 도출한다. 보통 최종 연산에 의해 List, Integer, void 등 스트림 이외의 결과가 반환된다. 

ex) 파이프라인에서 forEach는 소스의 각 요리에 람다를 적용한 다음에 void를 반환하는 최종 연산이다. 

## 4.4.3 스트림 이용하기

이용 과정

1. 질의를 수행할(컬렉션 같은) 데이터 소스
2. 스트림 파이프라인을 구성할 중간 연산 연결
3. 스트림 파이프라인을 실행하고 결과를 만들 최종 연산

→ 스트림 파이프라인의 개념은 빌터 패턴과 유사하다.

❓빌더 패턴: 복잡한 객체의 생성 과정과 표현 방법을 분리하여 다양한 구성의 인스턴스를 만드는 생성 패턴

```java
//builder 패턴 예제
Person person = new Person.Builder("Hon", "Gik")
                        .age(30)
                        .address("123 Main St")
                        .build();
```

- 중간 연산

| 연산 | 형식 | 반환 형식 | 연산의 인수 | 함수 디스크립터 |
| --- | --- | --- | --- | --- |
| filter | 중간 연산 | Stream<T> | Predicate<T> | T → boolean |
| map | 중간 연산 | Stream<R> | Function<T, R> | T → R |
| limit | 중간 연산 | Stream<T> |  |  |
| sorted | 중간 연산 | Stream<T> | Comparator<T> | (T, T) → int |
| distinct | 중간 연산 | Stream<T> |  |  |
- 최종연산

| 연산 | 형식 | 반환 형식 | 목적 |
| --- | --- | --- | --- |
| forEach | 최종연산 | void | 스트림의 각 요소를 소비하면서 람다를 적용한다. |
| count | 최종연산 | long(generic) | 스트림의 요소 개수를 반환한다. |
| collect | 최종연산 |  | 스트림을 리듀스해서 리스트, 맵, 정수 형식의 컬렉션을 만든다. |