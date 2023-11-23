# Arrays.asList vs List.of

## 변경 가능 여부

- Arrays.asList → list 변경 가능
    - ArrayList 반환 (Arrays 내부 클래스)
    - 크기 변경 x
- List.of → list 변경 불가능
    - ListN 반환 : 불변 객체 (immutable)

```java
List<Integer> list = Arrays.asList(1, 2, null);
list.set(1, 10); // OK

List<Integer> list = List.of(1, 2, 3);
list.set(1, 10); // UnsupportedOperationException
```

## Null 허용 여부

- Arrays.asList → null 허용
- List.of → 객체 생성 시 null 체크, 허용x

```java
List<Integer> list = Arrays.asList(1, 2, null); // OK
List<Integer> list = List.of(1, 2, null); // NullPointerException
```

```java
List<Integer> list = Arrays.asList(1, 2, 3);
list.contains(null); // false

List<Integer> list = List.of(1, 2, 3);
list.contains(null); // NullPointerException
```

## 참조 / 비참조

- Array.asList → 크기 늘이거나 줄일수 x (add, remove 구현x)
    - 참조로 동작하기 떄문
    - 배열 값이 변경되면 list에도 영향
    - thread safety x

```java
Integer[] array = {1,2};
List<Integer> list = Arrays.asList(array);
array[0] = 100;
System.out.println(list); // [100, 2]
```

- List.of → 값을 기반으로 독립적인 객체 생성
    - 참조 일어나지 x
    - thread safety

```java
Integer[] array = {1,2};
List<Integer> list = List.of(array);
array[0] = 100;
System.out.println(list); // [1, 2]
```

## 메모리 사용

- Arrays.asList → 힙에 더 많은 개체 생성
- List.of → 값 요소만 필요할 때 적합
    - 필드 기반 구현
    - Array → ArrayList, HashSet으로 변환시 요소만 알면 됨

```java
List<String> list = new ArrayList<>(List.of(array));
Set<String> set = new HashSet<>(List.of(array));
```

<aside>
☝🏻 **크기를 변경하려면?**

---

- `Arrays.asList` `List.of` 둘다 크기 변경 불가능
- 변경하려면 Collections 생성해서 요소들의 값 옮겨야함
  
# Method Reference의 정확한 사용법에 대한 분석

## Method Reference란?

- 클래스 또는 객체에서 메서드 참조
- 람다식을 더 간결하게 : 불필요한 매개변수 out

```java
// 람다식
str -> str.length()
// 메서드 참조
String::length
```

- 이중 콜른(`::`)을 사용하여 클래스 이름과 메서드 이름 구분

## Method Reference의 사용법

- static method reference
- instance method reference of a particular object
- instance method reference of an arbitrary object of a particular type
- constructor reference

### Reference to a static method

- `Class::staticMethod`
- 정적 메서드 : 객체의 생성 없이 호출 가능
    - 호출 형식 : `클래스이름.메서드이름(매개변수)`
- 해당하는 `클래스`의 정적 메서드 참조

```java
@FunctionalInterface
public interface IAdd {
	int add(int x, int y);
}

public class MathUtils {
	public static int AddElement(int x, int y) {
		return x + y;
	}
}
```

```java
// 람다식
IAdd addLambda = (x, y) -> MathUtils.AddElement(x, y);
// 메서드 참조
IAdd addMethodRef = MathUtils::AddElement;
```

### Reference to an instance method of a particular object

- `obj::instanceMethod`
- 인스턴스 메서드 : 반드시 객체 생성해야만 호출 가능
- 해당하는 `객체`의 인스턴스 메서드 참조

```java
public class MathUtils {
	public int AddElement(int x, int y) {
		return x + y;
	}
}
```

```java
// 객체 생성
MathUtils mu = new MathUtils;
// 람다식
IAdd addLambda = (x, y) -> mu.AddElement(x, y);
// 메서드 참조
IAdd addMethodRef = mu::AddElement;
```

### Reference to an instance method of an arbitrary object of a particular type

- `ObjectType::instanceMethod`
- 임의의 객체의 특정 `타입`에 대한 인스턴스 메서드 참조

```java
// 람다식
(obj, args) -> obj.instanceMethod(args);
// 메서드 참조
ObjectType::instanceMethod
```

```java
Arrays.sort(strArr, new Comparator<String>() {
	@Override
	public int compare(String s1, String s2) {
		return s1.compareToIgnoreCase(s2);
	}
});

// 람다식
Arrays.sort(strArr, (s1, s2) -> s1.compareToIgnoreCase(s2));
// 메소드 참조
Arrays.sort(strArr, String::compareToIgnoreCase);
```

### Constructor method reference

- `ClassName::new`
- `생성자`도 참조 가능

```java
// 람다식
constructor = (name, major) -> new Student(name, major);
// 메소드 참조
constructor = Student::new;
constructor.apply("이름", "전공");
```
