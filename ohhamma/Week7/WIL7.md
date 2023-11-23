# [Ch8] 컬렉션 API 개선

<aside>
✅ **이 장의 내용**

---

- 컬렉션 팩토리 사용하기
- 리스트 및 집합과 사용할 새로운 관용 패턴 배우기
- 맵과 사용할 새로운 관용 패턴 배우기
</aside>

## 8.1 컬렉션 팩토리

- 자바9 : 작은 컬렉션 객체를 쉽게 만들 수 있는 몇가지 방법 제공
- 왜 필요할까?

```java
List<String> friends = new ArrayList<>();
friends.add("Raphael");
friends.add("Olivia");
friends.add("Thibaut");
```

```java
List<String> friends = Arryas.asList("Raphael", "Olivia");
friends(0, "Richard");		// 요소 갱신 OK
friends.add("Thibaut");		// 요소 추가 UnsupportedOperationException
```

### UnsupportedOperationException 예외 발생

- 내부적으로 고정된 크기의 변환할 수 있는 배열로 구현되었기 때문에 발생

<aside>
💡 **컬렉션 리터럴**

- 특별한 문법을 이용해 컬렉션을 만들 수 있는 기능 지원 → 파이썬, 그루비
- 자바에서는 너무 큰 언어 변화 → 대신 컬렉션 API 개선
</aside>

### 8.1.1 리스트 팩토리

- `List.of` 팩토리 메서드 → 간단하게 리스트 생성
    - 변경할 수 없는 리스트
    - 컬렉션이 의도치 않게 변하는 것을 막을 수 있음
        - but 요소 자체가 변하는 것은 막을 수 없음
    - null 요소 금지

```java
List<String> friends = List.of("Raphael", "Olivia", "Thibaut");
System.out.println(friends);
friends.add("Chih-Chun");		// java.lang.UnsupportedOperationException
```

<aside>
💡 **오버로딩** vs **가변 인수**

- `List.of`의 다양한 **오버로드** 버전
    - 고정된 숫자의 요소(최대 10개)를 API로 정의
    - `Set.of`와 `Map.of`에서도 같은 패턴 등장
- **가변 인수** 버전 → 추가 배열을 할당해서 리스트로 감쌈
    - 나중에 가비지 컬렉션하는 비용 지불
</aside>

- 데이터 처리 형식을 설정하거나 데이터를 변활 필요가 없다면 → 팩토리 메서드 이용 권장

### 8.1.2 집합 팩토리

- 바꿀 수 없는 집합 생성

```java
Set<String> friends = Set.of("Raphael", "Olivia", "Thibaut");
System.out.println(friends);
```

```java
// IllegalArgumentException
Set<String> friends = Set.of("Raphael", "Olivia", "Olivia");
```

### 8.1.3 맵 팩토리

- 맵을 만드려면 키 & 값 필요
- 키와 값을 번갈아 제공하는 방법으로 맵 생성

```java
// 10개 이하의 키와 값 쌍을 가진 작은 맵을 만들 때 유용
Map<String, Integer> ageOfFriends
	= Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
System.out.println(ageOgFriends);
```

```java
// Map.Entry<K,V> 객체를 인수로 받으며 가변 인수로 구현되 Map.ofEntries
// 키와 값을 감쌀 추가 객체 할당 필요
import static java.util.Map.entry;
Map<String, Integer> ageOfFriends = Map.ofEntries(entry("Raphael", 30),
                                                    entry("Olivia", 25),
                                                    entry("Thibaut", 26));
System.out.println(ageOfFriends);
```

- `Map.entry` : `Map.Entry` 객체를 만드는 새로운 팩토리 메서드

## 8.2 리스트와 집합 처리

- 자바8에 추가된 List, Set 인터페이스의 메서드
    - `removeIf` : predicate를 만족하는 요소 제거
    - `replaceAll` : 리스트에서 사용 가능, UnaryOperator 함수로 요소 변경
    - `sort` : List 인터페이서에서 제공, 리스트 정렬
- 호출한 기존 컬렉션 자체를 바꿈

### 8.2.1 removeIf 메서드

- `removeIf` : 삭제할 요소를 가리키는 predicate를 인수로 받음

```java
transaction.removeIf(transaction ->
		Character.isDigit(transaction.getReferenceCode().charAt(0)));
```

### 8.2.2 replaceAll 메서드

- `replaceAll` : 리스트의 각 요소를 새로운 요소로 변경
- 컬렉션 객체를 Iterator 객체와 혼용 → 반복과 컬렉션 변경이 동시에 이루어짐 → fragile

```java
referenceCodes.replaceAll(code ->
		Character.toUpperCase(code.charAt(0)) + code.substring(1));
```

## 8.3 맵 처리

- 자바8 → `Map` 인터페이스에 추가된 디폴드 메서드

### 8.3.1 forEach 메서드

- `forEach` : `BiConsumer`(키와 값을 인수로 받음)를 인수로 받음

```java
ageOfFriends.forEach((friend, age) ->
		System.out.println(friend + "is " + age + " years old"));
```

### 8.3.2 정렬 메서드

- 맵의 항목을 값 또는 키를 기준으로 정렬
    - `Entry.comparingByValue`
    - `Entry.comparingByKey`

```java
Map<String, String> favouriteMovies
	= Map.ofEntries(entry("Raphael", "Star Wars"),
                    entry("Christina", "Matrix"),
                    entry("Olivia", "James Bond"));

favouriteMovies.entrySet()
                .stream()
                .sorted(Entry.comparingByKey())
                .forEachOrdered(System.out::println);
```

<aside>
💡 **HashMap 성능**

- 기존의 맵 → 키로 생성한 해시코드로 접근할 수 있는 버켓에 저장
    - 많은 키가 같은 해시코드를 반환하는 상황 → `O(n)`의 LinkedList (성능↓)
- 버킷이 너무 커질 경우 **정렬된 트리** 이용 `O(log(n))`
    - 동적으로 치환해 충돌이 일어나는 요소 반환 성능 개선
    - Comparable 형태만 지원
</aside>

### 8.3.3 getOrDefault 메서드

- 기존 : 찾으려는 키 존재 x → `NullPointerException`
- `getOrDefault` : 맵에 키가 존재하지 않으면 두번째 인수로 받은 **기본값** 반환

```java
Map<String, String> favouriteMovies
	= Map.ofEntries(entry("Raphael", "Star Wars"),
                    entry("Olivia", "James Bond"));

System.out.println(favouriteMovies.getOrDefault("Thibaut" "Matrix"));
```

- 키가 존재하더라도 값이 null인 상황 → null 반환 가능

### 8.3.4 계산 패턴

- `computeIfAbsent` : 제공된 키에 해당하는 값이 없으면(or null), 키를 이용해 새 값을 계산하고 맵에 추가
    - 정보 caching할 때 사용
- `computeIfPresent` : 제공된 키가 존재하면 새 값을 계산하고 맵에 추가
- `compute` : 제공된 키로 새 값을 계산하고 맵에 저장

```java
friendsToMovies.computeIfPresent("Raphael", name -> new ArrayList<>())
                .add("Star Wards");
```

- 여러 값을 저장하는 맵을 처리할 때 유용
- `Map<K, List<V>>`에 요소를 추가하려면 항목이 초기화되어있는지 확인 필요
    - `computeIfPresent` : 키와 관련된 값이 null이 아닐 때만 새 값 계산

### 8.3.5 삭제 패턴

- `remove` : 키가 특정한 값과 연관되었을 때만 항목을 제거
    - 오버로드 버전 메서드 제공 (자바8)

```java
favouriteMovies.remove(key, value);
```

### 8.3.6 교체 패턴

- `replaceAll` : BiFunction을 적용한 결과로 각 항목의 값 교체
    - List의 replaceAll과 비슷한 동작
- `Replace` : 키가 존재하면 맵의 값 바꿈
    - 키가 특정 값으로 매핑 되었을 때만 값을 교체하는 오버로드 버전

```java
Map<String, String> favouriteMovies = new HashMap<>();
favouriteMovies.put("Raphael", "Star Wars");
favouriteMovies.put("Olivia", "james bond");
favouriteMovies.replaceAll((friend, movie) -> movie.toUpperCase());
```

### 8.3.7 합침

- `merge` : **중복**된 키를 어떻게 합칠지 결정하는 `BiFunction`을 인수로 받음

```java
Map<String, String> family = Map.ofEntries(entry("Teo", "Star Wars"),
                                            entry("Cristina", "James Bond"));
Map<String, String> friends = Map.ofEntries(entry("Raphael", "Star Wars"),
                                            entry("Cristina", "Matrix"));

Map<String, String> everyone = new HashMap<>()(family);
friends.forEach((k, v) ->
	everyone.merge(k, v, (movie1, movie2) -> movie + " & " + movie2));
// 중복된 키가 있으면 두 값을 연결
```

- merge를 이용해 초기화 검사 구현
    - 키의 반환값이 null이므로 처음에는 1 사용
    - 그 다음부터는 BiFunction을 적용해 값 증가

```java
moviesToCount.merge(movieName, 1L, (count, increment) -> count + 1L);
```

## 8.4 개선된 ConcurrentHashMap

- 동시성 친화적
    - 내부 자료구조의 특정 부분만 잠굼
    - 동시 추가, 갱신 작업 허용
    - 읽기 쓰기 연산 성능↑
- 최신 기술을 반영한 HashMap 버전
    - 표준 HashMap → 비동기 동작

### 8.4.1 리듀스와 검색

- `forEach` : 각 (키, 값) 쌍에 주어진 액션 실행
- `reduce` : 모든 (키, 값) 쌍을 제공된 레듀스 함수를 이용해 결과로 합침
- `search` : null이 아닌 값을 반환할 때까지 각 (키, 값) 쌍에 함수 적용
- 4가지 연산 형태 지원
    - 키, 값으로 연산
    - 키로 연산
    - 값으로 연산
    - Map, Entry 객체로 연산
- ConcurrentHashMap의 상태를 잠그지 않고 연산 수행 → 바뀔 수 있는 값에 의존하지 않아야함
- 병렬성 기준값(threshold) 지정 : 맵의 크기가 주어진 기준값보다 작으면 순차적으로 연산 실행

```java
// 맵의 최댓값 검색
ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
long parallelismThreshold = 1;
Optional<Integer> maxValue = 
	Optional.ofNullable(map.reduceValues(parallelismThreshold, Long::max));
```

- 전용 each reduce 연산 제공 : int, long, double 등의 기본값
    - 박싱 작업 x

### 8.4.2 개수

- `mappingCount` : 맵의 매핑 개수 반환 (long)

### 8.3.4 집합뷰

- `keySet` : ConcurrentHashMap → 집합 뷰로 반환
- `newKeySet` : ConcurrentHashMap으로 유지되는 집합 생성