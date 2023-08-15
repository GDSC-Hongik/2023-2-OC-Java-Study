# Java Colllection Framework

- 대량의 데이터를 효율적으로 처리할 수 있는 기능 제공하는 클래스의 집합
- 데이터를 저장하는 `자료구조` & 데이터 처리를 위한 `알고리즘` 구조화 → 클래스로 구현
- 주로 사용되는 인터페이스 : `List` `Set` `Map` `Queue`
- List, Set, Queue : Collection 인터페이스 상속받음
    - Element
- Map : 독립적으로 정의 (구조적 특성)
    - Kev & Value

# List 인터페이스 살펴보기

- `순서` 개념이 있는 데이터의 집합 → Element 순서 유지
    - 인덱스 관리
- 동일 요소의 중복 저장 허용
- 대표적인 List 관련 클래스
    - `ArrayList`
    - `LinkedList`
    - `Vector`
    - `Stack`

## ArrayList<E>

- 단방향 포인터 구조
- 각 데이터의 인덱스 가짐 → `조회` 성능↑
- 내부적으로 배열을 이용하여 Element 저장
- 배열의 크기 고정x
    - 크기 조정을 위해 새로운 배열 생성 & 기존 요소 옮겨야함
- 주요 메소드

| 메소드 | Return Type | 설명 |
| --- | --- | --- |
| add(E e) | boolean | 데이터를 배열 끝에 추가 |
| add(int index, E e) | void | 지정된 index 위치에 데이터 저장 |
| size() | int | 데이터 개수 리턴 |
| get(int index) | E | 해당 index의 데이터 리텅 |
| indexOf(Object o) | int | 전달된 객체와 동일한 데이터의 위치 리턴 |
| toArray() | Object[] | 객체의 값들을 해당 타입의 배열로 변환하여 리텅 |
| clear() | void | 모든 데이터 삭제 |
| remove(int index) | E | 해당 index 위치의 데이터 삭제 후 그 데이터 리턴 |
| set(int index, E e) | E | 해당 index 위치의 데이터 변경 후 원래 데이터 리턴 |
- 정렬하기

```java
Collections.sort(arrayList);  // 오름차순 정렬
```

- iterator

```java
Iterator iterator = arrayList.iterator();
while (iterator.hasNext()) {
	System.out.println(iterator.next());
}
```

## LinkedList<E>

- 각 데이터가 `노드`와 `포인터`로 구성되어 연결되어 있는 방식
- 중간에 데이터를 삽입하거나 삭제하기 용이
- 주요 메소드

| 메소드 | Return Type | 설명 |
| --- | --- | --- |
| add(int idx, Object o) | void | 지정된 index에 객체 추가 |
| offer(E o) | boolean | 해당 요소를 끝에 추가 |
| peek() | E | 첫번째 요소 리턴 |
| poll() | E | 첫번쨰 요소 리턴 후 삭제 |
| subList(int f, int t) | List | f~t 사이의 객체를 List로 변환하여 리턴 |

## Vector<E>

- 기본적으로 ArrayList와 동일한 자료구조, 동일한 기능
- 가장 큰 차이점은 ArrayList와 달리 `Thread-Safe`하다는 것
    - 동기화된 메소드로 구성되어 있음
- Thread가 1개일 경우에도 동기화 → ArrayList에 비해 성능↓
- capacity() : 기본적으로 10으로 시작
    - InitialCapacity default인 경우

## Stack<E>

- Vector 클래스를 상속받음
- 전형적인 Stack 메모리 구조 제공
- `LIFO` : 후입선출 구조
- 주요 메소드

| 메소드 | Return Type | 설명 |
| --- | --- | --- |
| peek() | E | Stack의 제일 마지막 저장된 요소 리턴 |
| pop() | E | Stack의 제일 마지막 저장된 요소 리턴 후 삭제 |
| push(E e) | E | Stack의 제일 마지막에 요소 저장 |
- add() : boolean 반환

# Queue 인터페이스 살펴보기

## Queue<E>

- 데이터를 일시적으로 쌓아두기 위한 자료구조
- `FIFO` 형태 : First In First Out
- 생성할 때 LinkedList 활용하여 생성

```java
Queue<Integer> queue = new LinkedList<>();
```

- 주요 메소드

| 메소드 | Return Type | 설명 |
| --- | --- | --- |
| add(E e) | boolean | Queue의 마지막에 요소 삽입 (실패시 예외 발생) |
| offer(E e) | boolean | Queue의 마지막에 요소 삽입 (실패시 false 리턴) |
| element() | E | Queue의 제일 앞의 요소 리턴 (실패시 예외 발생) |
| peek() | E | Queue의 제일 앞의 요소 리턴 (비어있으면 null 리턴) |
| poll() | E | Queue의 제일 앞의 요소 리턴 후 삭제 (비어있으면 null 리턴) |
| remove() | E | Queue의 제일 앞의 요소 리턴 후 삭제 (비어있으면 예외 발생) |

## PriorityQueue<E>

- 우선순위 큐 : 데이터의 `우선순위` 결정, 우선순위가 가장 높은 원소가 먼저 나가는 구조
- `힙`을 이용하여 구현
    - 우선순위 기준으로 최대힙(MaxHeap), 최소힙(MinHeap)으로 구성
    - `이진 트리` 구조
- `O(NlogN)`
- 생성시 MinHeap or MaxHeap 결정 (default : 오름차순)

```java
PriorityQueue<Person> priorityQueue = new PriorityQueue<>();
PriorityQueue<Person> priorityQueue = new PriorityQueue<>(Collections.reverseOrder());
```

# Set 인터페이스 살펴보기

- `순서` 개념이 `없는` 데이터의 집합 (인덱스 관리 x)
- 데이터 `중복` 허용 `x`
- iterator를 생성하여 데이터 탐색
- 대표적인 클래스
    - `HashSet` : 데이터 중복 저장x, 순서 보장 x
    - `TreeSet` : 오름차순으로 데이터 정렬 저장
    - `LinkedHashSet` : 입력된 순서대로 데이터 관리
- 주로 사용되는 메소드

| 메소드 | Return Type | 설명 |
| --- | --- | --- |
| add() | boolean | set에 전달된 요소 추가 |
| clear() | void | set의 모든 요소 제거 |
| contains() | boolean | 해당 set에 특정 요소 포함되어 있는지 체크 |
| equals() | boolean | set의 요소 집합과 전달된 객체가 같은지 체크 |
| isEmpty() | boolean | set이 비어 있는지 체크 |
| iterator() | Iterator<E> | set의 반복자(iterator) 리턴 |
| remove() | boolean | set에서 전달된 객체 제거 |
| size() | int | set에 포함된 요소의 개수 리턴 |
| toArray() | Object[] | set의 모든 요소를 배열로 반환 |

## HashSet<E>

- Set 인터페이스의 구현체
- 동일한 객체 `중복` 저장 `x`
- `순서` 보장 `x`
    - 순서 보장을 원한다면 LinkedHashSet 사용
    - 해시 알고리즘에 의해 순서 배치
- 값을 추가 및 삭제하는 과정에서 Set 내부에 해당 값이 있는지 확인하는 단계를 거쳐 진행됨
    - List에 비해 `느림`
- 본체는 HashMap

## TreeSet<E>

- Set 인터페이스의 구현체
- 동일한 객체 `중복` 저장 `x`
- `순서` 보장 `x`
- HashSet과는 다르게 `이진 탐색 트리` 구조
    - 값의 추가 / 제거에는 시간↑
    - `정렬` 및 `검색`에 성능↑
- 본체는 TreeMap
- default 생성 → 오름차순 정렬

# 자바의 effectively final

- final로 선언되지 않았지만
초기화 이후 값이 재할당되지 않아 `final처럼` 동작

## final은 아니지만 final처럼

- `effectively final`
- 자바 8에 도입
- 익명 클래스 or 람다식이 사용된 코드에서 발견 가능
    - 참조하는 외부 지역 변수가 final로 선언되었거나
    - effectively final인 경우에만 접근 가능

→ 참조하는 지역 변수가 내부에서 변경되면 컴파일 에러

```java
// Anonymous Classes
public void someMethod() {
	int count = 0;
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// local variables referenced from an inner class
			// must be final or effectively final
			count++;
		}
	};
}

// Lambda Expressions
public void someMethod() {
	List<Integer> list = Arrays.asList(1, 2, 3, 4);
	Integer criteria;

	for (Integer integer : list) {
		if (integer > 2) {
			criteria = 3;
			// local variables referenced from an inner class
			// must be final or effectively final
			list.removeIf(o -> o.equals(criteria));
		}
	}
}
```

## effectively final

- 다음과 같은 조건을 만족하는 지역 변수는 effectively final로 간주한다 :
    - final로 선언하지 않았다
    - 초기화를 진행한 후에 다시 할당하지 않았다
    - prefix 또는 postfix 증감 연산자가 사용되지 않았다
- 객체의 경우 : 참조만 변경하지 않으면 됨
    - 객체 상태 변경해도 effectively final 유지

```java
List<Person> personList = List.of(new Person(2), new Person(3));
for (Person p : personList) {
	p.setId(2);
	personList.removeIf(o -> o.getId() == p.getID());
}
```

## Lambda Capturing

- 외부에 정의된 변수를 사용할 때 내부에서 사용할 수 있도록 `복사본 생성`
    - 외부 변수 : 지역 변수, 인스턴스 변수, 클래스 변수

```java
// 외부 인스턴스 변수 참조
public class Tester {
	private int count = 0;
	
	public void someMethod() {
		Runnable runnable = () -> System.out.println("count: " + count);
	}
}

// 외부 지역 변수 참조
public void someMethod() {
	int count = 0;
	Runnable runnable = () -> System.out.println(count);
}
```

## 왜 복사본을 만들까?

- 지역 변수 → 메모리 영역 중 `stack`에 할당
    - 스레드마다 자신만의 고유한 영역
    - 스레드끼리 공유 x
    - 스레드 종료되면 스택 영역 소멸
- 따라서 외부 지역 변수 그대로 참조하지 x → 복사본 생성

```java
public void test() {
	// 외부 지역 변수
	int count = 0;

	new Thread(() -> {
		try {
			// 'count' 복사하지 않는다고 가정
				Thread.sleep(1000);
				System.out.println("count: " + count);
		} catch (InterruptedException e) {
			// Exception Handling
		}
	}).start();

	System.out.println("count: " + count);
}
```

- test 스레드가 람다식 스레드가 끝나기 전에 스택 영역에서 사라짐 → count 참조 못함

## 왜 람다에서 외부 지역 변수의 값을 변경할 수 없을까?

- 람다식은 별도 스레드에서 수행 가능
- 외부 지역 변수를 제어하는 스레드와 서로 다를 수 있음

```java
public class Tester {
	ExecutorService executor = Executors.newFixedThreadPool(1);
	
	public void testMultiThreading() {
		// Thread A
		boolean doLoop = true;

		executor.execute(() -> {
			// Thread B
			while (doLoop) {
				// something to do
			}
		});
		doLoop = false;
	}
}
```

- 스레드 B(람다식)에서 지역변수 doLoop의 값 참조하기 위해 복사
- 복사된 값인 외부 지역 변수가 변경 가능해지면 복사된 값이 최신값임을 보장할 수 x
    - 변수의 가시성
    - 한 스레드에서 다른 스레드 스택에 있는 값의 변경사항 확인 불가능
- 복사된 값 보장 불가능하므로 동시성 문제 발생
    - 예측할 수 없는 상황 발생

## 그렇다면 인스턴스 변수와 클래스 변수는?

- 인스턴스 변수 : 클래스에 선언된 변수, `힙` 영역에 할당
- 클래스 변수 : 클래스에 선언된 static 변수, `메서드` 영역에 선언
- 스택 영역과는 달리 메모리 영역이 바로 회수되지 x
- 복사하는 과정 불필요

```java
public class Tester {
	private int instanceVariable = 0;
	private static int staticVariable = 0;

	public void someMethodWithInstanceVariable() {
		instanceVariable = 1;
		Ruunnable runnable = () -> {
			instanceVariable++;
		}
	}

	public void someMethodWithStaticVariable() {
		staticVariable = 1;
		Runnable runnable = () -> {
			staticVariable++;
		}
	}
}
```

- 정상적으로 컴파일

# Java HashMap은 어떻게 동작하는가?

- `HashMap` : Java Collections Framework에 속한 구현체 클래스
- HashMap 구현체의 성능 향상 방식 소개

## HashMap과 HashTable

> *키에 대한 해시 값을 사용하여 값을 저장하고 조회하며, 키-값 쌍의 개수에 따라 동적으로 크기가 증가하는 associative array*
> 
- HashTable : JDK 1.0 ~
    - Map 인터페이스 구현
- HashMap : Java 2 ~
    - 보조 해시 함수 사용 → 해시 충돌↓ 성능↑
    - 지속적으로 개선 중
    - 키 집합(정의역) & 값 집합(공역) 대응
- 둘이 제공하는 기능은 같음
- associative array 지칠
    - HashTable → extends Dictionary<K, V>
    - HashMap → extends Map<K, V>

## 해시 분포와 해시 충돌

<aside>
💡 동일하지 않은 어떤 객체 X와 Y

- X.equals(Y) → 거짓
- X.hashCode() ≠ Y.hashCode() → 완전한 해시 함수
</aside>

- Boolean(객체 종류 적음), Integer, Long, Double(Number 객체) → 값 자체를 해시값으로 사용 가능
- String, POJO → 완전한 해시 함수 제작 불가능
- HashMap → 각 객체의 `hashCode()` 메서드가 반환하는 값 사용
    - 결과 자료형 int(32bit)
    - 완전한 자료 해시 함수 만들 수 없다
    - 배열 크기 과다 ($2^{32}$)
- associative array 구현체에서 메모리 절약
    - 실제 표현 정수 범위보다 작은 배열 사용 (크기 M)
    - 버킷 인덱스 값 사용

```java
int index = X.hashCode() % M;
```

- 서로 다른 객체가 1/M 확률로 같은 해시 버킷 사용
- 해시 충돌 발생하더라도 키-값 쌍 데이터 잘 저장 & 조회 방식
    - `Open Addressing` (linear probing) : 이미 사용중이면 다른 해시 버킷에 데이터 삽입
        - 연속된 공간에 데이터 저장 → 캐시 효율↑
        - 데이터 개수 적으면 성능↑ (hit ratio↑)
    - `Seperate Chaining` : 링크드 리스트
        - Java HashMap에서 사용
        - 해시 출동리 잘 발생하지 않도록 조정
- 둘 다 worst case : `O(M)`

## Java 8 HashMap에서의 Seperate Chaining

- 만약 객체의 해시 함수 값이 균등 분포 상태
    - get() 호출에 대한 기댓값 : E(N/M)
    - (Java 8) : E(log(N/M))
        - 데이터 개수 많아지면 `트리` 사용 (성능↑↑)
- 데이터 개수 많아지면 birthday problem → 일부 해시 버켓 몇 개에 데이터 집중 가능성
- 하나의 해시 버킷에 8개의 키-값 쌍이 모이면 트리로 변경

```java
static final int TREEIFY_THRESHOLD = 8;
static final int UNTREEIFY_THRESHOLD = 6;
```

- Entry 클래스 대신 Node 클래스 사용
    - 하위 클래스 TreeNode 존재
    - `Red-Black Tree` 사용
- 트리 순회시 대소 판단 기준 → 해시 함수 값
    - Total Ordering 문제
    - tieBreakOrder() 메서드로 해결 : 어떤 2개 키의 해시함수 값이 같으면 임의로 대소 관계 지정

## 해시 버킷 동적 확장

- 해시버킷 개수↓ → 메모리사용↓ 해시충돌↑ 성능↓
- 일정 개수 이상이 되면 해시 버킷 개수 2배로 늘림
    - 기본값 16, 최대 개수 $2^{30}$
    - 모든 데이터 읽어 새로운 Seperate Chaining 구성해야하는 문제 → 생성자 인자로 데이터 개수 넣어주면됨

```java
// 인자로 사용한느 newCapacity는 언제나 2a
void resize(int newCapacity) {
	Entry[] oldTable = table;
	int oldCapacity = oldTable.length();

	// MAXIMUM_CAPACITY : 2^30
	if (oldCapacity == MAXIMUM_CAPACITY) {
		threshold = Integer.MAX_VALUE;
		return;
	}

	Entry[] newTable = new Entry[newCapacity];

	// 새 해시 버킷을 생성한 다음 기존의 모든 키-값 데이터들을
	// 새 해시 버킷에 저장
	transfer(newTable, initHashSeedAsNeeded(newCapacity));
	table = newTable;
	threshold = (int)Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY + 1);
}
```

- 임계점 : `load factor * 현재의 해시 버킷 개수`
    - load factor = 0.75
    - HashMap 생성자에서 지정 가능
- 결정적인 문제 : 해시 버킷 개수 M이 $2^a$ 형태
    - index = X.hashCode() % M 계산시
    - X.hashCode()의 하위 a 비트만 사용
    - 해시 충돌 쉽게 발생
    - 보조 해시 함수 필요

## 보조 해시 함수

- M 값 `소수`일 때 index 값 분포 가장 균등
    - 소수가 아니기 때문에 보조 해시 함수로 index 값 분포 균등하게 유지
- ‘키’의 해시 값 변형 → 해시 충돌 가능성↓
- Java7 HashMap 보조 해시 함수

```java
final int hash(Object k) {
	int h = hashSeed;
	if (0 != h && k instanceof String) {
		return sun.misc.Hashing.stringHash32((String) k);
	}
	h ^= k.hashCode();
	// 상위 비트 값이 해시 버킷 인덱스 값 결정할 때 반영될 수 있도록
	// shift 연산과 XOR 연산을 사용하여
	// 원래의 해시 값이 a 비트 내에서 값이 최대안 안 겹치도록 한다
	h ^= (h >>> 20) ^ (h >>> 12);
	return h ^ (h >>> 7) ^ (h >>> 4);
}
```

- Java8 HashMap 보조 해시 함수 (단순해짐)

```java
static final int hash(Object key) { int h; return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16); }
```

- 해시 충돌 많이 발생하면 트리 사용 : 이미 성능 문제 완화
- 최근 해시 함수는 균등 분포가 잘 되게 만들어짐
- 비트 연산을 사용하면 수행이 훨씬 빠름

## String 객체에 대한 해시 함수

- 수행 시간 : 문자열 길이에 비례
- JDK 1.1 : 빠르게 해시 함수 수행하기 위해 해시를 누적한 값을 해시값으로
    - 문자열 길이가 16을 넘으면 최소 하나의 문자 건너감
    - 서로 다른 URL의 해시값이 같아지는 빈도↑

```java
public int hashCode() {
	int hash = 0;
	int skip = Math.max(1, length() / 8);
	for (int i = 0; i < length(); i += skip)
		hash = s[i] + (37 * hash);
	return hash;
}
```

- Java 8 까지 사용 : Horner’s method
    - 다항식을 계산하기 쉽도록 단항식을 재귀적으로 표현

```java
public int hashCode() {
	int h = hash;
	if (h == 0 && value.length > 0) {
		char val[] = value;

		for (int i = 0; i < value.length; i++) {
			h = 31 * h + val[i];
		}
		hash = h;
	}
	return h;
}
```

- `31`을 승수로 사용하는 이유
    - `소수`이므로
    - `빠르게` 계산 가능 : 32곱한값(shift) - 해당값

## Java 7에서 String 객체에 대한 별도의 해시 함수

- HashMap에 저장된 키-값 쌍이 일정 개수 이상이면 String 객체에 한하여 별도의 해시 함수 사용
- JDK 7u40부터 삭제됨
- MurMur 해시 → String 객체에 대한 해시 충돌 매우 낮춤
    - hash seed 필요로함
    - 코어↑ CAS 연산 성능↓
    - 오히려 멀티코어 환경에서 성능 하락
- JDK 7u40부터 삭제됨

<aside>
💡 웹 애플리케이션 서버

→ HTTPRequest 생성될 때마다 여러 개의 HashMap 생성됨

</aside>