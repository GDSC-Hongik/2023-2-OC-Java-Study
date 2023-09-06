# WIL5 - chap06

# **스트림으로 데이터 수집**

지금까지 배운 스트림의 연산은 중간연산과, 최종 연산으로 구분되었다. 최종 연산은 스트림의 요소를 소비해 최종 결과를 도출하는데 4장과 5장에선, collect를 사용해 toList 로 묶어왔다. 컬렉터란 무엇일까?

---

# **컬렉터란?**

컬렉터가 존재하지 않을때, 통화별로 트랜잭션을 그룹화 하라라는 요청이 있다고 하자. 이때의 구현은 아래와 같은데, 상당히 길고 가독성이 떨어진다.

```
Map<Currency,List<Transaction>> transactionByCurrencies = new HashMap<>();

for (Transaction transaction : transactions){
	Currency currency = transaction.getCurrency();
	List<Transaction> transactionForCurrency = transactionsByCurrencies.get(currency);
	if( transactionsForCurrency == null ){
		transactionsForCurrency = new ArrayList<>();
		transactionsByCurrencies.put(currency,transactionsForCurrency);
	}
	transactionsForCurrency.add(transaction);
}

```

컬렉터의 등장으로 위 코드는 아래와 같이 간결해졌다.

```
Map<Currency,List<Transaction>> transactionsByCurrencies =
										transactions.stream().collect(groupingBy(Transaction::getCurrency));

```

### **고급 리듀싱 기능을 수행하는 컬렉터**

스트림에 collect를 호출하면스트림의 요소에 컬렉터로 파라미터화된 리듀싱 연산이 수행된다. 위의 코드를 아래 그림과 같이 내부적으로 reducing 연산을 통해 스트림의 각 요소를 방문하며 컬렉터가 작업을 처리한다.

![https://blog.kakaocdn.net/dn/bdXzVP/btstk4T4nNz/dvuHe2mkklDNUZFxAk64Ik/img.png](https://blog.kakaocdn.net/dn/bdXzVP/btstk4T4nNz/dvuHe2mkklDNUZFxAk64Ik/img.png)

1. 스트림의 각 트랜잭션 탐색
2. 트랜잭션의 통화 추출
3. 통화/트랜잭션 쌍을 그룹화된 Map으로 추가함.

주로, 함수의 데이터 저장 구조를 변환할 땐, 컬렉터를 적용해 최종 결과를 저장하는 자료구조에 값을 누적되며 어떤 Collector 인터페이스의 메서드를 어떻게 구현하느냐에 따라 스트림 리듀싱에 어떤 연산을 수행할지 결정된다. (커스텀 컬렉터도 구현이 가능하다.)

### **미리 정의된 컬렉터**

Collectors에서 제공되는 메서드의 기능은 3가지로 구분된다.

- 스트림의 요소를 하나의 값으로 리듀스하고 요약. ex) 트랜잭션의 총합 찾기
- 스트림의 요소를 그룹화.
- 스트림의 요소를 분할: 프레디케이트를 그룹화 함수로 사용함.

---

# **리듀싱과 요약**

### **스트림값에서 최댓값과 최솟값 검색**

4장에서 등장한 Dish.menu 에서 칼로리가 가장 높은 요리를 찾을때, Collectors.maxBy, Collectors.minBy 를 활용할 수 있다.

```
Comparator<Dish> dishComparator = Comparator.comparingInt(Dish::getCalories);
        Optional<Dish> mostCalorieDish = Dish.menu.stream()
                .collect(maxBy(dishComparator));

```

이 코드는 Java17 에선 아래와 같이 사용할 수 있다.

```
Comparator<Dish> dishComparator = Comparator.comparingInt(Dish::getCalories);
        Optional<Dish> mostCalorieDish = Dish.menu.stream()
                .max(dishComparator);

```

### **요약 연산**

스트림에 있는 객체의 숫자 필드의 합계, 평균을 반환하는 연산에 사용되는 리듀싱 연산을 요약 연산이라고 한다.

칼로리의 합을 구하는 요약 연산의 경우를 보자. 코드는 아래와 같다.

```
menu.stream().collect(summingInt(Dish::getCalories));

```

요약 과정은 아래 그림과 같은데, 우선 각 요소를 칼로리로 변환하는 **변환 과정**, 이후 **리듀싱을 통해 요소의 합**을 구하는 과정으로 나뉜다.

![https://blog.kakaocdn.net/dn/JZ2cU/btstlM6I2q7/zVqkBpYKY6CI8hvmhyNIVk/img.png](https://blog.kakaocdn.net/dn/JZ2cU/btstlM6I2q7/zVqkBpYKY6CI8hvmhyNIVk/img.png)

합을 구하는 summingInt summingDouble summingLong 외에도, 같은 방식으로 averagingInt averagingLong avaeragingDouble 등이 존재한다.

이렇게 컬렉터로 최댓값, 최솟값, 평균,합게를 계산할 수 있는데 이중 두개 이상의 연산을 한번에 수행하고 싶다면 summarizingInt 와 같은 함수를 사용하면 다 볼 수 있다.

![https://blog.kakaocdn.net/dn/RVRAE/btstk4zLTOk/oKCNn4Ox95B1ADMOlTCxl0/img.png](https://blog.kakaocdn.net/dn/RVRAE/btstk4zLTOk/oKCNn4Ox95B1ADMOlTCxl0/img.png)

### **문자열 연결**

컬렉터에 joining 팩토리 메서드를 이용하면 스트림의 각 객체에 toString 메서드를 적용해 추출한 모든 문자열을 하나의 문자열로 연결해서 반환할 수 있다.

```
String shortMenu = menu.stream().map(Dish::getName).collect(Collectors.joining());
        System.out.println("shortMenu = " + shortMenu);

```

![https://blog.kakaocdn.net/dn/xoNPy/btstnxVCkZG/BhukMuwNkjL91uZHsCKggK/img.png](https://blog.kakaocdn.net/dn/xoNPy/btstnxVCkZG/BhukMuwNkjL91uZHsCKggK/img.png)

띄어쓰기는 메뉴 자체에 있던 띄어쓰기이다. 합쳐진 문자열을 구분할 수 없다는 문제가 있는데 구분할 수 있는 문자열을 아래와 같이 넣을 수 있다.

```
String shortMenu = menu.stream().map(Dish::getName).collect(Collectors.joining(", "));

```

![https://blog.kakaocdn.net/dn/dPyztK/btstff3pTT2/6Bd6KfdVloF4gkVk8YMbBk/img.png](https://blog.kakaocdn.net/dn/dPyztK/btstff3pTT2/6Bd6KfdVloF4gkVk8YMbBk/img.png)

### **범용 리듀싱 요약 연산**

위에서 다룬 모든 컬렉터는 reducing 팩토리 메서드에 존재해 사용이 가능하다. 하지만**, 프로그래밍적 편의성, 가독성**의 이유로, 특화된 컬렉터를 사용한다.

reducing 의 라이브러리를 보면 다음과 같은데, 3개의 파라미터를 받는다.

- 리듀싱 연산의 시작값 or 스트림에 인수가 없을때의 반환값
- 변환에 사용하는 함수
- 같은 종류의 두 항목을 하나의 값으로 합치는 BinaryOprator

reducing 연산은 인자의 개수가 한개인 경우, 파라미터가 3개인 reducing 함수의 첫번째 인자에는 스트림의 첫번째 요소를, 두번째 인자엔 자신을 반환하는 항등 함수를, 세번째 인자에 전달 받은 값을 넣어 사용한다.

Collectors와 reducing을 아래와 같이 적절히 바꿔 활용할 수 있다.

```
String shortMenu = menu.stream().map(Dish::getName).collect(joining());

```

위 예제 joining을 사용한 collect 코드를 reducing을 사용해 적절히 바꾸면 아래와 같다.

```
String shortMenu = menu.stream().map(Dish::getName)
										.collect(reducing( (s1,s2) -> s1+s2)).get;String shortMenu = menu.stream()
										.collect(reducing("",Dish::getName,(s1,s2) -> s1+s2));
```

---

# **그룹화**

데이터를 하나 이상의 특성으로 분류해서 그룹화하는 연산은 DB에서 많이 수행되는 연산이다. 명령형으로 그룹화를 구현하는 것은 복잡하지만 자바 8의 함수형을 이용하면 가독성 있고 짧은 코드를 구현할 수 있다.

menu를 Type 별로 묶기 위해선 아래와 같이 구현할 수 있다.

```
Map<Type, List<Dish>> dishesByType = menu.stream().collect(groupingBy(Dish::getType));

```

이렇게 특정 타입과 일치하는 그룹화함수를 **분류 함수**라고 한다.

그룹화하는 원리는 아래 그림과 같다.

- 스트림에서 요소를 꺼내서 분류함수를 통해 적절한 Key를 찾는다.
- Key에 맞는 항목을 리스트로 분류한다.

![https://blog.kakaocdn.net/dn/ckQ2yt/btstey90Yhy/WMsYhQzpn9HUWZQuNfPXmK/img.png](https://blog.kakaocdn.net/dn/ckQ2yt/btstey90Yhy/WMsYhQzpn9HUWZQuNfPXmK/img.png)

여기서는 분류함수로 메서드 참조를 이용했는데, 더 복잡한 분류 기준(가령, 400칼로리 이하를 diet라고 하자)이 존재한다면, 메서드 참조를 사용할 수 없다. 이 경우엔 아래와 같이 람다를 이용한 코드를 작성해야한다.

```
menu.stream().collect(
                groupingBy(dish->{
                    if(dish.getCalories()<=400)return CaloricLevel.DIET;
                    else if (dish.getCalories()<=700)return CaloricLevel.NORMAL;
                    else return CaloricLevel.FAT;
                })
        )

```

그렇다면 **두 가지 기준으로 동시에 그룹화가 가능할까?**

### **그룹화된 요소 조작**

500칼로리가 넘는 요리를 타입 별로 필터링 하는 경우를 생각해보자.

일반적으로 filter 를 통해 500칼로리 이상의 음식을 찾고 이어서 Type별로 GroupBy를 수행하면 될 것이라고 생각하지만, filter를 하고 난 이후 존재하지 않는 Type에 대해서는 알 수가 없다. 즉, 키 자체가 사라진다.

따라서, Collector 클래스의 정적 팩토리 메서드로 filtering 을 사용할 수 있다. filter와 같이 프레디케이트를 인자로 받고, groupingBy 의 두번째 인자로 전달될 수 있다.

```
Map<Type, List<Dish>> filteringAfter = menu.stream()
                .collect(groupingBy(Dish::getType
                        , filtering(dish -> dish.getCalories() > 500, toList())));
        System.out.println("filteringAfter = " + filteringAfter);

```

![https://blog.kakaocdn.net/dn/cWCFyM/btsteyB6Zoc/tHGtAo2KQpeBEIbLFKO3WK/img.png](https://blog.kakaocdn.net/dn/cWCFyM/btsteyB6Zoc/tHGtAo2KQpeBEIbLFKO3WK/img.png)

그 결과 Grouping 된 항목들의 조절이 적절히 가능해졌다. (filter)

또한 filtering 대신 mapping 또한 사용 가능하다.

### **다수준 그룹화**

지금까진 한가지 기준으로 그룹화를 하였는데 두 가지 이상의 기준으로 그룹화가 가능할까?

두 가지 groupingBy를 사용하면 된다!

```
Map<Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel = menu.stream().collect(
                groupingBy(Dish::getType,
                        groupingBy(dish -> {
                            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                            else return CaloricLevel.FAT;
                        }))
        );

```

그 결과 바깥쪽 groupingBy 였던 Type에 대해 우선 분류가 되고 이후 Calories에 따른 분류가 된다.

결과.

```
{
	MEAT={NORMAL=[beef], FAT=[pork], DIET=[chicken]},
	FISH={NORMAL=[salmon], DIET=[prawns]},
	OTHER={NORMAL=[french fries, pizza], DIET=[rice, season fruit]}
}

```

이렇게 N 수준으로 그룹화를 하게된다면 n 수분 트리구조의 맵이 된다.

---

# **분할**

분할이란, 분할 함수라 불리는, 프레디케이트를 분류 함수로 사용하는 특수화 기능이다. Boolean을 반환하므로 2개의 그룹으로 분리된다. 채식 요리와 채식이 아닌 요리를 비교하는 코드는 아래와 같다.

```
menu.stream().collect(partitioningBy(Dish::isVegetarian));

```

분할을 통해 참, 거짓으로 분할된 스트림의 모든 요소를 유지할 수 있다.

또한, 두번째 인수로 컬렉터를 전달해 분할 이후의 명령을 지시할 수도 있다.

```
menu.stream().collect(partitioningBy(Dish::isVegetarian,
                collectingAndThen(maxBy(Comparator.comparingInt(Dish::getCalories))
                , Optional::get)));

```

### **소수 분류 문제**

1. 소수를 판별하기 위한 predicate를 구현하자

```
public boolean isPrime(int candidate){
		int candidateRoot = (int) Math.sqrt((double)candidate);
		return IntStream.rangedClosed(2,candidateRoot)
										.noneMatch(i->candidate%i==0);
}

```

1. partitioningBy에 Predicate로 isPrime 을 넣어 소수를 분류하자.

---

# **Collector 인터페이스**

Collector 인터페이스는 리듀싱(컬렉터) 연산을 어떻게 구현할지 제공하는 메서드 집합으로 구성되어있다.

Collector인터페이스는 아래와 같이 <T,A,R>을 받는다.

- T: 수집될 스트림 항목의 제네릭 형식
- A: 수집 과정에서 중간 결과를 누적하는 객체의 타입
- R: 수집 연산 결과 객체의 형식(주로 컬렉션 형식)

### **supplier: 새로운 결과 컨테이너 만들기**

supplier는 비어있는 결과로 이루어진 Suppplier를 반환해야한다.

```
public Supplier<List<T>> supplier(){
		return () -> new ArrayList<T>();
}

```

### **accumulator: 결과 컨테이너에 요소 추가하기**

```
public BiConsumer<List<T>, T> accumulator(){
		return (list,item) -> list.add(item);
}

```

### **finisher: 최종 변환값을 결과 컨테이너로 적용하기**

스트림 탐색을 끝내고 누적자 객체를 최종결과로 변환하여 누적 과정을 끝낼 떄 호출할 함수를 반환해야한다.

```
public Function<List<T>,List<T>> finisher(){
		return Function.identity();
}

```

위 세가지 메서드로 리듀싱을 아래 그림과 같이 실행할 수 있다.

![https://blog.kakaocdn.net/dn/mmDVa/btstmHqyimx/fwlKKWkO0NKY2dfhOOL1fk/img.png](https://blog.kakaocdn.net/dn/mmDVa/btstmHqyimx/fwlKKWkO0NKY2dfhOOL1fk/img.png)

### **combiner: 두 결과 컨테이너 병합**

스트림의 서로 다른 서브파트를 병렬로 처리할때 누적자가 이 결과를 어떻게 처리할지 정의함.

이 연산을 통해 리듀싱을 병렬로 수행할 수 있음

- 스트림을 재귀적으로 분할함 ( 분할을 그만할때까지 )
- 서브 스트림의 각 요소에 리듀싱 연산을 순차적으로 적용
- combiner 메서들르 통해 부분 결과를 합침

```
public BinaryOperator<List<T>> combiner(){
		return (list1,list2)->{
				list1.addAll(list2);
				return list1;
		}
}

```

### **Characteristics 메서드**

컬렉터의 연산을 정의하는 Characteristics 형식 불변 집합을 반환하는데 이를 통해 스트림을 병렬로 리듀스할 것인지, 병렬 시 어떻게 최적화를 할지 힌트를 제공한다. ENUM 타입으로 아래와 같은 3 항목을 포함한다.

- UNORDERED: 리듀싱 결과는 방문 순서, 누적 순서에 영향을 받지 않는다.
- CONCURRENT: accumulator 함수를 동시에 여러 스레드에서 호출해 병렬 리듀싱을 가능하게 한다.
- IDENTITY_FINISH

# WIL5 - 6장

# 스트림으로 데이터 수집

지금까지 배운 스트림의 연산은 중간연산과, 최종 연산으로 구분되었다. 최종 연산은 스트림의 요소를 소비해 최종 결과를 도출하는데 4장과 5장에선, collect를 사용해 toList 로 묶어왔다. 컬렉터란 무엇일까?

---

# 컬렉터란?

컬렉터가 존재하지 않을때, 통화별로 트랜잭션을 그룹화 하라라는 요청이 있다고 하자. 이때의 구현은 아래와 같은데, 상당히 길고 가독성이 떨어진다.

```
Map<Currency,List<Transaction>> transactionByCurrencies = new HashMap<>();

for (Transaction transaction : transactions){
	Currency currency = transaction.getCurrency();
	List<Transaction> transactionForCurrency = transactionsByCurrencies.get(currency);
	if( transactionsForCurrency == null ){
		transactionsForCurrency = new ArrayList<>();
		transactionsByCurrencies.put(currency,transactionsForCurrency);
	}
	transactionsForCurrency.add(transaction);
}

```

컬렉터의 등장으로 위 코드는 아래와 같이 간결해졌다.

```
Map<Currency,List<Transaction>> transactionsByCurrencies =
										transactions.stream().collect(groupingBy(Transaction::getCurrency));

```

### 고급 리듀싱 기능을 수행하는 컬렉터

스트림에 collect를 호출하면스트림의 요소에 컬렉터로 파라미터화된 리듀싱 연산이 수행된다. 위의 코드를 아래 그림과 같이 내부적으로 reducing 연산을 통해 스트림의 각 요소를 방문하며 컬렉터가 작업을 처리한다.

1. 스트림의 각 트랜잭션 탐색
2. 트랜잭션의 통화 추출
3. 통화/트랜잭션 쌍을 그룹화된 Map으로 추가함.

주로, 함수의 데이터 저장 구조를 변환할 땐, 컬렉터를 적용해 최종 결과를 저장하는 자료구조에 값을 누적되며 어떤 Collector 인터페이스의 메서드를 어떻게 구현하느냐에 따라 스트림 리듀싱에 어떤 연산을 수행할지 결정된다. (커스텀 컬렉터도 구현이 가능하다.)

### 미리 정의된 컬렉터

Collectors에서 제공되는 메서드의 기능은 3가지로 구분된다.

- 스트림의 요소를 하나의 값으로 리듀스하고 요약. ex) 트랜잭션의 총합 찾기
- 스트림의 요소를 그룹화.
- 스트림의 요소를 분할: 프레디케이트를 그룹화 함수로 사용함.

---

# 리듀싱과 요약

### 스트림값에서 최댓값과 최솟값 검색

4장에서 등장한 [Dish.menu](<[http://Dish.menu](http://dish.menu/)>) 에서 칼로리가 가장 높은 요리를 찾을때, Collectors.maxBy, Collectors.minBy 를 활용할 수 있다.

```
Comparator<Dish> dishComparator = Comparator.comparingInt(Dish::getCalories);
        Optional<Dish> mostCalorieDish = Dish.menu.stream()
                .collect(maxBy(dishComparator));

```

이 코드는 Java17 에선 아래와 같이 사용할 수 있다.

```
Comparator<Dish> dishComparator = Comparator.comparingInt(Dish::getCalories);
        Optional<Dish> mostCalorieDish = Dish.menu.stream()
                .max(dishComparator);

```

### 요약 연산

스트림에 있는 객체의 숫자 필드의 합계, 평균을 반환하는 연산에 사용되는 리듀싱 연산을 요약 연산이라고 한다.

칼로리의 합을 구하는 요약 연산의 경우를 보자. 코드는 아래와 같다.

```
menu.stream().collect(summingInt(Dish::getCalories));

```

요약 과정은 아래 그림과 같은데, 우선 각 요소를 칼로리로 변환하는 **변환 과정**, 이후 **리듀싱을 통해 요소의 합**을 구하는 과정으로 나뉜다.

합을 구하는 summingInt summingDouble summingLong 외에도, 같은 방식으로 averagingInt averagingLong avaeragingDouble 등이 존재한다.

이렇게 컬렉터로 최댓값, 최솟값, 평균,합게를 계산할 수 있는데 이중 두개 이상의 연산을 한번에 수행하고 싶다면 summarizingInt 와 같은 함수를 사용하면 다 볼 수 있다.

### 문자열 연결

컬렉터에 joining 팩토리 메서드를 이용하면 스트림의 각 객체에 toString 메서드를 적용해 추출한 모든 문자열을 하나의 문자열로 연결해서 반환할 수 있다.

```
String shortMenu = menu.stream().map(Dish::getName).collect(Collectors.joining());
        System.out.println("shortMenu = " + shortMenu);

```

띄어쓰기는 메뉴 자체에 있던 띄어쓰기이다. 합쳐진 문자열을 구분할 수 없다는 문제가 있는데 구분할 수 있는 문자열을 아래와 같이 넣을 수 있다.

```
String shortMenu = menu.stream().map(Dish::getName).collect(Collectors.joining(", "));

```

### 범용 리듀싱 요약 연산

위에서 다룬 모든 컬렉터는 reducing 팩토리 메서드에 존재해 사용이 가능하다. 하지만**, 프로그래밍적 편의성, 가독성**의 이유로, 특화된 컬렉터를 사용한다.

reducing 의 라이브러리를 보면 다음과 같은데, 3개의 파라미터를 받는다.

- 리듀싱 연산의 시작값 or 스트림에 인수가 없을때의 반환값
- 변환에 사용하는 함수
- 같은 종류의 두 항목을 하나의 값으로 합치는 BinaryOprator

reducing 연산은 인자의 개수가 한개인 경우, 파라미터가 3개인 reducing 함수의 첫번째 인자에는 스트림의 첫번째 요소를, 두번째 인자엔 자신을 반환하는 항등 함수를, 세번째 인자에 전달 받은 값을 넣어 사용한다.

Collectors와 reducing을 아래와 같이 적절히 바꿔 활용할 수 있다.

```
String shortMenu = menu.stream().map(Dish::getName).collect(joining());

```

위 예제 joining을 사용한 collect 코드를 reducing을 사용해 적절히 바꾸면 아래와 같다.

```
String shortMenu = menu.stream().map(Dish::getName)
										.collect(reducing( (s1,s2) -> s1+s2)).get;String shortMenu = menu.stream()
										.collect(reducing("",Dish::getName,(s1,s2) -> s1+s2));
```

---

# 그룹화

데이터를 하나 이상의 특성으로 분류해서 그룹화하는 연산은 DB에서 많이 수행되는 연산이다. 명령형으로 그룹화를 구현하는 것은 복잡하지만 자바 8의 함수형을 이용하면 가독성 있고 짧은 코드를 구현할 수 있다.

menu를 Type 별로 묶기 위해선 아래와 같이 구현할 수 있다.

```
Map<Type, List<Dish>> dishesByType = menu.stream().collect(groupingBy(Dish::getType));

```

이렇게 특정 타입과 일치하는 그룹화함수를 **분류 함수**라고 한다.

그룹화하는 원리는 아래 그림과 같다.

- 스트림에서 요소를 꺼내서 분류함수를 통해 적절한 Key를 찾는다.
- Key에 맞는 항목을 리스트로 분류한다.

여기서는 분류함수로 메서드 참조를 이용했는데, 더 복잡한 분류 기준(가령, 400칼로리 이하를 diet라고 하자)이 존재한다면, 메서드 참조를 사용할 수 없다. 이 경우엔 아래와 같이 람다를 이용한 코드를 작성해야한다.

```
menu.stream().collect(
                groupingBy(dish->{
                    if(dish.getCalories()<=400)return CaloricLevel.DIET;
                    else if (dish.getCalories()<=700)return CaloricLevel.NORMAL;
                    else return CaloricLevel.FAT;
                })
        )

```

그렇다면 **두 가지 기준으로 동시에 그룹화가 가능할까?**

### 그룹화된 요소 조작

500칼로리가 넘는 요리를 타입 별로 필터링 하는 경우를 생각해보자.

일반적으로 filter 를 통해 500칼로리 이상의 음식을 찾고 이어서 Type별로 GroupBy를 수행하면 될 것이라고 생각하지만, filter를 하고 난 이후 존재하지 않는 Type에 대해서는 알 수가 없다. 즉, 키 자체가 사라진다.

따라서, Collector 클래스의 정적 팩토리 메서드로 filtering 을 사용할 수 있다. filter와 같이 프레디케이트를 인자로 받고, groupingBy 의 두번째 인자로 전달될 수 있다.

```
Map<Type, List<Dish>> filteringAfter = menu.stream()
                .collect(groupingBy(Dish::getType
                        , filtering(dish -> dish.getCalories() > 500, toList())));
        System.out.println("filteringAfter = " + filteringAfter);

```

그 결과 Grouping 된 항목들의 조절이 적절히 가능해졌다. (filter)

또한 filtering 대신 mapping 또한 사용 가능하다.

### 다수준 그룹화

지금까진 한가지 기준으로 그룹화를 하였는데 두 가지 이상의 기준으로 그룹화가 가능할까?

두 가지 groupingBy를 사용하면 된다!

```
Map<Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel = menu.stream().collect(
                groupingBy(Dish::getType,
                        groupingBy(dish -> {
                            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                            else return CaloricLevel.FAT;
                        }))
        );

```

그 결과 바깥쪽 groupingBy 였던 Type에 대해 우선 분류가 되고 이후 Calories에 따른 분류가 된다.

결과.

```
{
	MEAT={NORMAL=[beef], FAT=[pork], DIET=[chicken]},
	FISH={NORMAL=[salmon], DIET=[prawns]},
	OTHER={NORMAL=[french fries, pizza], DIET=[rice, season fruit]}
}

```

이렇게 N 수준으로 그룹화를 하게된다면 n 수분 트리구조의 맵이 된다.

---

# 분할

분할이란, 분할 함수라 불리는, 프레디케이트를 분류 함수로 사용하는 특수화 기능이다. Boolean을 반환하므로 2개의 그룹으로 분리된다. 채식 요리와 채식이 아닌 요리를 비교하는 코드는 아래와 같다.

```
menu.stream().collect(partitioningBy(Dish::isVegetarian));

```

분할을 통해 참, 거짓으로 분할된 스트림의 모든 요소를 유지할 수 있다.

또한, 두번째 인수로 컬렉터를 전달해 분할 이후의 명령을 지시할 수도 있다.

```
menu.stream().collect(partitioningBy(Dish::isVegetarian,
                collectingAndThen(maxBy(Comparator.comparingInt(Dish::getCalories))
                , Optional::get)));

```

### 소수 분류 문제

1. 소수를 판별하기 위한 predicate를 구현하자

```
public boolean isPrime(int candidate){
		int candidateRoot = (int) Math.sqrt((double)candidate);
		return IntStream.rangedClosed(2,candidateRoot)
										.noneMatch(i->candidate%i==0);
}

```

1. partitioningBy에 Predicate로 isPrime 을 넣어 소수를 분류하자.

---

# Collector 인터페이스

Collector 인터페이스는 리듀싱(컬렉터) 연산을 어떻게 구현할지 제공하는 메서드 집합으로 구성되어있다.

Collector인터페이스는 아래와 같이 <T,A,R>을 받는다.

- T: 수집될 스트림 항목의 제네릭 형식
- A: 수집 과정에서 중간 결과를 누적하는 객체의 타입
- R: 수집 연산 결과 객체의 형식(주로 컬렉션 형식)

### supplier: 새로운 결과 컨테이너 만들기

supplier는 비어있는 결과로 이루어진 Suppplier를 반환해야한다.

```
public Supplier<List<T>> supplier(){
		return () -> new ArrayList<T>();
}

```

### accumulator: 결과 컨테이너에 요소 추가하기

```
public BiConsumer<List<T>, T> accumulator(){
		return (list,item) -> list.add(item);
}

```

### finisher: 최종 변환값을 결과 컨테이너로 적용하기

스트림 탐색을 끝내고 누적자 객체를 최종결과로 변환하여 누적 과정을 끝낼 떄 호출할 함수를 반환해야한다.

```
public Function<List<T>,List<T>> finisher(){
		return Function.identity();
}

```

위 세가지 메서드로 리듀싱을 아래 그림과 같이 실행할 수 있다.

### combiner: 두 결과 컨테이너 병합

스트림의 서로 다른 서브파트를 병렬로 처리할때 누적자가 이 결과를 어떻게 처리할지 정의함.

이 연산을 통해 리듀싱을 병렬로 수행할 수 있음

- 스트림을 재귀적으로 분할함 ( 분할을 그만할때까지 )
- 서브 스트림의 각 요소에 리듀싱 연산을 순차적으로 적용
- combiner 메서들르 통해 부분 결과를 합침

```
public BinaryOperator<List<T>> combiner(){
		return (list1,list2)->{
				list1.addAll(list2);
				return list1;
		}
}

```

### Characteristics 메서드

컬렉터의 연산을 정의하는 Characteristics 형식 불변 집합을 반환하는데 이를 통해 스트림을 병렬로 리듀스할 것인지, 병렬 시 어떻게 최적화를 할지 힌트를 제공한다. ENUM 타입으로 아래와 같은 3 항목을 포함한다.

- UNORDERED: 리듀싱 결과는 방문 순서, 누적 순서에 영향을 받지 않는다.
- CONCURRENT: accumulator 함수를 동시에 여러 스레드에서 호출해 병렬 리듀싱을 가능하게 한다.
- IDENTITY_FINISH