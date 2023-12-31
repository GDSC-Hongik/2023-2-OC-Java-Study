1장에서는 핵심적으로 바뀐 자바 기능과 책의 전체 흐름을 파악하는 것이 좋다.

- 자바가 거듭 변화하는 이유
- 컴퓨팅 환경의 변화
- 자바에 부여되는 시대적 변화 요구
- 자바 8과 자바 9의 핵심 기능 소개

# 역사의 흐름은 무엇인가?
```java
Collections.sort(inventory, new Comparator<Apple>() {
    public int compare(Apple a1, Apple a2) {
        return a1.getWeight().compareTo(a2.getWeight());
    }
});
```

자바8을 이용하면 위의 코드를 다음과 같이 자연어에 가깝게 간단한 방식으로 코드를 구현할 수 있다.

```java
inventory.sort(comparing(Apple::getWeight));
```

자바 8은 간결한 코드와 멀티코어 프로세서의 쉬운 활용이라는 두 가지 요구사항을 기반으로 한다. 자바 8이 제공하는 기술에 대해서 알아보자.
- 스트림 API
- 메서드에 코드를 전달하는 기법
- 인터페이스의 디폴트 메서드

스트림 API는 병렬 연산을 지원하는 새로운 기술이다. 스트림 API 덕분에 나머지 기능, 메서드에 코드를 전달하는 기법과 인터페이스의 디폴트 메서드가 존재한다. 

# 왜 아직도 자바는 변화하는가?
## 프로그래밍 언어 생태계에서 자바의 위치
>자바는 어떻게 대중적인 프로그래밍 언어로 성장했는가? <br>1. 캡슐화 <br> 2. 객체 지향의 정신적인 모델

병렬 프로세싱의 활용이 중요해지자 자바의 변화가 시작되었다. 자바 8은 더 다양한 프로그래밍 도구를 제공하며 다양한 프로그래밍 문제를 더 빠르고 정확하며 쉽게 유지보수할 수 있다는 장점이 있다.

## 1. 스트림 처리
스트림이란 한 번에 한 개씩 만들어지는 연속적인 데이터 항목들의 모임이다. 스트림 API는 파이프라인틀 만드는 데 필요한 많은 메서드를 제공한다. 스트림 API의 핵심은 우리가 하려는 작업을 고수준으로 추상화해서 일련의 스트림으로 만들어 처리할 수 있다는 것이다.

## 2. 동작 파라미터화로 메서드에 코드 전달하기
자바 8 이전의 자바에서는 메서드를 다른 메서드로 전달할 방법이 없었지만, 자바 8에서는 메서드를 다른 메서드의 인수로 넘겨주는 기능인 `동작 파라미터화`가 가능해졌다.

## 3. 병렬성과 공유 가변 데이터
스트림 메서드로 전달하는 코드는 다른 코드와 동시에 실행하더라도 안전하게 실행될 수 있어야 한다. 그렇게 하기 위해서는 공유 가변 데이터에 접근하지 않아야 한다. 공유되지 않은 가변 데이터, 메서드, 함수 코드를 다른 메서드로 전달하는 기능은 함수형 프로그래밍의 핵심이다.

## 자바가 진화해야 하는 이유
자바가 변화해왔기 때문에 사용자에게 편리함을 제공한다. 편리함은 컴파일을 할 때 더 많은 에러를 검출할 수 있으며, 리스트의 유형을 알 수 있어 가독성이 좋아졌다는 것을 말한다. 프로그래밍 언어는 하드웨어나 프로그래머 기대의 변화에 부응하는 방향으로 변화해야 한다.

# 자바 함수
프로그래밍 언어에서 함수란 정적인 메서드와 같은 의미로 사용된다.
함수가 필요한 이유는 프로그래밍 언어의 핵심은 값을 바꾸는 것이기 때문이다!

## 메서드와 람다를 일급 시민으로
- 메서드 참조(::): 이 메서드 값을 사용하라는 의미
- 기존에 비해 문제 자체를 더 직접적으로 설명한다는 점이 자바 8의 장점이다.

```java
// 자바 8 이전의 코드
File[] hiddenFiles = new File(".").listFiles(new FileFilter() {
    public boolean accept(File file) {
        return file.isHidden();
    }
})

// 자바 8 람다 
File[] hiddenFiles = new File(".").listFiles(file -> file.isHidden());

// 자바 8 메서드 참조
File[] hiddenFiles = new File(".").listFiles(File::isHidden());
```

### 람다: 익명 함수
```java
(int x) -> x + 1
```

x라는 인수로 호출하면 x + 1을 반환하는 동작을 수행하도록 코드를 구현할 수 있다. 이렇게 람다 문법을 통해서 메서드를 더 간결하게 구현할 수 있다.

## 코드 넘겨주기: 예제
모든 녹색 사과를 선택해서 리스트로 반환하는 프로그램을 구현해보자.

```java
public static List<Apple> filterGreenApples(List<Apple> inventory) {
  List<Apple> result = new ArrayList<>();
  for(Apple apple : inventory) {
    if(GREEN.equals(apple.getColor())) {
      result.add(apple);
    }
  }
  return result;
}
```

갑자기 사과를 무게로 필터링하고 싶은 사람이 나타났다면 다음과 같이 구현할 수 있다.

```java
public static List<Apple> filterHeavyApples(List<Apple> inventory) {
  List<Apple> result = new ArrayList<>();
  for(Apple apple : inventory) {
    if(apple.getWeight() > 150) {
      result.add(apple);
    }
  }
  return result;
}
```

자바 8에서는 코드를 인수로 넘겨줄 수 있으므로 filter 메서드를 중복으로 구현할 필요가 없다. 그렇다면 앞의 코드를 자바 8에 맞게 고쳐보자.

```java
public static boolean isGreenApple(Apple apple) {
  return GREEN.equals(apple.getColor());
}

public static boolean isHeavyApple(Apple apple) {
  return apple.getWeight() > 150;
}

public interface Predicate<T> {
  boolean test(T t);
}

static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p {
  List<Apple> result = new ArrayList<>();
  for(Apple apple : inventory) {
    if(p.test(apple)) {
      result.add(apple);
    }
  }
  return result;
}

filterApples(inventory, Apple:isGreenApple);
filterApples(inventory, Apple:isHeavyApple);
```

```java
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}
```

> predicate란 수학에서는 인수로 값을 받아 true나 false를 반환하는 함수를 일컫는다. 함수형 프로그래밍에서 @FunctionalInteface 어노테이션은 함수형 인터페이스를 나타내기 때문에 중요하다. 그리고 test 메서드는 주어진 입력 값을 평가하여 논리적인 조건을 검사하고 true 혹은 false로 반환한다.

## 메서드 전달에서 람다로
메서드를 값을 전달하는 것은 유용한 기능이지만 한두 번만 사용할 메서드를 매번 정의하는 것은 귀찮은 일이다. 

```java
filterApples(inventory, (Apple a) -> GREEN.equals(a.getColor()));
```

이렇게 메서드를 정의하지 않고 코드를 짧고 간결하게 구현할 수 있다.

# 스트림
스트림 API는 컬렉션 API와 데이터를 처리하는 방식의 차이점이 있다.
- 컬렉션: 외부 반복
- 스트림: 내부 반복

## 멀티 스레딩은 어렵다
자바 8은 스트림 API로 **컬렉션을 처리하면서 발생하는 모호함과 반복적인 코드 문제와 멀티코어 활용 어려움이라는 두 가지 문제를 모두 해결했다.**

- 필터링
- 데이터 추출
- 그룹화

컬렉션은 어떻게 데이터를 저장하고 접근할지에 중점을 두는 반면, 스트림은 데이터에 어떤 계산을 할 것인지 묘사하는 것에 중점을 둔다. 스트림은 스트림 내의 요소를 쉽게 병렬로 처리할 수 있는 환경을 제공한다는 것이 핵심이다.

# 디폴트 메서드와 자바 모듈
자바 8에서는 인터페이스를 쉽게 바꿀 수 있도록 디폴트 메서드를 지원한다. 디폴트 메서드를 이용하면 기존의 코드를 건드리지 않고도 원래의 인터페이스 설계를 자유롭게 확장할 수 있다.

```java
default void sort(Comparator<? super E> c) {
    Collections.sort(this, c);
}
```

# 함수형 프로그래밍에서 가져온 다른 유용한 아이디어
자바 8에서는 NullPointer 예외를 피할 수 있도록 도와주는 Optional<T> 클래스를 제공한다. Optional은 값을 가지지 않는 상황을 어떻게 처리할지 명시적으로 구현하는 메서드를 포함하고 있기 때문에 NullPointer 예외를 피할 수 있다.

구조적 패턴 매칭 기법도 있다. 하지만 자바 8에서 완벽하게 지원하지 않는다.