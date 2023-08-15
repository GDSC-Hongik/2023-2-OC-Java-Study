# WIL2 - 2장

소프트웨어 엔지니어는 항상 소비자의 요구사항이 변화하는 환경에서 일을 한다. 이를 처리하는데 비용은 최소화하고,  새로운 기능은 쉽게 구현되어야한다. 이와 같이 자주 바뀌는 요구사항에 효과적으로 대응하기 위한 개념인 **동작 파라미터화**에 대해 알아보자.

## **동작 파라미터화란?**

책에서는 동작 파라미터화를 '아직은 어떻게 실행할 것인지 결정하지 않은 코드 블록'으로 정의한다. 메서드의 파라미터로 전달된 코드블록의 실행이 나중으로 미뤄지는 것이다. 즉, 전달된 동작 파라미터에 따라 메서드의 동작이 결정되는 것이다.

이를 통해 다음과 같은 일반적인 예를 실행할 수 있다.

- 리스트의 모든 요소에 대해 '어떤 동작'을 수행할 수 있음
- 리스트 관련 작업을 끝낸 다음 '어떤 다른 동작'을 수행할 수 있음
- 에러가 발생시 '정해진 어떤 동작'을 수행할 수 있음

---

### **동작 파라미터화를 사용하지 않을때**

동작 파라미터화를 사용하지 않고 변화하는 요구사항에 대응 하는 코드를 짜는 예를 들어보자. 책의 예제대로 농부가 사과를 필터링하는 기준에 대한 요구사항이 매번 변하고 있다.

**녹색사과를 핕터링**

```
  public static List<Apple> filterGreenApples(List<Apple> inventory) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (apple.getColor() == Color.GREEN) {
        result.add(apple);
      }
    }
    return result;
  }
```

농부의 요구사항에 맞게 '녹색'사과로 사과를 필터링하는 코드를 작성했다. 이때 요구사항이 변해 빨간사과로 필터링을 원한다고 한다. 색에 대한 요구사항이 변했다. 따라서, 색을 기준으로 파라미터화해서 소비자가 색에 맞는 사과를 필터링할 수 있게 코드를 짰다.

**빨간 사과를 필터링 -> 색을 파라미터화**

`public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
  List<Apple> result = new ArrayList<>();
  for (Apple apple : inventory) {
    if (apple.getColor() == color) {
      result.add(apple);
    }
  }
  return result;
}`

이렇게 코드를 짜면 소비자 입장에선 *filterApplesByColor(inventory, RED)* 와 같은 코드를 작성할 수 있다. 원하는 색만 파라미터로 바꿔 입력하면 요구사항에 맞게 필터링할 수 있는 코드를 작성한 것이다.

그런데! 여기서 색이 아니라 무게로 필터링을 하고 싶다는 요구사항이 생기면? 현재 파라미터는 Color 밖에 없다. 무게에 대한 파라미터가 없는것이다. 그렇게 가능한 모든 속성을 메서드의 파라미터로 전달하면 어떻게 될까?

**무게로도 필터링? -> 가능한 모든 속성을 메서드의 파라미터로 ..?**

아래와 같은 코드를 짤 수 있는데 우선 가독성이 좋지 않다.

```
      if ((flag&&apple.getColor().equals(color))||(!flag&&apple.getWeight()>weight)) {
        result.add(apple);
      }
```

또한 이 코드를 사용하기 위해 아래처럼 코드를 짜면.. 가독성이 확떨어진다. 또 색과 무게외의 다른 요구사항이 생기면? 그에 맞게 편하게 수정하기가 어렵다.

(flag는 뭐고 null은 뭐고.. 읽기가 어렵다.)

```
    List<Apple> greenApples = filterApples(inventory,GREEN,0,true);
    List<Apple> heavyApples = filterApples(inventory,null,150,false);
```

따라서, 변화하는 요구사항에 대응하기 쉽게, 그러면서도 비용은 적고 구현은 쉽게 드는 코드를 짜기 위해 파라미터를 추가하는 방법은유연하지 않다는 것을 알 수 있다.

동작파라미터화가 등장한 것이다.

---

### **동작파라미터화**

필터링을 위해 **어떤 조건을** 만족해야한다면 해당 조건을 정의하는 인터페이스를 만들고, 필터에서 그 조건을 만족하는 컬렉션 클래스의 값만 추가해주는 방식으로 코드를 구현할 수 있다. 해당 인터페이스를 아래와 같이 만들면 boolean을 반환하는 test를 만족하는 값들만 filter에서 추려서 정의할 수 있다. 이렇게 참, 거짓을 반환하는 함수를 **프레디케이트**(predicate)라고 한다.

```java
public interface ApplePredicate{
	boolean test (Apple apple)
}
```

filter예시

```java
public static List<Apple> filter(List<Apple> inventory, ApplePredicate p) {
  List<Apple> result = new ArrayList<>();
  for (Apple apple : inventory) {
    if (p.test(apple)) {
      result.add(apple);
    }
  }
  return result;
}
```

이제 **ApplePredicate를 구현하는 적절한 구현 클래스만 구현**해주면 된다.

이렇게 조건에 따라 filter메서드가 다르게 동작하는 디자인 패턴을 '전략 디자인 패턴'이라고 부른다.

전략 디자인 패턴이란, 각 구현체 알고리즘을 Encapsulation하는 알고리즘들을 정의한 후 런타임에 적절한 구현 알고리즘전략을 선택하는 디자인 방식이다. (소공때 배운 Encapsulation과 유사하다)

이렇게 원하는 동작을 test메서드로 구현한 후 해당 메서드를 클래스로 감싸서 전달한다. 앞서 다룬 예제처럼 요구사항에 따라 전달할 파라미터의 형식이 달라지는 것이 아닌 어떠한 ApplePredicate의 구현 클래스를 전달하느냐에 따라 filter의 동작이 달라진다. 즉, 이렇게 filter 메서드의 동작을 파라미터화 한 것이다.

**책 예제: 간단하게 prettyPrintApple 구현하기**

요구사항: 다양한 방법으로 사과를 출력해보자!

우선 ApplePredicate에 해당하는 인터페이스를 하나 만들고 무게에 따라, 색에 따라 다른 형식으로 출력하는 클래스를 구현했다.

```java
  interface PrintFormat {
    String printFormat(Apple a);
  }

  static class IsHeavyApple implements PrintFormat {
    @Override
    public String printFormat(Apple a) {
      String output = a.getWeight()>150?"heavy":"light";
      return "This apple is "+output+" apple.";
    }
  }

  static class WhatColor implements PrintFormat{
    @Override
    public String printFormat(Apple a) {
      String output = a.getColor().toString();
      return "This apple's color is "+ output;
```

이후 filter에 준하는 프린트 함수를 만들고 실행했다.

```java
public static void prettyPrintApple(List<Apple> inventory,PrintFormat p){
  for(Apple apple:inventory){
    String output = p.printFormat(apple);
    System.out.println(output);
  }
}
```

![https://blog.kakaocdn.net/dn/sz3TE/btsrfjlwNZ7/gcFHRzvuzzMzTVyfB3mBnk/img.png](https://blog.kakaocdn.net/dn/sz3TE/btsrfjlwNZ7/gcFHRzvuzzMzTVyfB3mBnk/img.png)

결과

여기까지 책을 읽었을때 드는 생각은 결국은 이래도 ApplePredicate를 Implements한 세부 클래스를 정의해야하는 것이 아닌가? 결국 그것도 비효율적일 것 같은데.. 장점이 뭘까? 싶었다.

단지 요구사항이 변하더라도 소비자는 일정한 방식으로 filter에 전달할 메서드명만 전달받아서 편하게 접근할 수 있다 정도..?였는데 이어서 바로 익명클래스를 소개한다.

---

위와 같이 코드를 구현한다해도 결국 여러 클래스를 정의하고 인스턴스화 해야한다. 이는 너무 복잡한 과정이다. 또한, 구현 부분의 코드가 길어진다는 단점이 있다.

클래스 선언, 인스턴스화의 두 동작을 수행하는 방법인 익명 클래스를 소개한다.

### **익명 클래스**

자바의 local class와 비슷한 개념으로 이름이 없는 클래스를 의미한다. 이름이 없이 선언된 곳에서 즉시 구현해 인스턴스화 할 수 있는 클래스다. 아래와 같이 파라미터에 객체를 생성하고 바로 구현할 수 있다.

```java
List<Apple> redApples = filterApples(inventory, new ApplePredicate(){
	public boolean test(Apple a){
    	return RED.equals(a.getColor());
    }
});
```

하지만! 익명클래스 역시 단점이 존재한다.

우선, 객체를 생성하는 부분에서 반복되는 코드가 존재한다. 위 코드에선 boolean test()..를 구현하는 부분이 그러하다.

둘째로는 프로그래머들의 사용이 익숙지 않고 코드가 장황해질 수 있다는 점이다. 이는 유지보수에 큰 어려움을 준다.

### **람다 표현식**

람다 표현식을 사용하면 훨씬 더 간결한 코드를 가독성 있게 알 수 있다. 다음 코드를 보자.

```java
List<Apple> result = filter(inventory, (Apple a) -> RED.equels(apple.getColor()));
```

조금 더 자세한 설명은 3장에서 한다.

### 

### **리스트 형식으로 추상화**

predicate 인터페이스를 구현한 리스트 filter를 다음과 같이 만들고 적절한 람다 표현식을 사용할 수 있다.

```java
public static<T> List<T> filter(List<T> list, Predicate<T> p) {
  List<T> result = new ArrayList<>();
  for (T e : list) {
    if (p.test(e)) {
      result.add(e);
    }
  }
  return result;
}
```

```java
List<Integer> evenNum = filter(numbers,(Integer i) -> i%2==0);
```

### **동작 파라미터화 실전예제**

아래에선 동작 파라미터화를 사용하는 실제 예제를 간단하게 소개하겠다. 아래 예제들의 공통점은 반복되는 부분과 클래스의 정의, 인스턴스화 과정의 실전 코드를 간결하게 구현한다는 점이다. 

**Compartor 정렬**
```java
public interface Comparator<T>{
  int compare(T o1, T o2);
}
```
위 코드를 이용해 무게가 적은 순서로 사과를 정렬할 수 있고 람다로 간단한 표현이 가능하다.
```java
inventory.sort(new Comparator<Apple>){
  public int compare(Apple a1, Apple a2){
    return a1.getWeight().compareTo(a2.getWeight());
  }
}
//람다
inventory.sort((Apple a1 , Apple a2)-> a1.getWeight().compareTo(a2.getWeight()));

```

**Runnable**

**Callable등**
위 두가지 인터페이스 또한 람다로 구현이 가능하다.
---

## **3장 람다 표현식**

### **람다란 무엇인가?**

람다 표현식은 메서드로 전달할 수 있는 익명 함수를 단순화한 것으로 람다의 특징은 다음과 같다.

- 익명: 다른 메서드와 달리 이름이 없으므로 익명이다.
- 함수: 다른 메서드와 달리 클래스에 종속되지 않으므로 메서드가 아닌 함수라고 부른다. 그러나, 메서드와 같이 파라미터, return type등이 존재함
- 전달: 람다 표현식을 메서드 인수로 전달하거나 변수로 저장할 수 있음
- 간결성: 익명 클래스와 같이 많은 코드를 구현할 필요가 없다.

이러한 특징을 가지고 있는 람다표현식은 파라미터, 화살표 바디로 구성되어 아래와 같은 기본 형식을 띈다.

```java
( 람다 파라미터 ) -> -=~~~~람다 바디~~~~;
```

여러가지 유효한 람다 표현식의 예가 있으니 읽으며 익숙해져야겠다.

```java
(String s) -> s.length()
(Apple a) -> a.getWeight()>150
() -> 42
(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())
() -> "Raoul"//문자열을 반환하는 표현식
() -> {return "mario";}// 명시적 return을 통해 문자열을 반환하는 표현식
```

아래와 같은 람다 표현식은 옳지 않은데 이유는 다음과 같다.

```java
(Integer i) -> return "Alan"+1;// return은 흐름 제어문으로 명시적으로 중괄호를 써줘야한다.
(String s) -> {"Iron Man";}// "Iron Man"은 구문이 아니라 표현식이다.
```

**구문 vs 표현식?**

구문: 값이 기대되지 않고 행동을 수행하는 식

표현식: 단 하나의 값을 반환하는 코드

### **람다를 어디에 어떻게 사용해야할까?**

파라미터로 함수를 전달할때 람다 사용이 가능한 것은 어떻게 알 수 있을까?

지난 스터디에서 **함수형** **인터페이스**가 있으면 람다를 사용할 수 있다고 배웠다.

### **함수형 인터페이스란?**

오직 하나의 추상 메서드를 지정하는 인터페이스다. 많은 디폴트 메서드가 있더라도 추상 메서드가 오직 하나면 함수형 인터페이이다. 그 예로는 Predicate, Runnable, Callable등이 있다.

![https://blog.kakaocdn.net/dn/bHV8le/btsrfgvVORj/96G6QZGGmeFYnZ5Bo0TUN0/img.png](https://blog.kakaocdn.net/dn/bHV8le/btsrfgvVORj/96G6QZGGmeFYnZ5Bo0TUN0/img.png)

실제로 default 메서드가 존재한다.

![https://blog.kakaocdn.net/dn/5lfzm/btsq8HOn0WC/8v5ZTNvwA4nKXIBeWODMAk/img.png](https://blog.kakaocdn.net/dn/5lfzm/btsq8HOn0WC/8v5ZTNvwA4nKXIBeWODMAk/img.png)

![https://blog.kakaocdn.net/dn/cqiaOx/btsq8JMdGBf/34GM1P76x4MBtR7GfoBUZ0/img.png](https://blog.kakaocdn.net/dn/cqiaOx/btsq8JMdGBf/34GM1P76x4MBtR7GfoBUZ0/img.png)

따라서, 아래와 같은 코드는 상속 받은 Adder에 다른 add 메서드가 있으므로, 두개의 추상 메서드가 있다. 함수형 인터페이스가 아니다.

```java
public interface SmartAdder extends Adder{
	int add(double a, doubleb);
}
```

이렇게 추상 메서드가 한 개 이상이라면 "Multiple nonoverriding abstract methods found in interface Foo"라는 에러가 발생한다.

### **함수 디스크립터**

함수형 인터페이스의 추상 메서드 시그니처는 람다표현식의 시그니처를 가리킨다. 이때, 람다표현식의 시그니처를 서술하는 메서드를 함수 디스크립터라고 부른다. 람다 표현식에선 어떤 파라미터가 어떤 형식을 반환하는지가 중요한데 이를 설명한 것이 함수 디스크립터라는 것이다.

예를 들어 (Apple, Apple) -> int 라는 식은 Apple 두 개를 인수로 받아 int를 반환하는 함수를 가르킨다는 시그니쳐, 함수 디스크립터이다.

즉, 함수형 인터페이스의 파라미터와 반환형이 맞다면 람다를 사용할 수 있다.

```java
Predicate<Apple> p = (Apple a) -> a.getWeight()>150;//가능
Predicate<Apple> p = (Apple a) -> a.getWeight();//불가능
```

Predicate는 어떤 형식T를 인자로 받아서 boolean을 반환하는 test메서드를 구현한다. 따라서, T -> boolean의 형식이면 람다를 사용할 수 있는 것이다. 그러나, 아래식은 T -> int이므로 사용이 불가능하다.

### **람다 활용: 실행 어라운드 패턴**

자원 처리에 활용되는 순환 패턴은 자원을 열고 -> 처리하고 -> 자원을 닫는 순서로 이루어진다. 이와 같이 코드를 처리할때 **초기화/준비 코드 -> 작업 -> 정리/마무리 코드**의 형식을 실행 어라운드 패턴이라고 한다.

아래 상황을 보자.

현재 코드는 파일에서 한 번에 한줄만 읽을 수 있다. 한번에 두줄을 읽거나 자주 사용되는 단어를 반환하기 위해선 어떻게 하는게 좋을까?

### **1. 동작파라미터화를 기억하자.**

초기화/준비 코드 -> 작업 -> 정리/마무리 코드의 과정에서 설정, 정리 과정은 재사용하고 처리하는 processFile 메서드만 다른 동작을 수행할 수 있다면 좋지 않을까? 이때 사용하는 것이 동작 파라미터화이다. 동작 파라미터화를 통해 processFile메서드가 한 번에 두 행을 읽게 처리할 수 있다.

```java
String result = processFile((BufferedReader br) -> br.readLine() + br.readLine());
```

### **2. 함수형 인터페이스를 이용한 전달**

함수형 인터페이스 자리에 람다를 사용할 수 있다고 배웠다. 따라서, bufferedReader -> String 에 IOException을 던지는 시그니처와 일치하는 함수형 인터페이스를 만들자.

```java
@FunctionalInterface
public interface BufferedReaderProcessor{
	String process(BufferedReader b) throws IOException;
}
```

그 결과 정의한 인터페이스를 ProcessFile의 인수로 전달할 수 있다.

### **3. 동작 실행**

이제 process 메서드의 시그니처 (BufferedReader -> String)과 일치하는 람다를 전달할 수 있다. processFile 바디 안에서 process를 호출할 수 있다.

### **4. 람다 전달**

**람다를 이용해 이제 다양한 동작을 processFile의 메서드로 전달할  수 있다.**

```java
String oneLine = processFile((BufferedReader br) -> br.readLine());
String twoLine = processFile((BufferedReader br) -> br.readLine()+br.readLine());
```

위 과정을 통해 함수형 인터페이스를 이용해서 람다를 전달하는 방법을 확인할 수 있었다.

**함수형 인터페이스 사용**

java.util.function 패키지로 자바8에선 여러가지 새로운 함수형 인터페이스를 제공한다. 이러한 함수형 인터페이스를 통해 새로운 인터페이스를 정의할 필요 없이 바로 사용할 수 있다.

### **Predicate:** T -> boolean

![https://blog.kakaocdn.net/dn/criWIA/btsrf3XAK3B/bJl6HSPOFzusTWygokvABk/img.png](https://blog.kakaocdn.net/dn/criWIA/btsrf3XAK3B/bJl6HSPOFzusTWygokvABk/img.png)

### **Consumer:** T -> void

![https://blog.kakaocdn.net/dn/mAwc5/btsrdhIDf7A/Kf1oAY6IwMiVnQBrn9smLK/img.png](https://blog.kakaocdn.net/dn/mAwc5/btsrdhIDf7A/Kf1oAY6IwMiVnQBrn9smLK/img.png)

### **Function:** T -> R

![https://blog.kakaocdn.net/dn/KmjJl/btsrjsPEhYw/YzoQcFC0d4zozXqMBNBPe0/img.png](https://blog.kakaocdn.net/dn/KmjJl/btsrjsPEhYw/YzoQcFC0d4zozXqMBNBPe0/img.png)

Function은 두 가지 임의 타입을 받아 T를 인자로 받았을때 R로 반환한다.

---

**기본형 특화**

지금까지는 임의의 형식 T,R에 대한 이야기를 다뤘다. 그러나, 형식에 특화된 함수형 인터페이스도 존재한다.

자바 객체의 형식은 참조형과 기본형으로 나뉘는데 참조형이란 객체같은 형식(Byte, Integer Long), 기본형이란 (int, double ,char)로 구분된다. 이때 제네릭 파라미터 T에는 참조형만 사용할 수 있다.

자바에서는 기본형 -> 참조형으로 변환하는 **박싱**, 참조형 -> 기본형으로 변환하는 **언박싱**을 자동으로 이뤄주는 **오토박싱**기능이 존재한다. 이러한 변환 과정에는 Cost가 소모되는데, 자바8에서는 기본형을 위한, 오토박싱을 피하는 함수형 인터페이스를 제공한다.

```java
public interface IntPredicate{
	boolean test(int t);
}
```

![https://blog.kakaocdn.net/dn/bYK0Du/btsrfgpbtfW/3OqFa07m2o8X1iWgjYF7fK/img.png](https://blog.kakaocdn.net/dn/bYK0Du/btsrfgpbtfW/3OqFa07m2o8X1iWgjYF7fK/img.png)