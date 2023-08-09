# 1-1 Java의 정의와 동작 방식에 대해 살펴봅시다.

---

# 1. Java의 정의와 동작 방식

객체 지향 언어(Object-Oriented-Language)이다. 하지만 Pure한 Object-Oriented Language가 아니다.

## JAVA 란?

순수 객체 지향 언어의 특징

- 추상화
- 다형성
- 캡슐화
- 상속
- 모든 사전 정의 데이터 타입과 사용자 정의 타입은 객체여야 한다
- 객체에 대한 모든 작업은 객체 스스로 정해야 한다

자바가 지키지 못한 순수 객체 지향 언어의 특징

- 원시 타입(Primitive Type)
    - 일반 변수는 공유 가능
- 정적 메서드(Static Method)
    - 인스턴스 생성 없이 호출 가능
- 래퍼 클래스(Wrapper Class) 또한 Auto Boxing/Unboxing 을 통해 원시 타입 변수 사용
    - Wrapper Class: Integer, Double 등 원시 타입을 래핑한 클래스
    

## Java 아키텍처

<img width="371" alt="1" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/ada33dcd-8f09-400f-b160-37b7204b6b7d">

### Java 플랫 폼

- Java 개발 및 실행 환경을 의미

### JDK(Java Development Tools)

<img width="281" alt="2" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/e707ccc1-a98d-438e-96f5-707ef4b68491">

- 자바 개발 킷(JRE + Development Tools)
- JDK 11 이후 JRE 를 포함
- JRE(Java Runtime Environment)
    - 자바 실행 환경(JVM + Library)

### JVM(Java Virtual Machine)

<img width="362" alt="3" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/c3644498-8cd9-40b5-a875-e25cdb2f1e90">

- 자바 가상 머신(프로그램 작동)
- Java 바이트코드를 기계어로 변환하고 실행
- 논리적인 개념, 여러 모듈의 결합체
- Java 앱을 실행하는 주체
- JVM 때문에 다양한 플랫폼 위에서 동작 가능
- 기능
    - 클래스 로딩
    - GC 등 메모리 관리
    - 스레드 관리
    - 예외 처리

### JVM Architechure

<img width="362" alt="4" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/7b169fab-e00c-40bc-b5da-9db0af0dbafd">

### Class Loaders

- Runtime에 Java 클래스/ 인터페이스의 바이트 코드를 동적으로 메모리에 로딩
- 로딩 작업
    - Loading: JVM이 필요한 클래스 파일 로드
    - Linking: 로드된 클래스의 verify, prepare, resolve 작업 수행
    - Initializing: 클래스/정적 변수 등 초기화

### Runtime Data Areas

- 앱 실행을 위해 사용되는 JVM 메모리 영역
- pc Register
    - 스레드 별로 생성되며 실행 중인 명령을 저장하는 영역
- JVM Stacks
    - 스레드 별로 생성되며 메서드 실행 관련 정보를 저장하는 영역
- Heap
    - JVM 실행 시 생성되며 모든 객체 인스턴스/배열에 대한 메모리가 할당되는 영역
- Method Area
    - JVM 실행 시 생성되며 클래스의 구조나 정보를 저장하는 영역
- Native Method Stacks
    - 스레드 별로 생성되며 네이티브 코드 실행에 관련 정보를 저장하는 영역

### Execution Engine

- 메모리 영역에 있는 데이터를 가져와 해당하는 작업 수행
- Interpreter
    - 메모리에 로드된 바이트 코드를 한줄 씩 해석/실행
- JIT(Just-In-Time) Compiler
    - 자주 호출되는 메서드의 바이트코드를 네이티브 코드로 컴파일
- GC(Garbage Collector)
    - 메모리에서 사용하지 않는 개체를 식별해 삭제하는 프로세스(대표적으로 Heap 영역)
    - 데몬 스레드로 동작(명시적으로 호출해도 즉시 실행되지 않음)

### JNI(Java Native Interface)

- JVM과 네이티브 라이브러리 간 이진 호환성을 위한 인터페이스
- 네이티브 메서드(네이티브 언어 C/C++ 등으로 작성) 호출, 데이터 전달과 메모리 관리 등 수행

### Native Libraries

- 네이티브 메서드의 구현체를 포함한 플랫폼별 라이브러리

 

## 컴파일러와 인터프리터

- 컴파일러
    - 프로그래밍 언어로 작성된 코드 → 타겟 언어로 변환하는 프로그램
    - 주로 High-Level 언어를 → Low-Level 언어(assembly, object code 등)로 변환
    - 전처리, 코드 최적화, 기계어 생성 등 역할도 수행
- 인터프리터
    - 읽은 코드 및 해당 명령을 직접 분석/실행하는 프로그램
    - 인터프리터 전략
        1. 코드 구문을 분석, 동작을 직접 수행
        2. 코드를 object code(중간 코드)로 변환, 즉시 실행
        3. 컴파일러에 의해 생성된 바이트코드를 명시적으로 실행

⇒ Java는 2,3 의 혼합이다

Java는 Interpret, Compile 중 무슨 방식일까?

- Java는 두 가지 방식을 혼합하여 사용하는 하이브리드 모델
    - javac로 소스 코드를 바이트코드(object code)로 변환
    - 변환된 바이트코드를 JVM 인터프리터가 분석, 실행
- 일반적으로 컴파일 언어로 분류하는 이유: javac를 통해 .java 파일을 .class 파일로 컴파일 하기 때문

### Java 동작 방식 정리

1. 작성한 소스 파일(.java)을 Java 컴파일러(javac)가 바이트코드(.class)로 변환
2. JVM이 실행되면 바이트코드 실행에 필요한 것들을 클래스 로더가 로딩
3. 로딩된 클래스의 바이트코드를 JVM의 실행 엔진이 해석, 실행
4. 실행 준비가 모두 완료 되면 JVM은 메인 메서드(Entry Point)를 호출
5. 호출된 메인 메서드를 실행할 메인 스레드가 생성되며, 메인 스레드의 JVM stacks이 생성됨
6. 생성된 메인 스레드 JVM stacks에 메인 메서드 스택 프레임이 생성됨
7. 앱이 실행되고, 필요한 시점마다 필요한 처리를 수행하며 메모리 확보 및 데이터 저장
8. JVM은 실행되는 Java 앱에 대해서 메모리, 스레드 등 관리

---

# 2. 클래스 로더와 클래스 로딩

<img width="376" alt="5" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/1a1d2e4a-580b-4d03-98af-f2df1b712424">

<img width="600" alt="6" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/83bd9570-76a1-4729-96ff-b0bf9d5e1677">

## Class Loaders

- 런타임에 Java Bytecode를 JVM 메모리로 동적 로딩
    - Loading
    - Linking
    - Initializing
- 필요할 때마다 필요한 클래스/리소스만 로딩
- 부트 스트랩 클래스 로더(Bootstrap Class Loader)
    - 최상위 클래스 로더이자 네이티브 코드로 작성된 클래스 로더로 base 모듈 로딩
- 플랫폼 클래스 로더(Platform Class Loader)
    - Java SE platform의 모든 API/클래스 로딩
- 시스템 클래스 로더(System Class Loader)
    - Java 앱 레벨의 클래스 로딩

## Class Loaders 원칙

- 위임(Delegation Model)
    - 클래스/리소스 등을 자신이 찾기 전에 먼저 상위 클래스 로더에게 위임
        - 그 전에 이미 로딩되어 있다면 위임하지 않고 반환
- 유일성(Unique Classes)
    - 상위 클래스 로더가 로딩한 클래스를 하위 클래스 로더가 다시 로딩하는 것을 방지
    - Delegation 원칙으로 인해 클래스의 유일성을 확보하기 쉬움
- 가시성(Visibility)
    - 하위 클래스 로더는 상위 클래스 로더가 로딩한 클래스를 볼 수 있음
    - 반대로 상위 클래스 로더는 하위 클래스 로더가 로딩한 클래스를 볼 수 없음

### 좋은 클래스 로더의 속성

- 클래스명이 같다면 클래스 로더는 항상 같은 클래스 객체를 반환
- 하위 클래스 로더가 상위 클래스로더에게 특정 클래스 로딩을 위임한 경우
    
    → 두 클래스 로더는 해당 클래스에 대해 동일한 객체를 반환해야 함
    

### Loading - 동작 방식

1. 최하위 클래스 로더부터 클래스를 찾음
    - ‘java.lang.ClassLoader’의 생성자, 하위 클래스를 사용해서 새 클래스 로더의 상위 지정 가능
2. ‘java.lang.ClassLoader’의 loadClass 메서드를 통해 클래스 로딩 수행(배열 클래스는 JVM이 생성)
    - 이미 로딩된 클래스가 있다면 이를 반환
    - 해당 클래스가 로딘되지 않았다면 이를 바로 로딩하지 않고 상위 클래스 로더에게 위임
    - 상위 클래스 로더가 로드하지 못했다면 findClass 메서드를 호출해 클래스 로딩
    - 클래스 로딩될 때 Run-Time constant Pool도 같이 생성
    - 클래스 로더에 의해 생성된 객체가 참조하는 클래스를 로딩할 때도 같은 메커니즘
3. 상위 클래스 또한 해당 클래스를 찾지 못한다면 하위 클래스가 찾아서 로딩

### ClassNotFoundException & NoClassDefFoundError

- ClassNotFoundExeption
    - FQCN을 통해 로딩 시 클래스패스에서 해당 클래스를 찾을 수 없는 경우 발생
    - 일반적으로 클래스명 등을 통해 리플렉션할 때 발생
- NoClassDefFoundError
    - new 키워드 또는 팩터리 메서드 등을 통한 객체를 로드할 때 발생
    - 일반적으로 static 블록 실행이나 static 변수 초기화 시 예외가 발생한 상황에서 에러 발생
- 위 문제 예방하기 위한 주의점
    - 해당 파일의 존재 유무
    - 올바른 클래스패스 설정 여부
    - 해당 클래스를 로딩하는 클래스 로더

### Linking

로딩된 클래스를 실행하기 위해 결합하는 프로세스

검증(verification), 정적 필드 준비(Preparation), 심볼릭 레퍼런스 처리(Resolution)

- 활성화 조건
    - 클래스/인터페이스는 ‘링킹’ 되기 전에 완전히 ‘로딩’ 되어야 함
    - 클래스/인터페이스는 ‘초기화’ 되기 전에 완전히 ‘검증’ 되어야 함
    - 링킹 중 감지된 오류는 관련 클래스/인터페이스의 링킹이 직간접적으로 필요한 지점에서 발생해야 함
    - 심볼릭 레퍼런스는 실제 참조될 때까지 확인되지 않음

### Linking - Verification

- Java Bytecode의 유효성을 검사하는 프로세스
- 클래스/인터페이스 바이트코드가 유효하지 않은 경우 이 시점에서 VerifyError 에러가 발생해야 함
- 검증 중에 LinkageError 에러가 발생하면 이후 검증 시에는 항상 동일한 에러로 실패햐아 함

### Linking - Preparation

- 클래스/인터페이스의 스태틱 필드를 생성, 필요한 메모리를 할당하며 기본값으로 초기화하는 프로세스
    - static int → 0, 객체 참조 → null로 초기화
- 런타임 상수 풀(Run - Time Constant Pool)도 메서드 영역(Method Area)에 할당됨

### Run-Time Constant Pool

- 클래스/ 인터페이스가 로딩될 때 메서드 영역에 할당되는 자료구조
- 일반 상수 풀의 데이터를 기반으로 생성되며 스태틱 상수과 심볼릭 레퍼런스 등을 포함

### Linking - Resolution

- 심볼릭 레퍼런스가 구체적인 값을 가리키도록 동적으로 결정하는 프로세스
    - JVM의 많은 명령들이 런타임 상수 풀의 심볼릭 레퍼런스에 의존
- 심볼릭 레퍼런스에 대한 동적 계산은 아래 규칙을 준수하며 수행
    - 해당 작업을 수행하는 동안 오류가 발생하지 않음
    - 후속 시도는 초기 시도와 동일한 결과를 만듦

### Initialization

- 클래스/ 인터페이스의 초기화 메서드 등을 실행하며 초기화
- 이전에 Verification, Preparation, Resolution 작업이 모두 완료되어야 수행 가능
- 클래스/ 인터페이스의 초기화 조건
    - 인스턴스 생성
    - 스태틱 메서드가 호출되거나 참조
    - 상수가 아닌 스태틱 필드 사용시
    - 리플렉션을 통한 메서드 호출
    - 클래스일 때 서브 클래스의 초기화

### Initialization - Synchronization

- 멀티스레드 환경이기 때문에 초기화 시 동기화에 신경써야 함
    - 서로 다른 스레드에서 같은 클래스/인터페이스를 동시에 초기화를 시도하는 상황
    - 초기화 작업으로 재귀적인 초기화가 발생하는 상황
- 초기화 전제 조건
    - 검증/준비 등이 완료된 초기화 전 상태인 클래스
    - 다른 스레드에 의해 초기화 중인 클래스
    - 완전히 초기화가 끝난 클래스
    - 초기화가 실패로 끝난 클래스

Initialization - Procedure

1. 클래스/인터페이스의 초기화 잠금 획득 시도
2. 다른 스레드가 해당 클래스/인터페이스를 초기화하고 있다면 초기화 잠금을 해제하고 초기화가 완료될 때까지 해당 스레드 차단을 반복
3. 현재 스레드에서 초기화가 진행되고 있다면 초기화에 대한 재귀 요청이어야 함. 초기화 잠금을 해제하고 정상적으로 완료해야함
4. 이미 초기화가 완료된 상태라면 그대로 초기화 잠금해제
5. 클래스/인터페이스가 잘못되었다면 초기화가 불가능하며 NoClassDefFoundError가 발생, 잘못되지 않았다면 해당 스레드에서 초기화 진행중이라고 기록 후 초기화 잠금 해제
6. 초기화 대상이 클래스라면 수퍼클래스와 수퍼인터페이스 초기화
7. 정의된 클래스 로더에게 쿼리하여 해당 클래스/인터페이스의 어설션 활성화 여부 확인
8. 위 작업들을 마치고 클래스/인터페이스의 초기화 메서드 실행

---

# 3. Java 바이트코드와 코드 캐시

- Machine code
    - CPU를 제어하는 기계어로 된 명령 코드
    - 각 명령 코드는 CPU 레지스터 또는 메모리에 있는 데이터에 대한 로딩, 저장, 연산 등 수행
    - 일반 프로그래머가 액세스할 수 없는 특정 CPU 내부 코드
- Binary code
    - 두 개의 기호(일반적으로 0,1)을 사용해 텍스트 또는 프로세스 명령을 나타내는 코드
- Object code
    - 일반적으로 컴파일러에 의해 생성된 중간 언어로 작성된 명령 코드

## Java 바이트코드

<img width="622" alt="7" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/0fda4ddf-2cc8-4d03-8bfd-c3adc348aafc">

- Java 컴파일러가 소스 코드를 통해 생성한 JVM이 인식할 수 있는 명령어 집합
- Compiler는 JVM의 요소가 아닌 JDK의 일부

### Invokedynamic

<img width="779" alt="8" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/e8d185ed-e2d0-421d-abae-1672db578a35">

- 런타임에 동적으로 호출할 메서드를 결정하기 위해 사용
    - lambda, Record 클래스, String concat 기능 등에 활용됨
- 부트스트랩 메서드
    - invokedynamic 명령 호출 단계에서 JVM이 호출하는 메서드이며 MethodHandle 객체의 생성과 초기화를 담당
    - Java 컴파일러에 의해 생성되며 JVM에 의해 단 한번 호출되고 CallSite객체를 생성
- 메타 팩터리 메서드
    - 모든 람다의 부트스트랩 메서드이며 CallSite객체를 생성
- 콜 사이트
    - Bootstrap 메서드 호출 결과로 반환되는 객체(CallSite)이며 Method Handle를 담아두는 홀더
- 메서드 핸들
    - 메서드를 동적으로 찾거나, 적용, 호출하기 위한 저수준(JVM) 메커니즘이며 해당 연산의 참조를 타입화 한 것
- 메서드 디스크립터
    - 메서드의 파라미터(타입, 순서)와 반환 타입을 문자열로 표현한 것

## Java 코드 캐시

JVM이 Java 바이트코드를 컴파일한 네이티브 코드를 저장하는 메모리 영역

- JIT 컴파일러가 코드 캐시 영역을 가장 많이 사용하게 됨
- 고정된 크기로, 확장 불가하며 가득차면 JIT 컴파일러가 꺼져 추가 코드를 컴파일 하지 않음

### Java 코드 캐시의 세그먼트

JDK 9 버전부터 코드 캐시를 3개의 세그먼트로 분리

- non-profiled: 세그먼트 수명이 긴 완전히 최적화된 코드가 포함됨
- profiled-code: 수명이 짧은 조금 최적화된 코드가 포함됨
- non-method: 바이트코드 인터프리터와 같은 JVM 내부 관련 코드가 포함됨

# 4. 바이트코드를 컴파일하는 AOT, JIT 컴파일러

## AOT(Ahead of Time) 컴파일러

- Java 바이트코드를 앱 ‘실행 전’ 모두 한 번에 컴파일 해두는 방식
- Java 바이트코드(.class 파일)를 AOT 컴파일러(joatc) 통해 컴파일 하는 방식
- 해당 기능 수행하는 jaotc가 JDK17에서 제거됨.(Graal VM 추가시 이용 가능)

## JIT(Just-In-Time) 컴파일

<img width="493" alt="9" src="https://github.com/GDSC-Hongik/2023-2-OC-Java-Study/assets/66353672/42078f50-3222-4055-bbe2-b3a0553877a6">

- ‘Hot Method’를 추적하여 컴파일**(Profile-Guided Optimization)**
- C1, C2 컴파일러 모드로 구성
- C1
    - 런타임에 바이트코드를 기계어로 컴파일
    - 빠른 시작과 견고한 최적화가 필요한 앱에서 사용됨
- C2
    - 변환된 기계어를 분석하여 C1보다 오래 걸리지만 더욱 최적화된 네이티브 코드로 컴파일
    - 오랫동안 실행을 하는 서버 앱 용도

### AOT vs JIT

- AOT 장점
    - 부팅시간(JVM Warm up) 최소화
    - 메모리 사용량 최소화(런타임 오버헤드)
    - 사전 컴파일 효과로 런타임 특성 중 일부는 예측 가능
    - 일반적인 현대 백엔드 API 서버의 아키텍서와 어울림
        - 빠른 시작으로 부팅시간 단축
        - 최적화를 통한 컨테이너 이미지 크기의 최소화
- JIT 장점
    - 플랫폼 확장성
    - 런타임 정보를 포함한 최적화
