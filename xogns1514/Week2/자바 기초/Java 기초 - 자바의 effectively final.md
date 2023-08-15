# Java 기초 - 자바의 effectively final

자바에서 final로 선언되지 않았지만 초기화된 이후 참조가 변경되지 않아 final처럼 동작하는 “effectively final”이란 무엇일까?

---

## final은 아니지만 final 처럼

📌 effectively final 이란? final 키워드가 선언되지 않은 변수지만, 값이 재할당되지 않아 final과 유사하게 동작하는 것

- 자바 8에 도입된 개념으로, 익명 클래스 또는 람다식이 사용된 코드에서 찾을 수 있다
- 익명 클래스 또는 람다식에서는 참조하는 외부 지역 변수가 `final`로 선언됐거나 선언된 후 참조가 변경되지 않는 `effectively final`인 경우에만 접근 가능
- 그렇지 않다면 `“local variables referenced from a lambda expression must be final or effectively final”` ****컴파일 오류가 발생한다

```java
// 익명 클래스
public void someMethod() {
    int count = 0;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //에러 발생
            count++;//count는 외부에서 선언된 변수이다
        }
    };
}

// 람다식
public void someMethod() {
    List<Integer> list = Arrays.asList(1, 2, 3, 4);
    Integer criteria;
    
    for (Integer integer : list) {
        if (integer > 2) {
            criteria = 3;
            //에러 발생
            list.removeIf(o -> o.equals(criteria)); //criteria는 외부에서 선언된 변수이다
        }
    }
}
```

## effectively final인 경우

- 다음 조건을 만족하는 지역변수는 effective final로 간주된다.
1. final로 선언되지 않음
2. 초기화를 진행한 후에 다시 할당하지 않음
3. 전위(prefix) 또는 후위(postfix)에 증감 또는 감소 연산자가 사용되지 않음

📌 객체의 경우 객체가 가리키는 참조를 변경하지 않으면 된다.

```java
List<Person> personList = List.of(new Person(2), new Person(3));
for (Person p : personList) {
    p.setId(2);
    personList.removeIf(o -> o.getId() == p.getId());
}
//객체가 가리키는 참조를 변경하지 않았다. 따라서 객체의 상태를 변경해도 effective final 이다.
```

## Lambda Capturing

람다 캡처링(Lambda Capturing): 람다에서 외부에 정의된 변수를 사용할 때 내부에서 사용할 수 있도록 복사본을 생성하는 것. (외부 변수는 지역 변수뿐 아니라 인스턴스 변수와 클래스 변수를 포함함)

```java
//외부 인스턴스 변수 참조
public class Tester {
	private int count = 0;

	public void someMethod() {
		Runnable runnable = () -> System.out.println("count: " + count);
	}
}

//외부 지역 변수 참조
public void someMethod() {
    int count = 0;
    Runnable runnable = () -> System.out.println(count);
}
```

```java
//람다 내부에서 접근하는 외부 변수가 없는 예제
Runnable runnable = () -> {
    String msg = "Taengtest"; 
    System.out.println(msg) //msg는 람다 내부 변수
};

Function<Integer, Integer> func = (param) -> 5 * param; //전달된 매개변수만 활용
func.apply(5);
```

## 왜 복사본을 만들까?

- `지역 변수`는 메모리 영역 중 `스택(Stack)에 할당`된다.
- 스택 영역은 `스레드마다 고유한 영역`을 갖는다. 즉 공유되지 않는다
- 스레드가 종료되는 경우 생성된 스택 영역도 사라지게 된다. 따라서 외부 지역 변수를 그대로 참조하지 못하기 때문에 복사본을 생성하는 것이다.