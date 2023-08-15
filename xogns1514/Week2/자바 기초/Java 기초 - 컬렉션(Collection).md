# Java 기초 - 컬렉션(Collection)

---

## 자바 컬렉션 프레임 워크(Java Collection Framework)

`컬렉션 프레임 워크`: 대량의 데이터를 효율적으로 처리할 수 있는 기능을 제공해주는 클래스의 집합

<img width="764" alt="스크린샷 2023-08-14 오후 4 37 22" src="https://github.com/xogns1514/test/assets/66353672/cde0b389-2f35-4ed5-975f-035b2912f481">

<img width="764" alt="스크린샷 2023-08-14 오후 4 37 59" src="https://github.com/xogns1514/test/assets/66353672/aca812e7-0cda-48e3-9164-eb4609776be0">


- 데이터를 저장하는 ‘자료 구조’와 데이터 처리를 위한 ‘알고리즘’을 구조화하여 클래스로 구현
- 주로 사용되는 인터페이스
    - List
    - Set
    - Map
- List와 Set, Queue 인터페이스는 Collection 인터페이스를 상속받지만 Map 인터페이스는 구조적 특성으로 독립적으로 정의되어 있음

## List 인터페이스

순서 개념이 있는 데이터의 집합(인덱스 관리)

- 요소의 순서가 유지되며, 동일한 요소의 중복 저장을 허용
- ArrayList
- LinkedList
- Vector
- Stack

## ArrayList<E>

- `단방향 포인터 구조`로 각 데이터의 인덱스를 가지고 있어 조회 성능이 뛰어남
- 내부적으로 배열을 이용하여 요소를 저장
- 배열의 크기를 고정할 수 없는 인스턴스이다. 크기 조정을 위해 새로운 배열을 생성하고 기존 요소들을 옮기는 작업이 수행됨

| 메소드 | Retun Type | 설명 |
| --- | --- | --- |
| add(E e) | boolean | 데이터를 배열 끝에 추가 |
| add(int index, E e) | void | index 위치에 데이터 저장 |
| size() | int | 데이터 개수 반환 |
| get(int index) | E | index 위치 데이터 반환 |
| indexOf(Object o) | int | 전달 된 객체와 동일한 데이터의 위치 반환 |
| toArray() | Object[] | 객체의 값들을 해당 타입의 배열로 변환하여 반환 |
| clear() | void | 모든 데이터를 삭제 |
| remove(int index) | E | 해당 index 위치 데이터 삭제 후, 그 데이터 반환 |
| set(int index, E e) | E | 해당 index 위치 데이터 변경하고, 원래 데이터를 반환 |

```java
ArrayList<Integer> arrayList = new ArrayList<>();
arrayList.add(100);
arrayList.add(400);
arrayList.add(200);
arrayList.add(300);
arrayList.add(600);

//Iterator로 탐색하기
Iterator iterator = arrayList.iterator();
while (iterator.hasNext()) {
System.out.println(iterator.next());
}
```

## LinkedList<E>

- 각 데이터가 `노드와 포인터로 구성`이 되어 연결되어 있는 방식의 구조
- 각 데이터가 포인터로 연결되어 있는 구조여서 `중간에 데이터를 삽입하거나 용이함(ArrayList에 비해)`

| 메소드 | Return Type | 설명 |
| --- | --- | --- |
| add(int index, Object o) | void | 지정된 index에 객체 추가 |
| offer(E o) | boolean | 해당 요소를 끝에 추가 |
| peek() | E | 첫번째 요소를 리턴 |
| poll() | E | 첫번째 요소를 리턴 후 삭제 |
| subList(int f, int t) | List | f~t 사이의 객체를 List로 변환하여 리턴 |

## Vector<E>

- 기본적으로 `ArrayList와 동일한 자료 구조`를 가지고 있음
- ArrayList와 동일한 기능으로 동작
- 유일하게 다른 부분은 `ArrayList와 다르게 Thread-Safe` 하다는 것
    - Vector는 동기화된 메소드로 구성되어 있음
    - `Thread가 접근할 때마다 동기화`를 함
- Thread가 한개일 경우에도 동기화를 하기 때문에 ArrayList에 비해 성능이 조금 떨어짐

## Stack<E>

- Vector 클래스를 상속받아 전형적인 Stack 메모리 구조를 제공
- 다루는 데이터에 대해 후입선출(LIFO)의 구조를 가지고 있음
- LIFO: 나중에 저장된 데이터가 먼저 나오는 구조

| 메소드 | Return Type | 설명 |
| --- | --- | --- |
| peek() | E | Stack의 제일 마지막 저장된 요소를 리턴 |
| pop() | E | Stack의 제일 마지막 저장된 요소를 리턴 후 삭제 |
| push(E e) | E | Stack의 제일 마지막에 요소를 저장 |

```java
Stack<Integer> stack = new Stack<>();

stack.add(1);
stack.push(3);

System.out.println(stack.add(7)); //true
System.out.println(stack.push(2)); //2

System.out.println(stack.size()); //4

System.out.println(stack.peek()); //2
```

- ArrayList 구조

<img width="609" alt="Untitled" src="https://github.com/xogns1514/test/assets/66353672/6d56ff46-444f-49f7-b6e9-21fb49cb79eb">

<ArrayList>는 AbstractList를 상속하고 있다.

<img width="715" alt="Untitled 1" src="https://github.com/xogns1514/test/assets/66353672/48b9f2fc-d778-49a8-8d67-ace0192700e2">

List는 AbstractListCollection을 상속하고 있으며, List를 구현하고 있다.

<img width="715" alt="Untitled 2" src="https://github.com/xogns1514/test/assets/66353672/51969b6c-6023-40b7-b0cb-e9f55f3eeda8">

List는 Collection을 상속하고 있다.

<img width="715" alt="Untitled 3" src="https://github.com/xogns1514/test/assets/66353672/08fe5b26-66e5-4ef8-b257-324839ec899f">

Collection은 Iterable을 상속하고 있다.

## Queue<E>

데이터를 일시적으로 쌓아 두기 위한 자료구조

- Stack과 다르게 FIFO(First In First Out) 형태를 가짐
- Queue를 생성할 때 LinkedList를 활용하여 생성

| 메소드 | Return Type | 설명 |
| --- | --- | --- |
| add(E e) | boolean | Queue의 마지막에 요소를 삽입. 실패 시, 예외 발생 |
| offer(E e) | boolean | Queue의 마지막에 요소를 삽입. 실패 시, false를 리턴 |
| element() | E | Queue의 제일 앞(제일 먼저 저장)의 요소를 리턴. 실패 시, 예외 발생 |
| peek() | E | Queue의 제일 앞(제일 먼저 저장)의 요소를 리턴. Queue가 비어있으면 null 리턴 |
| poll() | E | Queue의 제일 앞의 요소를 리턴 후 삭제. Queue가 비어있으면 null을 리턴 |
| remove() | E | Queue의 제일 앞의 요소를 리턴 후 삭제. Queue가 비어있으면 예외 발생 |

## Priority Queue<E>

우선순위 큐는 데이터의 우선순위를 결정하여 우선순위가 가장 높은 요소가 먼저 나가는 자료 구조

- 우선순위 큐는 힙을 이용하여 구현되어, 우선순위를 기준으로 최대힙(MaxHeap), 최소힙(MinHeap)으로 구성
- 힙으로 구성되어 있다는 것은 이진 트리 구조로 이루어져 있다는 것을 의미
- 시간 복잡도: O(NLongN)

```java
	
	static class Person implements Comparable<Person> {
        private String name;
        private int age;

        Person(String name, int time) {
            this.name = name;
            this.age = time;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public int compareTo(Person person) {
            if (this.age == person.age) {
                return this.name.compareTo(person.name);
            }
            return this.age - person.age;
        }
    }

    PriorityQueue<Person> priorityQueue = new PriorityQueue<>();

	// 우선순위 큐의 값 추가
        priorityQueue.add(new Person("김민수", 25));
        priorityQueue.add(new Person("황진이", 30));
        priorityQueue.add(new Person("이민아", 30));
        priorityQueue.add(new Person("홍길동", 27));

        // 우선순위 큐의 값 제거
        System.out.println("이름 : " + priorityQueue.peek().getName() + ", 나이 : " + priorityQueue.poll().getAge());
        System.out.println("이름 : " + priorityQueue.peek().getName() + ", 나이 : " + priorityQueue.poll().getAge());
        System.out.println("이름 : " + priorityQueue.peek().getName() + ", 나이 : " + priorityQueue.poll().getAge());
        System.out.println("이름 : " + priorityQueue.peek().getName() + ", 나이 : " + priorityQueue.poll().getAge());
	/*
	이름 : 김민수, 나이 : 25
	이름 : 홍길동, 나이 : 27
	이름 : 이민아, 나이 : 30
	이름 : 황진이, 나이 : 30
	*/
```

## Set 인터페이스

순서 개념이 없는 데이터의 집합을 의미(인덱스 관리가 없음)

- 데이터의 `중복이 허용되지 않음`
- 데이터를 탐색하기 위해서는 Iterator를 생성하여 데이터를 가져와야 함
- 대표적으로 HashSet, TreeSet, LinkedHashSet이 있음
    - HashSet: 데이터를 중복 저장할 수 없고 순서를 보장하지 않음
    - TreeSet: 오름차순으로 데이터를 정렬하여 저장
    - LinkedHashSet: 입력된 순서대로 데이터를 관리

| 메소드 | Return Type | 설명 |
| --- | --- | --- |
| add() | boolean | set에 전달된 요소를 추가 |
| clear() | void | set의 모든 요소를 제거 |
| contains() | boolean | 해당 집합에 특정 요소가 포함되어 있는지 체크 |
| equals() | boolean | set의 요소 집합과 전달된 객체가 같은지 체크 |
| isEmpty | boolean | set이 비어있는지 체크 |
| iterator() | iterator<E> | set의 반복자(iterator)를 리턴 |
| remove() | boolean | set에 포함된 요소의 개수를 리턴 |
| size() | int | set에 포함된 요소의 개수를 리턴 |
| toArray() | Object[] | set의 모든 요소를 배열로 반환 |

## HashSet<E>

HashSet은 Set 인터페이스의 구현체

- 동일한 객체를 중복해서 저장할 수 없으며, 순서 또한 보장되지 않음
- 순서 보장을 위해서는 LinkedHashSet 사용 필요
- 값을 추가 및 삭제하는 과정에서 Set 내부에 해당 값이 있는지 확인하는 단계를 거쳐 진행됨(List에 비해 느림)
- 중복 데이터를 체크하기 위해 내부 로직으로 검증함
    1. hashCode() 메소드를 호출하여 반환된 해쉬값으로 범위 결정
    2. 범위 내 요소들을 equals() 메소드로 비교 

```java
String a = "해시코드 체크";
String b = "해시코드 체크";

//String 클래스는 내부 hashCode 및 equals 메소드에 의해 동일한 문자열의 경우 같은 객체로 판단됨
System.out.println(a.hashCode() + " " + b.hashCode()); //동일한 해시코드 나옴

System.out.println(hashSet.add("중복데이터"));//true
System.out.println(hashSet.add("중복데이터"));//false, 중복데이터로 처리
```

## TreeSet<E>

TreeSet은 Set 인터페이스의 구현체

- 동일한 객체를 중복해서 저장할 수 없으며, 순서 또한 보장되지 않음
- HashSet과는 다르게 이진 탐색 트리 구조로 이루어져 있음
    - 값의 추가 및 제거에는 식간이 오래 걸림
    - 정렬 및 검색에 높은 성능

```java
Set<String> treeSet = new TreeSet<>();

treeSet.add("2");
treeSet.add("1");
treeSet.add("3");
treeSet.add("B");
treeSet.add("A");

Iterator<String> iterator = treeSet.iterator();

while(iterator.hasNext()){
	System.out.println(iterator.next()); //iterator로 treeSet 탐색
}
```