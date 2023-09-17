# CH 6 스트림으로 데이터 수집

reduce가 그랬던 것 처럼 collect 역시 다양한 요소 누적 방식을 인수로 받아서 스트림을 최종 결과로 두출하는 리듀싱 연산을 수행할 수 있음을 설명한다. 다양한 요소 누적 방식은 Collector 인터페이스에 정의되어 있다. 

```java
Map<Currency, List<Transaction>> transactionByCurrencies = new HashMap<>();
        for(Transaction transaction : transactions) { //트랜잭션 리스트 반복
            Currency currency = transaction.getCurrency();
            List<Transaction> transactionsForCurrency =
                    transactionByCurrencies.get(currency);
            if(transactionsForCurrency == null) {
                transactionsForCurrency = new ArrayList<>();
                transactionByCurrencies.put(currency, transactionsForCurrency);
            }
            transactionsForCurrency.add(transaction);
        }
```

<통화별로 트랜잭션을 그룹화한 코드(명령형 버전)>

문제점: ‘통화별로 트랜잭션 리스트를 그룹화하시오’ 라고 간단히 표현할 수 있지만 코드가 무엇을 실행하는 지 한눈에 파악하기 어렵다.

```java
Map<Currency, List<Transaction>> transactionByCurrencies = 
	transactions.stream().collect(groupingBy(Transaction::getCurrency));
```

<collect 메서드를 이용한 구현>

## 6.1 컬렉터란 무엇인가?

함수형 프로그래밍에서는 ‘무엇’을 원하는지 직접 명시할 수 있어서 어떤 방법으로 이를 얻을 지 신경 쓸 필요가 없다. Collector 인터페이스 구현은 스트림의 요소를 어떤 식으로 도출할지 지정한다.

다수준(multilevel)으로 그룹화를 수행할 때 명령형 프로그래밍과 함수형 프로그래밍의 차이점이 더욱 두드러진다. 명령형 코드에서는 문제를 해결하는 과정에서 다중 루프와 조건문을 추가하며 가독성과 유지보수성이 크게 떨어진다.

## 6.1.1 고급 리듀싱 기능을 수행하는 컬렉터

<aside>
💡 훌륭하게 설계된 함수형 API의 또 다른 장점으로 높은 수준의 조합성과 재사용성을 꼽을 수 있다.

</aside>

<aside>
💡 collect로 결과를 수집하는 과정은 간단하면서도 유연한 방식으로 정의할 수 있다는 점이 컬렉터의 최대 강점이다.

</aside>

<img width="371" alt="3" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/0de49633-31d8-43e2-a266-cf8d5326bcec">

<내부적으로 리듀싱 연산이 일어나는 과정>

보통 함수를 요소로 변환할 때는 컬렉터를 적용하며 최종 결과를 저장하는 자료구조에 값을 누적한다. Collectors 유틸리티 클래스는 자주 사용하는 컬렉터 인스턴스를 손쉽게 생성할 수 있는 정적 팩토리 메서드를 제공한다. 

ex) Collectors.toList()

## 6.1.2 미리 정의된 컬렉터

Collectors에서 제공하는 메서드 기능

1. 스트림 요소를 하나의 값으로 리듀스하고 요약
    - ex) 트랜잭션 리스트에서 트랜잭션 총합 찾기
2. 요소 그룹화
    - 다수준으로 그룹화하거나 각각의 결과 서브그룹에 추가로 리듀싱 연산 적용
3. 요소 분할
    - 한 개의 인수를 받아 불리언을 반환하는 함수, 즉 프레디케이트를 그룹화 함수로 사용한다.

## 6.2 리듀싱과 요약

컬렉터로 스트림의 항목을 컬렉셔능로 재구성 할 수 이싿. 즉 컬렉터로 스트림의 모든 항목을 하나의 결과로 합칠 수 있다. 

```java
long howManyDishes = menu.stream().collect(Collectors.counting());
logn howManyDishes = menu.stream().count();
```

## 6.2.1 스트림값에서 최댓값과 최솟값 검색

- Collectros.maxBy, Collectoers.minBy: 스트림의 최댓값과 최솟값을 계산할 수 있다.

두 컬렉터는 스트림의 요소를 비교하는데 사용할 Comparator를 인수로 받는다.

```java
//칼로리로 요리 비교 Comparator
Comparator<Dish> dishCaloriesComparator = Comparator.comparingInt(Dish::getCalories);
Optional<Dish> mostCalorieDish = menu.stream().collect(maxBy(dishCaloriesComparator));
```

## 6.2.2 요약 연산

스트림에 있는 객체의 숫자 필드의 합계나 평균 등을 반환하는 연산에도 리듀싱 기능이 자주 사용된다. 이러한 연산을 `요약연산(summarization)`이라 부른다.

- `Collectors.summingInt`: 객체를 int로 매핑하는 함수를 인수로 받는다. 인수로 전달된 함수는 객체를 int로 매핑한 컬렉터를 반환한다. summingInt가 collect 메서드로 전달되면 요약 작업을 수행한다.

```java
int totalCalories = menu.stream().collect(summingIng(Dish::getCalories));
```

<img width="423" alt="4" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/fc604be5-2c7b-4da3-8a50-5fb7adf3e84d">

- `averageInt, averageLong, averageDouble`: 다양한 형식으로 이루어진 숫자 집합의 평균을 계산할 수 있다.

```java
double avgCalories = menu.stream().collect(averagingInt(Dish::getCalories));
```

- `summarizingInt`: 컬렉터로 스트림의 요소 수를 계산하고, 최댓값과 최솟값을 찾고, 합계와 평균을 계산하는 방법 중 두 개 이상의 연산을 한번에 수행해야 할 때도 있다. 이때 이 메서드가 반환한 컬렉터를 사용할 수 있다.

```java
IntSummaryStatistics menuStatistics = 
	menu.stream().collect(summarizingInt(Dish::getCaloires));

//IntSummaryStatistics{count = 9, sum = 4300, min = 120, average = 477.777778, max = 800} 
```

(summarizingLong - LongSummaryStatistics, summarizingDouble - DoubleSummaryStatistics 도 있다)

## 6.2.3 문자열 연결

- `joining`: 스트림의 각 객체에 toString 메서드를 호출해서 추출한 모든 문자열을 하나의 문자열로 연결해서 반환한다.

```java
String shortMenu = menu.stream().map(Dish::getName).collect(joining());
//porkbeefchickenfrench friesriceseason fruitpizzaprawnssalmon
```

joining 메서드는 내부적으로 StringBuilder를 이용해서 문자열을 하나로 만든다.

두 요소 사이에 구분 문자열을 넣을 수 있도록 오버로드된 joining 팩토리 메서드도 있다.

```java
String shortMenu = menu.stream.map(Dish::getName).collect(joining(", "));
//pork, beef, chicken, french fries, rice, season fruit, pizza, prawns, salmon
```

## 6.2.4 범용 리듀싱 연산

특화된 컬렉터를 사용한 이유는 프로그래밍적 편의성 때문이다. 하지만 프로그래머의 편의성 뿐만 아니라 가독성도 중요하다.

```java
int totalCalories = menu.stream.collect(reducung(
	0, Dish::getCalories, (i, j) -> i + j));
//reducing 메서드를 활용한 메뉴의 모든 칼로리 합계
```

reducing은 세개의 인수를 받는다

1. 리듀싱 연산의 시작값이나거나, 스트림에 인수가 없을 때는 반환값
2. 요리를 칼로리 정수로 변환할 때 사용한 변환 함수
3. 같은 종류의 두 항목을 하나의 값으로 더하는 BinaryOperator

```java
Optional<Dish> mostCalorieDish = 
	menu.stream().collect(reducing(
		(d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));
//한 개의 인수를 갖는 reducing 팩토리 메서드
```

한 개의 인수를 갖는 reducing 팩토리 메서드

- 스트림의 첫 번째 요소를 시작요소, 첫번 째 인수로 받음
- 자신을 그래도 반환하는 항등함수를 두 번째 인수로 받음

## 컬렉션 프레임워크 유연성: 같은 연산도 다양한 방식으로 수행할 수 있다.

```java
//람다 대신 메서드 참조를 이용한 단순화
int totalCalories = menu.stream().collect(reducing(0,//초깃값
	Dish::getCalories,//변환 함수
	Integer::sum));//합계 함수
```

<img width="457" alt="5" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/11318916-c096-4a99-8ed9-73d44e7220c5">

- 리듀싱 연산: 누적자를 초깃값으로 초기화하고, 합계 함수를 이용해서 각 요소에 변환 함수를 적용한 결과 숫자를 반복적으로 조합한다.

counting 컬렉터도 세 개의 인수를 갖는 reducing 팩토리 메서드를 이용해서 구현할 수 있다.

```java
public static <T> Collector<T, ?, Long> couting() {
	return reducing(0L, e -> 1L, Long::sum);
}
```

Collector를 이용하지 않은 방법

```java
int totalCalories = 
	menu.stream().map(Dish::getCalories).reduce(Integer::sum).get
//빈 stream과 관련한 null 문제를 피할 수 있도록 Optional<Integer> 반환
```

IntStream의 sum을 이용한 방법

```java
int totalCalories = 
	menu.mapToInt(Dish::getCalories).sum();
```

## 자신의 상황에 맞는 최적의 해법 선택

함수형 프로그래밍에서는 하나의 연산을 다양한 방법으로 해결할 수 있음을 보여준다.

스트림 인터페이스에서 직접 제공하는 메서드를 이용하는 것에 비해 컬렉터를 이용하는 코드가 좀 더 복잡한 대신 재사용성과 커스터마이즈 가능성을 제공하는 높은 수준의 추상화와 일반화를 얻을 수 있다. 

→ `가장 일반적으로 문제에 특화된 해결책을 고르는 것이 바람직하다.`

## 6.3 그룹화

데이터 집합을 하나 이상의 특성으로 분류해서 그룹화하는 연산도 데이터베이스에서 많이 수행되는 작업이다. 명령형으로 그룹화를 구한하려면 까다롭고, 할일이 많으며, 에러도 많이 발생한다. 

하지만 자바 8의 함수형을 이용하면 가독성 있는 한 줄의 코드로 그룹화를 구현할 수 있다.

Collectors.groupingBy를 이용한 그룹화

```java
Map<Dish.Type, List<Dish>> dishesByType = 
	menu.stream().collect(groupingBy(Dish::getType));
/*
toSting이 name 반환인 경우이다.
{MEAT=[pork, beef, chicken], FISH=[prawns, salmon], 
OTHER=[french fries, rice, season fruit, pizza]}
*/
```

스트림의 각 요리에서 Dish.type과 일치하는 모든 요리를 추출하는 함수를 groupingBy 메서드로 전달했다. 이 함수를 기준으로 스트림이 그룹화되므로 이를 `분류함수(classification function)`

<img width="465" alt="6" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/da7aefe4-c78c-43dc-aa44-c449781b301f">

그룹화 연산의 결과로 그룹화 함수가 반환하는 키 그리고 각 키에 대응하는 스트림의 모든 항목 리스트를 값으로 갖는 맵이 반환된다. 메뉴 그룹화 예제에서 키는 요리 종류고, 값은 해당 종류에 포함되는 모든 요리다. 

문제점: 단순한 속성 접근자 대신 더 복잡한 분류 기준이 필요한 상황에서는 메서드 참조를 분류 함수로 사용할 수 없다. 

→ 메서드 참조 대신 람다 표현식으로 필요한 로직을 구현할 수 있다.

```java
private static Map<CaloricLevel, List<Dish>> groupDishesByCaloricLevel() {
        return Dish.menu.stream().collect(
                groupingBy(dish -> {
                    if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                    else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                    else return CaloricLevel.FAT;
                })
        );
    }
```

## 6.3.1 그룹화된 요소 조작

요소를 그룹화 한 다음 각 결과 그룹의 요소를 조작하는 연산이 필요하다. 

```java
//500칼로리가 넘는 요리만 필터링 후 그룹화
Map<Dish.Type, List<Dish>> caloricDishesByType = 
	menu.stream()
	.filter(dish -> dish.getCalories() > 500)
	.collect(groupingBy(Dish::getType));
//{MEAT=[pork, beef], OTHER=[french fries, pizza]}
```

단점: 필터 프레디케이트를 만족하는 FISH 종류 요리는 없으므로 결과 맵에서 해당 키 자체가 사라진다. 

→ Collectors 클래스는 일반적인 분류 함수에 Collector 형식의 두 번째 인수를 갖도록 groupingBy 팩토리 메서드를 오버로드해 이 문제를 해결한다. 

```java
Map<Dish.Type, List<Dish>> caloricDishesByType = 
	menu.stream()
	.collect(groupingType(Dish::getType, 
		filtering(dish -> dish.getCalories() > 500, toList())));
//두 번째 Collector 안으로 필터 프레디케이트를 이동함으로 이 문제를 해결한다.
//{MEAT=[pork, beef], FISH=[], OTHER=[french fries, pizza]}
```

그룹화된 항목을 조작하는 다른 유용한 기능 중 또 다른 하나로 매핑 함수를 이용해 요소를 변환하는 작업이 있다. Collectors 클래스는 매핑 함수와 각 항목에 적용한 함수를 모으는 데 사용하는 또 다른 컬렉터를 인수로 받는 mapping 메서드를 제공한다.

```java
private static Map<Dish.Type, List<String>> groupDishNamesByType() {
        return Dish.menu.stream().collect(
                groupingBy(Dish::getType,
                        mapping(Dish::getName, toList())));
    }
//{MEAT=[pork, beef, chicken], FISH=[prawns, salmon], OTHER=[french fries, rice, season fruit, pizza]}
```

groupingBy와 연계해 세 번째 컬렉터를 사용해서 일반 맵이 아닌 flatMap 변환을 수행할 수 있다. 

```java
private static Map<Dish.Type, Set<String>> groupDishTagsByType() {
        return Dish.menu.stream()
                .collect(groupingBy(Dish::getType,
                        flatMapping(dish -> Dish.dishTags.get(dish.getName()).stream(),
                                toSet())));
    }
//{MEAT=[salty, greasy, roasted, fried, crisp], FISH=[roasted, tasty, fresh, delicious], OTHER=[salty, greasy, natural, light, tasty, fresh, fried]}
```

- 두 수준의 리스트를 한 수준으로 평면화한다. 각 그룹에 수행한 flatMapping 연산 결과를 수집해서 리스트가 아니라 집합으로 그룹화해 중복 태그를 제거한다.

## 6.3.2 다수준 그룹화

두 인수를 받는 팩토리 메서드 `Collectors.groupingBy`를 이용해서 항목을 다수준으로 그룹화할 수 있다. 

Collectors.groupingBy는 일반적인 분류 함수와 컬렉터를 인수로 받는다. 

`바깥쪽 groupingBy 메서드에` 스트림의 항목을 분류할 두 번째 기준을 정의하는 `내부 groupingBy를 전달`해서 두 수준으로 스트림의 항목을 그룹화할 수 있다.

```java
private static Map<Dish.Type, Map<CaloricLevel, List<Dish>>> groupDishedByTypeAndCaloricLevel() {
        return Dish.menu.stream().collect(
                groupingBy(Dish::getType,
                        groupingBy(dish -> {
                            if (dish.getCalories() <= 400)
                                return CaloricLevel.DIET;
                            else if (dish.getCalories() <= 700)
                                return CaloricLevel.NORMAL;
                            else
                                return CaloricLevel.FAT;
                        }))
        );
    }
/*
{MEAT={DIET=[chicken], NORMAL=[beef], FAT=[pork]}, FISH={DIET=[prawns], 
NORMAL=[salmon]}, OTHER={DIET=[rice, season fruit], NORMAL=[french fries, pizza]}}
*/
```

<img width="423" alt="7" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/c3d1c74a-1985-45f8-a83d-080cefaf9fd0">

❗️ 보통 groupingBy의 연산을 ‘버킷’ 개념으로 생각하면 쉽다. 첫 번째 groupBy는 각 키의 버킷을 만든다. 그리고 준비된 각각의 버킷을 서브스트림 컬렉터로 채워가기를 반복하면서 n 수준 그룹화를 달성한다.

## 6.3.3 서브그룹으로 데이터 수집

첫 번째 groupingBy로 넘겨주는 커레터의 형식은 제한이 없다. 

→ 분류 함수 한 개의 인수를 갖는 groupingBy(f)는 사실 groupingBy(f, toList())의 축약형이다.

```java
//메뉴의 요리수를 종류별로 계산
Map<Dish.Type, Long> typesCount = menu.stream()
	.collect(groupingBy(Dish::getType, counting()));
//{MEAT=3, FISH=2, OTHER=4}
```

```java
//메뉴에서 가장 높은 칼로리를 가진 요리를 찾음
Map<Dish.Type, Optional<Dish>> mostCaloricByType = 
	menu.stream()
		.collect(groupingBy(Dish::getType,
			maxBy(comparingIng(Dish::getCalories))));
//{MEAT=Optional[pork], FISH=Optional[salmon], OTHER=Optional[pizza]}
```

📌 maxBy 반환값에 따라 Optional을 반환한다. 하지만 메뉴 중 Optional.empty()를 값으로 갖는 요리는 없다. 

groupingBy 컬렉터는 스트림의 첫 번째 요소를 찾은 이후에야 그룹화 맵에 새로운 키를 추가한다. 리듀싱 컬렉터가 반환하는 형식을 사용하는 상황이므로 굳이 Optional 래퍼를 사용할 필요가 없다

### 컬렉터 결과를 다른 형식에 적용하기

팩토리 메서드 `Collectors.collectingAndThen`으로 컬렉터가 반환한 결과를 다른 형식으로 활용할 수 있다.

```java
Dish.menu.stream()
                .collect(groupingBy(Dish::getType, collectingAndThen(
                        maxBy(Comparator.comparingInt(Dish::getCalories)),
                        Optional::get
                )));
//{MEAT=pork, FISH=salmon, OTHER=pizza}
//maxBy가 collectingAndThen으로 감싸지며, Optional::get으로 반환된 Optional에 포함된 값을 추출한다.
```

- collectingAndThen은 적용할 컬렉터와 변환 함수를 인수로 받아 다른 컬렉터를 반환한다. 반환되는 컬렉터는 기존 컬렉터의 래퍼 역할을 하며 collect의 마지막 과정에서 변환 함수로 자신이 반환하는 값을 매핑한다.
    
    <img width="394" alt="8" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/47aae4f7-2e5e-499b-8f32-a23cafbbbcc7">
    
    1. 컬렉터는 점선으로 표시되어 있으며 groupingBy는 가장 바깥쪽에 위치하면서 요리의 종류에 따라 메뉴스트림을 세 개의 서브스트림으로 그룹화한다.
    2. groupingBy 컬렉터는 collectingAndThen 컬렉터를 감싼다. 따라서 두 번째 컬렉터는 그룹화된 세 개의 서브스트림에 적용된다.
    3. collectiongAndThen 컬렉터는 세 번째 컬렉터 maxBy를 감싼다
    4. 리듀싱 컬렉터가 서브스트림에 연산을 수행한 결과에 collectiongAndThen의 Optional::get 변환 함수가 적용된다
    5. groupingBy 컬렉터가 반환하는 맵의 분류 키에 대응하는 세 값이 각각의 요리 형식에서 가장 높은 칼로리다.
    
    ### groupingBy와 함께 사용하는 다른 컬렉터 예제
    
    일반적으로 스트림에서 같은 그룹으로 분류된 모든 요소에 리듀싱 작업을 수행할 때는 팩토리 메서드 groupingBy에 `두 번째 인수로 전달한 컬렉터`를 사용한다.
    
    ```java
    //각 그룹 요리의 칼로리 합계
    Map<Dish.Type, Integer> totalCaloriesByType = 
    	menu.stream()
    		.collect(groupingBy(Dish::getType,
    		summingInt(Dish::getCalories)));
    ```
    
    `mapping 메서드로 만들어진 컬렉터`도 groupingBy와 자주 사용된다. mapping 메서드는 스트림의 인수를 변환하는 함수와 변환 함수의 결과 객체를 누적하는 컬렉터를 인수로 받는다. 
    
    ```java
    Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType = 
    	menu.stream()
    		.collect(
    			groupingBy(Dish::getType, mappingBy(dish -> {
    				if(dish.getCalories() <= 400) return CaloricLevel.DIET;
    				else if(dish.getCalories() <= 700) return CaloricLevel.NORMAL;
    				else return CaloricLevel.FAT; }.
    			.toSet() )));
    //{MEAT=[DIET, NORMAL, FAT], FISH=[DIET, NORMAL], OTHER=[DIET, NORMAL]}
    ```
    
    mapping 메서드에 전달한 변환 함수는 Dish를 CaloricLevel로 매핑한다. CaloricLevel 결과 스트림은 toSet 컬렉터로 전달되면서 집합으로 스트림의 요소가 누적된다. 
    
    위에서는 Set의 형식이 정해져 있지 않았다. toCollection을 이용하면 원하는 방식으로 결과를 제어할 수 있다.
    
    ```java
    Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType = 
    	menu.stream()
    		.collect(
    			groupingBy(Dish::getType, mappingBy(dish -> {
    				if(dish.getCalories() <= 400) return CaloricLevel.DIET;
    				else if(dish.getCalories() <= 700) return CaloricLevel.NORMAL;
    				else return CaloricLevel.FAT; }.
    			.toCollection(HashSet::new) )));
    //{MEAT=[DIET, NORMAL, FAT], FISH=[DIET, NORMAL], OTHER=[DIET, NORMAL]}
    ```
    
    ## 6.4 분할
    
    분할은 분할 함수(partitioning function)라 불리는 `프레디케이트를 분류 함수로` 사용하는 `특수한 그룹화 기능`이다. 분할 함수는 불리언을 반환하므로 맵의 키 형식은 Boolean이다. 결과적으로 그룹화 맵은 최대 두 개의 그룹으로 분류된다.
    
    ```java
    //모든 요리를 채식요리와 채식이 아닌 요리로 분류
    Map<Boolean, List<Dish>> partitionedMenu = 
    	menu.stream()
    		.collect(partitioningBy(Dish::isVegetarian)); //분할 함수
    /*
    false=[pork, beef, chicken, prawns, salmon], 
    true=[french fries, rice, season fruit, pizza]}
    */
    //참값의 키로 맵에서 모든 채식 요리를 얻을 수 있다.
    List<Dish> = vegetarianDishes = partitionedMap.get(true);
    ```
    
    ## 6.4.1 분할의 장점
    
    컬렉터를 두 번째 인수로 전달할 수 있는 오버로드 된 버전의 partitioningBy 메서드도 있다.
    
    ```java
    private static Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType() {
            return Dish.menu.stream().collect(partitioningBy(Dish::isVegetarian, groupingBy(Dish::getType)));
        }
    /*
    {false={FISH=[prawns, salmon], MEAT=[pork, beef, chicken]}, 
    true={OTHER=[french fries, rice, season fruit, pizza]}}
    */
    ```
    
    채식 요리의 스트림과 채식이 아닌 요리의 스트림을 각각 요리 종류로 그룹화해서 두 수준의 맵이 반환되었다. 
    
    ```java
    //채식 요리와 채식이 아닌 요리 각각의 그룹에서 칼로리가 높은 요리 찾기
    private static Map<Boolean, Dish> mostCaloricPartitionedByVegetarian() {
           return Dish.menu.stream()
                    .collect(
                            partitioningBy(Dish::isVegetarian,
                                    collectingAndThen(maxBy(Comparator.comparingInt(Dish::getCalories)),
                                            Optional::get))
                    );
    //{false=pork, true=pizza}
    ```
    
    → partitoningBy가 반환한 맵 구현은 참과 거짓 두 가지 키만 포함하므로 더 간결하고 효과적이다. 
    
    ## 6.4.2 숫자를 소수와 비소수로 분할하기
    
    ```java
    //주어진 소가 소수인지 아닌지 판단하는 프레디케이트
    public static boolean isPrime(int candidate) {
            return IntStream.rangeClosed(2, candidate) 2~candidate 미만 자연수 생성
                    .noneMatch(i -> candidate % i == 0);
        }
    ```
    
    ```java
    //제곱근 이하의 수로 제한
    public static boolean isPrime(int candidate) {
            int candidateRoot = (int) Math.sqrt((double) candidate);
    
            return IntStream.rangeClosed(2, candidateRoot)
                    .noneMatch(i -> candidate % i == 0);
        }
    ```
    
    위 프레디케이트를 이용해 partitioningBy 컬렉터로 리듀스해서 숫자를 소수와 비소수로 분류할 수 있다.
    
    ```java
    public static Map<Boolean, List<Integer>> partitionPrimes(int n) {
            return IntStream.rangeClosed(2, n).boxed()
                    .collect(partitioningBy(candidate -> isPrime(candidate)));
        }
    ```
    
    ## 6.5 Collector 인터페이스
    
    Collector 인터페이스는 리듀싱 연산(즉, 컬렉터)을 어떻게 구현할 지 제공하는 메서드 집합으로 구성된다. 
    
    ```java
    //Collector 인터페이스
    public interface Collector<T, A, R>{
    	Supplier<A> supplier();
    	BiConsumer<A, T> accumlator();
    	Function<A, R> finisher();
    	BinaryOperator<A> combiner();
    	Set<Characteristics> characteristics();
    }
    ```
    
- T: 수집될 스트림 항목의 제네릭 형식이다.
- A: 누적자, 즉 수집 과정에서 중간 결과를 누적하는 객체의 형식이다.
- R: 수집 연산 결과 객체의 형식(보통 컬렉션 형식이다)이다.

Stream<T>의 모든 요소를 List<T>로 수집하는 ToListCollector<T>라는 클래스를 구현 할 수 있다

```java
public class ToListCollector<T> implemets Collector<T, List<T>, List<T>>
```

## 6.5.1 Collector 인터페이스의 메서드 살펴보기

### supplier 메서드: 새로운 결과 컨테이너 만들기

`수집 과정에서 빈 누적자 인스턴스를 만드는 파라미터가 없는 함수`다. ToListCollector처럼 누적자를 반환하는 컬렉터에서는 빈 누적자가 비어있는 스트림의 수집 과정의 결과가 될 수 있다.

```java
public Supplier<List<T>> supplier(){
	return () -> new ArrayList<T>();
}
//생성자 참조 전달
public Supplier<List<T>> supplier(){
	return ArrayList::new;
}
```

### accumlator 메서드: 결과 컨테이너에 요소 추가하기

`accumlator 메서드는 리듀싱 연산을 수행하는 함수를 반환`한다. 스트림에서 n번째 요소를 탐색할 때 두 인수, 즉 누적자(스트림의 첫 n-1개 항목을 수집한 상태)와 n번째 요소를 함수에 적용한다. 함수의 반환값은 void, 요소를 탐색하면서 적용하는 함수에 의해 누적자 내부상태가 바뀌므로 누적자가 어떤 값일지 단정할 수 없다.

```java
//이미 탐색한 항목을 포함하는 리스트에 현재 항목을 추가한다.
public BiConsumer<List<T>, T> accumlator(){
	return (list, item) -> list.add(item);
}
//메서드 참조 
public BiConsumer<List<T>, T> accumlator(){
	return List::add;
}
```

### finisher 메서드: 최종 변환값을 결과 컨테이너로 적용하기

스트림 탐색을 끝내고 `누적자 객체를 최종 결과로 변환하면서 누적 과정을 끝낼 때 호출할 함수를 반환`한다.

```java
//누적자 결과가 이미 최종 결과이다. 따라서 항등함수를 반환
public Function<List<T>, List<T>> finisher() {
	return Function.identity();
}
```

<img width="463" alt="9" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/62ec0428-dbde-40ed-ba49-e5b37bd2e900">

### combiner 메서드: 두 결과 컨테이너 병합

스트림의 서로 다른 서브파트를 `병렬로 처리할 때 누적자가 이 결과를 어떻게 처리할지 정의`한다. 

```java
//toList의 combiner 구현, 스트림의 두 번째 서브파트에서 수집한 항목 리스트를 첫 번째 서브파트 결과 리스트의 뒤에 추가
public BinaryOperator<List<T>> combiner() {
	return (list1, list2) -> {
		list1.addAll(list2);
		return list1;
	}
}
```

<img width="468" alt="10" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/cfe12f22-093b-4f05-9e33-268d89b92506">

1. 스트림을 분할해야 하는지 정의하는 조건이 거짓으로 바뀌기 전까지 원래 스트림을 재귀적으로 분할한다. 

→ 분산된 작업의 크기가 너무 작아지면 병렬 수행의 속도는 순차 수행의 속도보다 느려진다. 즉, 병렬 수행의 효과가 상쇄된다.

1. 모든 서브스트림(substream)의 각 요소에 리듀싱 연산을 순차적으로 적용해서 서브스트림을 병렬로 처리할 수 있다.
2. 컬렉터의 combiner 메서드가 반환하는 함수로 모든 부분결과를 쌍으로 합친다. 

### Characteristics 메서드

`컬렉터의 연산을 정의하는 Characteristics 형식의 불변 집합을 반환`한다. 스트림을 병렬로 리듀스할 것인지 그리고 병렬로 리듀스한다면 어떤 최적화를 선택해야 할지 힌트를 제공한다.

다음 항목을 포함하는 열거형이다.

- `UNOREDED`: 리듀싱 결과는 스트림 요소의 방문 순서나 누적 순서에 영향을 받지 않는다.
- `CONCURRENT`: 다중 스레드에서 accumlator 함수를 동시에 호출할 수 있으며 이 컬렉터는 스트림의 병렬 리듀싱을 수행할 수 있다.
- `IDENTITY_FINISH`: finisher 메서드가 반환하는 함수는 단순히 identity를 적용할 뿐이므로 이를 생략할 수 있다. 따라서 리듀싱 과정의 최종 결과로 누적자 객체를 바로 사용할 수 있다. 누적자 A를 결과 R로 안전하게 형변환할 수 있다.