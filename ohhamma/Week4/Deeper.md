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
</aside>