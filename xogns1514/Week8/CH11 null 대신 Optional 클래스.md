# CH11 null 대신 Optional 클래스

---

## 11.1 값이 없는 상황을 어떻게 처리할까?

```java
// Person/Car/Insurance 데이터 모델
public class Person1 {
    private Car1 car;
    public Car1 getCar(){
        return car;}
}

public class Car1 {
    private Insurance insurance;
    public Insurance getInsurance() {return insurance;}
}

public class Insurance {
    private String name;
    public String getName() {return name;}
}
```

❗️ 다음 코드에서 발생할 수 있는 문제

```java
public String getCarInsuranceName(Person person) {
	return person.getCar().getInsurance().getName();
}
```

person이 car를 갖고 있지 않다면 어떻게 될까? getInsurance는 null참조의 보험 정보를 반환하려 할 것이다. 

→ 런타임에 NullPointerException 에러가 발생한다.

## 11.1.1 보수적인 자세로 NullPointerException 줄이기

❓예기치 않은 NullPointerException을 피하려면 어떻게 해야 할까?

- 대부분의 프로그래머는 필요한 곳에 null 확인 코드를 추가해서 null 예외 문제를 해결하려 할 것이다.

```java
// null 안전시도1: 깊은 의심
public class OptionalMain {
    public String getCarInsuranceName(Person1 person) {
        if(person != null) {
            Car1 car = person.getCar();
            if (car != null) {
                Insurance insurance = car.getInsurance();
                if(insurance != null) {
                    return insurance.getName();
                }
            }
        }
        return "Unknown";
    }
}
```

- 모든 변수가 null인지 의심하므로 변수를 접근할 때마다 중첩된 if가 추가되면서 코드 들여쓰기 수준이 증가한다. 이와 같은 반복 패턴(recurring pattern)코드를 `‘깊은 의심’(deep doubt)` 이라고 부른다.

→ 코드의 구조가 엉망이 되고 가독성이 떨어진다.

```java
// null 안전시도 2: 너무 많은 출구
public String getCarInsuranceName2(Person1 person) {
        if (person == null) {
            return  "Unknown";
        }
        
        Car1 car1 = person.getCar();
        if (car1 == null) {
            return "Unknown";
        }
        
        Insurance insurance = car1.getInsurance();
        if(insurance == null) {
            return "Unknown";
        }
        return insurance.getName();
    }
```

- null 변수가 있으면 즉시 “Unkown”을 반환한다.

→ 메서드에 네 개의 출구가 생겼기 때문에 유지보수가 어려워진다. 

→ null로 값이 없다는 사실을 표현하는 것은 좋은 방법이 아니다. 따라서 값이 있거나 없음을 표현할 수 있는 좋은 방법이 필요하다.

## 11.1.2 null 때문에 발생하는 문제

- 에러의 근원이다: NullPointerException은 자바에서 가장 흔히 발생하는 에러이다.
- 코드를 어지럽힌다: 때로는 중첩된 null 확인 코드를 추가해야 하므로 null 때문에 코드 가독성이 떨어진다.
- 아무 의미가 없다: null은 아무 의미도 표현하지 않는다. 특히 정적 언어에서 값이 없음을 표현하는 방법으로는 적절하지 않다.
- 자바 철학에 위배된다: 자바는 개발자로부터 모든 포인터를 숨겼다. 하지만 예외는 null 포인터이다.
- 형식 시스템에 구멍을 만든다. null은 무형식이며 정보를 포함하고 있지 않으므로 모든 참조 형식에 null을 할당할 수 있다. 이런식으로 null이 할당되기 시작하면서 시스템의 다른 부분으로 null이 퍼졌을 때 에초에 null이 어떤 의미로 사용되었는지 알 수 없다.

## 11.1.3 다른 언어는 null 대신 무얼 사용하나?

- 그루비(groovy)에서는 안전 내비게이션 연산자(safe navigation operator)(?.)를 도입해 null 문제를 해결했다.

```java
def carInsuranceName = person?.car?.insurance?.name
```

→ 호출체인에 null인 참조가 있으면 결과로 null을 반환한다. 

- 하스켈은 `선택형 값(optional value)`을 저장할 수 있는 Maybe라는 형식을 제공한다. Maybe는 주어진 형식의 값을 갖거나 아니면 아무 값도 갖지 않을 수 있다.
- 스칼라는 T 형식의 값을 갖거나 아무 값도 갖지 않을 수 있는 Option[T]라는 구조를 제공한다.

❗️ 자바8은 ‘선택형 값’ 개념의 영향을 받아 java.util.Optional<T>라는 새로운 클래스를 제공한다.

## 11.2 Optional 클래스 소개

Optional은 선택형값을 캡슐화하는 클래스이다. 

- 값이 있으면 Optional 클래스는 값을 감싼다.
- 값이 없으면 Optional.empty 메서드로 Optional을 반환한다.
- Optional.empty는 Optional의 특별한 싱글턴 인스턴스를 반환하는 정적 팩토리 메서드이다.

<img width="230" alt="1" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/b181356f-591e-48f1-9063-d41c7e309291">

- Car 형식이 Optional<Car>로 바뀌었다. 이는 값이 없을 수 있음을 명시적으로 보여준다.

```java
// Optional로 Person/Car/Insurance 데이터 모델 재정의
public class Person {
    private Optional<Car> car;
    public Optional<Car> getCar(){
        return car;
    }
}

public class Car {
    private Optional<Insurance> insurance;
    public Optional<Insurance> getInsurance() {
        return insurance;
    }
}

public class Insurance {
    private String name;
    public String getName() {return name;}
}
```

- Optional 클래스를 사용하면서 모델의 의미sematic가 더 명확해졌음을 확인할 수 있다.
- Optional을 이용하면 값이 없는 상황이 우리 데이터에 문제가 있는 것인지 아니면 알고리즘의 버그인지 명확하게 구분할 수 있다.
- 모든 null 참조를 Optional로 대치하는 것은 바람직하지 않다. Optional의 역할은 더 이해하기 쉬운 API를 설계하도록 돕는 것이다.

## 11.3 Optional 적용 패턴

## 11.3.1 Optional 객체 만들기

### 빈 Optional

```java
Optional<Car> optCar = Optional.empty();
```

- 정적 팩토리 메서드 Optional.empty로 빈 Optional을 만들 수 있다.

### null이 아닌 값으로 Optional 만들기

```java
Optional<Car> optCar = Optional.of(car);
```

- car가 null이라면 즉시 NPE가 발생한다.
- Optional을 사용하지 않았다면 car의 프로퍼티에 접근하려 할 때 에러가 발생했을 것이다.

### null 값으로 Optional 만들기

```java
Optional<Car> optCar = Optional.ofNullable(car);
```

- 정적 팩토리 메서드 Optional.ofNullable로 null 값을 저장할 수 있는 Optional을 만들 수 있다.
- car가 null이면 빈 Optional 객체가 반환된다.

## 11.3.2 맵으로 Optional의 값을 추출하고 변환하기

```java
String name = null;
if(insurace != null) {
	name = insurance.getName();
}
```

- 이름 정보에 접근하기 전에 null인지 확인해야 한다.
- 이런 유형의 패턴에 사용할 수 있도록 Optional은 map 메서드를 지원한다.

```java
Optional<Insurance> optInsurance = Optional.ofNullable(insurance);
Optional<String> name = optInsurance.map(Insurance::getName);
```

- Optional이 값을 포함하면 map의 인수로 제공된 함수가 값을 바꾼다
- Optional이 비어있으면 아무 일도 일어나지 않는다.
    
    <img width="341" alt="2" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/ae6be81a-9e9b-4a51-adec-0aac5247c2d3">

    

## 11.3.3 flatMap으로 Optional 객체 연결

```java
Optional<Person> optPerson = Optional.of(person);
Optional<String> name = 
	optPerson.map(Person::getCar)
	.map(Car::getInsurance)
	.map(Insurance::getName);
```

<img width="138" alt="3" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/b688a587-07b9-4bda-a37d-fae3883ec621">

❓ 위 코드 문제점: getCar은 Optional<Car>을 반환한다. 따라서 map 연산의 결과는 Optional<Optional<Car>> 형식의 객체이다. getInsurance는 또 다른 Optional 객체를 반환하므로 getInsurance 메서드를 지원하지 않는다.

→ 해결방안: flatMap을 활용해 이차원 Optional을 일차원 Optional로 평준화 해야한다. 

<img width="419" alt="4" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/9bd118b2-c60e-4055-ad5b-5ad1a857cc44">


- 스트림과 Optional의 flatMap 메서드 비교

### Optional로 자동차의 보험회사 이름 찾기

```java
public String getCarInsuranceName(Optional<Person> person) {
        return person.flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown"); // 결과 Optional 비어있으면 기본값 사용
    }
```

<img width="461" alt="5" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/483eb112-3b72-4547-81c6-40b5fcad43c3">


- null을 확인하느라 조건 분기문을 추가해서 코드를 복잡하게 만들지 않으면서도 쉽게 이해할 수 있는 코드이다.
- 도메인 모델과 관련한 암묵적인 지식에 의존하지 않고 명시적으로 형식 시스템을 정의할 수 있다. 이 메서드를 사용하는 모든 사람에게 이 메서드가 빈 값을 받거나 빈 결과를 반환할 수 있음을 잘 문서화해서 제공하는 것과 같다.

📌 도메인 모델에 Optional을 사용했을 때 데이터를 직렬화할 수 없는 이유

- 잡 언어 아키텍트인 브라인언 고츠는 Optional의 용도가 선택형 반환값을 지원하는 것이라고 명확하게 못박았다.
- Optional 클래스는 필드 형식으로 사용할 것을 가정하지 않았으므로 Serializable 인터페이스를 구현하지 않는다.
- 직렬화 모델이 필요하다면 다음과 같이 사용하는 것을 권장한다.

```java
public class Person {
	private Car car;
	public Optional<Car> getCarAsOptional(){
		return Optional.ofNullable(car);
	}
}
```

## 11.3.4 Optional 스트림 조작

- 자바9에서는 Optional을 포함하는 스트림을 쉽게 처리할 수 있도록 Optional에 stream() 메서드를 추가했다.
- Optional 스트림 값을 가진 스트림으로 변환할 때 유용하게 사용할 수 있다.

```java
// 사람 목록을 이용해 가입한 보험 회사 이름 찾기
public Set<String> getCarInsuranceNames(List<Person> people) {
        return people.stream()
                .map(Person::getCar) // Optional<Car>
                .map(optCar -> optCar.flatMap(Car::getInsurance)) // Optional<Insurance>
                .map(optIns -> optIns.map(Insurance::getName)) //Optional<String>
                .flatMap(Optional::stream) // Stream<Optional<String>>을 Stream<String>으로 변환
                .collect(Collectors.toSet());
    }
```

```java
Stream<Optional<String>> stream = ...
Set<String> result = stream.filter(Optional::isPresent)
	.map(Optional::get)
	.collect(toSet());
```

- 마지막 결과를 얻으려면 빈 Optional을 제거하고 값을 언랩해야 한다는 것이 문제다. 밑의 코드와 같이 filter, map을 순서대로 사용해 결과를 얻을 수 있다.
- 하지만 위 코드와 같이 Optional::stream을 통해 한번의 연산으로 같은 결과를 얻을 수 있다.
- Optional 클래스의 stream() 메서드는 각 Optional이 비어있는지 아닌지에 따라 Optional을 0개 이상의 항목을 포함하는 스트림으로 변환한다.

## 11.3.5 디폴트 액션과 Optional 언랩

- `get()`: 값을 읽는 가장 간단한 메서드면서 동시에 가장 안전하지 않은 메서드이다.
    - 래핑된 값이 있으면 해당 값을 반환하고 값이 없으면 NoSuchElementException을 발생시킨다.
    - Optional에 값이 반드시 있다고 가정할 수 있는 상황이 아니면 get 메서드를 사용하지 않는 것이 바람직하다.
- `orElse(T other)`: Optional이 값을 포함하지 않을 때 기본값을 제공할 수 있다.
- `orElseGet(Supplier<? extends T> other)`: orElse 메서드에 대응하는 게으른 버전의 메서드이다.
    - Optional에 값이 없을 때만 Supplier가 실행되기 때문이다.
    - 디폴트 메서드를 만드는데 시간이 오래 걸리거나 or 기본값이 반드시 필요한 상황에 사용한다.
- `orElseThrow(Supplier<? extends X> exceptionSupplier)`: Optional이 비어있을 때 예외를 발생시킨다는 점에서 get 메서드와 비슷하다. 하지만 이 예외는 예외의 종류를 선택할 수 있다.
- `ifPresent(Consumer<? super T> consumer):` 값이 존재할 때 인수로 넘겨준 동작을 실행할 수 있다.
- (자바9) `ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction)`: 이 메서드는 Optional이 비어있을 때 실행할 수 있는 Runnable을 인수로 받는다는 점만 ifPresent와 다르다.

## 11.3.6 두 Optional 합치기

- Person과 Car 정보를 이용해 가장 저렴한 보험료를 제공하는 보험회사를 찾는 외부 서비스가 있다고 가정

```java
public Insurance findCheapestInsurance(Person person, Car car) {
	// 다양한 보험회사가 제공하는 서비스 조회
	// 모든 결과 데이터 비교
	return cheapestCompany;
}
```

두 Optional을 인수로 받아 Optional<Insurance>를 반환하는 null-safe의 메서드를 구현해야 한다고 가정하자.

```java
public Optional<Insurance> nullSafeFindCheapestInsurance(
	Optiona<Person> person, Optional<Car> car) {
	if(person.isPresent() && car.isPresent()) {
		return Optional.of(findCheapestInsurance(person.get(), car.get()));
	else{
		return Optional.empty();
	}
}
```

- 이 메서드는 person과 car의 시그니처만으로 둘 다 아무 값고 반환하지 않을 수 있다는 정보를 명시적으로 보여준다는 점이 장점이다.
- 하지만 null 확인 코드와 크게 다른 점이 없다

**Optional 언랩하지 않고 두 Optional 합치기**

```java
public Optional<Insurance> nullSafeFindCheapestInsurance(
	Optional<Person> person, Optional<Car> car {
	return person.flatMap(p -> car.map(c -> findCheapestInsurance(p, c)));
}
```

- person 값이 있으면 flatMap 메서드에 필요한 Optional<Insurance>를 반환하는 Function의 입력으로 person을 사용
- car가 존재하면 map 메서드로 전달한 람다 표현식이 findCheapestInsurance 호출

## 11.3.7 필터로 특정값 거르기

```java
Insurance insurance = ...;
if(insurance != null && "CambridgeInsurance.equals(insurance.getName())){
	System.out.println("ok");
}
```

- Insurance 객체가 null 인지 여부를 확인한다음 getName() 메서드를 호출한다

```java
Optional<Insurance> optInsurance = ...;
optInsurance.filter(insurance ->
	"CambridgeInsurance".equals(insurance.getName())) 
	.ifPresent(x -> System.out.println("ok"));
```

- Optional 객체의 filter 메서드를 이용해 위와 같이 구현할 수 있다.

## 11.4 Optional을 사용한 실용 예제

## 11.4.1 잠재적으로 null이 될 수 있는 대상을 Optional로 감싸기

Optional 클래스를 효과적으로 이용하려면 잠재적으로 존재하지 않는 값의 처리 방법을 바꿔야 한다. 

즉, 코드 구현만 바꾸는 것이 아니라 네이티브 자바 API와 상호작용하는 방식도 바꿔야 한다.

```java
Object value = map.get("key");
```

- Map의 get 메서드는 요청한 키에 대응하는 값을 찾지 못했을 때 null을 반환한다.
- get 메서드의 시그니처는 고칠 수 없지만, 반환값을 Optional로 감쌀 수 있다.

```java
Optional<Object> value = Optional.ofNullable(map.get("key"));
```

- 이처럼 null일 수 있는 값을 Optional로 안전하게 변환할 수 있다.

## 11.4.2 예외와 Optional 클래스

- 자바 API는 어떤 이유에서 값을 제공할 수 없을 때 null을 반환하는 대신 예외를 발생시킬 때도 있다.
- 예외를 발생시키는 메서드에서는 try/catch 블록을 사용한다.

```java
// 문자열 정수 Optional로 변환
public static Optional<Integer> stringToInt(String s){
	try{
		return Optional.of(Integer.parseInt(s));
	} catch (NumberFormatException e) {
		return Optional.empty();
	}
}
```

## 11.4.3 기본형 Optional을 사용하지 말아야 하는 이유

- 5장에서 스트림이 많은 요소를 가질 때는 기본형 특화 스트림을 이용해서 성능을 향상시킬 수 있다고 했다. 하지만 Optional의 최대 요소 수는 한 개이므로 Optional에서 기본형 특화 클래스로 성능을 개선할 수 없다.
- 기본형 특화 Optional은 map, flatMap, filter 등을 지원하지 않으므로 기본형 특화 Optional을 사용할 것을 권장하지 않는다.
- 스트림과 마찬가지로 기본형 특화 Optional로 생성한 결과를 일반 Optional과 혼용할 수 없다.