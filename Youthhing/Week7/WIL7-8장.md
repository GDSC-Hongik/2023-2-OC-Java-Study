# WIL7 8장

이번 장에서는 컬렉션 API를 다룬다.

# 8.1 컬렉션 팩토리

자바 9에선 작은 컬렉션 객체를 쉽게 만들 수 있는 몇가지 방법들이 제공되었다.

친구 3명의 이름을 컬렉션에 Array형태로 저장하는 두가지 방법들을 보자.

### 방법1: ArrayList활용

```java
List<String> friends = new ArrayList<>();
friends.add("Kim");
friends.add("Min");
friends.add("Jae");
```

### 방법2: 팩토리 메서드 Arrays.asList사용

```java
List<String> friends = Arrays.asList("Kim","Min","Jae");
```

방법1의 경우, 3개의 문자열을 저장함에도 많은 코드가 필요하지만, 방법2 `Arrays.asList()`라는 팩토리 메서드를 사용하면 코드 길이를 줄일 수 있다.

다만, ArrayList를 사용하는 방법1은 가변 크기의 리스트이지만, 방법 2의 경우 고정 크기의 리스트이므로, 요소를 갱신할 수는 있지만 추가시에 `UnsupportedOperationException`이 발생한다.

## UnsuppportedOperationException

팩토리 메서드 List 인터페이스를 상속받아서 구현된다. 하지만, List와 같이 `불변 객체` 의 역할을 하는 팩토리에선 해당 메서드를 사용할 수 없다. 따라서, 해당 메소드가 IDE에서 List.add와 같이 사용할 수 있다고 나오지만 막상 실행할 경우 UOE가 발생한다.

## List 팩토리

`List.of`라는 팩토리 메서드를 통해 간단하게 리스트를 만들 수 있다.

```java
List<String> friends = List.of("Kim","Min","Jae");
```

이는 가변 크기가 아닌, 고정된 크기의 팩토리메서드로, add, remove와 같은 크기를 변경하는 메서드가 실행될때 UnsupportedOperationException을 발생시킨다.

![https://blog.kakaocdn.net/dn/bvy9el/btsv9Mi78R5/3G9Z1nbxR8kxMf3jzzdSlK/img.png](https://blog.kakaocdn.net/dn/bvy9el/btsv9Mi78R5/3G9Z1nbxR8kxMf3jzzdSlK/img.png)

### 오버로딩 vs 가변인수

List 인터페이스를 보면 `List.of` 의 다양한 오버로드 버전이 존재한다. 

![https://blog.kakaocdn.net/dn/c2BWiP/btsv7gyxIyt/uZAFk14dmPPnNBAdwvdjkK/img.png](https://blog.kakaocdn.net/dn/c2BWiP/btsv7gyxIyt/uZAFk14dmPPnNBAdwvdjkK/img.png)

```java
static<E> List<E> of(E ... elements)
```

위 코드처럼 가변 인수를 사용하면 다중 요소를 받을 수 있는 API를 쓸 수 있는데 왜 사용하지 않고 이런 다양한 오버로드 버전이 존재할까?

내부적으로 가변 인수 버전은 추가 배열을 할당해서 리스트로 감싼다. 따라서, 배열을 할당하고 초기화한 이후 가비지 컬렉션을 하는 비용을 지불해야하는데 고정된 파라미터를 받는API를 정의하면 이런 비용을 제거할 수 있다.

## Set 팩토리

아래와 같이 `Set.of`메소드를 통해 집합 팩토리를 만들 수 있다.

```java
Set<String> friends = Set.of("Kim","Min","Jae");
```

다만, Set 메소드는 중복된 요소를 허용하지 않기에 아래와 같이 "Kim"이라는 값을 중복해서 넣으려하면 `IllegalArugumentException`이 발생한다.

```java
Set<String> friends = Set.of("Kim","Kim","Jae");//IllegalArgumentException
```

## Map 팩토리

Map은 <Key,Value>의 쌍으로 묶인 자료구조이기에, Map 을 만드는 과정은 List나, Set보다는 조금 복잡하다.

우선, 10개 이하의 key,value쌍을 가진 크기가 작은 맵에선 `Map.of` 메서드로 아래와 같이 맵을 만들 수 있다.

```java
Map<String, Integer> ageOfKim = Map.of("Kim", 30, "Min", 20, "Jae", 10);
```

하지만, 그 이상의 맵에서는 `Map.Entry<Key,Value>` 객체를 인수로 받는 가변 인수로된 팩토리 메서드 `Map.ofEntries`를 사용하는 것이 좋다. Map.entry라는 팩토리 메서드를 통해 생성해주자.

```java
Map<String, Integer> integerMap = Map.ofEntries(Map.entry("Ra", 30),
								Map.entry("Pa", 20),
								Map.entry("el", 10));
```

# 리스트와 집합 처리

자바8에서 List, Set 인터페이스에는 `removeIf`, `replaceAll`, `sort` 와 같이 호출한 컬렉션 자체를 바꾸는 메서드가 추가되었다. 이들은 스트림처럼 새로운 결과를 만드는 것이 아닌, 기존의 컬렉션을 바꾸는 역할을 한다. why?

## removeIf 메서드

아래와 같이 vegetarian인 dish를 컬렉션에서 제거하는 코드를 보자.

해당 코드를 실행하면, `ConcurrentModificationException`이 발생된다.

```java
List<Dish> menu = new ArrayList<>();
menu.add(new Dish("chicken",false,500, Dish.Type.MEAT));
menu.add(new Dish("fish ",false,300, Dish.Type.FISH));
menu.add(new Dish("vegeterian",true,100, Dish.Type.OTHER));

for (Dish dish : menu) {
		if(dish.isVegetarian()==true){
			menu.remove(dish);
		}
}
```

### Iterator를 사용한 코드

```java
List<Dish> menu = new ArrayList<>();
menu.add(new Dish("chicken",false,500, Dish.Type.MEAT));
menu.add(new Dish("fish ",false,300, Dish.Type.FISH));
menu.add(new Dish("vegeterian",true,100, Dish.Type.OTHER));

for (Iterator<Dish> iterator = menu.iterator();iterator.hasNext()){
		Dish nextDish = iterator.next();
		if(nextDish.isVegetarian()){
			menu.remove(nextDish);
		}
}
```

그 이유는, for-each문이 내부적으로 iterator를 사용해 다음 코드와 동일하게 작동하기 때문인데 Iterator와 컬렉션 두 개의 객체에서 컬렉션을 관리하기에 위와 같은 에러가 발생한다.

- Iterator 객체에서, next(), hasNext()를 이용해 소스를 질의
- Collection 객체 자체에서 remove()를 호출에 요소를 삭제함.

따라서, Iterator의 상태와 컬렉션의 상태가 서로 동기화되지 않는 문제가 발생한다.

이를 해결하기 위해 우선, 아래와 같이 iterator를 명시적으로 사용해서 해결하는 방법이 있는가하면

```java
if(nextDish.isVegetarian()){
		iterator.remove(nextDish);
}
```

자바8에서 제공하는 removeIf 메서드를 활용해서도 해결할 수 있다.

`removeIf()`메서드는 프레디케이트를 만족하는 요소를 제거할 수 있다. 이는 List,Set을 구현하거나 상속받은 모든 클래스에서 이용할 수 있다.

replaceAll 메서드

List인터페이스의 `replaceAll` 메서드를 통해 리스트의 각 요소를 새로운 요소로 바꿀 수 있다.

우선, 지난 스트림을 배우는 과정에서 스트림 -> map -> Collectors를 통해 요소를 바꿀 수 있다는 것을 배웠다. 그러나, 스트림에서 요소를 바꾸는 것은 기존 컬렉션의 값을 바꾸는 것이 아닌, 새로운 문자열 컬렉션을 만드는 것이다.

우리가 원하는 것은 배열의 값을 바꾸는 것처럼 단순한 작업이기에 이를 위한 코드를 짜보면 다음과 같다.

```java
List<String> referenceCodes = Arrays.asList("kim","min","jae");

for(ListIterator<String> iterator = referenceCodes.listIterator(); iterator.hasNext();){
		String code = iterator.next();
		iterator.set(Character.toUpperCase(code.charAt(0))+code.substring(1));
}
```

이 코드를 `replaceAll` 을 통해 조금 더 간결하게 만들 수 있다.

```java
referenceCodes.replaceAll(code -> Character.toUpperCase(code.charAt(0))+code.substring(1));
```

# Map 처리

### 1. forEach 메서드

Map 자료형에서 key, value를 반복하며 확인하는 작업은 상당히  복잡하다. 자바8에서는 Map 인터페이스에서  키와 값을 인수로 받는 BiConsumer 를 인수로 받는 forEach 메서드를 지원하기에 간단하게 코드 구현이 가능하다.

가령, 맵에서 키와 밸류를 차례대로 짜는 코드는 다음과 같이 짤 수 있다.

```java
Map<String,Integer> ageOfFriends = Map.of("Kim",30,"Min",20,"Jae",10);
ageOfFriends.forEach((friend,age)-> System.out.println(friend+ " is "+ age));
```

### 2. 정렬

맵은 키를 기준으로, 값을 기준으로 두 가지 방법으로 정렬할 수 있다. `Entry.comparingByKey`, `Entry.comparingByValue`가 존재하며, Stream을 사용해 다음과 같이 활용할 수 있다.

```java
Map<String,Integer> ageOfFriends = Map.of("Kim",30,"Min",20,"Jae",10);
			ageOfFriends.entrySet()
			.stream()
			.sorted(Map.Entry.comparingByValue())
			.forEachOrdered(System.out::println);
```

HashMap 성능 개선

자바8에서는 HashMap의 내부 구조를 바꿔서 성능을 개선했다.

기존에는 여러 HashCode가 같은 값을 갖는 상황에서 O(n)의 시간 복잡도를 가지는 LinkedList의 형태로 버킷을 반환해 성능이 저하되었다. 그러나, 자바8부터는 버킷이 너무 커질 경우 정렬된 트리 형태로 버킷을 반환해 O(log(n))의 시간복잡도로 동적으로 충돌이 일어나는 요소 반환 성능을 개선했다.

(단, 키가 String, Number같은 Comparable 객체여야한다.)

### 3. getOrDefault 메서드

맵에서는 기존에 찾으려는 키가 존재하지 않으면 null이 반환되고 NullPointerException이 발생했다. 이때 기본값을 지정하는 방식으로 에러를 방지할 수 있다.

이 메서드의 첫번째 인수로는 Key를 두번째 인수로는 해당 key에 맞는 value가 존재하지 않을때의 Default 값을 명시해준다.

```java
Map<String,String> favouriteMovies = Map.ofEntries(
						entry("Rahel","Star wars"),
                        entry("Olivia","James Bond"));

System.out::println(favouriteMovies.getOrDefaults("Olivia","Yuseung");// Olivia 키가 존재하므로 James 반환
System.out::println(favouriteMovies.getOrDefaults("Wrong","Yuseung");// Wrong 키가 존재하지 않으므로 Yuseung 반환
```

단, 키가 존재하더라도 value가 null인 경우엔 null을 반환할 수 있다.

### 4. 계산 패턴

맵에 키가 존재하는지 여부에 따라 어떤 동작을 실행하는지가 결정되는 경우가 있다.

- computeIfAbsent: 제공된 키에 해당하는 값이 없거나 null 이면, 키를 이용해 새 값을 곗나하고 맵에 추가하라.
- computeIfPresent: 제공된 키가 존재하면 새 값을 계산하고 맵에 추가하라.
- compute: 제공된 키로 새 값을 계산하고 맵에 저장하라.

예를 들어, 정보를 캐시에 저장할때 computeIfAbsent를 사용할 수 있다. 첫번째 인자에는 key를, 두번째 인자에는 존재하지 않을 경우 실행할 함수를 지정할 수 있다.

```java
computeIfAbsent(Key, 함수);
```

하나하나 null인지 확인하고 null 일 경우 값을 넣어주는 코드를 굳이 짤 필요가 없어진다.

### **5. 삭제 패턴**

제공된 키에 해당하는 맵항목을 제거하는 remove 가 존재한다.

자바 8에서는 특정 키가 특정한 값과 연관되어있을때만 항목을 제거하는 오버로드 버전 메서드가 존재한다.

Key에 해당하는 모든 값을 지우는게 아닌, 그 중에서도 관련있는 값을 지운다!

```java
favouriteMovies.remove(key,value);
```

첫번째 파라미터는 key, 두번째 파라미터는 value의 형태로 사용한다.

### 6. 교체 패턴

맵의 특정 요소를 바꾸고 맵에서 요소를 삭제하는 방법으로 두가지 메서드가 있다.

- **replaceAll**: List에서 살펴본 메서드와 비슷한 동작으로 BiFunction을 적용해 모든 값의 형식을 바꿀 수 있다.

```java
favouriteMovies.replaceAll((friend,movie)->movie.toUpperCase());//모든 value를 대문자로
```

- **Replace**: 키가 존재하면 맵의 value를 바꾼다. 키에 해당하는 특정 값이 존재할땜나 교체가 가능한 경우도 존재한다.

### 7. 합침

두 개의 맵을 합친다고할때는 `putAll`이라는 메서드를 활용할 수 있다.

```java
Map<String,String> everyone = new HashMap<>();
Map<String,String> friends = new HashMap<>();
//초기화
everyone.putAll(friends);
```

그러나, 이때 중복된 Key가 해당한다면 어떻게 될까?

이 경우에는 중복을 해결하는 `merge`메서드를 사용할 수 있다. BiFunction을 인수로 받아 oldValue, newValue에 관한 람다식을 작성하자.

**중복된 값이 존재할때 두 value를 합치는 함수**

```java
everyOne.forEach((k,v)->
	psg.merge(k,v,(oldValue,newValue)->(oldValue+newValue)));
```

키와 관련된 값이 없거나 null인 경우 merge는 키를 null이 아닌 값으로 연결한다.

또한, merge 함수를 통해 초기화도 가능하다. 해당 하는 키가 존재하면, 3번째 파라미터의 동작을, 존재하지 않으면 key의 초기값을 두번째 인자로 설정할 수 있다.

```java
moviesToCount.merge(movieName,1L,(key,count)->count+1L);
```

---

## 개선된 ConcurrentHashMap

### 1. 리듀스와 검색

스트림과 비슷하게 3가지 연산이 가능하다.

- forEach: 키,값 쌍에 주어진 행동을 함
- reduce: 모든 <키, 값> 쌍을 주어진 리듀스 함수를 이용해 합침
- search: null이 아닌 값을 반환할때까지 <키,값> 쌍에 함수를 적용

이들은 키로 연산, 값으로 연산, 키,값으로 연산 Map.Entry로 연산의 4가지 형태로 분류된다.

이들의 연산은 상태를 lock하지 않고 연산을 수행한다는 점이 있다. 따라서, 연산을 진행하는 동안 값, 객체, 순서가 바뀔수 있는 것에 의존된 연산을 하면 안된다.

또한, 병렬성 기준값, parallel threshold를 지정해야한다. 맵의 크기가 해당 값 이상이면 병렬 연산을, 값보다 작으면 순차적 연산을 실행한다.

### 2. 계수

ConcurrentHashMap에서 맵의 매핑 개수를 반환하는 mappingCount를 제공한다. 이때 long을 반환하는 mappingCount를 사용해 int 범위가 넘어갈 경우에 대처하자.

### 3. 집합 뷰

ConcurrentHashMap에선 Set으로 반환하는 KeySet이라는 메서드가 존재한다. 맵이 바뀌면 set도 바뀌고 set이 바뀌면 맵도 영향을 받는다.

newKeySet을 사용하면 ConcurrentHashMap을 유지하는 set을 만들 수 있다.