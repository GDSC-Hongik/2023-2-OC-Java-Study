# CH 8 컬렉션 API 개선

---

## 8.1 컬렉션 팩토리

자바 9에서는 작은 컬렉션 객체를 쉽게 만들 수 있는 몇 가지 방법을 제공한다. 

- 자바에서 적은 요소를 포함하는 리스트 만드는 방법

```java
List<String> friend = new ArrayList<>();
friend.add("Raphael");
friend.add("Olivia");
friend.add("Thibaut");
```

→ 세 문자열을 저장하는데도 많은 코드가 필요하다. Arrays.asList() 팩토리 메서드를 이용하면 코드를 간단하게 줄일 수 있다.

```java
List<String> friends 
	= Arrays.asList("Raphael", "Olivia", "Thibaut");
```

- 고정 크기의 리스트를 만들었으므로 요소를 갱신할 순 있지만 새 요소를 추가하거나 요소를 삭제할 수 없다.

→ 요소를 갱신하는 것은 괜찮지만 요소를 추가하려 하면 `UnsupportedOperationException`이 발생한다.

## UnsupportedOperationException 예외 발생

내부적으로 고정된 크기의 변환할 수 있는 배열로 구현되었기 때문에 발생한다.

- HashSet 생성자를 사용한 집합 생성

```java
Set<String> friends
	= new HashSet<>(Arrays.asList("Raphael", "Olivia", "Thibaut"));
```

- Stream API를 이용한 집합 생성

```java
Set<String> friends
	= Stream.of("Raphel", "Olivia", "Thibaut")
		.collect(Collectors.toSet());
```

❗️ 두 방법 모두 매끄럽지 못하며 내부적으로 불필요한 객체 할당을 필요로 한다. 결과는 변환할 수 있는 집합이다.

**컬렉션 리터럴**

- 파이썬, 그루비등을 포함한 일부 언어는 [42, 1, 5] 같은 특별한 문법(컬렉션 리터럴)을 이용해 컬렉션을 만들 수 있는 기능을 지원한다.

→ 자바에서는 너무 큰 언어 변화와 관련된 비요이 든다는 이유로 이와 같은 기능을 지원하지 못했다.

## 8.1.1 리스트 팩토리

- List.of 팩토리 메소드를 이용해서 간단하게 리스트를 만들 수 있다.

```java
List<String> friends = List.of("Raphael", "Olivia", "Thibaut");
System.out.println(friends); //[Raphael", "Olivia", "Thibaut]
```

- freinds 리스트에 요소 추가

```java
List<String> friends = List.of("Raphael", "Olivia", "Thibaut");
friends.add("Chih-Chun");
```

❗️ 위 코드를 실행하면 java.lang.UnsupportedOperationException이 발생한다. 사실 변경할 수 없는 리스트가 만들어졌기 때문이다. set을 사용해도 같은 오류가 발생한다.

→ 컬렉션이 의도치 않게 변하는 것을 막을 수 있기 때문이다.

**오버로딩 vs 가변인수**

- List 인터페이스를 살펴보면 List.of의 다양한 오버로드 버전이 있다는 사실을 알 수 있다.

```java
// 오버로딩
static <E> List<E> of(E e1, E e2, E e3, E e4)
static <E> List<E> of(E e1, E e2, E e3, E e4, E e5)
// 가변인수 
static <E> List<E> of(E... elements)
```

→ 내부적으로 가변 인수 버전은 추가 배열을 할당해서 리스트로 감싼다. 따라서 배열을 할당하고 초기화하며 나중에 가비지 컬렉션을 하는 비용을 지불해야 한다.

## 8.1.2 집합 팩토리

- List.of와 비슷한 방법으로 바꿀 수 없는 집합을 만들 수 있다.

```java
Set<String> friends = Set.of("Raphael", "Olivia", "Thibaut"); 
System.out.println(friends); //[Raphael", "Olivia", "Thibaut]
```

- 중복된 요소를 제공해 집합을 만들려고 하면 Olivia라는 요소가 중복되어 있다는 설명과 함께 IllegalArgumentException이 발생한다.

## 8.1.3 맵 팩토리

1. Map.of 팩토리 메서드에 키와 값을 번갈아 제공하는 방법

```java
Map<String, Integer> ageOfFriends =
	Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
System.out.println(ageOfFriends); // {Olivia=25, Raphael=30, Thibaut=26}
```

- 열개 이하의 키와 값 쌍을 가진 작은 맵을 만들 때는 이 메소드가 유용하다.
- 그 이상의 맵에는 Map.Entry<K, V> 객체를 인수로 받으며 가변 인수로 구현된 Map.ofEntries 팩토리 메서드를 이용하는 것이 좋다. 이 메서드는 키와 값을 감쌀 추가 객체 할당을 필요로 한다.

```java
import static java.util.Map.entry;
Map<String, Integer> ageOfFriends = Map.ofEntries(entry("Raphael", 30), entry("Olivia", 25), entry("Thibaut", 26));
System.out.println(ageOfFriends);// {Olivia=25, Raphael=30, Thibaut=26}
```

## 8.2 리스트와 집합 처리

자바 8에서는 List, Set 인터페이스에 다음과 같은 메서드를 추가했다.

- removeIf: 프레디케이트를 만족하는 요소를 제거한다. List나 Set을 구현하거나 그 구현을 상속받은 모든 클래스에서 이용할 수 있다.
- replaceAll: 리스트에서 이용할 수 있는 기능으로 UnaryOperator 함수를 이용해 요소를 바꾼다.
- sort: List 인터페이스에서 제공하는 기능으로 리스트를 정렬한다.

→ 이들 메서드는 호출한 컬렉션 자체를 바꾼다. 새로운 결과를 만드는 스트림 동작과 다리 기존 컬렉션을 바꾼다.

## 8.2.1 removeIf 메서드

```java
// 숫자로 시작되는 참조 코드를 가진 트랜잭션을 삭제하는 코드
for(Transaction transaction : transactions){
	if(Character.isDigit(transaction.getReferenceCode().charAt(0)) {
		transactions.remove(transaction);
	}
}
```

❗️ 위 코드는 ConcurrentModificationException을 일으킨다.

→ 내부적으로 for-each 루프는 Iterator 객체를 사용한다.

```java
for(Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext();){
	Transaction transaction = iterator.hasNext()
	if(Character.isDigit(transaction.getReferenceCode().charAt(0)) {
		transactions.remove(transaction); // 반복하면서 별도의 두 객체를 통해 컬렉션을 바꾸고 있는 문제
	}
}
```

- 두 개의 개별 객체가 컬렉션을 관리한다는 사실을 주목하자
    - Iterator 객체: next(), hasNext()를 이용해 소스를 질의한다.
    - Collection 객체 자체: remove를 호출해 요소를 삭제한다.

→ 결과적으로 반복자의 상태는 컬렉션의 상태와 서로 동기화 되지 않는다.

```java
// Iterator 객체를 명시적으로 사용하고 그 객체의 remove() 메서드를 호출함으로 이 문제를 해결할 수 있다.
for(Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext();){
	Transaction transaction = iterator.hasNext()
	if(Character.isDigit(transaction.getReferenceCode().charAt(0)) {
		iterator.remove(transaction); 
	}
}
```

- 자바 8의 removeIf로 위 코드를 바꿀 수 있다.

```java
transactions.removeIf(transaction ->
	Charater.isDigit(transaction.getReferenceCode().charAt(0)));
```

## 8.2.2 replaceAll 메서드

- List 인터페이스의 replaceAll 메서드를 이용해 리스트의 각 요소를 새로운 요소로 바꿀 수 있다.

```java
// 스트림 API 이용
referceCodes.stream() //[a12, C14, b13]
	.map(code -> Character.toUpperCase(code.charAt(0) + code.substring(1))
	.collect(Collectors.toList())
	.forEach(System.out::println); // A12, C14, B13
	
```

❗️ 위 코드는 새 문자열 컬렉션을 만든다. 기존의 컬렉션을 바꾸는 것이 아니다.

```java
// ListIterator 객체를 이용한 요소 변경
for(ListIterator<String> iterator = refernceCodes.listIterator();
	iterator.hasNext();) {
	String code = iterator.next();
	iterator.set(Character.toUpperCase(code.charAt(0) + code.substring(1));
}
```

- 자바 8의 replaceAll 메서드를 이용해 바꿀 수 있다.

```java
referenceCode.replaceAll(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1));
```

## 8.3 맵 처리

## 8.3.1 forEach 메서드

- Map.Entry<K, V> 반복자를 이용해 맵의 항목 집합을 반복할 수 있다.

```java
for(Map.Entry<String, Integer> entry : ageOfFriends.entrySet()){
	String friend = entry.getKey();
	Integer age = entry.getValue();
	System.out.println(friend + " is " + age + " years old");
```

- 자바 8부터는 Map 인터페이스는 BiConsumer를 인수로 받는 forEach 메서드를 지원한다..

```java
ageOfFriends.forEach((friend, age) -> System.out.println(friend + " is " + age + " years old"));
```

## 8.3.2 정렬 메서드

다음 두 개의 새로운 유틸리티를 이용하면 맵의 항목을 값 또는 키를 기준으로 정렬할 수 있다.

- Entry.comparingByValue
- Entry.comparingByKey

```java
Map<String, String> favouriteMovies = 
	Map.ofEntries(entry("Raphael", "Star Wars"),
	entry("Christina", "Matrix"),
	entry("Olivia" , "James Bond"));

favouriteMovies.entrySet()
	.stream()
	.sorted(Entry.comparingByKey())
	.forEachOrdered(System.out::println); // 사람의 이름을 알파벳 순으로 스트림 요소를 처리한다.

/* 결과
	Christina=Matrix
	Olivia=James Bond
	Raphael=Star Wars
*/
```

## 8.3.3 getOrDefault 메서드

- 기존에는 찾으려는 키가 존재하지 않으면 null이 반환되어 NPE을 방지할면 요청 결과가 널인지 확인해야 했다. 기본값을 반환하는 형식으로 이 문제를 해결했다.

```java
// 첫번째 인수: 키 두번째 인수: 기본값
Map<String, String> favoriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"),
	entry("Olivia", "James Bond"));

System.out.println(favoriteMovies.getOrDefault("Olivia", "Matrix")); // James Bond
System.out.println(favoriteMovies.getOrDefault("Thibaut", "Matrix")); // Matrix
```

## 8.3.4 계산 패턴

맵에 키가 존재하는지 여부에 따라 어떤 동작을 실행하고 결과를 저장해야 하는 상황이 필요한 때가 있다.

상황: 키를 이용해 값비싼 동작을 실행해서 얻은 결과를 캐시하려 할때

- `computeIfAbsent`: 제공된 키에 해당하는 값이 없으면(값이 없거나 널), 키를 이용해 새 값을 계산하고 맵에 추가한다.
- `computeIfPresent`: 제공된 키가 존재하면 새 값을 계산하고 맵에 추가한다.
- `compute`: 제공된 키로 새 값을 계산하고 맵에 저장한다.

```java
// 파일 집합의 각 행을 파싱해 SHA-256을 계산
Map<String, byte[]> dataToHash = new HashMap<>();
MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
// 데이터 반복
lines.forEach(line ->
	dataToHash.computeIfAbsent(line, this::calculateDigest)); // 키가 존재하지 않으면 동작 실행

private byte[] calculateDigest(String key){
	return messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
```

```java
// Raphael에게 줄 영화 목록을 만든다고 가정
String friend = "Raphael";
List<String> movies = friendsToMovies.get(friend);
if(movies == null) { // 리스트 초기화 확인
	movies = new ArrayList<>();
	friendsToMovies.put(friend, movies);
}
movies.add("Star Wars"); // 영화 추가
System.out.println(friendsToMovies); // {Raphael:[Star Wars]}
```

- 위 코드를 computeIfAbsent로 구현

```java
friendsToMovies.computeIfAbsent("Raphael", name -> new ArrayList<>())
	.add("Star Wars"); // {Raphael:[Star Wars]}
```

## 8.3.5 삭제 패턴

```java
String key = "Raphael";
String value = "Jack Reacher 2";
if(favouriteMovies.containsKey(key) && Objects.equals(favouriteMovies.get(key), value)) {
	favouriteMovies.remove(key);
	return true;
}
else {
	return false;
}
```

- 자바 8의 오버로드 버전 메서드 이용

```java
favouriteMovies.remove(key, value)
```

## 8.3.6 교체 패턴

- 맵의 항목을 바꾸는 데 사용할 수 있는 두 개의 메서드
    - `replaceAll`: BiFunction을 적용한 결과로 각 항목 값을 교체
    - `Replace`: 키가 존재하면 맵의 값을 바꾼디. 키가 특정 값으로 매핑되었을 때만 값을 교체하는 오버로드 된 버전도 있다.

```java
Map<String, String> favouriteMovies = new HashMap<>();
favouriteMovies.put("Raphael", "Star Wars");
favouriteMovies.put("Olivia", "james bond");
favouriteMovies.replaceAll((freind, movie) -> movie.toUpperCase());
System.out.println(favouriteMovies); // {Olivia=JAMES BOND, RAPHAEL=STAR WARS}
```

## 8.3.7 합침

두 그룹의 연락처를 포함하는 두 개의 맵을 합친다고 가정.

```java
Map<String, String> family = Map.ofEntries(
	entry("Teo", "Star Wars"), entry("Cristina", "James Bond"));
Map<String, String> friends= Map.ofEntries(
	entry("Raphael", "Star Wars"));
Map<String, String> everyone= new HashMap<>(family);
everyone.putAll(friends); // friends 모든 항목 everyone으로 복사
System.out.println(everyone); // {Christina=James Bond, Raphael=Star Wars, Teo=Star Wars}
```

→ 중복된 키가 없다면 위 코드는 정상 동작한다. 값을 유연하게 합쳐야 한다면 merge 메서드를 이용할 수 있다.

```java
Map<String, String> family = Map.ofEntries(
	entry("Teo", "Star Wars"), entry("Cristina", "James Bond"));
Map<String, String> friends= Map.ofEntries(
	entry("Raphael", "Star Wars"), entry("Cristina", "Matrix"); // family와 같은 key가 있다.
//forEach와 merge 메서드를 이용한 충돌 해결
Map<String, String> everyone = new HashMap<>(family);
friends.forEach((k,v) -> 
	everyone.merge(k, v, (movie1, movie2) -> movie1 + " & " + movie2));
System.out.println(everyone); // {Raphael=Star Wars, Christina=James Bond & Matrix, Teo=Star Wars}
```

- merge를 이용해 초기화 검사도 할 수 있다.

```java
// 영화 시청 횟수 기록 
Map<String, Long> moviesToCount = new HashMap<>();
String movieName = "JamesBond";
long count = moviesToCount.get(movieName);
if(count == null) {
	moviesToCount.put(movieName, 1);
}
else {
	moviesToCount.put(movieName, count + 1);
}
```

- merge를 이용한 구현

```java
moviesToCount.merge(movieName, 1L, (count, increment) -> count + 1L);
// 두 번쨰 인수: 키와 연관된 기존 값에 합쳐질 널이 아닌 값 
// 또는 값이 없거나 키에 널 값이 연관되어 있다면 이 값을 키와 연결
// 키의 반환값이 널이므로 처음에는 1이 사용된다.
```

## 8.4 개선된 ConcurrentHashMap

- `동시성 친화적`이며 최신 기술을 반영한 HashMap 버전이다.
- 내부 자료구조의 특정 부분만 잠궈 동시 추가, 갱신 작업을 허용한다.

→ 동기화된 HashTable 버전에 비해 읽기 쓰기 연산 성능이 월등하다.

## 8.4.1 리듀스와 검색

ConcurrentHashMap은 다음 세가지 연산을 지원한다.

- `forEach`: 각 (키, 값) 쌍에 주어진 액션을 실행
- `reduce`: 모든 (키, 값) 쌍을 제공된 리듀스 함수를 이용해 결과로 합침
- `search`: 널이 아닌 값을 반환할 때까지 (키, 값) 쌍에 함수를 적용

키에 함수 받기, 값, Map.Entry, (키, 값) 인수를 이용한 네 가지 연산 형태 지원

- 키, 값으로 연산(forEach, reduce, search)
- 키로 연산(forEachKey, reduceKeys, searchKeys)
- 값으로 연산(forEachValue, reduceValues, searchValues)
- Map.Entry 객체로 연산(forEachEntry, reduceEntrys, searchEntrys)

→ 이들 연산에 제공한 함수는 계산이 진행되는 동안 바뀔 수 있는 객체, 값, 순서 등에 의존하지 않아야 한다.

→ 연산에 `병렬성 기준값(threshold)`을 지정해야 한다. 맵의 크기가 주어진 기준값보다 작으면 순차적으로 연산을 실행한다.

## 8.4.2 계수

`mappingCount`: 맵의 매핑 개수 반환한다. 

- 기존의 size 메서드 대신 long을 반환하는 mappingByCount 메서드를 사용하는 것이 좋다

→ 매핑의 개수가 int의 범위를 넘어서는 이후 상황 대처 가능하기 때문

## 8.4.3 집합뷰

`keySet`: ConcurrentHashMap 클래스를 집합 뷰로 반환

- 맵을 바꾸면 집합도 바뀌고, 집합을 바꾸면 맵도 영향을 받는다